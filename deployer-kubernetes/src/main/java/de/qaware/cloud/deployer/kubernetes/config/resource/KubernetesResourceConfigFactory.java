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
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KubernetesResourceConfigFactory extends BaseResourceConfigFactory<KubernetesResourceConfig> {

    private static final String KUBERNETES_CONFIG_SEPARATOR = "---";
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceConfig.class);

    public KubernetesResourceConfigFactory() {
        super(LOGGER);
    }

    @Override
    public List<KubernetesResourceConfig> createConfigs(List<File> files) throws ResourceConfigException {
        List<KubernetesResourceConfig> resourceConfigs = super.createConfigs(files);
        resourceConfigs = splitConfigs(resourceConfigs, KUBERNETES_CONFIG_SEPARATOR);
        return resourceConfigs;
    }

    @Override
    public KubernetesResourceConfig createConfig(File file) throws ResourceConfigException {
        String filename = file.getName();
        ContentType contentType = retrieveContentType(file);
        String content = readFileContent(file);
        return new KubernetesResourceConfig(filename, contentType, content);
    }

    private List<KubernetesResourceConfig> splitConfigs(List<KubernetesResourceConfig> resourceConfigs, String splitString) throws ResourceConfigException {
        List<KubernetesResourceConfig> splitResourceConfigs = new ArrayList<>();
        for (KubernetesResourceConfig resourceConfig : resourceConfigs) {
            List<String> splitContents = splitContent(resourceConfig.getContent(), splitString);
            for (String splitContent : splitContents) {
                KubernetesResourceConfig splitResourceConfig = new KubernetesResourceConfig(resourceConfig.getFilename(), resourceConfig.getContentType(), splitContent);
                splitResourceConfigs.add(splitResourceConfig);
                LOGGER.info("- " + splitResourceConfig);
            }
        }
        return splitResourceConfigs;
    }

    private List<String> splitContent(String content, String splitString) {
        List<String> splitContents = new ArrayList<>();
        List<String> tempSplitContents = Arrays.asList(content.split(splitString));
        // Remove whitespaces
        tempSplitContents.forEach(s -> splitContents.add(s.trim()));
        return splitContents;
    }
}
