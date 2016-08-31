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
import de.qaware.cloud.deployer.kubernetes.test.CheckUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesClientUtil;
import de.qaware.cloud.deployer.kubernetes.test.TestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.TestEnvironmentUtil;
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
import java.util.stream.Collectors;

public class SoftUpdateStrategyTest extends TestCase {

    private NamespaceResource namespaceResource;
    private SoftUpdateStrategy softUpdateStrategy;
    private List<Resource> resourcesV1;
    private List<Resource> resourcesV2;
    private KubernetesClient kubernetesClient;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        TestEnvironment testEnvironment = TestEnvironmentUtil.createTestEnvironment("SOFT");
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        CloudConfig cloudConfig = testEnvironment.getCloudConfig();
        TestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create update strategy
        softUpdateStrategy = new SoftUpdateStrategy();

        // Create resource factory
        ResourceFactory factory = new ResourceFactory(namespaceResource.getNamespace(), cloudConfig);

        // Create the resources for v1
        List<File> filesV1 = new ArrayList<>();
        filesV1.add(new File(this.getClass().getResource("/resource/update/soft-update-v1.yml").getPath()));
        List<ResourceConfig> configsV1 = ResourceConfigFactory.createConfigs(filesV1);
        resourcesV1 = factory.createResources(configsV1);

