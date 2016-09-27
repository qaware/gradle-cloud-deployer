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

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentResource;
import de.qaware.cloud.deployer.kubernetes.resource.pod.PodResource;
import de.qaware.cloud.deployer.kubernetes.resource.replication.controller.ReplicationControllerResource;
import de.qaware.cloud.deployer.kubernetes.resource.service.ServiceResource;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KubernetesResourceFactoryTest {

    private static final String NAMESPACE = "test-namespace";
    private static final String TEMP_FILE = "temp";

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(WireMockConfiguration.options().dynamicPort());

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    private KubernetesResourceFactory resourceFactory;

    @Before
    public void setUp() throws Exception {
        instanceRule.stubFor(get(urlEqualTo("/api/v1/namespaces"))
                .willReturn(aResponse().withStatus(200)));

        KubernetesEnvironmentConfig environmentConfig = new KubernetesEnvironmentConfig("test", "http://localhost:" + instanceRule.port(), Strategy.REPLACE, NAMESPACE);
        resourceFactory = new KubernetesResourceFactory(environmentConfig);
    }

    @Test
    public void testFactoryCreationWithFailingPing() throws ResourceConfigException {
        boolean exceptionThrown = false;
        instanceRule.resetMappings();
        instanceRule.stubFor(get(urlEqualTo("/api/v1/namespaces"))
                .willReturn(aResponse().withStatus(404)));
        KubernetesEnvironmentConfig environmentConfig = new KubernetesEnvironmentConfig("test", "http://localhost:" + instanceRule.port(), Strategy.REPLACE, NAMESPACE);
        try {
            resourceFactory = new KubernetesResourceFactory(environmentConfig);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_PING_FAILED", "test", 404), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testCreateWithEmptyResourceConfig() throws ResourceException, ResourceConfigException {
        boolean exceptionThrown = false;
        KubernetesResourceConfig resourceConfig = NamespaceResourceConfigFactory.create(NAMESPACE);
        resourceConfig.setContent("");
        try {
            resourceFactory.createResource(resourceConfig);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_EMPTY_CONFIG", resourceConfig.getFilename()), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    @Test
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

    @Test
    public void testCreateWithValidTypeButUnknownVersion() throws ResourceConfigException {
        String content = "apiVersion: unknown\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: zwitscher-eureka";
        String message = KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_API_VERSION", TEMP_FILE);
        assertException(content, message);
    }

    @Test
    public void testCreateWithValidDeployment() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.YAML, FileUtil.readFileContent(getTestFilePath("deployment.yml")));
        KubernetesResource deploymentResource = resourceFactory.createResource(config);
        assertTrue(deploymentResource instanceof DeploymentResource);
        assertEquals("zwitscher-eureka", deploymentResource.getId());
        assertEquals(NAMESPACE, deploymentResource.getNamespace());
    }

    @Test
    public void testCreateWithValidPod() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.JSON, FileUtil.readFileContent(getTestFilePath("pod.json")));
        KubernetesResource podResource = resourceFactory.createResource(config);
        assertTrue(podResource instanceof PodResource);
        assertEquals("nginx-mysql", podResource.getId());
        assertEquals(NAMESPACE, podResource.getNamespace());
    }

    @Test
    public void testCreateWithValidService() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.YAML, FileUtil.readFileContent(getTestFilePath("service.yml")));
        KubernetesResource serviceResource = resourceFactory.createResource(config);
        assertTrue(serviceResource instanceof ServiceResource);
        assertEquals("zwitscher-eureka", serviceResource.getId());
        assertEquals(NAMESPACE, serviceResource.getNamespace());
    }

    @Test
    public void testCreateWithValidReplicationController() throws ResourceConfigException, ResourceException {
        KubernetesResourceConfig config = new KubernetesResourceConfig("temp", ContentType.YAML, FileUtil.readFileContent(getTestFilePath("replication-controller.yml")));
        KubernetesResource controllerResource = resourceFactory.createResource(config);
        assertTrue(controllerResource instanceof ReplicationControllerResource);
        assertEquals("nginx", controllerResource.getId());
        assertEquals(NAMESPACE, controllerResource.getNamespace());
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

    private String getTestFilePath(String filename) {
        return "/de/qaware/cloud/deployer/kubernetes/resource/factory/" + filename;
    }
}
