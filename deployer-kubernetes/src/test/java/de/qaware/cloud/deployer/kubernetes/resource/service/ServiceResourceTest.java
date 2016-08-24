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

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestUtil;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
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
        ResourceTestEnvironment testEnvironment = ResourceTestUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        ResourceTestUtil.createNamespace(namespaceResource);

        // Create the ServiceResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String serviceDescription = ResourceTestUtil.readFile("/service.yml");
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.YAML, serviceDescription);
        serviceResource = new ServiceResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = retrieveService();
        assertNull(service);

        // Test exists method
        assertFalse(serviceResource.exists());

        // Create service
        serviceResource.create();

        // Check that the service exists
        service = retrieveService();
        assertNotNull(service);

        // Test exists method
        assertTrue(serviceResource.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = retrieveService();
        assertNull(service);

        // Create service
        serviceResource.create();

        // Check that the service exists
        service = retrieveService();
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
        Service service = retrieveService();
        assertNotNull(service);

        // Delete service
        serviceResource.delete();

        // Check that service doesn't exist anymore
        service = retrieveService();
        assertNull(service);
    }

    private Service retrieveService() {
        return kubernetesClient.services().inNamespace(serviceResource.getNamespace()).withName(serviceResource.getId()).get();
    }
}