        // Create the resources for v2
        List<File> filesV2 = new ArrayList<>();
        filesV2.add(new File(this.getClass().getResource("/resource/update/soft-update-v2.yml").getPath()));
        List<ResourceConfig> configsV2 = ResourceConfigFactory.createConfigs(filesV2);
        resourcesV2 = factory.createResources(configsV2);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testSingleDeployment() throws ResourceException {
        // Deploy v1
        softUpdateStrategy.deploy(namespaceResource, resourcesV1);
        String version = "v1";

        // Check that everything was deployed correctly
        Resource serviceResource1 = resourcesV1.get(0);
        Resource deploymentResource1 = resourcesV1.get(1);
        Resource serviceResource2 = resourcesV1.get(2);
        Resource deploymentResource2 = resourcesV1.get(3);

        // Check services
        assertEquals(2, KubernetesClientUtil.retrieveServices(kubernetesClient, namespaceResource).getItems().size());
        Service service1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource1);
        Service service2 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource2);
        CheckUtil.checkService(serviceResource1, service1, version, 8761);
        CheckUtil.checkService(serviceResource2, service2, version, 8888);

        // Check deployments
        assertEquals(2, KubernetesClientUtil.retrieveDeployments(kubernetesClient, namespaceResource).getItems().size());
        Deployment deployment1 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource1);
        Deployment deployment2 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource2);
        CheckUtil.checkDeployment(deploymentResource1, deployment1, version);
        CheckUtil.checkDeployment(deploymentResource2, deployment2, version);

        // Check pods
        PodList podList = KubernetesClientUtil.retrievePods(kubernetesClient, namespaceResource);
        assertEquals(3, podList.getItems().size());

        List<Pod> pods1 = podList.getItems().stream().filter(pod -> pod.getMetadata().getName().contains(deploymentResource1.getId())).collect(Collectors.toList());
        assertEquals(2, pods1.size());
        Pod pod1a = pods1.get(0);
        Pod pod1b = pods1.get(1);
        CheckUtil.checkPod(deploymentResource1, pod1a, version);
        CheckUtil.checkPod(deploymentResource1, pod1b, version);

        List<Pod> pods2 = podList.getItems().stream().filter(pod -> pod.getMetadata().getName().contains(deploymentResource2.getId())).collect(Collectors.toList());
        assertEquals(1, pods2.size());
        Pod pod2 = pods2.get(0);
        CheckUtil.checkPod(deploymentResource2, pod2, version);

        // Check replica sets
        ReplicaSetList replicaSetList = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, namespaceResource);
        assertEquals(2, replicaSetList.getItems().size());

        List<ReplicaSet> replicaSets1 = replicaSetList.getItems().stream().filter(replicaSet -> replicaSet.getMetadata().getName().contains(deploymentResource1.getId())).collect(Collectors.toList());
        assertEquals(1, replicaSets1.size());
        ReplicaSet replicaSet1 = replicaSets1.get(0);
        CheckUtil.checkReplicaSet(deploymentResource1, replicaSet1, version);

        List<ReplicaSet> replicaSets2 = replicaSetList.getItems().stream().filter(replicaSet -> replicaSet.getMetadata().getName().contains(deploymentResource2.getId())).collect(Collectors.toList());
        assertEquals(1, replicaSets2.size());
        ReplicaSet replicaSet2 = replicaSets2.get(0);
        CheckUtil.checkReplicaSet(deploymentResource2, replicaSet2, version);
    }

    public void testMultipleDeployments() throws ResourceException {
        // Deploy v1 - already tested above
        softUpdateStrategy.deploy(namespaceResource, resourcesV1);
        String version1 = "v1";


        // Deploy v2
        softUpdateStrategy.deploy(namespaceResource, resourcesV2);
        String version2 = "v2";

        // Check that everything was deployed correctly
        Resource serviceResource1 = resourcesV1.get(0);
        Resource deploymentResource1 = resourcesV1.get(1);
        Resource serviceResource2 = resourcesV2.get(0);
        Resource deploymentResource2 = resourcesV2.get(1);

        // Check services
        assertEquals(2, KubernetesClientUtil.retrieveServices(kubernetesClient, namespaceResource).getItems().size());
        Service service1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource1);
        Service service2 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource2);
        CheckUtil.checkService(serviceResource1, service1, version1, 8888);
        CheckUtil.checkService(serviceResource2, service2, version2, 8761);

        // Check deployments
        assertEquals(2, KubernetesClientUtil.retrieveDeployments(kubernetesClient, namespaceResource).getItems().size());
        Deployment deployment1 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource1);
        Deployment deployment2 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource2);
        CheckUtil.checkDeployment(deploymentResource1, deployment1, version1);
        CheckUtil.checkDeployment(deploymentResource2, deployment2, version2);

        // Check pods
        PodList podList = KubernetesClientUtil.retrievePods(kubernetesClient, namespaceResource);
        assertEquals(2, podList.getItems().size());

        List<Pod> pods1 = podList.getItems().stream().filter(pod -> pod.getMetadata().getName().contains(deploymentResource1.getId())).collect(Collectors.toList());
        assertEquals(1, pods1.size());
        Pod pod1 = pods1.get(0);
        CheckUtil.checkPod(deploymentResource1, pod1, version1);

        List<Pod> pods2 = podList.getItems().stream().filter(pod -> pod.getMetadata().getName().contains(deploymentResource2.getId())).collect(Collectors.toList());
        assertEquals(1, pods2.size());
        Pod pod2 = pods2.get(0);
        CheckUtil.checkPod(deploymentResource2, pod2, version2);

        // Check replica sets
        ReplicaSetList replicaSetList = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, namespaceResource);
        assertEquals(2, replicaSetList.getItems().size());

        List<ReplicaSet> replicaSets1 = replicaSetList.getItems().stream().filter(replicaSet -> replicaSet.getMetadata().getName().contains(deploymentResource1.getId())).collect(Collectors.toList());
        assertEquals(1, replicaSets1.size());
        ReplicaSet replicaSet1 = replicaSets1.get(0);
        CheckUtil.checkReplicaSet(deploymentResource1, replicaSet1, version1);

        List<ReplicaSet> replicaSets2 = replicaSetList.getItems().stream().filter(replicaSet -> replicaSet.getMetadata().getName().contains(deploymentResource2.getId())).collect(Collectors.toList());
        assertEquals(1, replicaSets2.size());
        ReplicaSet replicaSet2 = replicaSets2.get(0);
        CheckUtil.checkReplicaSet(deploymentResource2, replicaSet2, version2);
    }
}
