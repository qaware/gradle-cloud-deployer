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

public abstract class BaseResource<ConfigType extends BaseResourceConfig> implements Resource {

    // Maximum call-duration in seconds
    private static final int TIMEOUT = 300;
    private static final double INTERVAL = 0.5;

    private final ConfigType resourceConfig;
    private final ClientFactory clientFactory;

    public BaseResource(ConfigType resourceConfig, ClientFactory clientFactory) {
        this.resourceConfig = resourceConfig;
        this.clientFactory = clientFactory;
    }

    public String getId() {
        return resourceConfig.getResourceId();
    }

    public ConfigType getResourceConfig() {
        return resourceConfig;
    }

    public <T> T createClient(Class<T> serviceClass) {
        return clientFactory.create(serviceClass);
    }

    public void executeCall(Call<ResponseBody> call) throws ResourceException {
        try {
            Response<ResponseBody> response = call.execute();
            if (!isSuccessResponse(response)) {
                throw new ResourceException("Received a unhandled http status code: " + response.code());
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    public boolean executeExistsCall(Call<ResponseBody> existsCall) throws ResourceException {
        try {
            Response<ResponseBody> response = existsCall.execute();
            if (isSuccessResponse(response)) {
                return true;
            } else if (isNotFoundResponse(response)) {
                return false;
            } else {
                throw new ResourceException("Received a unhandled http status code: " + response.code());
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    public void executeCreateCallAndBlock(Call<ResponseBody> createCall) throws ResourceException {
        try {
            Response<ResponseBody> response = createCall.execute();
            if (isSuccessResponse(response)) {
                CallBlocker blocker = new CallBlocker(TIMEOUT, INTERVAL, "Resource wasn't created within specified time (" + createCall.request().url() + ")");
                while (!this.exists()) {
                    blocker.block();
                }
            } else {
                throw new ResourceException("Received a unhandled http status code: " + response.code());
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    public void executeDeleteCallAndBlock(Call<ResponseBody> deleteCall) throws ResourceException {
        try {
            Response<ResponseBody> response = deleteCall.execute();
            if (isSuccessResponse(response)) {
                CallBlocker blocker = new CallBlocker(TIMEOUT, INTERVAL, "Resource wasn't deleted within specified time (" + deleteCall.request().url() + ")");
                while (this.exists()) {
                    blocker.block();
                }
            } else {
                throw new ResourceException("Received a unhandled http status code: " + response.code());
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public abstract String toString();

    protected RequestBody createRequestBody() {
        return RequestBody.create(createMediaType(), getResourceConfig().getContent());
    }

    protected abstract MediaType createMediaType();

    private boolean isNotFoundResponse(Response<ResponseBody> response) {
        return response.code() == 404;
    }

    private boolean isSuccessResponse(Response<ResponseBody> response) {
        return response.code() == 200 || response.code() == 201;
    }
}
