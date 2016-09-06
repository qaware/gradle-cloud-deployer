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

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.CloudConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.test.TestEnvironmentUtil;
import de.qaware.cloud.deployer.marathon.config.cloud.MarathonCloudConfigTokenUtil;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonClient;

import java.io.IOException;
import java.util.Map;

public class MarathonTestEnvironmentUtil {

    // Environment variables.
    private static final String MARATHON_URL_ENV = "MARATHON_URL";
    private static final String MARATHON_TOKEN_ENV = "MARATHON_TOKEN";

    // Constants.
    private static final String MARATHON_DEFAULT_UPDATE_STRATEGY = "HARD";

    private static CloudConfig createCloudConfig(Map<String, String> environmentVariables, String updateStrategy) {
        CloudConfig cloudConfig = new CloudConfig(environmentVariables.get(MARATHON_URL_ENV), updateStrategy);
        cloudConfig.setToken(environmentVariables.get(MARATHON_TOKEN_ENV));
        return cloudConfig;
    }

    private static ClientFactory createClientFactory(CloudConfig cloudConfig) throws ResourceException {
        return new ClientFactory(cloudConfig);
    }

    private static Marathon createMarathonClient(CloudConfig cloudConfig) {
        return AuthorizedMarathonClient.createInstance(cloudConfig.getBaseUrl(), cloudConfig.getToken());
    }

    public static MarathonTestEnvironment createTestEnvironment() throws ResourceConfigException, ResourceException, IOException, CloudConfigException {
        return createTestEnvironment(MARATHON_DEFAULT_UPDATE_STRATEGY);
    }

    public static MarathonTestEnvironment createTestEnvironment(String updateStrategy) throws ResourceConfigException, ResourceException, IOException, CloudConfigException {
        Map<String, String> environmentVariables = TestEnvironmentUtil.loadEnvironmentVariables(
                MARATHON_TOKEN_ENV,
                MARATHON_URL_ENV
        );

        CloudConfig cloudConfig = createCloudConfig(environmentVariables, updateStrategy);
        ClientFactory clientFactory = createClientFactory(cloudConfig);

        // Replace the token.
        MarathonCloudConfigTokenUtil.retrieveApiToken(cloudConfig);

        Marathon marathonClient = createMarathonClient(cloudConfig);
        return new MarathonTestEnvironment(clientFactory, cloudConfig, marathonClient);
    }
}
