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
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a basic resource config factory.
 *
 * @param <ConfigType> The type of config this implementation belongs to.
 */
public abstract class BaseResourceConfigFactory<ConfigType extends BaseResourceConfig> {

    /**
     * The logger used for logging.
     */
    private final Logger logger;

    /**
     * Creates a new base resource config factory.
     *
     * @param logger The logger used for logging.
     */
    protected BaseResourceConfigFactory(Logger logger) {
        this.logger = logger;
    }

    /**
     * Creates a list of config objects out of the specified list of files.
     *
     * @param files The files which are the sources for the configs.
     * @return The created configs.
     * @throws ResourceConfigException If an error during config creation occurs.
     */
    public List<ConfigType> createConfigs(List<File> files) throws ResourceConfigException {

        logger.info("Reading config files...");

        List<ConfigType> resourceConfigs = new ArrayList<>();
        for (File file : files) {
            resourceConfigs.add(createConfig(file));
        }

        logger.info("Finished reading config files...");

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
                throw new ResourceConfigException("Unsupported content type for file ending: " + fileEnding + "(File: " + file.getName() + ")");
        }
    }
}
