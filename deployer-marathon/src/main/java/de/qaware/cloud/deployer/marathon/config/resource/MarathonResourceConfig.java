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

import com.fasterxml.jackson.databind.JsonNode;
import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfig;
import de.qaware.cloud.deployer.commons.config.util.ContentTreeUtil;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;

/**
 * Represents a config for a marathon resource.
 */
public class MarathonResourceConfig extends BaseResourceConfig {

    /**
     * Creates a new resource config for marathon.
     *
     * @param filename The name of the file which contains this config.
     * @param contentType The content type (json, yml, ...) of the file.
     * @param content The content of the file.
     * @throws ResourceConfigException If the content doesn't contain all necessary attributes or can't be parsed.
     */
    public MarathonResourceConfig(String filename, ContentType contentType, String content) throws ResourceConfigException {
        super(filename, contentType, content);

        // Create the object tree and retrieve the id.
        JsonNode contentObjectTree = ContentTreeUtil.createObjectTree(contentType, content);
        String id = ContentTreeUtil.readStringValue(contentObjectTree, "id");
        this.setResourceId(id);
    }
}
