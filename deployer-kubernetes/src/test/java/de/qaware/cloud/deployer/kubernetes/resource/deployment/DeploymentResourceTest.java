package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.BaseResourceTest;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

public class DeploymentResourceTest extends BaseResourceTest {

    private DeploymentResource deploymentResource;

    @Override
    public void setUp() throws Exception {

        // Prepare namespace
        super.setUp();
        getNamespaceResource().create();

        // Wait until the namespace is created by kubernetes
        Thread.sleep(5000);

        // Create the DeploymentResource object
        File deploymentDescriptionFile = new File(this.getClass().getResource("/deployment.yml").getPath());
        String deploymentDescription = FileUtils.readFileToString(deploymentDescriptionFile, Charset.defaultCharset());
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.YAML, deploymentDescription);
        deploymentResource = new DeploymentResource(getNamespaceResource().getNamespace(), resourceConfig, getClientFactory());
    }

    public void testExists() throws ResourceException, InterruptedException {

        // Check that the deployment doesn't exist already
        Deployment deployment = retrieveDeployment();
        assertNull(deployment);

        // Test exists method
        assertFalse(deploymentResource.exists());

        // Create deployment
        assertTrue(deploymentResource.create());

        // Wait a little bit for kubernetes to create the deployment
        Thread.sleep(2000);

        // Check that the deployment exists
        deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Test exists method
        assertTrue(deploymentResource.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException {

        // Check that the deployment doesn't exist already
        Deployment deployment = retrieveDeployment();
        assertNull(deployment);

        // Create deployment
        assertTrue(deploymentResource.create());

        // Wait a little bit for kubernetes to create the deployment
        Thread.sleep(2000);

        // Check that the deployment exists
        deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Compare deployments
        assertEquals(deployment.getMetadata().getName(), deploymentResource.getId());
        assertEquals(deployment.getApiVersion(), deploymentResource.getResourceConfig().getResourceVersion());
        assertEquals(deployment.getKind(), deploymentResource.getResourceConfig().getResourceType());
    }

    private Deployment retrieveDeployment() {
        return getKubernetesClient().extensions().deployments().inNamespace(deploymentResource.getNamespace()).withName(deploymentResource.getId()).get();
    }
}
