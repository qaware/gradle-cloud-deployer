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

import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfigFactory;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * A factory which creates resource configs using the specified files.
 */
public class KubernetesResourceConfigFactory extends BaseResourceConfigFactory<KubernetesResourceConfig> {

    /**
     * The string which splits multiple kubernetes configs within the same file.
     */
    private static final String KUBERNETES_CONFIG_SEPARATOR = "---";

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceConfig.class);

    @Override
    public List<KubernetesResourceConfig> createConfigs(List<File> files) throws ResourceConfigException {
        LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_READING_CONFIGS_STARTED"));
        List<KubernetesResourceConfig> resourceConfigs = super.createConfigs(files);
        resourceConfigs = splitConfigs(resourceConfigs, KUBERNETES_CONFIG_SEPARATOR);
        LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_READING_CONFIGS_DONE"));
        return resourceConfigs;
    }

    @Override
    public KubernetesResourceConfig createConfig(File file) throws ResourceConfigException {
        String filename = file.getName();
        ContentType contentType = retrieveContentType(file);
        String content = FileUtil.readFileContent(file);

        // Is the content empty?
        if (content.isEmpty()) {
            throw new ResourceConfigException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_EMPTY_CONFIG", file.getName()));
        }

        return new KubernetesResourceConfig(filename, contentType, content);
    }

    /**
     * Splits the specified resource configs if they should contain multiple kubernetes configs in their content.
     *
     * @param resourceConfigs The configs which might contain multiple kubernetes configs in their content.
     * @param splitString     The string which is used to split the content if it contains multiple configs.
     * @return The split configs.
     * @throws ResourceConfigException If a problem during resource config creation occurs.
     */
    private List<KubernetesResourceConfig> splitConfigs(List<KubernetesResourceConfig> resourceConfigs, String splitString) throws ResourceConfigException {
        List<KubernetesResourceConfig> splitResourceConfigs = new ArrayList<>();
        for (KubernetesResourceConfig resourceConfig : resourceConfigs) {
            List<String> splitContents = splitContent(resourceConfig.getContent(), splitString);
            for (String splitContent : splitContents) {
                KubernetesResourceConfig splitResourceConfig = new KubernetesResourceConfig(resourceConfig.getFilename(), resourceConfig.getContentType(), splitContent);
                splitResourceConfigs.add(splitResourceConfig);
                LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_READING_CONFIGS_SINGLE_CONFIG", splitResourceConfig));
            }
        }
        return splitResourceConfigs;
    }

    /**
     * Splits the specified content using the specified string.
     *
     * @param content     The content that might be split.
     * @param splitString The string that is used for splitting.
     * @return A list containing the split contents if possible, otherwise it contains the specified content only.
     */
    private List<String> splitContent(String content, String splitString) {
        List<String> splitContents = new ArrayList<>();
        List<String> tempSplitContents = Arrays.asList(content.split(splitString));
        // Remove whitespaces
        tempSplitContents.forEach(s -> splitContents.add(s.trim()));
        return splitContents;
    }
}
