package de.qaware.cloud.deployer.kubernetes.config.resource;

import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceConfigFactory {

    private static final String KUBERNETES_CONFIG_SEPARATOR = "---";
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceConfig.class);

    private ResourceConfigFactory() {
    }

    public static List<ResourceConfig> getConfigs(List<File> files) throws ResourceConfigException {

        LOGGER.info("Reading config files...");

        List<ResourceConfig> resourceConfigs = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName();
            ContentType contentType = retrieveContentType(file);
            String content = readFileContent(file);
            resourceConfigs.add(new FileResourceConfig(filename, contentType, content));
        }

        resourceConfigs = splitConfigs(resourceConfigs, KUBERNETES_CONFIG_SEPARATOR);

        LOGGER.info("Finished reading config files...");

        return resourceConfigs;
    }

    private static List<ResourceConfig> splitConfigs(List<ResourceConfig> resourceConfigs, String splitString) throws ResourceConfigException {
        List<ResourceConfig> splitResourceConfigs = new ArrayList<>();
        for (ResourceConfig resourceConfig : resourceConfigs) {
            List<String> splitContents = splitContent(resourceConfig.getContent(), splitString);
            for (String splitContent : splitContents) {
                if (resourceConfig instanceof FileResourceConfig) {
                    FileResourceConfig fileResourceConfig = (FileResourceConfig) resourceConfig;
                    splitResourceConfigs.add(new FileResourceConfig(fileResourceConfig.getFilename(), resourceConfig.getContentType(), splitContent));
                } else {
                    splitResourceConfigs.add(new ResourceConfig(resourceConfig.getContentType(), splitContent));
                }
                LOGGER.info("- " + resourceConfig);
            }
        }
        return splitResourceConfigs;
    }

    private static ContentType retrieveContentType(File file) throws ResourceConfigException {
        String fileEnding = FilenameUtils.getExtension(file.getName());
        switch (fileEnding) {
            case "json":
                return ContentType.JSON;
            case "yml":
                return ContentType.YAML;
            default:
                throw new ResourceConfigException("Unknown content type for file ending: " + fileEnding + "(File: " + file.getName() + ")");
        }
    }

    // TODO: charset correct?
    private static String readFileContent(File file) throws ResourceConfigException {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            throw new ResourceConfigException(e.getMessage(), e);
        }
    }

    private static List<String> splitContent(String content, String splitString) {
        return Arrays.asList(content.split(splitString));
    }
}

