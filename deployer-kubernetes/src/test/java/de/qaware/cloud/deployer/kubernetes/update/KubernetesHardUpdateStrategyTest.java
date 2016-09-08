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

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.resource.KubernetesResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.CheckUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesClientUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironmentUtil;
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

public class KubernetesHardUpdateStrategyTest extends TestCase {

    private NamespaceResource namespaceResource;
    private KubernetesHardUpdateStrategy hardUpdateStrategy;
    private List<KubernetesResource> resourcesV1;
    private List<KubernetesResource> resourcesV2;
    private List<KubernetesResource> resourcesV3;
    private KubernetesClient kubernetesClient;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment("HARD");
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        CloudConfig cloudConfig = testEnvironment.getCloudConfig();
        KubernetesTestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create update strategy
        hardUpdateStrategy = new KubernetesHardUpdateStrategy();

        // Create config and resource factory
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        KubernetesResourceFactory factory = new KubernetesResourceFactory(namespaceResource.getNamespace(), cloudConfig);

        // Create the resources for v1
        List<File> filesV1 = new ArrayList<>();
        filesV1.add(new File(this.getClass().getResource("/resource/update/hard-update-v1.yml").getPath()));
        List<KubernetesResourceConfig> configsV1 = resourceConfigFactory.createConfigs(filesV1);
        resourcesV1 = factory.createResources(configsV1);

        // Create the resources for v2
        List<File> filesV2 = new ArrayList<>();
        filesV2.add(new File(this.getClass().getResource("/resource/update/hard-update-v2.yml").getPath()));
        List<KubernetesResourceConfig> configsV2 = resourceConfigFactory.createConfigs(filesV2);
        resourcesV2 = factory.createResources(configsV2);

        // Create the resources for v3
        List<File> filesV3 = new ArrayList<>();
        filesV3.add(new File(this.getClass().getResource("/resource/update/hard-update-v3.yml").getPath()));
        List<KubernetesResourceConfig> configsV3 = resourceConfigFactory.createConfigs(filesV3);
        resourcesV3 = factory.createResources(configsV3);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testSingleDeployment() throws ResourceException {
        // Deploy v1
        hardUpdateStrategy.deploy(namespaceResource, resourcesV1);
        String version = "v1";

        // Check that everything was deployed correctly
        KubernetesResource serviceResource1 = resourcesV1.get(0);
        KubernetesResource deploymentResource = resourcesV1.get(1);
        KubernetesResource serviceResource2 = resourcesV1.get(2);

        // Check services
        assertEquals(2, KubernetesClientUtil.retrieveServices(kubernetesClient, namespaceResource).getItems().size());
        Service service1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource1);
        Service service2 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource2);
        CheckUtil.checkService(serviceResource1, service1, version, 8761);
        CheckUtil.checkService(serviceResource2, service2, version, 8765);

        // Check deployment
        assertEquals(1, KubernetesClientUtil.retrieveDeployments(kubernetesClient, namespaceResource).getItems().size());
        Deployment deployment = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource);
        CheckUtil.checkDeployment(deploymentResource, deployment, version);

        // Check pod
        PodList podList = KubernetesClientUtil.retrievePods(kubernetesClient, namespaceResource);
        assertEquals(1, podList.getItems().size());
        Pod pod = podList.getItems().get(0);
        CheckUtil.checkPod(deploymentResource, pod, version);

        // Check replica set
        ReplicaSetList replicaSetList = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, namespaceResource);
        assertEquals(1, replicaSetList.getItems().size());
        ReplicaSet replicaSet = replicaSetList.getItems().get(0);
        CheckUtil.checkReplicaSet(deploymentResource, replicaSet, version);
    }

    public void testMultipleDeployments() throws ResourceException {
        // Deploy v1 - already tested above
        hardUpdateStrategy.deploy(namespaceResource, resourcesV1);


        // Deploy v2
        hardUpdateStrategy.deploy(namespaceResource, resourcesV2);
        String version = "v2";

        // Check that everything was deployed correctly
        KubernetesResource serviceResource2 = resourcesV2.get(0);
        KubernetesResource deploymentResource2 = resourcesV2.get(1);

        // Check service2
        assertEquals(1, KubernetesClientUtil.retrieveServices(kubernetesClient, namespaceResource).getItems().size());
        Service service2 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource2);
        CheckUtil.checkService(serviceResource2, service2, version, 8762);

        // Check deployment2
        assertEquals(1, KubernetesClientUtil.retrieveDeployments(kubernetesClient, namespaceResource).getItems().size());
        Deployment deployment2 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource2);
        CheckUtil.checkDeployment(deploymentResource2, deployment2, version);

        // Check pods of deployment2
        PodList podList2 = KubernetesClientUtil.retrievePods(kubernetesClient, namespaceResource);
        assertEquals(2, podList2.getItems().size());
        Pod pod2a = podList2.getItems().get(0);
        CheckUtil.checkPod(deploymentResource2, pod2a, version);
        Pod pod2b = podList2.getItems().get(1);
        CheckUtil.checkPod(deploymentResource2, pod2b, version);

        // Check replica sets of deployment2
        ReplicaSetList replicaSetList2 = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, namespaceResource);
        assertEquals(1, replicaSetList2.getItems().size());
        ReplicaSet replicaSet2 = replicaSetList2.getItems().get(0);
        CheckUtil.checkReplicaSet(deploymentResource2, replicaSet2, version);


        // Deploy v3
        hardUpdateStrategy.deploy(namespaceResource, resourcesV3);
        version = "v3";

        // Check that everything was deployed correctly
        KubernetesResource deploymentResource3 = resourcesV3.get(0);

        // Check that no service exists
        assertEquals(0, KubernetesClientUtil.retrieveServices(kubernetesClient, deploymentResource3).getItems().size());

        // Check deployment3
        assertEquals(1, KubernetesClientUtil.retrieveDeployments(kubernetesClient, namespaceResource).getItems().size());
        Deployment deployment3 = KubernetesClientUtil.retrieveDeployment(kubernetesClient, deploymentResource3);
        CheckUtil.checkDeployment(deploymentResource3, deployment3, version);

        // Check pods of deployment3
        PodList podList3 = KubernetesClientUtil.retrievePods(kubernetesClient, namespaceResource);
        assertEquals(1, podList3.getItems().size());
        Pod pod3 = podList3.getItems().get(0);
        CheckUtil.checkPod(deploymentResource3, pod3, version);

        // Check replica sets of deployment3
        ReplicaSetList replicaSetList3 = KubernetesClientUtil.retrieveReplicaSets(kubernetesClient, namespaceResource);
        assertEquals(1, replicaSetList3.getItems().size());
        ReplicaSet replicaSet3 = replicaSetList3.getItems().get(0);
        CheckUtil.checkReplicaSet(deploymentResource3, replicaSet3, version);
    }
}
