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
package de.qaware.cloud.deployer.commons.config.util;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.junit.Test;

import java.io.File;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author sjahreis
 */
public class FileUtilTest {

    private static final String TEST_FILE = "/de/qaware/cloud/deployer/commons/config/util/app.json";
    private static final String TEST_FILE_UNTRIMMED = "/de/qaware/cloud/deployer/commons/config/util/app-untrimmed.json";

    @Test
    public void testReadFileContentWithFilename() throws ResourceConfigException {
        String expectedFileContent = getTestFileContent();
        String fileContent = FileUtil.readFileContent(TEST_FILE);
        assertEquals(expectedFileContent, fileContent);
    }

    @Test
    public void testReadFileContentWithNotExistingFilename() {
        String filename = "bla.blub";
        assertExceptionOnReadFileContentWithFilename(filename, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_MISSING_FILE", filename));
    }

    @Test
    public void testReadFileContentWithNullFilename() {
        String filename = null;
        assertExceptionOnReadFileContentWithFilename(filename, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_INVALID_FILENAME"));
    }

    @Test
    public void testReadFileContentWithEmptyFilename() {
        String filename = "";
        assertExceptionOnReadFileContentWithFilename(filename, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_INVALID_FILENAME"));
    }

    @Test
    public void testReadFileContentWithFile() throws ResourceConfigException {
        File file = new File(this.getClass().getResource(TEST_FILE).getPath());
        String expectedContent = getTestFileContent();
        String content = FileUtil.readFileContent(file);
        assertEquals(expectedContent, content);
    }

    @Test
    public void testReadFileContentWithNotExistingFile() {
        File file = new File("bla.blub");
        assertExceptionOnReadFileContentWithFile(file, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_MISSING_FILE", file.getName()));
    }

    @Test
    public void testReadFileContentWithNullFile() {
        assertExceptionOnReadFileContentWithFile(null, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_INVALID_FILENAME"));
    }

    @Test
    public void testReadFileContentsTrimFunctionality() throws ResourceConfigException {
        File file = new File(this.getClass().getResource(TEST_FILE_UNTRIMMED).getPath());
        String expectedContent = getTestFileContent();
        String content = FileUtil.readFileContent(file);
        assertEquals(expectedContent, content);
    }

    private void assertExceptionOnReadFileContentWithFilename(String filename, String message) {
        boolean exceptionThrown = false;
        try {
            FileUtil.readFileContent(filename);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void assertExceptionOnReadFileContentWithFile(File file, String message) {
        boolean exceptionThrown = false;
        try {
            FileUtil.readFileContent(file);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private String getTestFileContent() {
        String lineSeparator = System.getProperty("line.separator");
        String expectedFileContent = "{\n" +
                "  \"id\": \"zwitscher-eureka\",\n" +
                "  \"container\": {\n" +
                "    \"type\": \"DOCKER\"\n" +
                "  }\n" +
                "}";
        return expectedFileContent.replace("\n", lineSeparator);
    }
}
