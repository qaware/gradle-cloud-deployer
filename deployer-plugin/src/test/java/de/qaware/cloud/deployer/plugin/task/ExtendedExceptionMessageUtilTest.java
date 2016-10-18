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
package de.qaware.cloud.deployer.plugin.task;

import de.qaware.cloud.deployer.plugin.environment.Environment;
import org.junit.Test;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static de.qaware.cloud.deployer.plugin.task.ExtendedExceptionMessageUtil.createExtendedMessage;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class ExtendedExceptionMessageUtilTest {

    @Test
    public void testCreateExtendedMessage() {
        Environment environment = mock(Environment.class);
        when(environment.getId()).thenReturn("test-env");

        String originalMessage = "hello";
        String expectedMessage = originalMessage + " " + PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_SINGLE_ENVIRONMENT", environment.getId());
        String extendedMessage = createExtendedMessage(environment, originalMessage);
        assertEquals(expectedMessage, extendedMessage);
    }

    @Test
    public void testCreateExtendedMessageMultiline() {
        Environment environment = mock(Environment.class);
        when(environment.getId()).thenReturn("test-env");

        String originalMessage = "hello\nhello";
        String expectedMessage = originalMessage + "\n" + PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_SINGLE_ENVIRONMENT", environment.getId());
        String extendedMessage = createExtendedMessage(environment, originalMessage);
        assertEquals(expectedMessage, extendedMessage);
    }
}
