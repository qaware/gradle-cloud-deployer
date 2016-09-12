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

import static de.qaware.cloud.deployer.commons.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

public class KubernetesResourceConfigFactoryTest extends TestCase {

    public void testCreateConfigsWithExistingJsonFile() throws ResourceConfigException, IOException {
        String testFile = "/pod/pod.json";

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
        String testFile = "/service/service.yml";

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
        String testFile1 = "/pod/pod.json";
        String testFile2 = "/pod/pod2.json";

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
        String testFile = "/pod/pods.json";

        // Create config
        File file = new File(this.getClass().getResource(testFile).getPath());
        List<File> files = new ArrayList<>();
        files.add(file);
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        List<KubernetesResourceConfig> configs = resourceConfigFactory.createConfigs(files);

        // Check createConfigs result
        assertEquals(2, configs.size());

        // Load single configs
        String pod1Filename = "/pod/pod.json";
        String pod2Filename = "/pod/pod2.json";
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
        File nonExistingFile = new File("/service-non-existing.yml");
        File existingFile = new File("/service/service.yml");
        assertExceptionOnCreation(nonExistingFile, existingFile, "File '\\service-non-existing.yml' does not exist");
    }

    public void testCreateConfigsWithExistingAndEmptyFiles() {
        String emptyFile = "/service/service-empty.yml";
        String validFile = "/service/service.yml";
        assertExceptionOnCreation2(emptyFile, validFile, KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_EMPTY_CONFIG", "service-empty.yml"));
    }

    public void testCreateConfigsWithExistingEmptyFiles() {
        String testFile = "/service/service-empty.yml";
        assertExceptionOnCreation(testFile, KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_EMPTY_CONFIG", "service-empty.yml"));
        testFile = "/service/service-empty.json";
        assertExceptionOnCreation(testFile, KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_EMPTY_CONFIG", "service-empty.json"));
    }

    public void testCreateConfigsWithNonExistingFile() {
        File testFile = new File("/service/service-non-existing.yml");
        assertExceptionOnCreation1(testFile, "File '\\service\\service-non-existing.yml' does not exist");
    }

    public void testCreateConfigsWithExistingUnsupportedFiles() {
        String testFile = "/service/service.xml";
        assertExceptionOnCreation(testFile, KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_CONTENT_TYPE", "service.xml"));
        testFile = "/service/service";
        assertExceptionOnCreation(testFile, KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_CONTENT_TYPE", "service"));
    }

    public void testCreateConfigsWithMissingResourceVersionFromJson() {
        String testFile = "/pod/pod-missing-version.json";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", "apiVersion"));
    }

    public void testCreateConfigsWithMissingResourceTypeFromJson() {
        String testFile = "/pod/pod-missing-type.json";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", "kind"));
    }

    public void testCreateConfigsWithMissingResourceIdFromJson() {
        String testFile = "/pod/pod-missing-id.json";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", "name"));
    }

    public void testCreateConfigsWithMissingResourceVersionFromYaml() {
        String testFile = "/service/service-missing-version.yml";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", "apiVersion"));
    }

    public void testCreateConfigsWithMissingResourceTypeFromYaml() {
        String testFile = "/service/service-missing-type.yml";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", "kind"));
    }

    public void testCreateConfigsWithMissingResourceIdFromYaml() {
        String testFile = "/service/service-missing-id.yml";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", "name"));
    }

    public void testCreateConfigsWithInvalidJson() {
        String testFile = "/pod/pod-invalid.json";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_CONTENT_PARSING"));
    }

    public void testCreateConfigsWithInvalidYaml() {
        String testFile = "/service/service-invalid.yml";
        assertExceptionOnCreation(testFile, COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_CONTENT_PARSING"));
    }

    private void assertExceptionOnCreation(String filename, String exceptionMessage) {
        File file = new File(this.getClass().getResource(filename).getPath());
        assertExceptionOnCreation1(file, exceptionMessage);
    }

    private void assertExceptionOnCreation1(File file, String exceptionMessage) {
        assertExceptionOnCreation(file, null, exceptionMessage);
    }

    private void assertExceptionOnCreation2(String filename1, String filename2, String exceptionMessage) {
        File file1 = new File(this.getClass().getResource(filename1).getPath());
        File file2 = new File(this.getClass().getResource(filename2).getPath());
        assertExceptionOnCreation(file1, file2, exceptionMessage);
    }

    private void assertExceptionOnCreation(File file1, File file2, String exceptionMessage) {
        boolean exceptionThrown = false;

        List<File> files = new ArrayList<>();
        files.add(file1);
        if (file2 != null) {
            files.add(file2);
        }

        try {
            KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
            resourceConfigFactory.createConfigs(files);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(exceptionMessage, e.getMessage());
        }

        // Check if exception was thrown
        assertTrue(exceptionThrown);
    }
}
