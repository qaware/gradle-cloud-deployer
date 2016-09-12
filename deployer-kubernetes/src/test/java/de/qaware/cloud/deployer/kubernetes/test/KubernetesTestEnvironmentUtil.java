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

import de.qaware.cloud.deployer.commons.config.cloud.AuthConfig;
import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.test.TestEnvironmentUtil;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesCloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceResourceConfigFactory;
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

    // Environment variables.
    private static final String KUBERNETES_NAMESPACE_PREFIX_ENV = "KUBERNETES_NAMESPACE_PREFIX";
    private static final String KUBERNETES_URL_ENV = "KUBERNETES_URL";
    private static final String KUBERNETES_USERNAME_ENV = "KUBERNETES_USERNAME";
    private static final String KUBERNETES_PASSWORD_ENV = "KUBERNETES_PASSWORD";

    // Constants.
    private static final String KUBERNETES_DEFAULT_UPDATE_STRATEGY = "HARD";

    private static AtomicInteger subNamespaceCounter = new AtomicInteger(0);

    private KubernetesTestEnvironmentUtil() {
    }

    private static String createNewTestNamespace(Map<String, String> environmentProperties) {
        return environmentProperties.get(KUBERNETES_NAMESPACE_PREFIX_ENV) + "-" + subNamespaceCounter.getAndIncrement();
    }

    private static KubernetesClient createKubernetesClient(CloudConfig cloudConfig) {
        Config config = new ConfigBuilder()
                .withMasterUrl(cloudConfig.getBaseUrl())
                .withTrustCerts(true)
                .withUsername(cloudConfig.getAuthConfig().getUsername())
                .withPassword(cloudConfig.getAuthConfig().getPassword())
                .build();
        return new DefaultKubernetesClient(config);
    }

    private static KubernetesCloudConfig createCloudConfig(Map<String, String> environmentVariables, String updateStrategy, String namespace) {
        KubernetesCloudConfig cloudConfig = new KubernetesCloudConfig(environmentVariables.get(KUBERNETES_URL_ENV), updateStrategy, namespace);
        AuthConfig authConfig = new AuthConfig();
        authConfig.setUsername(environmentVariables.get(KUBERNETES_USERNAME_ENV));
        authConfig.setPassword(environmentVariables.get(KUBERNETES_PASSWORD_ENV));
        cloudConfig.setAuthConfig(authConfig);
        cloudConfig.setSslConfig(new SSLConfig(true));
        return cloudConfig;
    }

    private static ClientFactory createClientFactory(CloudConfig cloudConfig) throws ResourceException {
        return new ClientFactory(cloudConfig);
    }

    private static NamespaceResource createNamespaceResource(ClientFactory clientFactory, String namespace) throws ResourceConfigException {
        KubernetesResourceConfig namespaceResourceConfig = NamespaceResourceConfigFactory.create(namespace);
        return new NamespaceResource(namespaceResourceConfig, clientFactory);
    }

    public static KubernetesTestEnvironment createTestEnvironment() throws IOException, ResourceConfigException, ResourceException {
        return createTestEnvironment(KUBERNETES_DEFAULT_UPDATE_STRATEGY);
    }

    public static KubernetesTestEnvironment createTestEnvironment(String updateStrategy) throws IOException, ResourceConfigException, ResourceException {
        Map<String, String> environmentVariables = TestEnvironmentUtil.loadEnvironmentVariables(
                KUBERNETES_URL_ENV,
                KUBERNETES_USERNAME_ENV,
                KUBERNETES_PASSWORD_ENV,
                KUBERNETES_NAMESPACE_PREFIX_ENV
        );

        String namespace = createNewTestNamespace(environmentVariables);

        KubernetesCloudConfig cloudConfig = createCloudConfig(environmentVariables, updateStrategy, namespace);
        ClientFactory clientFactory = createClientFactory(cloudConfig);

        KubernetesClient kubernetesClient = createKubernetesClient(cloudConfig);
        NamespaceResource namespaceResource = createNamespaceResource(clientFactory, namespace);
        return new KubernetesTestEnvironment(clientFactory, cloudConfig, namespaceResource, kubernetesClient);
    }

    public static void createTestNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            namespaceResource.delete();
        }
        namespaceResource.create();
    }
}
