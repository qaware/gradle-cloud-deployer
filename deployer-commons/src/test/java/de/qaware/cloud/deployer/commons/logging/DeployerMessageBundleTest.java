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
package de.qaware.cloud.deployer.commons.logging;

import de.qaware.cloud.deployer.commons.resource.BaseResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class DeployerMessageBundleTest {

    private DeployerMessageBundle messageBundle = new DeployerMessageBundle("commons-log-messages-test");

    @Test
    public void testGetMessage() {
        String message = messageBundle.getMessage("TEST");
        assertEquals("test", message);
    }

    @Test
    public void testGetMessageWithPlaceholder() {
        String message = messageBundle.getMessage("TEST_WITH_PLACEHOLDER", "test");
        assertEquals("test test", message);
    }

    @Test
    public void testGetMessageWithTwoPlaceholders() {
        String message = messageBundle.getMessage("TEST_WITH_TWO_PLACEHOLDERS", "test", "test");
        assertEquals("test test test test", message);
    }

    @Test
    public void testGetMessageWithPlaceholderAndApostrophes() {
        String message = messageBundle.getMessage("TEST_WITH_PLACEHOLDER_AND_APOSTROPHES", "test");
        assertEquals("'test'", message);
    }

    @Test
    public void testGetMessageWithObject() {
        BaseResource resource = mock(BaseResource.class);
        when(resource.toString()).thenReturn("test");

        String message = messageBundle.getMessage("TEST_WITH_PLACEHOLDER_AND_APOSTROPHES", resource);
        assertEquals("'test'", message);
    }
}
