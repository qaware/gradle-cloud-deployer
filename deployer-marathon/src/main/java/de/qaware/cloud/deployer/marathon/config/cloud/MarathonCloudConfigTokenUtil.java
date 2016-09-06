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
package de.qaware.cloud.deployer.marathon.config.cloud;

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.CloudConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import retrofit2.Response;

import java.io.IOException;

/**
 * Utility that replaces a cloud config's dcos token with a marathon api token.
 */
public class MarathonCloudConfigTokenUtil {

    /**
     * UTILITY.
     */
    private MarathonCloudConfigTokenUtil() {
    }

    /**
     * Replaces the dcos token in the specified cloud config with a marathon api token.
     *
     * @param cloudConfig The cloud config which contains the dcos token that will be replaced by the marathon api token.
     * @throws CloudConfigException If the specified dcos token isn't valid.
     * @throws ResourceException    If a problem with the cloud config exists.
     */
    public static void retrieveApiToken(CloudConfig cloudConfig) throws CloudConfigException, ResourceException {
        String dcosToken = cloudConfig.getToken();
        if (dcosToken != null && !dcosToken.isEmpty()) {
            try {
                // Create a client factory and a client.
                ClientFactory clientFactory = new ClientFactory(cloudConfig);
                TokenClient tokenClient = clientFactory.create(TokenClient.class);

                // Create token description.
                Token token = new Token(dcosToken);

                // Execute request.
                Response<Token> tokenResponse = tokenClient.login(token).execute();
                if (tokenResponse.code() != 200 || tokenResponse.body() == null) {
                    throw new CloudConfigException("Couldn't retrieve a api token - please recheck your dcos token");
                }

                // Return api token.
                String apiToken = tokenResponse.body().getToken();

                // Replace token
                cloudConfig.setToken(apiToken);
            } catch (IOException e) {
                throw new ResourceException("Couldn't connect");
            }
        }
    }
}