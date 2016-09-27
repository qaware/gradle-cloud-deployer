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

import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSet;
import junit.framework.TestCase;

public class CheckUtil {

    public static void checkService(KubernetesResource serviceResource, Service service, String version, int port) {
        TestCase.assertNotNull(service);
        ObjectMeta serviceMetadata = service.getMetadata();
        TestCase.assertEquals(serviceResource.getId(), serviceMetadata.getName());
        TestCase.assertEquals(serviceResource.getNamespace(), serviceMetadata.getNamespace());
        TestCase.assertEquals(new Integer(port), service.getSpec().getPorts().get(0).getPort());
        TestCase.assertEquals(version, serviceMetadata.getLabels().get("version"));
    }

    public static void checkDeployment(KubernetesResource deploymentResource, Deployment deployment, String version) {
        TestCase.assertNotNull(deployment);
        ObjectMeta deploymentMetadata = deployment.getMetadata();
        TestCase.assertEquals(deploymentResource.getId(), deploymentMetadata.getName());
        TestCase.assertEquals(deploymentResource.getNamespace(), deploymentMetadata.getNamespace());
        TestCase.assertEquals(version, deploymentMetadata.getLabels().get("version"));
    }

    public static void checkPod(KubernetesResource deploymentResource, Pod pod, String version) {
        ObjectMeta podMetadata = pod.getMetadata();
        TestCase.assertTrue(podMetadata.getName().contains(deploymentResource.getId() + "-"));
        TestCase.assertEquals(deploymentResource.getNamespace(), podMetadata.getNamespace());
        TestCase.assertEquals(version, podMetadata.getLabels().get("version"));
    }

    public static void checkReplicaSet(KubernetesResource deploymentResource, ReplicaSet replicaSet, String version) {
        ObjectMeta replicaSetMetadata = replicaSet.getMetadata();
        TestCase.assertTrue(replicaSetMetadata.getName().contains(deploymentResource.getId() + "-"));
        TestCase.assertEquals(deploymentResource.getNamespace(), replicaSetMetadata.getNamespace());
        TestCase.assertEquals(version, replicaSetMetadata.getLabels().get("version"));
    }
}
