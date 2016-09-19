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

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.update.UpdateStrategy;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.resource.KubernetesResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.*;
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
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class KubernetesReplaceUpdateStrategyTest extends TestCase {

    private NamespaceResource namespaceResource;
    private KubernetesReplaceUpdateStrategy softUpdateStrategy;
    private List<KubernetesResource> resourcesV1;
    private List<KubernetesResource> resourcesV2;
    private KubernetesClient kubernetesClient;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment(UpdateStrategy.REPLACE);
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        KubernetesEnvironmentConfig environmentConfig = testEnvironment.getEnvironmentConfig();
        KubernetesTestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create update strategy
        softUpdateStrategy = new KubernetesReplaceUpdateStrategy();

        // Create config and resource factory
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        KubernetesResourceFactory factory = new KubernetesResourceFactory(environmentConfig);

        // Create the resources for v1
        List<File> filesV1 = new ArrayList<>();
        filesV1.add(new File(this.getClass().getResource("/update/soft-update-v1.yml").getPath()));
        List<KubernetesResourceConfig> configsV1 = resourceConfigFactory.createConfigs(filesV1);
        resourcesV1 = factory.createResources(configsV1);

        // Create the resources for v2
        List<File> filesV2 = new ArrayList<>();
        filesV2.add(new File(this.getClass().getResource("/update/soft-update-v2.yml").getPath()));
        List<KubernetesResourceConfig> configsV2 = resourceConfigFactory.createConfigs(filesV2);
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
        KubernetesResource serviceResource1 = resourcesV1.get(0);
        KubernetesResource deploymentResource1 = resourcesV1.get(1);
        KubernetesResource serviceResource2 = resourcesV1.get(2);
        KubernetesResource deploymentResource2 = resourcesV1.get(3);

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

    public void testMultipleDeployments() throws ResourceException, TimeoutException, InterruptedException {
        // Deploy v1 - already tested above
        softUpdateStrategy.deploy(namespaceResource, resourcesV1);
        String version1 = "v1";

        KubernetesResource deploymentResource0 = resourcesV1.get(1);
        List<Pod> pods0 = KubernetesClientUtil.retrievePods(kubernetesClient, deploymentResource0).getItems();
        assertEquals(3, pods0.size());
        Pod pod0a = pods0.get(0);
        Pod pod0b = pods0.get(1);
        Pod pod0c = pods0.get(1);
        PodDeletionBlocker podDeletionBlocker0a = new PodDeletionBlocker(kubernetesClient, pod0a);
        PodDeletionBlocker podDeletionBlocker0b = new PodDeletionBlocker(kubernetesClient, pod0b);
        PodDeletionBlocker podDeletionBlocker0c = new PodDeletionBlocker(kubernetesClient, pod0c);

        // Deploy v2
        softUpdateStrategy.deploy(namespaceResource, resourcesV2);
        String version2 = "v2";

        // Wait until the pods are deleted
        podDeletionBlocker0a.block();
        podDeletionBlocker0b.block();
        podDeletionBlocker0c.block();

        // Check that everything was deployed correctly
        KubernetesResource serviceResource1 = resourcesV1.get(2);
        KubernetesResource deploymentResource1 = resourcesV1.get(3);
        KubernetesResource serviceResource2 = resourcesV2.get(0);
        KubernetesResource deploymentResource2 = resourcesV2.get(1);

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
