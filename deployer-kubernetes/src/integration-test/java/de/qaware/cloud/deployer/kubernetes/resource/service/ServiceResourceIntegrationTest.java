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

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesClientUtil;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.test.KubernetesTestEnvironmentUtil;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

import java.util.List;

public class ServiceResourceIntegrationTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private ServiceResource serviceResourceV1;
    private ServiceResource serviceResourceV2;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        KubernetesTestEnvironment testEnvironment = KubernetesTestEnvironmentUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        KubernetesTestEnvironmentUtil.createTestNamespace(namespaceResource);

        // Create the service resource v1 object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String serviceDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/kubernetes/resources/service/service-v1.yml");
        KubernetesResourceConfig resourceConfigV1 = new KubernetesResourceConfig("test", ContentType.YAML, serviceDescriptionV1);
        serviceResourceV1 = new ServiceResource(namespaceResource.getNamespace(), resourceConfigV1, clientFactory);

        // Create the service resource v2 object
        String serviceDescriptionV2 = FileUtil.readFileContent("/de/qaware/cloud/deployer/kubernetes/resources/service/service-v2.yml");
        KubernetesResourceConfig resourceConfigV2 = new KubernetesResourceConfig("test", ContentType.YAML, serviceDescriptionV2);
        serviceResourceV2 = new ServiceResource(namespaceResource.getNamespace(), resourceConfigV2, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNull(service);

        // Test exists method
        assertFalse(serviceResourceV1.exists());

        // Create service
        serviceResourceV1.create();

        // Check that the service exists
        service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNotNull(service);

        // Test exists method
        assertTrue(serviceResourceV1.exists());
    }

    public void testCreate() throws ResourceException {

        // Check that the service doesn't exist already
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNull(service);

        // Create service
        serviceResourceV1.create();

        // Check that the service exists
        service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNotNull(service);

        // Compare services
        assertEquals(service.getMetadata().getName(), serviceResourceV1.getId());
        assertEquals(service.getApiVersion(), serviceResourceV1.getResourceConfig().getResourceVersion());
        assertEquals(service.getKind(), serviceResourceV1.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException {

        // Create service
        serviceResourceV1.create();

        // Check that the service exists
        Service service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNotNull(service);

        // Delete service
        serviceResourceV1.delete();

        // Check that service doesn't exist anymore
        service = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNull(service);
    }

    public void testUpdate() throws ResourceException {

        // Check that the service doesn't exist already
        Service serviceV1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNull(serviceV1);

        // Create service
        serviceResourceV1.create();

        // Retrieve service v1
        serviceV1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);

        // Update service
        serviceResourceV2.update();

        // Retrieve service v2
        Service serviceV2 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);

        // Check service
        assertFalse(serviceV1.equals(serviceV2));
        List<ServicePort> portsV1 = serviceV1.getSpec().getPorts();
        List<ServicePort> portsV2 = serviceV2.getSpec().getPorts();
        assertEquals(1, portsV1.size());
        assertEquals(1, portsV2.size());
        assertEquals(new Integer(8761), portsV1.get(0).getPort());
        assertEquals(new Integer(8762), portsV2.get(0).getPort());
    }

    public void testEmptyUpdate() throws ResourceException {

        // Check that the service doesn't exist already
        Service serviceV1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);
        assertNull(serviceV1);

        // Create service
        serviceResourceV1.create();

        // Retrieve service v1
        serviceV1 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);

        // Update service v1
        serviceResourceV1.update();

        // Retrieve service v2
        Service serviceV2 = KubernetesClientUtil.retrieveService(kubernetesClient, serviceResourceV1);

        // Check service
        assertEquals(serviceV1.getMetadata().getUid(), serviceV2.getMetadata().getUid());
        assertEquals(serviceV1.getMetadata().getCreationTimestamp(), serviceV2.getMetadata().getCreationTimestamp());
        List<ServicePort> portsV1 = serviceV1.getSpec().getPorts();
        List<ServicePort> portsV2 = serviceV2.getSpec().getPorts();
        assertEquals(1, portsV1.size());
        assertEquals(1, portsV2.size());
        assertEquals(new Integer(8761), portsV1.get(0).getPort());
        assertEquals(new Integer(8761), portsV2.get(0).getPort());
    }
}
