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
package de.qaware.cloud.deployer.dcos.token;

import de.qaware.cloud.deployer.commons.config.cloud.AuthConfig;
import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.CloudConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import retrofit2.Response;

import java.io.IOException;

import static de.qaware.cloud.deployer.dcos.logging.DCOSMessageBundle.DCOS_MESSAGE_BUNDLE;

/**
 * Retrieves a dcos api token using a dcos cli token.
 */
public final class TokenResource {

    /**
     * The config for the cloud.
     */
    private final CloudConfig cloudConfig;

    /**
     * The client which is used to retrieve the token from the backend.
     */
    private final TokenClient tokenClient;

    /**
     * Creates a new token resource.
     *
     * @param cloudConfig The config which the describes the cloud.
     * @throws ResourceException If the config isn't valid.
     */
    public TokenResource(CloudConfig cloudConfig) throws ResourceException {
        this.cloudConfig = cloudConfig;

        // Create the client
        ClientFactory clientFactory = new ClientFactory(cloudConfig);
        this.tokenClient = clientFactory.create(TokenClient.class);
    }

    /**
     * Retrieves the dcos api token using a dcos cli token.
     *
     * @throws CloudConfigException If the specified dcos cli token isn't valid.
     * @throws ResourceException    If a problem with the cloud config exists.
     */
    public String retrieveApiToken() throws CloudConfigException, ResourceException {
        AuthConfig authConfig = cloudConfig.getAuthConfig();
        String dcosToken = authConfig.getToken();
        if (dcosToken != null && !dcosToken.isEmpty()) {
            try {
                // Create token description.
                Token token = new Token(dcosToken);

                // Execute request.
                Response<Token> tokenResponse = tokenClient.login(token).execute();
                if (tokenResponse.code() != 200 || tokenResponse.body() == null) {
                    throw new CloudConfigException(DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN"));
                }

                // Return api token.
                return tokenResponse.body().getToken();
            } catch (IOException e) {
                throw new CloudConfigException(DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_ESTABLISH_CONNECTION"), e);
            }
        } else {
            throw new CloudConfigException(DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_EMPTY_TOKEN"));
        }
    }
}