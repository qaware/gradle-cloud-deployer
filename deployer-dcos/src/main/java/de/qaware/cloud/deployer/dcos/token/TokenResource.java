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

import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import retrofit2.Response;

import java.io.IOException;

import static de.qaware.cloud.deployer.dcos.logging.DcosMessageBundle.DCOS_MESSAGE_BUNDLE;

/**
 * Retrieves a dcos authentication token using a OpenId connect id token.
 */
public final class TokenResource {

    /**
     * The client which is used to retrieve the token from the backend.
     */
    private final TokenClient tokenClient;

    /**
     * Creates a new token resource.
     *
     * @param environmentConfig The config which the describes the environment.
     * @throws ResourceException If the config isn't valid.
     */
    public TokenResource(EnvironmentConfig environmentConfig) throws ResourceException {
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        this.tokenClient = clientFactory.create(TokenClient.class);
    }

    /**
     * Retrieves a dcos authentication token using a OpenId connect id token.
     *
     * @param authToken The OpenId connect id token which is used to authorize.
     * @return The dcos authentication token.
     * @throws EnvironmentConfigException If the specified OpenId connect id token isn't valid.
     * @throws ResourceException          If a problem with the cloud config exists.
     */
    public String retrieveAuthenticationToken(String authToken) throws EnvironmentConfigException, ResourceException {
        if (authToken != null && !authToken.isEmpty()) {
            try {
                // Create token description.
                Token token = new Token(authToken);

                // Execute request.
                Response<Token> tokenResponse = tokenClient.login(token).execute();
                if (tokenResponse.code() != 200 || tokenResponse.body() == null) {
                    throw new EnvironmentConfigException(DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN"));
                }

                // Return authentication token.
                return tokenResponse.body().getToken();
            } catch (IOException e) {
                throw new EnvironmentConfigException(DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_ESTABLISH_CONNECTION"), e);
            }
        } else {
            throw new EnvironmentConfigException(DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_EMPTY_TOKEN"));
        }
    }
}