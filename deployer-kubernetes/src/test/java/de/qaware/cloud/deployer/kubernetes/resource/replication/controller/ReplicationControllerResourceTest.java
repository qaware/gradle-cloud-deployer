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
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.FileUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesClientUtil;
import de.qaware.cloud.deployer.kubernetes.test.TestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.TestEnvironmentUtil;
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
        TestEnvironment testEnvironment = TestEnvironmentUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        TestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create the ReplicationControllerResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String controllerDescription = FileUtil.readFile("/replication-controller.yml");
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.YAML, controllerDescription);
        replicationControllerResource = new ReplicationControllerResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException, InterruptedException {

        // Check that the controller doesn't exist already
        ReplicationController controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNull(controller);

        // Test exists method
        assertFalse(replicationControllerResource.exists());

        // Create controller
        replicationControllerResource.create();

        // Check that the controller exists
        controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNotNull(controller);

        // Test exists method
        assertTrue(replicationControllerResource.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException {

        // Check that the controller doesn't exist already
        ReplicationController controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNull(controller);

        // Create controller
        replicationControllerResource.create();

        // Check that the controller exists
        controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNotNull(controller);

        // Check if the pods were created
        assertEquals(3, KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResource).getItems().size());

        // Compare services
        assertEquals(controller.getMetadata().getName(), replicationControllerResource.getId());
        assertEquals(controller.getApiVersion(), replicationControllerResource.getResourceConfig().getResourceVersion());
        assertEquals(controller.getKind(), replicationControllerResource.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException, InterruptedException {

        // Create controller
        replicationControllerResource.create();

        // Check that the controller exists
        ReplicationController controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNotNull(controller);

        // Check if the pods were created
        assertEquals(3, KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResource).getItems().size());

        // Delete controller
        replicationControllerResource.delete();

        // TODO: remove waiting for deletion...
        Thread.sleep(10000);

        // Check that all pods were deleted
        assertEquals(0, KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResource).getItems().size());

        // Check that controller doesn't exist anymore
        controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNull(controller);
    }
}
