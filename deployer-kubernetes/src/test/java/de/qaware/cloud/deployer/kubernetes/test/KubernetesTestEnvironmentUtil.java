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

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.test.TestEnvironmentUtil;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class KubernetesTestEnvironmentUtil {

    private static AtomicInteger subNamespaceCounter = new AtomicInteger(0);

    private KubernetesTestEnvironmentUtil() {
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

    public static KubernetesTestEnvironment createTestEnvironment() throws IOException, ResourceConfigException, ResourceException {
        return createTestEnvironment("HARD");
    }

    public static KubernetesTestEnvironment createTestEnvironment(String updateStrategy) throws IOException, ResourceConfigException, ResourceException {
        Map<String, String> environmentVariables = TestEnvironmentUtil.loadEnvironmentVariables();
        ClientFactory clientFactory = TestEnvironmentUtil.createClientFactory(environmentVariables);
        CloudConfig cloudConfig = TestEnvironmentUtil.createCloudConfig(environmentVariables, updateStrategy);

        KubernetesClient kubernetesClient = createKubernetesClient(environmentVariables);
        NamespaceResource namespaceResource = createNamespaceResource(clientFactory, environmentVariables);
        return new KubernetesTestEnvironment(clientFactory, cloudConfig, namespaceResource, kubernetesClient);
    }

    public static void createTestNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            namespaceResource.delete();
        }
        namespaceResource.create();
    }
}
