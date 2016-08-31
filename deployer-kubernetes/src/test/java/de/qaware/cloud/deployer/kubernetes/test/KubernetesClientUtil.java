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
package de.qaware.cloud.deployer.kubernetes.test;

import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSetList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class KubernetesClientUtil {

    private KubernetesClientUtil() {
    }

    public static Deployment retrieveDeployment(KubernetesClient kubernetesClient, Resource resource) {
        return kubernetesClient.extensions().deployments().inNamespace(resource.getNamespace()).withName(resource.getId()).get();
    }

    public static PodList retrievePods(KubernetesClient kubernetesClient, Resource resource) {
        return kubernetesClient.pods().inNamespace(resource.getNamespace()).list();
    }

    public static ReplicaSetList retrieveReplicaSets(KubernetesClient kubernetesClient, Resource resource) {
        return kubernetesClient.extensions().replicaSets().inNamespace(resource.getNamespace()).list();
    }

    public static Namespace retrieveNamespace(KubernetesClient kubernetesClient, Resource resource) {
        return kubernetesClient.namespaces().withName(resource.getNamespace()).get();
    }

    public static Service retrieveService(KubernetesClient kubernetesClient, Resource resource) {
        return kubernetesClient.services().inNamespace(resource.getNamespace()).withName(resource.getId()).get();
    }

    public static Pod retrievePod(KubernetesClient kubernetesClient, Resource resource) {
        return kubernetesClient.pods().inNamespace(resource.getNamespace()).withName(resource.getId()).get();
    }

    public static ReplicationController retrieveReplicationController(KubernetesClient kubernetesClient, Resource resource) {
        return kubernetesClient.replicationControllers().inNamespace(resource.getNamespace()).withName(resource.getId()).get();
    }
}
