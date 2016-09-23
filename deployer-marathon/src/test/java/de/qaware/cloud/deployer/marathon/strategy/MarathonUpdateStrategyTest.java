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
import de.qaware.cloud.deployer.marathon.resource.app.AppResource;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import de.qaware.cloud.deployer.marathon.resource.group.GroupResource;
import junit.framework.TestCase;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class MarathonUpdateStrategyTest extends TestCase {

    private AppResource appResource;
    private GroupResource groupResource;
    private List<MarathonResource> resources;

    @Before
    public void setUp() throws Exception {
        appResource = mock(AppResource.class);
        groupResource = mock(GroupResource.class);

        resources = new ArrayList<>();
        resources.add(appResource);
        resources.add(groupResource);
    }

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
}
