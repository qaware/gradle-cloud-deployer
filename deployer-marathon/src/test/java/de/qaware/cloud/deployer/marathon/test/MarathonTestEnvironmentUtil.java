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
package de.qaware.cloud.deployer.marathon.test;

import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.test.TestEnvironmentUtil;
import de.qaware.cloud.deployer.commons.update.UpdateStrategy;
import de.qaware.cloud.deployer.dcos.token.TokenResource;
import mesosphere.marathon.client.Marathon;

import java.io.IOException;
import java.util.Map;

public class MarathonTestEnvironmentUtil {

    // Environment variables.
    private static final String MARATHON_URL_ENV = "MARATHON_URL";
    private static final String MARATHON_TOKEN_ENV = "MARATHON_TOKEN";

    // Constants.
    private static final UpdateStrategy MARATHON_DEFAULT_UPDATE_STRATEGY = UpdateStrategy.REPLACE;

    private static EnvironmentConfig createEnvironmentConfig(Map<String, String> environmentVariables, UpdateStrategy updateStrategy) {
        return new EnvironmentConfig("test", environmentVariables.get(MARATHON_URL_ENV), updateStrategy);
    }

    private static ClientFactory createClientFactory(EnvironmentConfig environmentConfig) throws ResourceException {
        return new ClientFactory(environmentConfig);
    }

    private static Marathon createMarathonClient(EnvironmentConfig environmentConfig) {
        return AuthorizedMarathonClient.createInstance(environmentConfig.getBaseUrl() + "/service/marathon", environmentConfig.getAuthConfig().getToken());
    }

    public static MarathonTestEnvironment createTestEnvironment() throws ResourceConfigException, ResourceException, IOException, EnvironmentConfigException {
        return createTestEnvironment(MARATHON_DEFAULT_UPDATE_STRATEGY);
    }

    public static MarathonTestEnvironment createTestEnvironment(UpdateStrategy updateStrategy) throws ResourceConfigException, ResourceException, IOException, EnvironmentConfigException {
        Map<String, String> environmentVariables = TestEnvironmentUtil.loadEnvironmentVariables(
                MARATHON_TOKEN_ENV,
                MARATHON_URL_ENV
        );

        EnvironmentConfig environmentConfig = createEnvironmentConfig(environmentVariables, updateStrategy);

        // Replace the token.
        TokenResource tokenResource = new TokenResource(environmentConfig);
        String apiToken = tokenResource.retrieveApiToken(environmentVariables.get(MARATHON_TOKEN_ENV));
        environmentConfig.getAuthConfig().setToken(apiToken);

        ClientFactory clientFactory = createClientFactory(environmentConfig);

        Marathon marathonClient = createMarathonClient(environmentConfig);
        return new MarathonTestEnvironment(clientFactory, environmentConfig, marathonClient);
    }
}
