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
package de.qaware.cloud.deployer.commons.config.resource;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;

/**
 * Implementation of a basic resource config factory.
 *
 * @param <ConfigType> The type of config this implementation belongs to.
 */
public abstract class BaseResourceConfigFactory<ConfigType extends BaseResourceConfig> {

    /**
     * Creates a list of config objects out of the specified list of files.
     *
     * @param files The files which are the sources for the configs.
     * @return The created configs.
     * @throws ResourceConfigException If an error during config creation occurs.
     */
    public List<ConfigType> createConfigs(List<File> files) throws ResourceConfigException {
        List<ConfigType> resourceConfigs = new ArrayList<>();
        for (File file : files) {
            resourceConfigs.add(createConfig(file));
        }
        return resourceConfigs;
    }

    /**
     * Creates a config out of the specified file.
     *
     * @param file The file which is the source for this config.
     * @return The created config.
     * @throws ResourceConfigException If an error during config creation occurs.
     */
    public abstract ConfigType createConfig(File file) throws ResourceConfigException;

    /**
     * Returns the content type of a file using the file ending.
     *
     * @param file The file whose content type should be returned.
     * @return The content type of the file.
     * @throws ResourceConfigException If the content type isn't supported.
     */
    protected ContentType retrieveContentType(File file) throws ResourceConfigException {
        String fileEnding = FilenameUtils.getExtension(file.getName());
        switch (fileEnding) {
            case "json":
                return ContentType.JSON;
            case "yml":
                return ContentType.YAML;
            default:
                throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNKNOWN_CONTENT_TYPE", file.getName()));
        }
    }
}
