package de.qaware.cloud.deployer.kubernetes.config.resource;

import de.qaware.cloud.deployer.kubernetes.FileUtil;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
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

    public void testCreateConfigWithExistingYamlFile() throws ResourceConfigException, IOException {
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
}
