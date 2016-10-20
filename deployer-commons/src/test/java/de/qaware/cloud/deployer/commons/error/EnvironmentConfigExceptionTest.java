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
package de.qaware.cloud.deployer.commons.error;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author sjahreis
 */
public class EnvironmentConfigExceptionTest {

    @Test
    public void testWithMessage() {
        String message = "message";
        EnvironmentConfigException environmentConfigException = new EnvironmentConfigException(message);
        assertEquals(message, environmentConfigException.getMessage());
    }

    @Test
    public void testWithMessageAndCause() {
        Throwable cause = mock(Throwable.class);
        String message = "message";
        EnvironmentConfigException environmentConfigException = new EnvironmentConfigException(message, cause);
        assertEquals(message, environmentConfigException.getMessage());
        assertEquals(cause, environmentConfigException.getCause());
    }
}
