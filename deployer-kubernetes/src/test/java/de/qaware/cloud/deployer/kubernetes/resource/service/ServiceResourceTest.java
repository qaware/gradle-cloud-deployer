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
package de.qaware.cloud.deployer.kubernetes.resource.service;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.FileUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesClientUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironmentUtil;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

public class ServiceResourceTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private ServiceResource serviceResource;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        KubernetesTestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create the ServiceResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String serviceDescription = FileUtil.readFile("/config/resource/service.yml");
        KubernetesResourceConfig resourceConfig = new KubernetesResourceConfig("test", ContentType.YAML, serviceDescription);
        serviceResource = new ServiceResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNull(service);

        // Test exists method
        assertFalse(serviceResource.exists());

        // Create service
        serviceResource.create();

        // Check that the service exists
        service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNotNull(service);

        // Test exists method
        assertTrue(serviceResource.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNull(service);

        // Create service
        serviceResource.create();

        // Check that the service exists
        service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNotNull(service);

        // Compare services
        assertEquals(service.getMetadata().getName(), serviceResource.getId());
        assertEquals(service.getApiVersion(), serviceResource.getResourceConfig().getResourceVersion());
        assertEquals(service.getKind(), serviceResource.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException {

        // Create service
        serviceResource.create();

        // Check that the service exists
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNotNull(service);

        // Delete service
        serviceResource.delete();

        // Check that service doesn't exist anymore
        service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResource);
        assertNull(service);
    }
}
