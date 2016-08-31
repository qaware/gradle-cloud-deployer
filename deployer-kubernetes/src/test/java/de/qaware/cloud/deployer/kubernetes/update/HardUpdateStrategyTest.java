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
    private List<Resource> resourcesV3;
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

        // Create the resources for v3
        List<File> filesV3 = new ArrayList<>();
        filesV3.add(new File(this.getClass().getResource("/resource/update/update-v3.yml").getPath()));
        List<ResourceConfig> configsV3 = ResourceConfigFactory.createConfigs(filesV3);
        resourcesV3 = factory.createResources(configsV3);
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
        assertEquals(1, KubernetesClientUtil.retrieveServices(kubernetesClient, serviceResource).getItems().size());
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNotNull(service);
        ObjectMeta serviceMetadata = service.getMetadata();
        assertEquals(deploymentResource.getId(), serviceMetadata.getName());
        assertEquals(deploymentResource.getNamespace(), serviceMetadata.getNamespace());
        assertEquals(new Integer(8761), service.getSpec().getPorts().get(0).getPort());

        // Check deployment
        assertEquals(1, KubernetesClientUtil.retrieveDeployments(kubernetesClient, deploymentResource).getItems().size());
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
        Resource serviceResource2 = resourcesV2.get(0);
        Resource deploymentResource2 = resourcesV2.get(1);

        // Check service2
        assertEquals(1, KubernetesClientUtil.retrieveServices(kubernetesClient, serviceResource2).getItems().size());
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource2);
        assertNotNull(service);
        ObjectMeta serviceMetadata = service.getMetadata();
        assertEquals(deploymentResource2.getId(), serviceMetadata.getName());
        assertEquals(deploymentResource2.getNamespace(), serviceMetadata.getNamespace());
        assertEquals(new Integer(8762), service.getSpec().getPorts().get(0).getPort());

        // Check deployment2
        assertEquals(1, KubernetesClientUtil.retrieveDeployments(kubernetesClient, deploymentResource2).getItems().size());
        Deployment deployment2 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource2);
        assertNotNull(deployment2);
        ObjectMeta deploymentMetadata2 = deployment2.getMetadata();
        assertEquals(deploymentResource2.getId(), deploymentMetadata2.getName());
        assertEquals(deploymentResource2.getNamespace(), deploymentMetadata2.getNamespace());
        assertEquals("v2", deploymentMetadata2.getLabels().get("version"));

        // Check pods of deployment2
        PodList podList2 = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource2);
        assertEquals(2, podList2.getItems().size());
        Pod pod2a = podList2.getItems().get(0);
        ObjectMeta podMetadata2a = pod2a.getMetadata();
        assertTrue(podMetadata2a.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource2.getNamespace(), podMetadata2a.getNamespace());
        assertEquals("v2", podMetadata2a.getLabels().get("version"));
        Pod pod2b = podList2.getItems().get(1);
        ObjectMeta podMetadata2b = pod2b.getMetadata();
        assertTrue(podMetadata2b.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource2.getNamespace(), podMetadata2b.getNamespace());
        assertEquals("v2", podMetadata2b.getLabels().get("version"));

        // Check replica sets of deployment2
        ReplicaSetList replicaSetList2 = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResource2);
        assertEquals(1, replicaSetList2.getItems().size());
        ReplicaSet replicaSet2 = replicaSetList2.getItems().get(0);
        ObjectMeta replicaSetMetadata2 = replicaSet2.getMetadata();
        assertTrue(replicaSetMetadata2.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource2.getNamespace(), replicaSetMetadata2.getNamespace());
        assertEquals("v2", replicaSetMetadata2.getLabels().get("version"));


        // Deploy v3
        hardUpdateStrategy.deploy(namespaceResource, resourcesV3);

        // Check that everything was deployed correctly
        Resource deploymentResource3 = resourcesV3.get(0);

        // Check that no service exists
        assertEquals(0, KubernetesClientUtil.retrieveServices(kubernetesClient, deploymentResource3).getItems().size());

        // Check deployment3
        assertEquals(1, KubernetesClientUtil.retrieveDeployments(kubernetesClient, deploymentResource3).getItems().size());
        Deployment deployment3 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource3);
        assertNotNull(deployment3);
        ObjectMeta deploymentMetadata3 = deployment3.getMetadata();
        assertEquals(deploymentResource3.getId(), deploymentMetadata3.getName());
        assertEquals(deploymentResource3.getNamespace(), deploymentMetadata3.getNamespace());
        assertEquals("v3", deploymentMetadata3.getLabels().get("version"));

        // Check pods of deployment3
        PodList podList3 = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource3);
        assertEquals(1, podList3.getItems().size());
        Pod pod3 = podList3.getItems().get(0);
        ObjectMeta podMetadata3 = pod3.getMetadata();
        assertTrue(podMetadata3.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource3.getNamespace(), podMetadata3.getNamespace());
        assertEquals("v3", podMetadata3.getLabels().get("version"));

        // Check replica sets of deployment3
        ReplicaSetList replicaSetList3 = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, deploymentResource3);
        assertEquals(1, replicaSetList3.getItems().size());
        ReplicaSet replicaSet3 = replicaSetList3.getItems().get(0);
        ObjectMeta replicaSetMetadata3 = replicaSet3.getMetadata();
        assertTrue(replicaSetMetadata3.getName().contains("zwitscher-eureka-"));
        assertEquals(deploymentResource3.getNamespace(), replicaSetMetadata3.getNamespace());
        assertEquals("v3", replicaSetMetadata3.getLabels().get("version"));
    }
}
