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
import junit.framework.TestCase;
import org.junit.Ignore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Ignore
public class BaseResourceTest extends TestCase {

    private static AtomicInteger subNamespaceCounter = new AtomicInteger(0);
    private NamespaceResource namespaceResource;
    private ClientFactory clientFactory;
    private KubernetesClient kubernetesClient;

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

    @Override
    public void setUp() throws Exception {
        Map<String, String> environmentVariables = loadEnvironmentVariables();
        kubernetesClient = createKubernetesClient(environmentVariables);
        clientFactory = createClientFactory(environmentVariables);
        namespaceResource = createNamespaceResource(clientFactory, environmentVariables);

        // Delete namespace resource if it already exists
        if (namespaceResource.exists()) {
            namespaceResource.delete();
        }
    }

    @Override
    public void tearDown() throws ResourceException {
        // Delete the test namespace
        namespaceResource.delete();
    }

    public NamespaceResource getNamespaceResource() {
        return namespaceResource;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public KubernetesClient getKubernetesClient() {
        return kubernetesClient;
    }
}
