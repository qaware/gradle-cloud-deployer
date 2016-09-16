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
package de.qaware.cloud.deployer.kubernetes.resource;

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.resource.pod.PodResource;
import de.qaware.cloud.deployer.kubernetes.resource.replication.controller.ReplicationControllerResource;
import de.qaware.cloud.deployer.kubernetes.resource.service.ServiceResource;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironmentUtil;
import junit.framework.TestCase;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

public class KubernetesResourceFactoryTest extends TestCase {

    private static final String TEMP_FILE = "temp";

    private KubernetesResourceConfig resourceConfig;
    private KubernetesResourceFactory resourceFactory;
    private NamespaceResource namespaceResource;

    @Override
    public void setUp() throws Exception {
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment();
        KubernetesEnvironmentConfig environmentConfig = testEnvironment.getEnvironmentConfig();
        resourceConfig = testEnvironment.getNamespaceResource().getResourceConfig();
        namespaceResource = testEnvironment.getNamespaceResource();
        resourceFactory = new KubernetesResourceFactory(environmentConfig);
    }

    public void testCreateWithEmptyResourceConfig() throws ResourceException, ResourceConfigException {
        boolean exceptionThrown = false;
        resourceConfig.setContent("");
        try {
            resourceFactory.createResource(resourceConfig);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_EMPTY_CONFIG", resourceConfig.getFilename()), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    public void testCreateWithValidVersionButUnknownType() throws ResourceConfigException {
        // Version: extensions/v1beta1
        String content = "apiVersion: extensions/v1beta1\n" +
                "kind: Unknown\n" +
                "metadata:\n" +
                "  name: zwitscher-eureka";
        String message = KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_RESOURCE_TYPE", TEMP_FILE);
        assertException(content, message);

        // Version: v1
        content = "apiVersion: v1\n" +
                "kind: Unknown\n" +
                "metadata:\n" +
                "  name: zwitscher-eureka";
        message = KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_RESOURCE_TYPE", TEMP_FILE);
        assertException(content, message);
    }

    public void testCreateWithValidTypeButUnknownVersion() throws ResourceConfigException {
        String content = "apiVersion: unknown\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: zwitscher-eureka";
        String message = KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_API_VERSION", TEMP_FILE);
        assertException(content, message);
    }

    public void testCreateWithValidDeployment() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.YAML, FileUtil.readFileContent("/deployment/deployment.yml"));
        KubernetesResource deploymentResource = resourceFactory.createResource(config);
        assertTrue(deploymentResource instanceof DeploymentResource);
        assertEquals("zwitscher-eureka", deploymentResource.getId());
        assertEquals(namespaceResource.getId(), deploymentResource.getNamespace());
    }

    public void testCreateWithValidPod() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.JSON, FileUtil.readFileContent("/pod/pod.json"));
        KubernetesResource podResource = resourceFactory.createResource(config);
        assertTrue(podResource instanceof PodResource);
        assertEquals("nginx-mysql", podResource.getId());
        assertEquals(namespaceResource.getId(), podResource.getNamespace());
    }

    public void testCreateWithValidService() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.YAML, FileUtil.readFileContent("/service/service.yml"));
        KubernetesResource serviceResource = resourceFactory.createResource(config);
        assertTrue(serviceResource instanceof ServiceResource);
        assertEquals("zwitscher-eureka", serviceResource.getId());
        assertEquals(namespaceResource.getId(), serviceResource.getNamespace());
    }

    public void testCreateWithValidReplicationController() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.YAML, FileUtil.readFileContent("/replication/controller/replication-controller.yml"));
        KubernetesResource controllerResource = resourceFactory.createResource(config);
        assertTrue(controllerResource instanceof ReplicationControllerResource);
        assertEquals("nginx", controllerResource.getId());
        assertEquals(namespaceResource.getId(), controllerResource.getNamespace());
    }

    private void assertException(String content, String exceptionMessage) throws ResourceConfigException {
        boolean exceptionThrown = false;
        KubernetesResourceConfig config = new KubernetesResourceConfig(TEMP_FILE, ContentType.YAML, content);
        try {
            resourceFactory.createResource(config);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(exceptionMessage, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
