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

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.*;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentResource.DEPLOYMENT_MARKER_LABEL;

public class DeploymentResourceTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private DeploymentResource deploymentResource;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        KubernetesTestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create the DeploymentResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String deploymentDescription = FileUtil.readFile("/resource/deployment/deployment.yml");
        KubernetesResourceConfig resourceConfig = new KubernetesResourceConfig("test", ContentType.YAML, deploymentDescription);
        deploymentResource = new DeploymentResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        assertNull(deployment);

        // Test exists method
        assertFalse(deploymentResource.exists());

        // Create deployment
        deploymentResource.create();

        // Check that the deployment exists
        deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        assertNotNull(deployment);

        // Test exists method
        assertTrue(deploymentResource.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        assertNull(deployment);

        // Create deployment
        deploymentResource.create();

        // Check that the deployment exists
        deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        assertNotNull(deployment);

        // Check if the pod was created
        assertEquals(1, KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource).getItems().size());

        // Check if the replica set was created
        List<ReplicaSet> replicaSets = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResource).getItems();
        assertEquals(1, replicaSets.size());
        assertEquals("zwitscher-eureka", replicaSets.get(0).getMetadata().getLabels().get(DEPLOYMENT_MARKER_LABEL));

        // Compare deployments
        assertEquals(deployment.getMetadata().getName(), deploymentResource.getId());
        assertEquals(deployment.getApiVersion(), deploymentResource.getResourceConfig().getResourceVersion());
        assertEquals(deployment.getKind(), deploymentResource.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException, InterruptedException, TimeoutException {

        // Create deployment
        deploymentResource.create();

        // Check that the deployment exists
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        assertNotNull(deployment);

        // Check if the pod was created
        List<Pod> pods = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource).getItems();
        assertEquals(1, pods.size());

        // Check if the replica set was created
        assertEquals(1, KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResource).getItems().size());

        // Create event blocker
        Pod pod = pods.get(0);
        PodDeletionBlocker deleteBlocker = new PodDeletionBlocker(kubernetesClient, pod);

        // Delete deployment
        deploymentResource.delete();

        // Block until deletion
        deleteBlocker.block();

        // Check that all pods were deleted
        assertEquals(0, KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource).getItems().size());

        // Check that the replica set was deleted
        assertEquals(0, KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResource).getItems().size());

        // Check that deployment doesn't exist anymore
        deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        assertNull(deployment);
    }
}
