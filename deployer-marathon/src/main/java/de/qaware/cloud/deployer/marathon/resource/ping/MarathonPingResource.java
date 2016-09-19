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
package de.qaware.cloud.deployer.marathon.resource.ping;

import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BasePingResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Represents a marathon ping resource.
 */
public class MarathonPingResource extends BasePingResource {

    /**
     * The client used for backend communication.
     */
    private final MarathonPingClient marathonPingClient;

    /**
     * Creates a new marathon ping resource.
     *
     * @param environmentConfig The config of the environment this resource belongs to.
     */
    public MarathonPingResource(EnvironmentConfig environmentConfig) throws ResourceException {
        super(environmentConfig.getId());
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        marathonPingClient = clientFactory.create(MarathonPingClient.class);
    }

    @Override
    public void ping() throws ResourceException {
        Call<ResponseBody> call = marathonPingClient.ping();
        executePingCall(call);
    }
}
