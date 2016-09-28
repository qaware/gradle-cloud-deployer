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
package de.qaware.cloud.deployer.marathon.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.marathon.test.BaseMarathonStrategyTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MarathonUpdateStrategyTest extends BaseMarathonStrategyTest {

    @Test
    public void testDeployWithNotExistingResources() throws ResourceException {
        // Update
        MarathonUpdateStrategy updateStrategy = new MarathonUpdateStrategy();
        updateStrategy.deploy(resources);

        // Create the app
        verify(appResource, times(1)).create();
        verify(appResource, times(0)).update();
        verify(appResource, times(1)).exists();
        verify(appResource, times(0)).delete();

        // Create the group
        verify(groupResource, times(1)).create();
        verify(groupResource, times(0)).update();
        verify(groupResource, times(1)).exists();
        verify(groupResource, times(0)).delete();
    }

    @Test
    public void testDeployWithExistingResources() throws ResourceException {
        // Tune resources - all exist already
        when(appResource.exists()).thenReturn(true);
        when(groupResource.exists()).thenReturn(true);

        // Update
        MarathonUpdateStrategy updateStrategy = new MarathonUpdateStrategy();
        updateStrategy.deploy(resources);

        // Update the app
        verify(appResource, times(0)).create();
        verify(appResource, times(1)).update();
        verify(appResource, times(1)).exists();
        verify(appResource, times(0)).delete();

        // Update the group
        verify(groupResource, times(0)).create();
        verify(groupResource, times(1)).update();
        verify(groupResource, times(1)).exists();
        verify(groupResource, times(0)).delete();
    }

    @Test
    public void testDeployWithExistingAndNotExistingResources() throws ResourceException {
        // Tune resources - app exists already
        when(appResource.exists()).thenReturn(true);

        // Update
        MarathonUpdateStrategy updateStrategy = new MarathonUpdateStrategy();
        updateStrategy.deploy(resources);

        // Update the app
        verify(appResource, times(0)).create();
        verify(appResource, times(1)).update();
        verify(appResource, times(1)).exists();
        verify(appResource, times(0)).delete();

        // Create the group
        verify(groupResource, times(1)).create();
        verify(groupResource, times(0)).update();
        verify(groupResource, times(1)).exists();
        verify(groupResource, times(0)).delete();
    }

    @Test
    public void testDeleteWithNotExistingResources() throws ResourceException {
        // Delete
        MarathonUpdateStrategy strategy = new MarathonUpdateStrategy();
        strategy.delete(resources);

        // Verify
        // Ignore the app
        verify(appResource, times(0)).create();
        verify(appResource, times(0)).update();
        verify(appResource, times(1)).exists();
        verify(appResource, times(0)).delete();

        // Ignore the group
        verify(groupResource, times(0)).create();
        verify(groupResource, times(0)).update();
        verify(groupResource, times(1)).exists();
        verify(groupResource, times(0)).delete();
    }

    @Test
    public void testDeleteWithExistingResources() throws ResourceException {
        // Tune resources
        when(appResource.exists()).thenReturn(true);
        when(groupResource.exists()).thenReturn(true);

        // Delete
        MarathonUpdateStrategy strategy = new MarathonUpdateStrategy();
        strategy.delete(resources);

        // Verify
        // Delete the app
        verify(appResource, times(0)).create();
        verify(appResource, times(0)).update();
        verify(appResource, times(1)).exists();
        verify(appResource, times(1)).delete();

        // Delete the group
        verify(groupResource, times(0)).create();
        verify(groupResource, times(0)).update();
        verify(groupResource, times(1)).exists();
        verify(groupResource, times(1)).delete();
    }
}
