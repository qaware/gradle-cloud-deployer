package de.qaware.cloud.deployer.kubernetes.resource.pod;

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.BaseResourceTest;
import io.fabric8.kubernetes.api.model.Pod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

public class PodResourceTest extends BaseResourceTest {

    private PodResource podResource;

    @Override
    public void setUp() throws Exception {

        // Prepare namespace
        super.setUp();
        getNamespaceResource().create();

        // Create the PodResource object
        File podDescriptionFile = new File(this.getClass().getResource("/pod.json").getPath());
        String podDescription = FileUtils.readFileToString(podDescriptionFile, Charset.defaultCharset());
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.JSON, podDescription);
        podResource = new PodResource(getNamespaceResource().getNamespace(), resourceConfig, getClientFactory());
    }

    public void testExists() throws ResourceException, InterruptedException {

        // Check that the pod doesn't exist already
        Pod pod = retrievePod();
        assertNull(pod);

        // Test exists method
        assertFalse(podResource.exists());

        // Create pod
        assertTrue(podResource.create());

        // Check that the pod exists
        pod = retrievePod();
        assertNotNull(pod);

        // Test exists method
        assertTrue(podResource.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException {

        // Check that the pod doesn't exist already
        Pod pod = retrievePod();
        assertNull(pod);

        // Create pod
        assertTrue(podResource.create());

        // Check that the pod exists
        pod = retrievePod();
        assertNotNull(pod);

        // Compare services
        assertEquals(pod.getMetadata().getName(), podResource.getId());
        assertEquals(pod.getApiVersion(), podResource.getResourceConfig().getResourceVersion());
        assertEquals(pod.getKind(), podResource.getResourceConfig().getResourceType());
    }

    private Pod retrievePod() {
        return getKubernetesClient().pods().inNamespace(podResource.getNamespace()).withName(podResource.getId()).get();
    }
}
