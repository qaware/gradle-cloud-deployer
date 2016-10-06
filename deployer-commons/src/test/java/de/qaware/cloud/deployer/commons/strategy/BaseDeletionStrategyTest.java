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
package de.qaware.cloud.deployer.commons.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.Resource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author sjahreis
 */
public class BaseDeletionStrategyTest {

    private List<Resource> resources;
    private Resource resource1;
    private Resource resource2;

    @Before
    public void setup() {
        resource1 = mock(Resource.class);
        resource2 = mock(Resource.class);

        resources = new ArrayList<>();
        resources.add(resource1);
        resources.add(resource2);
    }

    @Test
    public void testDeleteExistingResources() throws ResourceException {
        when(resource1.exists()).thenReturn(true);
        when(resource2.exists()).thenReturn(true);

        BaseDeletionStrategy strategy = spy(BaseDeletionStrategy.class);
        strategy.deleteResources(resources);

        verify(resource1, times(0)).create();
        verify(resource1, times(1)).exists();
        verify(resource1, times(1)).delete();
        verify(resource1, times(0)).update();


        verify(resource2, times(0)).create();
        verify(resource2, times(1)).exists();
        verify(resource2, times(1)).delete();
        verify(resource2, times(0)).update();
    }

    @Test
    public void testDeleteNotExistingResources() throws ResourceException {
        BaseDeletionStrategy strategy = spy(BaseDeletionStrategy.class);
        strategy.deleteResources(resources);

        verify(resource1, times(0)).create();
        verify(resource1, times(1)).exists();
        verify(resource1, times(0)).delete();
        verify(resource1, times(0)).update();


        verify(resource2, times(0)).create();
        verify(resource2, times(1)).exists();
        verify(resource2, times(0)).delete();
        verify(resource2, times(0)).update();
    }

    @Test
    public void testDeletePartialExistingResources() throws ResourceException {
        when(resource1.exists()).thenReturn(true);

        BaseDeletionStrategy strategy = spy(BaseDeletionStrategy.class);
        strategy.deleteResources(resources);

        verify(resource1, times(0)).create();
        verify(resource1, times(1)).exists();
        verify(resource1, times(1)).delete();
        verify(resource1, times(0)).update();


        verify(resource2, times(0)).create();
        verify(resource2, times(1)).exists();
        verify(resource2, times(0)).delete();
        verify(resource2, times(0)).update();
    }
}
