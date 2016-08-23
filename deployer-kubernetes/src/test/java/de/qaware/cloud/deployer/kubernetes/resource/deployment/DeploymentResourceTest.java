package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestUtil;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

public class DeploymentResourceTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private DeploymentResource deploymentResource;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        ResourceTestEnvironment testEnvironment = ResourceTestUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        ResourceTestUtil.createNamespace(namespaceResource);

        // Create the DeploymentResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String deploymentDescription = ResourceTestUtil.readFile("/deployment.yml");
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.YAML, deploymentDescription);
        deploymentResource = new DeploymentResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = retrieveDeployment();
        assertNull(deployment);

        // Test exists method
        assertFalse(deploymentResource.exists());

        // Create deployment
        assertTrue(deploymentResource.create());

        // Check that the deployment exists
        deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Test exists method
        assertTrue(deploymentResource.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = retrieveDeployment();
        assertNull(deployment);

        // Create deployment
        assertTrue(deploymentResource.create());

        // Check that the deployment exists
        deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Compare deployments
        assertEquals(deployment.getMetadata().getName(), deploymentResource.getId());
        assertEquals(deployment.getApiVersion(), deploymentResource.getResourceConfig().getResourceVersion());
        assertEquals(deployment.getKind(), deploymentResource.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException {

        // Create deployment
        assertTrue(deploymentResource.create());

        // Check that the deployment exists
        Deployment deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Delete deployment
        assertTrue(deploymentResource.delete());

        // Check that deployment doesn't exist anymore
        deployment = retrieveDeployment();
        assertNull(deployment);
    }

    private Deployment retrieveDeployment() {
        return kubernetesClient.extensions().deployments().inNamespace(deploymentResource.getNamespace()).withName(deploymentResource.getId()).get();
    }
}
