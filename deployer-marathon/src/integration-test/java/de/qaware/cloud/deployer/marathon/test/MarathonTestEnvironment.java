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

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.test.TestEnvironment;
import mesosphere.marathon.client.Marathon;

public class MarathonTestEnvironment extends TestEnvironment {

    private Marathon marathonClient;
    private EnvironmentConfig environmentConfig;

    MarathonTestEnvironment(ClientFactory clientFactory, EnvironmentConfig environmentConfig, Marathon marathonClient) {
        super(clientFactory);
        this.marathonClient = marathonClient;
        this.environmentConfig = environmentConfig;
    }

    public Marathon getMarathonClient() {
        return marathonClient;
    }

    public EnvironmentConfig getEnvironmentConfig() {
        return environmentConfig;
    }
}
