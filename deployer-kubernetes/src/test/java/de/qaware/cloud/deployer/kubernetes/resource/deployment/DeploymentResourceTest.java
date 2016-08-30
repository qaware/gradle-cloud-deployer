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

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestUtil;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

public class DeploymentResourceTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private DeploymentResource deploymentResource;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        ResourceTestEnvironment testEnvironment = ResourceTestUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        ResourceTestUtil.createNamespace(namespaceResource);

        // Create the DeploymentResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String deploymentDescription = ResourceTestUtil.readFile("/deployment.yml");
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.YAML, deploymentDescription);
        deploymentResource = new DeploymentResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = retrieveDeployment();
        assertNull(deployment);

        // Test exists method
        assertFalse(deploymentResource.exists());

        // Create deployment
        deploymentResource.create();

        // Check that the deployment exists
        deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Test exists method
        assertTrue(deploymentResource.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the deployment doesn't exist already
        Deployment deployment = retrieveDeployment();
        assertNull(deployment);

        // Create deployment
        deploymentResource.create();

        // Check that the deployment exists
        deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Check if the pod was created
        assertEquals(1, retrievePods().getItems().size());

        // Compare deployments
        assertEquals(deployment.getMetadata().getName(), deploymentResource.getId());
        assertEquals(deployment.getApiVersion(), deploymentResource.getResourceConfig().getResourceVersion());
        assertEquals(deployment.getKind(), deploymentResource.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException, InterruptedException {

        // Create deployment
        deploymentResource.create();

        // Check that the deployment exists
        Deployment deployment = retrieveDeployment();
        assertNotNull(deployment);

        // Check if the pod was created
        assertEquals(1, retrievePods().getItems().size());

        // Delete deployment
        deploymentResource.delete();

        // TODO: remove waiting for deletion...
        Thread.sleep(30000);

        // Check that all pods were deleted
        assertEquals(0, retrievePods().getItems().size());

        // Check that deployment doesn't exist anymore
        deployment = retrieveDeployment();
        assertNull(deployment);
    }

    private Deployment retrieveDeployment() {
        return kubernetesClient.extensions().deployments().inNamespace(deploymentResource.getNamespace()).withName(deploymentResource.getId()).get();
    }

    private PodList retrievePods() {
        return kubernetesClient.pods().inNamespace(deploymentResource.getNamespace()).list();
    }
}
