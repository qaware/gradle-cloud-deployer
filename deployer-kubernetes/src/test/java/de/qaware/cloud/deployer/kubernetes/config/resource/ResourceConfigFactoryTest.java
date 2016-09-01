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
package de.qaware.cloud.deployer.kubernetes.config.resource;

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.test.FileUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourceConfigFactoryTest extends TestCase {

    public void testCreateConfigsWithExistingJsonFile() throws ResourceConfigException, IOException {
        String testFile = "/pod.json";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        List<ResourceConfig> configs = ResourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(1, configs.size());

        // Check config
        ResourceConfig config = configs.get(0);
        assertEquals("nginx-mysql", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFile(testFile), config.getContent());
    }

    public void testCreateConfigsWithExistingYamlFile() throws ResourceConfigException, IOException {
        String testFile = "/service.yml";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        List<ResourceConfig> configs = ResourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(1, configs.size());

        // Check config
        ResourceConfig config = configs.get(0);
        assertEquals("zwitscher-eureka", config.getResourceId());
        assertEquals("Service", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.YAML, config.getContentType());
        assertEquals(FileUtil.readFile(testFile), config.getContent());
    }

    public void testCreateConfigsWithMultipleExistingJsonFiles() throws ResourceConfigException, IOException {
        String testFile1 = "/pod.json";
        String testFile2 = "/pod2.json";

        // Create config
        File file1 = new File(this.getClass().getResource(testFile1).getPath());
        File file2 = new File(this.getClass().getResource(testFile2).getPath());
        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);
        List<ResourceConfig> configs = ResourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(2, configs.size());

        // Check configs
        ResourceConfig config = configs.get(0);
        assertEquals("nginx-mysql", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFile(testFile1), config.getContent());

        config = configs.get(1);
        assertEquals("nginx-mysql2", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFile(testFile2), config.getContent());
    }

    public void testCreateConfigsWithMultipleResourcesInOneJsonFile() throws ResourceConfigException, IOException {
        String testFile = "/pods.json";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        List<ResourceConfig> configs = ResourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(2, configs.size());

        // Load single configs
        String pod1Filename = "/pod.json";
        String pod2Filename = "/pod2.json";
        File pod1 = new File(this.getClass().getResource(pod1Filename).getPath());
        File pod2 = new File(this.getClass().getResource(pod2Filename).getPath());

        // Check configs
        ResourceConfig config = configs.get(0);
        assertEquals("nginx-mysql", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFile(pod1), config.getContent());

        config = configs.get(1);
        assertEquals("nginx-mysql2", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFile(pod2), config.getContent());
    }

    public void testCreateConfigsWithExistingAndNonExistingFiles() {
        File nonExistingFile = new File("/service-non-existing.yml");
        File existingFile = new File("/service.yml");
        assertExceptionOnCreation(nonExistingFile, existingFile);
    }

    public void testCreateConfigsWithExistingAndEmptyFiles() {
        String emptyFile = "/service-empty.yml";
        String validFile = "/service.yml";
        assertExceptionOnCreation(emptyFile, validFile);
    }

    public void testCreateConfigsWithExistingEmptyFiles() {
        String testFile = "/service-empty.yml";
        assertExceptionOnCreation(testFile);
        testFile = "/service-empty.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithNonExistingFile() {
        File testFile = new File("/service-non-existing.yml");
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithExistingUnsupportedFiles() {
        String testFile = "/service.xml";
        assertExceptionOnCreation(testFile);
        testFile = "/service";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceVersionFromJson() {
        String testFile = "/pod-missing-version.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceTypeFromJson() {
        String testFile = "/pod-missing-type.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceIdFromJson() {
        String testFile = "/pod-missing-id.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceVersionFromYaml() {
        String testFile = "/service-missing-version.yml";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceTypeFromYaml() {
        String testFile = "/service-missing-type.yml";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceIdFromYaml() {
        String testFile = "/service-missing-id.yml";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithInvalidJson() {
        String testFile = "/pod-invalid.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithInvalidYaml() {
        String testFile = "/service-invalid.yml";
        assertExceptionOnCreation(testFile);
    }

    private void assertExceptionOnCreation(String filename) {
        File file = new File(this.getClass().getResource(filename).getPath());
        assertExceptionOnCreation(file);
    }

    private void assertExceptionOnCreation(File file) {
        boolean exceptionThrown = false;

        // Create config
        List<File> files = new ArrayList<>();
        files.add(file);
        try {
            ResourceConfigFactory.createConfigs(files);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
        }

        // Check if exception was thrown
        assertTrue(exceptionThrown);
    }

    private void assertExceptionOnCreation(String filename1, String filename2) {
        File file1 = new File(this.getClass().getResource(filename1).getPath());
        File file2 = new File(this.getClass().getResource(filename2).getPath());
        assertExceptionOnCreation(file1, file2);
    }

    private void assertExceptionOnCreation(File file1, File file2) {
        boolean exceptionThrown = false;

        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        try {
            ResourceConfigFactory.createConfigs(files);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
        }

        // Check if exception was thrown
        assertTrue(exceptionThrown);
    }
}
