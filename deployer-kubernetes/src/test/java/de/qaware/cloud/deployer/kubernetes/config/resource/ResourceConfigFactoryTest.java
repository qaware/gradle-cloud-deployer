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
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourceConfigFactoryTest extends TestCase {

    public void testCreateConfigsWithExistingJsonFile() throws ResourceConfigException, IOException {
        String testFile = "/config/resource/pod.json";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        List<KubernetesResourceConfig> configs = resourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(1, configs.size());

        // Check config
        KubernetesResourceConfig config = configs.get(0);
        assertEquals("nginx-mysql", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(testFile), config.getContent());
    }

    public void testCreateConfigsWithExistingYamlFile() throws ResourceConfigException, IOException {
        String testFile = "/config/resource/service.yml";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        List<KubernetesResourceConfig> configs = resourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(1, configs.size());

        // Check config
        KubernetesResourceConfig config = configs.get(0);
        assertEquals("zwitscher-eureka", config.getResourceId());
        assertEquals("Service", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.YAML, config.getContentType());
        assertEquals(FileUtil.readFileContent(testFile), config.getContent());
    }

    public void testCreateConfigsWithMultipleExistingJsonFiles() throws ResourceConfigException, IOException {
        String testFile1 = "/config/resource/pod.json";
        String testFile2 = "/config/resource/pod2.json";

        // Create config
        File file1 = new File(this.getClass().getResource(testFile1).getPath());
        File file2 = new File(this.getClass().getResource(testFile2).getPath());
        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        List<KubernetesResourceConfig> configs = resourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(2, configs.size());

        // Check configs
        KubernetesResourceConfig config = configs.get(0);
        assertEquals("nginx-mysql", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(testFile1), config.getContent());

        config = configs.get(1);
        assertEquals("nginx-mysql2", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(testFile2), config.getContent());
    }

    public void testCreateConfigsWithMultipleResourcesInOneJsonFile() throws ResourceConfigException, IOException {
        String testFile = "/config/resource/pods.json";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        List<KubernetesResourceConfig> configs = resourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(2, configs.size());

        // Load single configs
        String pod1Filename = "/config/resource/pod.json";
        String pod2Filename = "/config/resource/pod2.json";
        File pod1 = new File(this.getClass().getResource(pod1Filename).getPath());
        File pod2 = new File(this.getClass().getResource(pod2Filename).getPath());

        // Check configs
        KubernetesResourceConfig config = configs.get(0);
        assertEquals("nginx-mysql", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(pod1), config.getContent());

        config = configs.get(1);
        assertEquals("nginx-mysql2", config.getResourceId());
        assertEquals("Pod", config.getResourceType());
        assertEquals("v1", config.getResourceVersion());
        assertEquals(ContentType.JSON, config.getContentType());
        assertEquals(FileUtil.readFileContent(pod2), config.getContent());
    }

    public void testCreateConfigsWithExistingAndNonExistingFiles() {
        File nonExistingFile = new File("/config/resource/service-non-existing.yml");
        File existingFile = new File("/config/resource/service.yml");
        assertExceptionOnCreation(nonExistingFile, existingFile);
    }

    public void testCreateConfigsWithExistingAndEmptyFiles() {
        String emptyFile = "/config/resource/service-empty.yml";
        String validFile = "/config/resource/service.yml";
        assertExceptionOnCreation(emptyFile, validFile);
    }

    public void testCreateConfigsWithExistingEmptyFiles() {
        String testFile = "/config/resource/service-empty.yml";
        assertExceptionOnCreation(testFile);
        testFile = "/config/resource/service-empty.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithNonExistingFile() {
        File testFile = new File("/config/resource/service-non-existing.yml");
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithExistingUnsupportedFiles() {
        String testFile = "/config/resource/service.xml";
        assertExceptionOnCreation(testFile);
        testFile = "/config/resource/service";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceVersionFromJson() {
        String testFile = "/config/resource/pod-missing-version.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceTypeFromJson() {
        String testFile = "/config/resource/pod-missing-type.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceIdFromJson() {
        String testFile = "/config/resource/pod-missing-id.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceVersionFromYaml() {
        String testFile = "/config/resource/service-missing-version.yml";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceTypeFromYaml() {
        String testFile = "/config/resource/service-missing-type.yml";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithMissingResourceIdFromYaml() {
        String testFile = "/config/resource/service-missing-id.yml";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithInvalidJson() {
        String testFile = "/config/resource/pod-invalid.json";
        assertExceptionOnCreation(testFile);
    }

    public void testCreateConfigsWithInvalidYaml() {
        String testFile = "/config/resource/service-invalid.yml";
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
            KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
            resourceConfigFactory.createConfigs(files);
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
            KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
            resourceConfigFactory.createConfigs(files);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
        }

        // Check if exception was thrown
        assertTrue(exceptionThrown);
    }
}
