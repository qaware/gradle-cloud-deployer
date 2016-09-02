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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseResourceConfigFactory<T extends BaseResourceConfig> {

    private final Logger logger;

    protected BaseResourceConfigFactory(Logger logger) {
        this.logger = logger;
    }

    public List<T> createConfigs(List<File> files) throws ResourceConfigException {

        logger.info("Reading config files...");

        List<T> resourceConfigs = new ArrayList<>();
        for (File file : files) {
            resourceConfigs.add(createConfig(file));
        }

        logger.info("Finished reading config files...");

        return resourceConfigs;
    }

    public abstract T createConfig(File file) throws ResourceConfigException;

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

    protected String readFileContent(File file) throws ResourceConfigException {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset()).trim();
        } catch (IOException e) {
            throw new ResourceConfigException(e.getMessage(), e);
        }
    }
}
