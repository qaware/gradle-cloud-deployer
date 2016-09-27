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
import de.qaware.cloud.deployer.kubernetes.test.BaseKubernetesStrategyTest;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class KubernetesResetStrategyTest extends BaseKubernetesStrategyTest {

    @Test
    public void testDeployWithNotExistingNamespace() throws ResourceException {
        // Reset
        KubernetesResetStrategy strategy = new KubernetesResetStrategy();
        strategy.deploy(namespaceResource, resources);

        // Verify
        // Create the namespace
        verify(namespaceResource, times(1)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(2)).exists();
        verify(namespaceResource, times(0)).delete();

        // Create the deployment
        verify(deploymentResource, times(1)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(0)).exists();
        verify(deploymentResource, times(0)).delete();

        // Create the service
        verify(serviceResource, times(1)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(0)).exists();
        verify(serviceResource, times(0)).delete();
    }

    @Test
    public void testDeployWithExistingNamespace() throws ResourceException {
        final boolean[] namespaceExists = {true};
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                namespaceExists[0] = true;
                return null;
            }
        }).when(namespaceResource).create();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                namespaceExists[0] = false;
                return null;
            }
        }).when(namespaceResource).delete();
        when(namespaceResource.exists()).then(invocation -> namespaceExists[0]);

        // Reset
        KubernetesResetStrategy strategy = new KubernetesResetStrategy();
        strategy.deploy(namespaceResource, resources);

        // Verify
        // Reset the namespace
        verify(namespaceResource, times(1)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(2)).exists();
        verify(namespaceResource, times(1)).delete();

        // Create the deployment
        verify(deploymentResource, times(1)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(0)).exists();
        verify(deploymentResource, times(0)).delete();

        // Create the service
        verify(serviceResource, times(1)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(0)).exists();
        verify(serviceResource, times(0)).delete();
    }

    @Test
    public void testDeleteWithNotExistingNamespace() throws ResourceException {
        // Delete
        KubernetesResetStrategy strategy = new KubernetesResetStrategy();
        strategy.delete(namespaceResource, resources);

        // Verify
        // Reset the namespace
        verify(namespaceResource, times(0)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(1)).exists();
        verify(namespaceResource, times(0)).delete();

        // Create the deployment
        verify(deploymentResource, times(0)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(0)).exists();
        verify(deploymentResource, times(0)).delete();

        // Create the service
        verify(serviceResource, times(0)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(0)).exists();
        verify(serviceResource, times(0)).delete();
    }

    @Test
    public void testDeleteWithExistingNamespace() throws ResourceException {
        // Tune resources - namespace exists already
        when(namespaceResource.exists()).thenReturn(true);

        // Delete
        KubernetesResetStrategy strategy = new KubernetesResetStrategy();
        strategy.delete(namespaceResource, resources);

        // Verify
        // Reset the namespace
        verify(namespaceResource, times(0)).create();
        verify(namespaceResource, times(0)).update();
        verify(namespaceResource, times(1)).exists();
        verify(namespaceResource, times(1)).delete();

        // Create the deployment
        verify(deploymentResource, times(0)).create();
        verify(deploymentResource, times(0)).update();
        verify(deploymentResource, times(0)).exists();
        verify(deploymentResource, times(0)).delete();

        // Create the service
        verify(serviceResource, times(0)).create();
        verify(serviceResource, times(0)).update();
        verify(serviceResource, times(0)).exists();
        verify(serviceResource, times(0)).delete();
    }
}
