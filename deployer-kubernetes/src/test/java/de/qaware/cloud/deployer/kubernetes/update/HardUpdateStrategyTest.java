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
package de.qaware.cloud.deployer.kubernetes.update;

import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesClientUtil;
import de.qaware.cloud.deployer.kubernetes.test.TestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.TestEnvironmentUtil;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSet;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSetList;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HardUpdateStrategyTest extends TestCase {

    private NamespaceResource namespaceResource;
    private HardUpdateStrategy hardUpdateStrategy;
    private List<Resource> resourcesV1;
    private List<Resource> resourcesV2;
    private KubernetesClient kubernetesClient;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        TestEnvironment testEnvironment = TestEnvironmentUtil.createTestEnvironment("HARD");
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        CloudConfig cloudConfig = testEnvironment.getCloudConfig();
        TestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create update strategy
        hardUpdateStrategy = new HardUpdateStrategy();

        // Create resource factory
        ResourceFactory factory = new ResourceFactory(namespaceResource.getNamespace(), cloudConfig);

        // Create the resources for v1
        List<File> filesV1 = new ArrayList<>();
        filesV1.add(new File(this.getClass().getResource("/resource/update/update-v1.yml").getPath()));
        List<ResourceConfig> configsV1 = ResourceConfigFactory.createConfigs(filesV1);
        resourcesV1 = factory.createResources(configsV1);

        // Create the resources for v2
        List<File> filesV2 = new ArrayList<>();
        filesV2.add(new File(this.getClass().getResource("/resource/update/update-v2.yml").getPath()));
        List<ResourceConfig> configsV2 = ResourceConfigFactory.createConfigs(filesV2);
        resourcesV2 = factory.createResources(configsV2);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testSingleDeployment() throws ResourceException {
        // Deploy v1
        hardUpdateStrategy.deploy(namespaceResource, resourcesV1);

        // Check that everything was deployed correctly
        Resource serviceResource = resourcesV1.get(0);
        Resource deploymentResource = resourcesV1.get(1);

        // Check service
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNotNull(service);
        ObjectMeta serviceMetadata = service.getMetadata();
        assertEquals(deploymentResource.getId(), serviceMetadata.getName());
        assertEquals(deploymentResource.getNamespace(), serviceMetadata.getNamespace());
        assertEquals(new Integer(8761), service.getSpec().getPorts().get(0).getPort());

        // Check deployment
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        assertNotNull(deployment);
        ObjectMeta deploymentMetadata = deployment.getMetadata();
        assertEquals(deploymentResource.getId(), deploymentMetadata.getName());
        assertEquals(deploymentResource.getNamespace(), deploymentMetadata.getNamespace());
        assertEquals("v1", deploymentMetadata.getLabels().get("version"));

        // Check pods
        PodList podList = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource);
        assertEquals(1, podList.getItems().size());
        Pod pod = podList.getItems().get(0);
        ObjectMeta podMetadata = pod.getMetadata();
        assertTrue(podMetadata.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource.getNamespace(), podMetadata.getNamespace());
        assertEquals("v1", deploymentMetadata.getLabels().get("version"));

        // Check replica sets
        ReplicaSetList replicaSetList = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResource);
        assertEquals(1, replicaSetList.getItems().size());
        ReplicaSet replicaSet = replicaSetList.getItems().get(0);
        ObjectMeta replicaSetMetadata = replicaSet.getMetadata();
        assertTrue(replicaSetMetadata.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource.getNamespace(), replicaSetMetadata.getNamespace());
        assertEquals("v1", deploymentMetadata.getLabels().get("version"));
    }

    public void testMultipleDeployments() throws ResourceException {
        // Deploy v1 - already tested above
        hardUpdateStrategy.deploy(namespaceResource, resourcesV1);

        // Deploy v2
        hardUpdateStrategy.deploy(namespaceResource, resourcesV2);

        // Check that everything was deployed correctly
        Resource serviceResource1 = resourcesV2.get(0);
        Resource deploymentResource1 = resourcesV2.get(1);

        // Check service1
        Service service1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource1);
        assertNotNull(service1);
        ObjectMeta serviceMetadata1 = service1.getMetadata();
        assertEquals(deploymentResource1.getId(), serviceMetadata1.getName());
        assertEquals(deploymentResource1.getNamespace(), serviceMetadata1.getNamespace());
        assertEquals(new Integer(8762), service1.getSpec().getPorts().get(0).getPort());

        // Check deployment1
        Deployment deployment1 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource1);
        assertNotNull(deployment1);
        ObjectMeta deploymentMetadata1 = deployment1.getMetadata();
        assertEquals(deploymentResource1.getId(), deploymentMetadata1.getName());
        assertEquals(deploymentResource1.getNamespace(), deploymentMetadata1.getNamespace());
        assertEquals("v2", deploymentMetadata1.getLabels().get("version"));

        // Check pods of deployment1
        PodList podList1 = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource1);
        assertEquals(2, podList1.getItems().size());
        Pod pod1 = podList1.getItems().get(0);
        ObjectMeta podMetadata1 = pod1.getMetadata();
        assertTrue(podMetadata1.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource1.getNamespace(), podMetadata1.getNamespace());
        assertEquals("v2", deploymentMetadata1.getLabels().get("version"));

        // Check replica sets of deployment1
        ReplicaSetList replicaSetList = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResource1);
        assertEquals(1, replicaSetList.getItems().size());
        ReplicaSet replicaSet = replicaSetList.getItems().get(0);
        ObjectMeta replicaSetMetadata = replicaSet.getMetadata();
        assertTrue(replicaSetMetadata.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource1.getNamespace(), replicaSetMetadata.getNamespace());
        assertEquals("v2", deploymentMetadata1.getLabels().get("version"));
    }
}
