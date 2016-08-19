package de.qaware.cloud.deployer.kubernetes.config.resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigFactory {

    private static final String KUBERNETES_CONFIG_SEPARATOR = "---";

    private ConfigFactory() {
    }

    public static List<ResourceConfig> getConfigs(List<File> files) throws IOException {
        List<ResourceConfig> resourceConfigs = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName();
            String fileEnding = FilenameUtils.getExtension(file.getName());
            ContentType contentType = retrieveContentType(fileEnding);
            String content = FileUtils.readFileToString(file, Charset.defaultCharset());
            resourceConfigs.add(new FileResourceConfig(filename, contentType, content));
        }
        return splitConfigs(resourceConfigs, KUBERNETES_CONFIG_SEPARATOR);
    }

    private static List<ResourceConfig> splitConfigs(List<ResourceConfig> resourceConfigs, String splitString) throws IOException {
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
            }
        }
        return splitResourceConfigs;
    }

    private static ContentType retrieveContentType(String fileEnding) {
        switch (fileEnding) {
            case "json":
                return ContentType.JSON;
            case "yml":
                return ContentType.YAML;
            default:
                throw new IllegalArgumentException("Unknown content type for file ending: " + fileEnding);
        }
    }

    private static List<String> splitContent(String content, String splitString) {
        return Arrays.asList(content.split(splitString));
    }
}

