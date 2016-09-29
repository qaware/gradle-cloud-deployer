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
package de.qaware.cloud.deployer.commons.config.resource;


import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BaseResourceConfigFactoryTest {

    @Test
    public void testCreateConfigs() throws ResourceConfigException {
        File file1 = mock(File.class);
        File file2 = mock(File.class);
        List<File> resources = new ArrayList<>();
        resources.add(file1);
        resources.add(file2);
        BaseResourceConfigFactory testFactory = spy(BaseResourceConfigFactory.class);
        doReturn(mock(BaseResourceConfig.class)).when(testFactory).createConfig(file1);
        doReturn(mock(BaseResourceConfig.class)).when(testFactory).createConfig(file2);

        testFactory.createConfigs(resources);
        verify(testFactory).createConfig(file1);
        verify(testFactory, times(1)).createConfig(file1);
        verify(testFactory).createConfig(file2);
        verify(testFactory, times(1)).createConfig(file2);
    }

    @Test
    public void testRetrieveContentTypeWithJson() throws ResourceConfigException {
        File file = mock(File.class);
        when(file.getName()).thenReturn("bla.json");
        BaseResourceConfigFactory testFactory = spy(BaseResourceConfigFactory.class);
        assertEquals(ContentType.JSON, testFactory.retrieveContentType(file));
    }

    @Test
    public void testRetrieveContentTypeWithYAML() throws ResourceConfigException {
        File file = mock(File.class);
        when(file.getName()).thenReturn("bla.yml");
        BaseResourceConfigFactory testFactory = spy(BaseResourceConfigFactory.class);
        assertEquals(ContentType.YAML, testFactory.retrieveContentType(file));
    }

    @Test
    public void testRetrieveContentTypeWithUnsupportedContentType() throws ResourceConfigException {
        File file = mock(File.class);
        when(file.getName()).thenReturn("bla.unsupported");
        BaseResourceConfigFactory testFactory = spy(BaseResourceConfigFactory.class);
        boolean exceptionThrown = false;
        try {
            testFactory.retrieveContentType(file);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNKNOWN_CONTENT_TYPE", file.getName()), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
