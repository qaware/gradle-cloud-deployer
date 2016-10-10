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

import de.qaware.cloud.deployer.commons.error.ResourceException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;

/**
 * Represents a basic resource which is used to execute a ping.
 */
public abstract class BasePingResource {

    /**
     * The id of the environment this ping resource belongs to.
     */
    private final String environmentId;

    /**
     * Creates a new base ping resource.
     *
     * @param environmentId The id of the environment this ping resource belongs to.
     */
    public BasePingResource(String environmentId) {
        this.environmentId = environmentId;
    }

    /**
     * Executes the specified ping call.
     *
     * @param call The ping call.
     * @throws ResourceException If the response is not successful.
     */
    protected void executePingCall(Call<ResponseBody> call) throws ResourceException {
        try {
            Response<ResponseBody> response = call.execute();
            if (!ResponseInterpreterUtil.isSuccessResponse(response)) {
                throw new ResourceException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_PING_FAILED", environmentId, response.code()));
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Executes a ping test again this resource.
     *
     * @throws ResourceException If the response isn't successful.
     */
    public abstract void ping() throws ResourceException;

    @Override
    public String toString() {
        return COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_PING", environmentId);
    }
}
