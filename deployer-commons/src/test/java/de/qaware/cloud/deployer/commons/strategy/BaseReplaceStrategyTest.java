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
import de.qaware.cloud.deployer.commons.test.BaseStrategyTest;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author sjahreis
 */
public class BaseReplaceStrategyTest extends BaseStrategyTest {

    @Test
    public void testReplaceExistingResources() throws ResourceException {
        when(resource1.exists()).thenReturn(true);
        when(resource2.exists()).thenReturn(true);

        BaseReplaceStrategy strategy = spy(BaseReplaceStrategy.class);
        strategy.replaceResources(resources);

        verify(resource1, times(1)).create();
        verify(resource1, times(1)).exists();
        verify(resource1, times(1)).delete();
        verify(resource1, times(0)).update();


        verify(resource2, times(1)).create();
        verify(resource2, times(1)).exists();
        verify(resource2, times(1)).delete();
        verify(resource2, times(0)).update();
    }

    @Test
    public void testReplaceNotExistingResources() throws ResourceException {
        BaseReplaceStrategy strategy = spy(BaseReplaceStrategy.class);
        strategy.replaceResources(resources);

        verify(resource1, times(1)).create();
        verify(resource1, times(1)).exists();
        verify(resource1, times(0)).delete();
        verify(resource1, times(0)).update();


        verify(resource2, times(1)).create();
        verify(resource2, times(1)).exists();
        verify(resource2, times(0)).delete();
        verify(resource2, times(0)).update();
    }

    @Test
    public void testReplacePartialExistingResources() throws ResourceException {
        when(resource1.exists()).thenReturn(true);

        BaseReplaceStrategy strategy = spy(BaseReplaceStrategy.class);
        strategy.replaceResources(resources);

        verify(resource1, times(1)).create();
        verify(resource1, times(1)).exists();
        verify(resource1, times(1)).delete();
        verify(resource1, times(0)).update();


        verify(resource2, times(1)).create();
        verify(resource2, times(1)).exists();
        verify(resource2, times(0)).delete();
        verify(resource2, times(0)).update();
    }
}
