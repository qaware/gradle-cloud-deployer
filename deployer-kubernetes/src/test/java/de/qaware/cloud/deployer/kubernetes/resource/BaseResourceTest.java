package de.qaware.cloud.deployer.kubernetes.resource;

import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Ignore
public class BaseResourceTest extends TestCase {

    private NamespaceResource namespaceResource;
    private ClientFactory clientFactory;
    private KubernetesClient kubernetesClient;

    private static AtomicInteger subNamespaceCounter = new AtomicInteger(0);

    @Override
    public void setUp() throws Exception {
        Properties environmentProperties = loadEnvironmentProperties();
        kubernetesClient = createKubernetesClient(environmentProperties);
        clientFactory = createClientFactory(environmentProperties);
        namespaceResource = createNamespaceResource(clientFactory, environmentProperties);
    }

    @Override
    public void tearDown() throws Exception {
        // Delete the test namespace
        namespaceResource.delete();

        // Wait a little bit for kubernetes to delete the test namespace
        Thread.sleep(5000);
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

    private static Properties loadEnvironmentProperties() throws IOException {
        File propertiesFile = new File(BaseResourceTest.class.getResource("/env.properties").getPath());
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        return properties;
    }

    private static ClientFactory createClientFactory(Properties environmentProperties) {
        SSLConfig sslConfig = new SSLConfig(true, null);
        CloudConfig cloudConfig = new CloudConfig(environmentProperties.getProperty("URL"),
                environmentProperties.getProperty("USERNAME"),
                environmentProperties.getProperty("PASSWORD"),
                sslConfig
        );
        return new ClientFactory(cloudConfig);
    }

    private static KubernetesClient createKubernetesClient(Properties environmentProperties) {
        Config config = new ConfigBuilder()
                .withMasterUrl(environmentProperties.getProperty("URL"))
                .withTrustCerts(true)
                .withUsername(environmentProperties.getProperty("USERNAME"))
                .withPassword(environmentProperties.getProperty("PASSWORD"))
                .build();
        return new DefaultKubernetesClient(config);
    }

    private static NamespaceResource createNamespaceResource(ClientFactory clientFactory, Properties environmentProperties) throws ResourceConfigException {
        String namespace = environmentProperties.getProperty("TEST_NAMESPACE_PREFIX") + "-" + subNamespaceCounter.getAndIncrement();
        ResourceConfig namespaceResourceConfig = NamespaceConfigFactory.create(namespace);
        return new NamespaceResource(namespaceResourceConfig, clientFactory);
    }
}
