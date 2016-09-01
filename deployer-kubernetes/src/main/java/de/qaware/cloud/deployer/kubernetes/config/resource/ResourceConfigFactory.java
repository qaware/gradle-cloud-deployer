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

    public static List<ResourceConfig> createConfigs(List<File> files) throws ResourceConfigException {

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
                ResourceConfig splitResourceConfig;
                if (resourceConfig instanceof FileResourceConfig) {
                    FileResourceConfig fileResourceConfig = (FileResourceConfig) resourceConfig;
                    splitResourceConfig = new FileResourceConfig(fileResourceConfig.getFilename(), resourceConfig.getContentType(), splitContent);
                    splitResourceConfigs.add(splitResourceConfig);
                } else {
                    splitResourceConfig = new ResourceConfig(resourceConfig.getContentType(), splitContent);
                    splitResourceConfigs.add(splitResourceConfig);
                }
                LOGGER.info("- " + splitResourceConfig);
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
                throw new ResourceConfigException("Unsupported content type for file ending: " + fileEnding + "(File: " + file.getName() + ")");
        }
    }

    // TODO: charset correct?
    private static String readFileContent(File file) throws ResourceConfigException {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset()).trim();
        } catch (IOException e) {
            throw new ResourceConfigException(e.getMessage(), e);
        }
    }

    private static List<String> splitContent(String content, String splitString) {
        List<String> splitContents = new ArrayList<>();
        List<String> tempSplitContents = Arrays.asList(content.split(splitString));
        // Remove whitespaces
        tempSplitContents.forEach(s -> splitContents.add(s.trim()));
        return splitContents;
    }
}
