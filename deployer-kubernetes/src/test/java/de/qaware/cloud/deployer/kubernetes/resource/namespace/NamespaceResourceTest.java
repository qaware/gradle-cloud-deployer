package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestUtil;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

public class NamespaceResourceTest extends TestCase {

    private NamespaceResource namespaceResource;
    private KubernetesClient kubernetesClient;

    @Override
    public void setUp() throws Exception {
        ResourceTestEnvironment testEnvironment = ResourceTestUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the namespace doesn't exist already
        Namespace namespace = retrieveNamespace();
        assertNull(namespace);

        // Test exists method
        assertFalse(namespaceResource.exists());

        // Create namespace
        assertTrue(namespaceResource.create());

        // Check that the namespace exists
        namespace = retrieveNamespace();
        assertNotNull(namespace);

        // Test exists method
        assertTrue(namespaceResource.exists());
    }

    public void testDelete() throws ResourceException {

        // Create namespace
        assertTrue(namespaceResource.create());

        // Check that the namespace exists
        Namespace namespace = retrieveNamespace();
        assertNotNull(namespace);

        // Delete namespace
        assertTrue(namespaceResource.delete());

        // Check that namespace doesn't exist anymore
        namespace = retrieveNamespace();
        assertNull(namespace);
    }

    public void testCreate() throws ResourceException {

        // Check that the namespace doesn't exist already
        Namespace namespace = retrieveNamespace();
        assertNull(namespace);

        // Create namespace
        assertTrue(namespaceResource.create());

        // Check that the namespace exists
        namespace = retrieveNamespace();
        assertNotNull(namespace);

        // Compare namespaces
        assertEquals(namespace.getMetadata().getName(), namespaceResource.getId());
        assertEquals(namespace.getApiVersion(), namespaceResource.getResourceConfig().getResourceVersion());
        assertEquals(namespace.getKind(), namespaceResource.getResourceConfig().getResourceType());
    }

    private Namespace retrieveNamespace() {
        return kubernetesClient.namespaces().withName(namespaceResource.getId()).get();
    }
}
