/*
 * Copyright 2016 QAware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qaware.cloud.deployer.commons.resource;

import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;

/**
 * Implements a basic resource independent of the target cloud system. It offers functionality for backend communication.
 *
 * @param <ConfigType> The type of ResourceConfig this base resource is defined for.
 */
public abstract class BaseResource<ConfigType extends BaseResourceConfig> implements Resource {

    /**
     * Id of the error message for a unhandled http status code.
     */
    private static final String ERROR_UNHANDLED_HTTP_STATUS_CODE = "DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE";

    /**
     * The timeout defines the maximum duration of a backend operation in seconds. If it takes longer to execute the
     * operation a error will be thrown.
     */
    private static final int TIMEOUT = 300;

    /**
     * The time in seconds between two requests to check if the backend operation finished.
     */
    private static final double BLOCK_TIME = 0.5;

    /**
     * The time in seconds to wait in the case of a server error before retrying a call.
     */
    private static final int SERVER_ERROR_WAITING_TIME = 5;

    /**
     * The config this resource belongs to.
     */
    private final ConfigType resourceConfig;

    /**
     * The client factory which is used to create the clients for backend communication.
     */
    private final ClientFactory clientFactory;

    /**
     * Creates a new base resource.
     *
     * @param resourceConfig The config this resource belongs to.
     * @param clientFactory  The client factory which is used to create the clients for backend communication.
     */
    public BaseResource(ConfigType resourceConfig, ClientFactory clientFactory) {
        this.resourceConfig = resourceConfig;
        this.clientFactory = clientFactory;
    }

    /**
     * Returns the id of this resource.
     *
     * @return The id.
     */
    public String getId() {
        return resourceConfig.getResourceId();
    }

    /**
     * Returns the resource config of this resource.
     *
     * @return The resource config.
     */
    public ConfigType getResourceConfig() {
        return resourceConfig;
    }

    @Override
    public abstract String toString();

    /**
     * Creates a retrofit client using the client factory.
     *
     * @param serviceClass The class of the client.
     * @param <T>          The class of the client.
     * @return Returns a new retrofit client of the specified class.
     */
    protected <T> T createClient(Class<T> serviceClass) {
        return clientFactory.create(serviceClass);
    }

    /**
     * Executes a call and interprets the answer.
     *
     * @param call The call which will be executed.
     * @throws ResourceException If the response contains a unhandled status code.
     */
    protected void executeCall(Call<ResponseBody> call) throws ResourceException {
        try {
            Response<ResponseBody> response = call.execute();
            if (!isSuccessResponse(response)) {
                throw new ResourceException(COMMONS_MESSAGE_BUNDLE.getMessage(ERROR_UNHANDLED_HTTP_STATUS_CODE, response.code()));
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Executes a exists call and interprets the answer.
     *
     * @param existsCall The call which will be executed.
     * @return TRUE if the resource exists, FALSE otherwise.
     * @throws ResourceException If the response contains a unhandled status code.
     */
    protected boolean executeExistsCall(Call<ResponseBody> existsCall) throws ResourceException {
        try {
            Response<ResponseBody> response = existsCall.execute();

            // Retry the call if the server had an error
            if (isServerErrorResponse(response)) {
                response = retryCall(existsCall);
            }

            // Interpret result
            if (isSuccessResponse(response)) {
                return true;
            } else if (isNotFoundResponse(response)) {
                return false;
            } else {
                throw new ResourceException(COMMONS_MESSAGE_BUNDLE.getMessage(ERROR_UNHANDLED_HTTP_STATUS_CODE, response.code()));
            }
        } catch (IOException | InterruptedException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Executes a create call and interprets the answer.
     *
     * @param createCall The call which will be executed.
     * @throws ResourceException If the backend operation didn't finish within the specified interval or the status code
     *                           is unhandled.
     */
    protected void executeCreateCallAndBlock(Call<ResponseBody> createCall) throws ResourceException {
        try {
            Response<ResponseBody> response = createCall.execute();

            // Retry the call if the server had an error
            if (isServerErrorResponse(response)) {
                response = retryCall(createCall);
            }

            // Interpret result
            if (isSuccessResponse(response)) {
                Blocker blocker = new Blocker(TIMEOUT, BLOCK_TIME, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_TIMEOUT_DURING_CREATION"));
                while (!this.exists()) {
                    blocker.block();
                }
            } else {
                throw new ResourceException(COMMONS_MESSAGE_BUNDLE.getMessage(ERROR_UNHANDLED_HTTP_STATUS_CODE, response.code()));
            }
        } catch (IOException | InterruptedException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Executes a delete call and interprets the answer.
     *
     * @param deleteCall The call which will be executed.
     * @throws ResourceException If the backend operation didn't finish within the specified interval or the status code
     *                           is unhandled.
     */
    protected void executeDeleteCallAndBlock(Call<ResponseBody> deleteCall) throws ResourceException {
        try {
            Response<ResponseBody> response = deleteCall.execute();

            // Retry the call if the server had an error
            if (isServerErrorResponse(response)) {
                response = retryCall(deleteCall);
            }

            // Interpret result
            if (isSuccessResponse(response)) {
                Blocker blocker = new Blocker(TIMEOUT, BLOCK_TIME, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_TIMEOUT_DURING_DELETION"));
                while (this.exists()) {
                    blocker.block();
                }
            } else {
                throw new ResourceException(COMMONS_MESSAGE_BUNDLE.getMessage(ERROR_UNHANDLED_HTTP_STATUS_CODE, response.code()));
            }
        } catch (IOException | InterruptedException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Creates a request body using the content and the content type of the config file.
     *
     * @return The request body for the content of the config file.
     * @throws ResourceException If the media type isn't valid.
     */
    protected RequestBody createRequestBody() throws ResourceException {
        return RequestBody.create(createMediaType(), getResourceConfig().getContent());
    }

    /**
     * Creates a media type object for the content type of this file.
     *
     * @return The media type for the content type of this file.
     * @throws ResourceException If the content type isn't valid.
     */
    protected abstract MediaType createMediaType() throws ResourceException;

    /**
     * Retries a call after a waiting period.
     *
     * @return The response of the call.
     */
    private Response<ResponseBody> retryCall(Call<ResponseBody> call) throws IOException, InterruptedException {
        Thread.sleep(SERVER_ERROR_WAITING_TIME * 1000);
        return call.clone().execute();
    }

    /**
     * Checks if the specified response is a not found response.
     *
     * @param response The response which will be checked.
     * @return TRUE if the response is a not found response, FALSE otherwise.
     */
    private boolean isNotFoundResponse(Response<ResponseBody> response) {
        return response.code() == 404;
    }

    /**
     * Checks if the specified response is a success response.
     *
     * @param response The response which will be checked.
     * @return TRUE if the response is a success response, FALSE otherwise.
     */
    private boolean isSuccessResponse(Response<ResponseBody> response) {
        return response.code() == 200 || response.code() == 201;
    }

    /**
     * Indicates whether the server threw an error.
     *
     * @param response The response which contains the response code.
     * @return TRUE if the server threw an error, FALSE otherwise.
     */
    private boolean isServerErrorResponse(Response<ResponseBody> response) {
        return response.code() == 409 || response.code() == 500;
    }
}
