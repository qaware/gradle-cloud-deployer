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

import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfigFactory;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static de.qaware.cloud.deployer.marathon.MarathonMessageBundle.MARATHON_MESSAGE_BUNDLE;

/**
 * A factory which creates resource configs using the specified files.
 */
public class MarathonResourceConfigFactory extends BaseResourceConfigFactory<MarathonResourceConfig> {

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MarathonResourceConfigFactory.class);

    @Override
    public List<MarathonResourceConfig> createConfigs(List<File> files) throws ResourceConfigException {
        LOGGER.info(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_MESSAGE_READING_CONFIGS_STARTED"));
        List<MarathonResourceConfig> resourceConfigs = super.createConfigs(files);
        resourceConfigs.forEach(resourceConfig -> LOGGER.info(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_MESSAGE_READING_CONFIGS_SINGLE_CONFIG", resourceConfig)));
        LOGGER.info(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_MESSAGE_READING_CONFIGS_DONE"));
        return resourceConfigs;
    }

    @Override
    public MarathonResourceConfig createConfig(File file) throws ResourceConfigException {
        String filename = file.getName();
        ContentType contentType = retrieveContentType(file);
        String content = FileUtil.readFileContent(file);

        // Is the content empty?
        if (content.isEmpty()) {
            throw new ResourceConfigException(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_ERROR_EMPTY_CONFIG", file.getName()));
        }

        return new MarathonResourceConfig(filename, contentType, content);
    }

    @Override
    protected ContentType retrieveContentType(File file) throws ResourceConfigException {
        String fileEnding = FilenameUtils.getExtension(file.getName());
        switch (fileEnding) {
            case "json":
                return ContentType.JSON;
            default:
                throw new ResourceConfigException(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_ERROR_UNKNOWN_CONTENT_TYPE", file.getName()));
        }
    }
}
