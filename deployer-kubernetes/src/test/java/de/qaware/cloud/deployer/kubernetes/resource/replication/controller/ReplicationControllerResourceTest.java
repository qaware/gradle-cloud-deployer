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

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.*;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

import java.util.List;
import java.util.concurrent.TimeoutException;

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
        String controllerDescription = FileUtil.readFile("/resource/replication/controller/replication-controller.yml");
        KubernetesResourceConfig resourceConfig = new KubernetesResourceConfig("test", ContentType.YAML, controllerDescription);
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

    public void testDelete() throws ResourceException, InterruptedException, TimeoutException {

        // Create controller
        replicationControllerResource.create();

        // Check that the controller exists
        ReplicationController controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNotNull(controller);

        // Check if the pods were created
        List<Pod> pods = KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResource).getItems();
        assertEquals(3, pods.size());

        // Create event blockers
        Pod pod0 = pods.get(0);
        PodDeletionBlocker deleteBlocker0 = new PodDeletionBlocker(kubernetesClient, pod0);
        Pod pod1 = pods.get(1);
        PodDeletionBlocker deleteBlocker1 = new PodDeletionBlocker(kubernetesClient, pod1);
        Pod pod2 = pods.get(2);
        PodDeletionBlocker deleteBlocker2 = new PodDeletionBlocker(kubernetesClient, pod2);

        // Delete controller
        replicationControllerResource.delete();

        // Block until deletion
        deleteBlocker0.block();
        deleteBlocker1.block();
        deleteBlocker2.block();

        // Check that all pods were deleted
        assertEquals(0, KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResource).getItems().size());

        // Check that controller doesn't exist anymore
        controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResource);
        assertNull(controller);
    }
}
