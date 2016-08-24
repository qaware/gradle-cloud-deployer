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
package de.qaware.cloud.deployer.kubernetes.resource.pod;

import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestEnvironment;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestUtil;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import junit.framework.TestCase;

public class PodResourceTest extends TestCase {

    private KubernetesClient kubernetesClient;
    private NamespaceResource namespaceResource;
    private PodResource podResource;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        ResourceTestEnvironment testEnvironment = ResourceTestUtil.createTestEnvironment();
        namespaceResource = testEnvironment.getNamespaceResource();
        kubernetesClient = testEnvironment.getKubernetesClient();
        ResourceTestUtil.createNamespace(namespaceResource);

        // Create the PodResource object
        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String podDescription = ResourceTestUtil.readFile("/pod.json");
        ResourceConfig resourceConfig = new ResourceConfig(ContentType.JSON, podDescription);
        podResource = new PodResource(namespaceResource.getNamespace(), resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        namespaceResource.delete();
    }

    public void testExists() throws ResourceException, InterruptedException {

        // Check that the pod doesn't exist already
        Pod pod = retrievePod();
        assertNull(pod);

        // Test exists method
        assertFalse(podResource.exists());

        // Create pod
        assertTrue(podResource.create());

        // Check that the pod exists
        pod = retrievePod();
        assertNotNull(pod);

        // Test exists method
        assertTrue(podResource.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException {

        // Check that the pod doesn't exist already
        Pod pod = retrievePod();
        assertNull(pod);

        // Create pod
        assertTrue(podResource.create());

        // Check that the pod exists
        pod = retrievePod();
        assertNotNull(pod);

        // Compare services
        assertEquals(pod.getMetadata().getName(), podResource.getId());
        assertEquals(pod.getApiVersion(), podResource.getResourceConfig().getResourceVersion());
        assertEquals(pod.getKind(), podResource.getResourceConfig().getResourceType());
    }

    public void testDelete() throws ResourceException {

        // Create pod
        assertTrue(podResource.create());

        // Check that the pod exists
        Pod pod = retrievePod();
        assertNotNull(pod);

        // Delete pod
        assertTrue(podResource.delete());

        // Check that pod doesn't exist anymore
        pod = retrievePod();
        assertNull(pod);
    }

    private Pod retrievePod() {
        return kubernetesClient.pods().inNamespace(podResource.getNamespace()).withName(podResource.getId()).get();
    }
}
