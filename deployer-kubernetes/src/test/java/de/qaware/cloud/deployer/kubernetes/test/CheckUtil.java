package de.qaware.cloud.deployer.kubernetes.test;

import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSet;
import junit.framework.TestCase;

public class CheckUtil {

    public static void checkService(Resource serviceResource, Service service, String version, int port) {
        TestCase.assertNotNull(service);
        ObjectMeta serviceMetadata = service.getMetadata();
        TestCase.assertEquals(serviceResource.getId(), serviceMetadata.getName());
        TestCase.assertEquals(serviceResource.getNamespace(), serviceMetadata.getNamespace());
        TestCase.assertEquals(new Integer(port), service.getSpec().getPorts().get(0).getPort());
        TestCase.assertEquals(version, serviceMetadata.getLabels().get("version"));
    }

    public static void checkDeployment(Resource deploymentResource, Deployment deployment, String version) {
        TestCase.assertNotNull(deployment);
        ObjectMeta deploymentMetadata = deployment.getMetadata();
        TestCase.assertEquals(deploymentResource.getId(), deploymentMetadata.getName());
        TestCase.assertEquals(deploymentResource.getNamespace(), deploymentMetadata.getNamespace());
        TestCase.assertEquals(version, deploymentMetadata.getLabels().get("version"));
    }

    public static void checkPod(Resource deploymentResource, Pod pod, String version) {
        ObjectMeta podMetadata = pod.getMetadata();
        TestCase.assertTrue(podMetadata.getName().contains(deploymentResource.getId() + "-"));
        TestCase.assertEquals(deploymentResource.getNamespace(), podMetadata.getNamespace());
        TestCase.assertEquals(version, podMetadata.getLabels().get("version"));
    }

    public static void checkReplicaSet(Resource deploymentResource, ReplicaSet replicaSet, String version) {
        ObjectMeta replicaSetMetadata = replicaSet.getMetadata();
        TestCase.assertTrue(replicaSetMetadata.getName().contains(deploymentResource.getId() + "-"));
        TestCase.assertEquals(deploymentResource.getNamespace(), replicaSetMetadata.getNamespace());
        TestCase.assertEquals(version, replicaSetMetadata.getLabels().get("version"));
    }
}
