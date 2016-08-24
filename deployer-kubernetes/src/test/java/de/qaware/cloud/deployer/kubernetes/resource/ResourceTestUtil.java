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
package de.qaware.cloud.deployer.kubernetes.resource;

import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceTestUtil {

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
        ResourceConfig namespaceResourceConfig = NamespaceConfigFactory.create(namespace);
        return new NamespaceResource(namespaceResourceConfig, clientFactory);
    }

    public static ResourceTestEnvironment createTestEnvironment() throws IOException, ResourceConfigException, ResourceException {
        Map<String, String> environmentVariables = loadEnvironmentVariables();
        KubernetesClient kubernetesClient = createKubernetesClient(environmentVariables);
        ClientFactory clientFactory = createClientFactory(environmentVariables);
        NamespaceResource namespaceResource = createNamespaceResource(clientFactory, environmentVariables);
        return new ResourceTestEnvironment(namespaceResource, clientFactory, kubernetesClient);
    }

    public static void createNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            namespaceResource.delete();
        }
        namespaceResource.create();
    }

    public static String readFile(String filename) throws IOException {
        File deploymentDescriptionFile = new File(ResourceTestUtil.class.getClass().getResource(filename).getPath());
        return FileUtils.readFileToString(deploymentDescriptionFile, Charset.defaultCharset());
    }
}
