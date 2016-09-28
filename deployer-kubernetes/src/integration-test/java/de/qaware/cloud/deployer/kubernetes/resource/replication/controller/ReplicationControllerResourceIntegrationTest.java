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

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesClientUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironmentUtil;
import de.qaware.cloud.deployer.kubernetes.test.PodDeletionBlocker;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class ReplicationControllerResourceIntegrationTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private ReplicationControllerResource replicationControllerResourceV1;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        KubernetesTestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create the replication controller resource v1 object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String controllerDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/kubernetes/resources/replication/controller/replication-controller.yml");
        KubernetesResourceConfig resourceConfigV1 = new KubernetesResourceConfig("test", ContentType.YAML, controllerDescriptionV1);
        replicationControllerResourceV1 = new ReplicationControllerResource(namespaceResource.getNamespace(), resourceConfigV1, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException, InterruptedException {

        // Check that the controller doesn't exist already
        ReplicationController controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResourceV1);
        assertNull(controller);

        // Test exists method
        assertFalse(replicationControllerResourceV1.exists());

        // Create controller
        replicationControllerResourceV1.create();

        // Check that the controller exists
        controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResourceV1);
        assertNotNull(controller);

        // Test exists method
        assertTrue(replicationControllerResourceV1.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException {

        // Check that the controller doesn't exist already
        ReplicationController controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResourceV1);
        assertNull(controller);

        // Create controller
        replicationControllerResourceV1.create();

        // Check that the controller exists
        controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResourceV1);
        assertNotNull(controller);

        // Check if the pods were created
        assertEquals(3, KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResourceV1).getItems().size());

        // Compare services
        assertEquals(controller.getMetadata().getName(), replicationControllerResourceV1.getId());
        assertEquals(controller.getApiVersion(), replicationControllerResourceV1.getResourceConfig().getResourceVersion());
        assertEquals(controller.getKind(), replicationControllerResourceV1.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException, InterruptedException, TimeoutException {

        // Create controller
        replicationControllerResourceV1.create();

        // Check that the controller exists
        ReplicationController controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResourceV1);
        assertNotNull(controller);

        // Check if the pods were created
        List<Pod> pods = KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResourceV1).getItems();
        assertEquals(3, pods.size());

        // Create event blockers
        Pod pod0 = pods.get(0);
        PodDeletionBlocker deleteBlocker0 = new PodDeletionBlocker(kubernetesClient, pod0);
        Pod pod1 = pods.get(1);
        PodDeletionBlocker deleteBlocker1 = new PodDeletionBlocker(kubernetesClient, pod1);
        Pod pod2 = pods.get(2);
        PodDeletionBlocker deleteBlocker2 = new PodDeletionBlocker(kubernetesClient, pod2);

        // Delete controller
        replicationControllerResourceV1.delete();

        // Block until deletion
        deleteBlocker0.block();
        deleteBlocker1.block();
        deleteBlocker2.block();

        // Check that all pods were deleted
        assertEquals(0, KubernetesClientUtil.retrievePods(kubernetesClient, replicationControllerResourceV1).getItems().size());

        // Check that controller doesn't exist anymore
        controller = KubernetesClientUtil.retrieveReplicationController(kubernetesClient, replicationControllerResourceV1);
        assertNull(controller);
    }
}
