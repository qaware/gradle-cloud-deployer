package de.qaware.cloud.deployer.kubernetes.resource.service;

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.BaseResourceTest;
import io.fabric8.kubernetes.api.model.Service;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

public class ServiceResourceTest extends BaseResourceTest {

    private ServiceResource serviceResource;

    @Override
    public void setUp() throws Exception {

        // Prepare namespace
        super.setUp();
        getNamespaceResource().create();

        // Create the ServiceResource object
        File serviceDescriptionFile = new File(this.getClass().getResource("/service.yml").getPath());
        String serviceDescription = FileUtils.readFileToString(serviceDescriptionFile, Charset.defaultCharset());
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.YAML, serviceDescription);
        serviceResource = new ServiceResource(getNamespaceResource().getNamespace(), resourceConfig, getClientFactory());
    }

    public void testExists() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = retrieveService();
        assertNull(service);

        // Test exists method
        assertFalse(serviceResource.exists());

        // Create service
        assertTrue(serviceResource.create());

        // Check that the service exists
        service = retrieveService();
        assertNotNull(service);

        // Test exists method
        assertTrue(serviceResource.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = retrieveService();
        assertNull(service);

        // Create service
        assertTrue(serviceResource.create());

        // Check that the service exists
        service = retrieveService();
        assertNotNull(service);

        // Compare services
        assertEquals(service.getMetadata().getName(), serviceResource.getId());
        assertEquals(service.getApiVersion(), serviceResource.getResourceConfig().getResourceVersion());
        assertEquals(service.getKind(), serviceResource.getResourceConfig().getResourceType());
    }

    private Service retrieveService() {
        return getKubernetesClient().services().inNamespace(serviceResource.getNamespace()).withName(serviceResource.getId()).get();
    }
}
