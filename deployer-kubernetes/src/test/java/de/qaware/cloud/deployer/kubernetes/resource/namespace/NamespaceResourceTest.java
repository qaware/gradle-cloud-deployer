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
        if (namespaceResource.exists()) {
            namespaceResource.delete();
        }
    }

    public void testExists() throws ResourceException {

        // Check that the namespace doesn't exist already
        Namespace namespace = retrieveNamespace();
        assertNull(namespace);

        // Test exists method
        assertFalse(namespaceResource.exists());

        // Create namespace
        namespaceResource.create();

        // Check that the namespace exists
        namespace = retrieveNamespace();
        assertNotNull(namespace);

        // Test exists method
        assertTrue(namespaceResource.exists());
    }

    public void testDelete() throws ResourceException {

        // Create namespace
        namespaceResource.create();

        // Check that the namespace exists
        Namespace namespace = retrieveNamespace();
        assertNotNull(namespace);

        // Delete namespace
        namespaceResource.delete();

        // Check that namespace doesn't exist anymore
        namespace = retrieveNamespace();
        assertNull(namespace);
    }

    public void testCreate() throws ResourceException {

        // Check that the namespace doesn't exist already
        Namespace namespace = retrieveNamespace();
        assertNull(namespace);

        // Create namespace
        namespaceResource.create();

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
