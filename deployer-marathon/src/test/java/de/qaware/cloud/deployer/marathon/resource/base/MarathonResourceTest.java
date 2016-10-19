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
package de.qaware.cloud.deployer.marathon.resource.base;

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.resource.group.GroupResource;
import okhttp3.MediaType;
import org.junit.Test;

import static de.qaware.cloud.deployer.marathon.logging.MarathonMessageBundle.MARATHON_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class MarathonResourceTest {

    @Test
    public void testCreateMediaTypeForJson() throws ResourceException {
        MarathonResourceConfig resourceConfig = mock(MarathonResourceConfig.class);
        when(resourceConfig.getContentType()).thenReturn(ContentType.JSON);
        MarathonResource marathonResource = new GroupResource(resourceConfig, mock(ClientFactory.class));
        MediaType mediaType = marathonResource.createMediaType();
        assertEquals(MediaType.parse("application/json"), mediaType);
    }

    @Test
    public void testCreateMediaTypeForUnknownMediaType() {
        String filename = "testFile";
        MarathonResourceConfig resourceConfig = mock(MarathonResourceConfig.class);
        when(resourceConfig.getContentType()).thenReturn(ContentType.YAML);
        when(resourceConfig.getFilename()).thenReturn(filename);
        MarathonResource marathonResource = new GroupResource(resourceConfig, mock(ClientFactory.class));

        boolean exceptionThrown = false;
        try {
            marathonResource.createMediaType();
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_ERROR_UNKNOWN_CONTENT_TYPE", filename), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
