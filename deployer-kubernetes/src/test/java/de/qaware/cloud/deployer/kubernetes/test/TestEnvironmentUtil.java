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
package de.qaware.cloud.deployer.kubernetes.test;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestEnvironmentUtil {

    private TestEnvironmentUtil() {
    }

    private static AtomicInteger subNamespaceCounter = new AtomicInteger(0);

    private static Map<String, String> loadEnvironmentVariables() throws IOException {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("USERNAME", System.getenv("USERNAME"));
        environmentVariables.put("PASSWORD", System.getenv("PASSWORD"));
        environmentVariables.put("URL", System.getenv("URL"));
        environmentVariables.put("TEST_NAMESPACE_PREFIX", System.getenv("TEST_NAMESPACE_PREFIX"));
        return environmentVariables;
    }

    private static ClientFactory createClientFactory(Map<String, String> environmentProperties) {
        SSLConfig sslConfig = new SSLConfig(true, null);
        CloudConfig cloudConfig = new CloudConfig(environmentProperties.get("URL"),
                environmentProperties.get("USERNAME"),
                environmentProperties.get("PASSWORD"),
                "HARD",
                sslConfig
        );
        return new ClientFactory(cloudConfig);
    }

    private static KubernetesClient createKubernetesClient(Map<String, String> environmentProperties) {
        Config config = new ConfigBuilder()
                .withMasterUrl(environmentProperties.get("URL"))
                .withTrustCerts(true)
                .withUsername(environmentProperties.get("USERNAME"))
                .withPassword(environmentProperties.get("PASSWORD"))
                .build();
        return new DefaultKubernetesClient(config);
    }

    private static NamespaceResource createNamespaceResource(ClientFactory clientFactory, Map<String, String> environmentProperties) throws ResourceConfigException {
        String namespace = environmentProperties.get("TEST_NAMESPACE_PREFIX") + "-" + subNamespaceCounter.getAndIncrement();
        KubernetesResourceConfig namespaceResourceConfig = NamespaceConfigFactory.create(namespace);
        return new NamespaceResource(namespaceResourceConfig, clientFactory);
    }

    private static CloudConfig createCloudConfig(Map<String, String> environmentProperties, String updateStrategy) {
        return new CloudConfig(environmentProperties.get("URL"),
                environmentProperties.get("USERNAME"),
                environmentProperties.get("PASSWORD"),
                updateStrategy,
                new SSLConfig(true, ""));
    }

    public static TestEnvironment createTestEnvironment() throws IOException, ResourceConfigException, ResourceException {
        return createTestEnvironment("HARD");
    }

    public static TestEnvironment createTestEnvironment(String updateStrategy) throws IOException, ResourceConfigException {
        Map<String, String> environmentVariables = loadEnvironmentVariables();
        KubernetesClient kubernetesClient = createKubernetesClient(environmentVariables);
        ClientFactory clientFactory = createClientFactory(environmentVariables);
        NamespaceResource namespaceResource = createNamespaceResource(clientFactory, environmentVariables);
        CloudConfig cloudConfig = createCloudConfig(environmentVariables, updateStrategy);
        return new TestEnvironment(namespaceResource, clientFactory, kubernetesClient, cloudConfig);
    }

    public static void createTestNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            namespaceResource.delete();
        }
        namespaceResource.create();
    }
}
