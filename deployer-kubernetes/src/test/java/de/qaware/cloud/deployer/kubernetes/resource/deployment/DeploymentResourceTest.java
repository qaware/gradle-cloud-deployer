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
package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.*;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class DeploymentResourceTest extends TestCase {

    private static final String DEPLOYMENT_MARKER_LABEL = "deployment-id";

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private DeploymentResource deploymentResourceV1;
    private DeploymentResource deploymentResourceV2;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        KubernetesTestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create the deployment resource v1 object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String deploymentDescriptionV1 = FileUtil.readFileContent("/deployment/deployment-v1.yml");
        KubernetesResourceConfig resourceConfigV1 = new KubernetesResourceConfig("test", ContentType.YAML, deploymentDescriptionV1);
        deploymentResourceV1 = new DeploymentResource(namespaceResource.getNamespace(), resourceConfigV1, clientFactory);

        // Create the deployment resource v2 object
        String deploymentDescriptionV2 = FileUtil.readFileContent("/deployment/deployment-v2.yml");
        KubernetesResourceConfig resourceConfigV2 = new KubernetesResourceConfig("test", ContentType.YAML, deploymentDescriptionV2);
        deploymentResourceV2 = new DeploymentResource(namespaceResource.getNamespace(), resourceConfigV2, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNull(deployment);

        // Test exists method
        assertFalse(deploymentResourceV1.exists());

        // Create deployment
        deploymentResourceV1.create();

        // Check that the deployment exists
        deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNotNull(deployment);

        // Test exists method
        assertTrue(deploymentResourceV1.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNull(deployment);

        // Create deployment
        deploymentResourceV1.create();

        // Check that the deployment exists
        deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNotNull(deployment);

        // Check if the pods were created
        assertEquals(3, KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResourceV1).getItems().size());

        // Check if the replica set was created
        List<ReplicaSet> replicaSets = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResourceV1).getItems();
        assertEquals(1, replicaSets.size());
        assertEquals(deploymentResourceV1.getId(), replicaSets.get(0).getMetadata().getLabels().get(DEPLOYMENT_MARKER_LABEL));

        // Compare deployments
        assertEquals(deployment.getMetadata().getName(), deploymentResourceV1.getId());
        assertEquals(deployment.getApiVersion(), deploymentResourceV1.getResourceConfig().getResourceVersion());
        assertEquals(deployment.getKind(), deploymentResourceV1.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException, InterruptedException, TimeoutException {

        // Create deployment
        deploymentResourceV1.create();

        // Check that the deployment exists
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNotNull(deployment);

        // Check if the pods were created
        List<Pod> pods = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResourceV1).getItems();
        assertEquals(3, pods.size());

        // Check if the replica set was created
        assertEquals(1, KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResourceV1).getItems().size());

        // Create event blocker
        Pod pod0 = pods.get(0);
        Pod pod1 = pods.get(1);
        Pod pod2 = pods.get(2);
        PodDeletionBlocker deleteBlocker0 = new PodDeletionBlocker(kubernetesClient, pod0);
        PodDeletionBlocker deleteBlocker1 = new PodDeletionBlocker(kubernetesClient, pod1);
        PodDeletionBlocker deleteBlocker2 = new PodDeletionBlocker(kubernetesClient, pod2);

        // Delete deployment
        deploymentResourceV1.delete();

        // Block until deletion
        deleteBlocker0.block();
        deleteBlocker1.block();
        deleteBlocker2.block();

        // Check that all pods were deleted
        assertEquals(0, KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResourceV1).getItems().size());

        // Check that the replica set was deleted
        assertEquals(0, KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResourceV1).getItems().size());

        // Check that deployment doesn't exist anymore
        deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNull(deployment);
    }

    public void testUpdate() throws ResourceException, TimeoutException, InterruptedException {
        // Check that the deployment doesn't exist already
        Deployment deploymentV1 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNull(deploymentV1);

        // Create deployment - already checked above in testCreate()
        deploymentResourceV1.create();

        // Retrieve deployment v1
        deploymentV1 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNotNull(deploymentV1);

        // Register for pod deletion
        List<Pod> pods = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResourceV1).getItems();
        assertEquals(3, pods.size());
        PodDeletionBlocker deleteBlocker0 = new PodDeletionBlocker(kubernetesClient, pods.get(0));
        PodDeletionBlocker deleteBlocker1 = new PodDeletionBlocker(kubernetesClient, pods.get(1));
        PodDeletionBlocker deleteBlocker2 = new PodDeletionBlocker(kubernetesClient, pods.get(2));

        // Update the deployment using v2
        deploymentResourceV2.update();

        // Wait until the old pods were deleted
        deleteBlocker0.block();
        deleteBlocker1.block();
        deleteBlocker2.block();

        // Check that the deployment still exists
        Deployment deploymentV2 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResourceV1);
        assertNotNull(deploymentV2);

        // Compare deployment v1 and v2
        assertFalse(deploymentV1.getMetadata().getResourceVersion().equals(deploymentV2.getMetadata().getResourceVersion()));

        // Check that two replica sets exist
        List<ReplicaSet> replicaSets = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResourceV1).getItems();
        assertEquals(2, replicaSets.size());

        // Check if the pods were created
        pods = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResourceV1).getItems();
        assertEquals(3, pods.size());

        // Check if the pods were updated
        for (Pod pod : pods) {
            PodSpec podSpec = pod.getSpec();
            assertEquals(1, podSpec.getContainers().size());
            assertEquals("nginx:1.9.1", podSpec.getContainers().get(0).getImage());
        }
    }
}
