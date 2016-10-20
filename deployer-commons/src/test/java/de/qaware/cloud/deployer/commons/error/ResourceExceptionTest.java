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
public class ResourceExceptionTest {

    @Test
    public void testWithMessage() {
        String message = "message";
        ResourceException resourceException = new ResourceException(message);
        assertEquals(message, resourceException.getMessage());
    }

    @Test
    public void testWithCause() {
        Throwable cause = mock(Throwable.class);
        ResourceException resourceException = new ResourceException(cause);
        assertEquals(cause, resourceException.getCause());
    }

    @Test
    public void testWithMessageAndCause() {
        Throwable cause = mock(Throwable.class);
        String message = "message";
        ResourceException resourceException = new ResourceException(message, cause);
        assertEquals(message, resourceException.getMessage());
        assertEquals(cause, resourceException.getCause());
    }
}
