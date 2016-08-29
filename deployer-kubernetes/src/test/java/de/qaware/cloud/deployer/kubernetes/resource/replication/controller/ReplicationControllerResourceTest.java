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
package de.qaware.cloud.deployer.kubernetes.resource.replication.controller;

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestUtil;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

public class ReplicationControllerResourceTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private ReplicationControllerResource replicationControllerResource;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        ResourceTestEnvironment testEnvironment = ResourceTestUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        ResourceTestUtil.createNamespace(namespaceResource);

        // Create the ReplicationControllerResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String controllerDescription = ResourceTestUtil.readFile("/replication-controller.yml");
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.YAML, controllerDescription);
        replicationControllerResource = new ReplicationControllerResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException, InterruptedException {

        // Check that the controller doesn't exist already
        ReplicationController controller = retrieveReplicationController();
        assertNull(controller);

        // Test exists method
        assertFalse(replicationControllerResource.exists());

        // Create controller
        replicationControllerResource.create();

        // Check that the controller exists
        controller = retrieveReplicationController();
        assertNotNull(controller);

        // Test exists method
        assertTrue(replicationControllerResource.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException {

        // Check that the controller doesn't exist already
        ReplicationController controller = retrieveReplicationController();
        assertNull(controller);

        // Create controller
        replicationControllerResource.create();

        // Check that the controller exists
        controller = retrieveReplicationController();
        assertNotNull(controller);

        // Compare services
        assertEquals(controller.getMetadata().getName(), replicationControllerResource.getId());
        assertEquals(controller.getApiVersion(), replicationControllerResource.getResourceConfig().getResourceVersion());
        assertEquals(controller.getKind(), replicationControllerResource.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException {

        // Create controller
        replicationControllerResource.create();

        // Check that the controller exists
        ReplicationController controller = retrieveReplicationController();
        assertNotNull(controller);

        // Delete controller
        replicationControllerResource.delete();

        // Check that controller doesn't exist anymore
        controller = retrieveReplicationController();
        assertNull(controller);
    }

    private ReplicationController retrieveReplicationController() {
        return kubernetesClient.replicationControllers().inNamespace(replicationControllerResource.getNamespace()).withName(replicationControllerResource.getId()).get();
    }
}
