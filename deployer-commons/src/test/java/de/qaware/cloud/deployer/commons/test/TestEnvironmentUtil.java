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
package de.qaware.cloud.deployer.commons.test;

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestEnvironmentUtil {

    private TestEnvironmentUtil() {
    }

    private static String loadEnvironmentVariable(String key) {
        return System.getenv(key);
    }

    public static CloudConfig createCloudConfig(Map<String, String> environmentVariables, String updateStrategy) {
        return new CloudConfig(environmentVariables.get("URL"),
                environmentVariables.get("USERNAME"),
                environmentVariables.get("PASSWORD"),
                "",
                updateStrategy,
                new SSLConfig(true, ""));
    }

    public static ClientFactory createClientFactory(Map<String, String> environmentVariables) throws ResourceException {
        SSLConfig sslConfig = new SSLConfig(true, null);
        CloudConfig cloudConfig = new CloudConfig(environmentVariables.get("URL"),
                environmentVariables.get("USERNAME"),
                environmentVariables.get("PASSWORD"),
                "",
                "HARD",
                sslConfig
        );
        return new ClientFactory(cloudConfig);
    }

    public static Map<String, String> loadEnvironmentVariables() throws IOException {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("URL", loadEnvironmentVariable("URL"));
        environmentVariables.put("USERNAME", loadEnvironmentVariable("USERNAME"));
        environmentVariables.put("PASSWORD", loadEnvironmentVariable("PASSWORD"));
        environmentVariables.put("TEST_NAMESPACE_PREFIX", loadEnvironmentVariable("TEST_NAMESPACE_PREFIX"));
        return environmentVariables;
    }
}
