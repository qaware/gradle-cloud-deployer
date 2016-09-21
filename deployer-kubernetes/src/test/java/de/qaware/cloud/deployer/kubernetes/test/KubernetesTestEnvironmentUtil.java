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
import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.test.TestEnvironmentUtil;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
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
    private static final Strategy KUBERNETES_DEFAULT_STRATEGY = Strategy.RESET;

    private static AtomicInteger subNamespaceCounter = new AtomicInteger(0);

    private KubernetesTestEnvironmentUtil() {
    }

    private static String createNewTestNamespace(Map<String, String> environmentProperties) {
        return environmentProperties.get(KUBERNETES_NAMESPACE_PREFIX_ENV) + "-" + subNamespaceCounter.getAndIncrement();
    }

    private static KubernetesClient createKubernetesClient(EnvironmentConfig environmentConfig) {
        Config config = new ConfigBuilder()
                .withMasterUrl(environmentConfig.getBaseUrl())
                .withTrustCerts(true)
                .withUsername(environmentConfig.getAuthConfig().getUsername())
                .withPassword(environmentConfig.getAuthConfig().getPassword())
                .build();
        return new DefaultKubernetesClient(config);
    }

    private static KubernetesEnvironmentConfig createEnvironmentConfig(Map<String, String> environmentVariables, Strategy strategy, String namespace) {
        KubernetesEnvironmentConfig environmentConfig = new KubernetesEnvironmentConfig("test", environmentVariables.get(KUBERNETES_URL_ENV), strategy, namespace);
        AuthConfig authConfig = new AuthConfig();
        authConfig.setUsername(environmentVariables.get(KUBERNETES_USERNAME_ENV));
        authConfig.setPassword(environmentVariables.get(KUBERNETES_PASSWORD_ENV));
        environmentConfig.setAuthConfig(authConfig);
        environmentConfig.setSslConfig(new SSLConfig(true));
        return environmentConfig;
    }

    private static ClientFactory createClientFactory(EnvironmentConfig environmentConfig) throws ResourceException {
        return new ClientFactory(environmentConfig);
    }

    private static NamespaceResource createNamespaceResource(ClientFactory clientFactory, String namespace) throws ResourceConfigException {
        KubernetesResourceConfig namespaceResourceConfig = NamespaceResourceConfigFactory.create(namespace);
        return new NamespaceResource(namespaceResourceConfig, clientFactory);
    }

    public static KubernetesTestEnvironment createTestEnvironment() throws IOException, ResourceConfigException, ResourceException {
        return createTestEnvironment(KUBERNETES_DEFAULT_STRATEGY);
    }

    public static KubernetesTestEnvironment createTestEnvironment(Strategy strategy) throws IOException, ResourceConfigException, ResourceException {
        Map<String, String> environmentVariables = TestEnvironmentUtil.loadEnvironmentVariables(
                KUBERNETES_URL_ENV,
                KUBERNETES_USERNAME_ENV,
                KUBERNETES_PASSWORD_ENV,
                KUBERNETES_NAMESPACE_PREFIX_ENV
        );

        String namespace = createNewTestNamespace(environmentVariables);

        KubernetesEnvironmentConfig environmentConfig = createEnvironmentConfig(environmentVariables, strategy, namespace);
        ClientFactory clientFactory = createClientFactory(environmentConfig);

        KubernetesClient kubernetesClient = createKubernetesClient(environmentConfig);
        NamespaceResource namespaceResource = createNamespaceResource(clientFactory, namespace);
        return new KubernetesTestEnvironment(clientFactory, environmentConfig, namespaceResource, kubernetesClient);
    }

    public static void createTestNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            namespaceResource.delete();
        }
        namespaceResource.create();
    }
}
