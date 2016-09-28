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
package de.qaware.cloud.deployer.marathon.config.resource;

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.qaware.cloud.deployer.marathon.logging.MarathonMessageBundle.MARATHON_MESSAGE_BUNDLE;

public class MarathonResourceConfigFactoryTest extends TestCase {

    public void testCreateConfigsWithExistingJsonFile() throws ResourceConfigException {
        String testFile = "/de/qaware/cloud/deployer/marathon/config/resource/app.json";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        MarathonResourceConfigFactory factory = new MarathonResourceConfigFactory();
        List<MarathonResourceConfig> configs = factory.createConfigs(files);

        // Check createConfigs result
        assertEquals(1, configs.size());

        // Check config
        MarathonResourceConfig config = configs.get(0);
        assertEquals("zwitscher-eureka", config.getResourceId());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(testFile), config.getContent());
    }

    public void testCreateConfigsWithExistingYamlFile() {
        String testFile = "/de/qaware/cloud/deployer/marathon/config/resource/app.yml";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        MarathonResourceConfigFactory factory = new MarathonResourceConfigFactory();

        // Assert exception
        boolean exceptionThrown = false;
        try {
            factory.createConfigs(files);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_ERROR_UNKNOWN_CONTENT_TYPE", "app.yml"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    public void testCreateConfigsWithEmptyFile() {
        String testFile = "/de/qaware/cloud/deployer/marathon/config/resource/empty-app.json";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        MarathonResourceConfigFactory factory = new MarathonResourceConfigFactory();

        // Assert exception
        boolean exceptionThrown = false;
        try {
            factory.createConfigs(files);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_ERROR_EMPTY_CONFIG", "empty-app.json"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    public void testCreateConfigsWithNonExistingFile() {
        String testFile = "non-existing-app.json";

        // Create config
        File file = new File(testFile);
        List<File> files = new ArrayList<>();
        files.add(file);
        MarathonResourceConfigFactory factory = new MarathonResourceConfigFactory();

        // Assert exception
        boolean exceptionThrown = false;
        try {
            factory.createConfigs(files);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals("File 'non-existing-app.json' does not exist", e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    public void testCreateConfigsWithMultipleJsonFiles() throws ResourceConfigException {
        String testFile1 = "/de/qaware/cloud/deployer/marathon/config/resource/app.json";
        String testFile2 = "/de/qaware/cloud/deployer/marathon/config/resource/group.json";

        // Create config
        File file1 = new File(this.getClass().getResource(testFile1).getPath());
        File file2 = new File(this.getClass().getResource(testFile2).getPath());
        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);
        MarathonResourceConfigFactory factory = new MarathonResourceConfigFactory();
        List<MarathonResourceConfig> configs = factory.createConfigs(files);

        // Check createConfigs result
        assertEquals(2, configs.size());

        // Check config1
        MarathonResourceConfig config = configs.get(0);
        assertEquals("zwitscher-eureka", config.getResourceId());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(testFile1), config.getContent());

        config = configs.get(1);
        assertEquals("group-test", config.getResourceId());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(testFile2), config.getContent());
    }
}
