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
package de.qaware.cloud.deployer.kubernetes.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.pod.PodResource;
import de.qaware.cloud.deployer.kubernetes.resource.replication.controller.ReplicationControllerResource;
import de.qaware.cloud.deployer.kubernetes.test.BaseKubernetesStrategyTest;
import org.junit.Test;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class KubernetesUpdateStrategyTest extends BaseKubernetesStrategyTest {

    @Test
    public void testDeployWithPodResources() {
        PodResource podResource = new PodResource("test", mock(KubernetesResourceConfig.class), mock(ClientFactory.class));
        assertNotSupported(podResource);
    }

    @Test
    public void testDeployWithReplicationControllerResource() {
        ReplicationControllerResource replicationControllerResource = new ReplicationControllerResource("test", mock(KubernetesResourceConfig.class), mock(ClientFactory.class));
        assertNotSupported(replicationControllerResource);
    }

    @Test
    public void testDeployWithSupportedNotExistingResources() throws ResourceException {
        // Update
        KubernetesUpdateStrategy updateStrategy = new KubernetesUpdateStrategy();
        updateStrategy.deploy(namespaceResource, resources);

        // Verify
        // Create the namespace
        verify(namespaceResource, times(1)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(1)).exists();
        verify(namespaceResource, times(0)).delete();

        // Create the deployment
        verify(deploymentResource, times(1)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(1)).exists();
        verify(deploymentResource, times(0)).delete();

        // Create the service
        verify(serviceResource, times(1)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(1)).exists();
        verify(serviceResource, times(0)).delete();
    }

    @Test
    public void testDeployWithSupportedExistingResources() throws ResourceException {
        // Tune resources - all exist already
        when(namespaceResource.exists()).thenReturn(true);
        when(serviceResource.exists()).thenReturn(true);
        when(deploymentResource.exists()).thenReturn(true);

        // Update
        KubernetesUpdateStrategy updateStrategy = new KubernetesUpdateStrategy();
        updateStrategy.deploy(namespaceResource, resources);

        // Verify
        // Don't touch the namespace
        verify(namespaceResource, times(0)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(1)).exists();
        verify(namespaceResource, times(0)).delete();

        // Update the deployment
        verify(deploymentResource, times(0)).create();
        verify(deploymentResource, times(1)).update();
        verify(deploymentResource, times(1)).exists();
        verify(deploymentResource, times(0)).delete();

        // Update the service
        verify(serviceResource, times(0)).create();
        verify(serviceResource, times(1)).update();
        verify(serviceResource, times(1)).exists();
        verify(serviceResource, times(0)).delete();
    }

    @Test
    public void testDeployWithSupportedExistingAndNotExistingResources() throws ResourceException {
        // Tune resources - namespace and deployment exist already
        when(namespaceResource.exists()).thenReturn(true);
        when(deploymentResource.exists()).thenReturn(true);

        // Update
        KubernetesUpdateStrategy updateStrategy = new KubernetesUpdateStrategy();
        updateStrategy.deploy(namespaceResource, resources);

        // Verify
        // Don't touch the namespace
        verify(namespaceResource, times(0)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(1)).exists();
        verify(namespaceResource, times(0)).delete();

        // Update the deployment
        verify(deploymentResource, times(0)).create();
        verify(deploymentResource, times(1)).update();
        verify(deploymentResource, times(1)).exists();
        verify(deploymentResource, times(0)).delete();

        // Create the service
        verify(serviceResource, times(1)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(1)).exists();
        verify(serviceResource, times(0)).delete();
    }

    @Test
    public void testDeleteWithExistingResources() throws ResourceException {
        // Tune resources - namespace and deployment exist already
        when(deploymentResource.exists()).thenReturn(true);
        when(serviceResource.exists()).thenReturn(true);
        when(namespaceResource.exists()).thenReturn(true);

        // Delete
        KubernetesUpdateStrategy updateStrategy = new KubernetesUpdateStrategy();
        updateStrategy.delete(namespaceResource, resources);

        // Verify
        // Ignore the namespace
        verify(namespaceResource, times(0)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(0)).exists();
        verify(namespaceResource, times(0)).delete();

        // Delete the deployment
        verify(deploymentResource, times(0)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(1)).exists();
        verify(deploymentResource, times(1)).delete();

        // Delete the service
        verify(serviceResource, times(0)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(1)).exists();
        verify(serviceResource, times(1)).delete();
    }

    @Test
    public void testDeleteWithExistingAndNotExistingResources() throws ResourceException {
        // Tune resources - namespace and deployment exist already
        when(serviceResource.exists()).thenReturn(true);
        when(namespaceResource.exists()).thenReturn(true);

        // Delete
        KubernetesUpdateStrategy updateStrategy = new KubernetesUpdateStrategy();
        updateStrategy.delete(namespaceResource, resources);

        // Verify
        // Ignore the namespace
        verify(namespaceResource, times(0)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(0)).exists();
        verify(namespaceResource, times(0)).delete();

        // Ignore the deployment
        verify(deploymentResource, times(0)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(1)).exists();
        verify(deploymentResource, times(0)).delete();

        // Delete the service
        verify(serviceResource, times(0)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(1)).exists();
        verify(serviceResource, times(1)).delete();
    }

    @Test
    public void testDeleteWithNotExistingResources() throws ResourceException {
        // Delete
        KubernetesUpdateStrategy updateStrategy = new KubernetesUpdateStrategy();
        updateStrategy.delete(namespaceResource, resources);

        // Verify
        // Ignore the namespace
        verify(namespaceResource, times(0)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(0)).exists();
        verify(namespaceResource, times(0)).delete();

        // Ignore the deployment
        verify(deploymentResource, times(0)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(1)).exists();
        verify(deploymentResource, times(0)).delete();

        // Delete the service
        verify(serviceResource, times(0)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(1)).exists();
        verify(serviceResource, times(0)).delete();
    }

    private void assertNotSupported(KubernetesResource resource) {
        boolean exceptionThrown = false;

        // Add unsupported resource
        resources.add(resource);

        // Try to deploy and assert exception
        KubernetesUpdateStrategy updateStrategy = new KubernetesUpdateStrategy();
        try {
            updateStrategy.deploy(namespaceResource, resources);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_RESOURCE_SUPPORTS_NO_UPDATES", resource), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
