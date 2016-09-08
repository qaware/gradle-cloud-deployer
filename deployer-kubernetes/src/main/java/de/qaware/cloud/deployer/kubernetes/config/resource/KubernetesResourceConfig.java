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

import com.fasterxml.jackson.databind.JsonNode;
import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfig;
import de.qaware.cloud.deployer.commons.config.resource.ContentTreeUtil;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;

/**
 * Represents a config for a kubernetes resource.
 */
public class KubernetesResourceConfig extends BaseResourceConfig {

    /**
     * The version of the resource.
     */
    private final String resourceVersion;

    /**
     * The type of the resource.
     */
    private final String resourceType;

    /**
     * Creates a new resource config for kubernetes.
     *
     * @param filename    The name of the file which contains this config.
     * @param contentType The content type (json, yml, ...) of the file.
     * @param content     The content of the file.
     * @throws ResourceConfigException If the content doesn't contain all necessary attributes or can't be parsed.
     */
    public KubernetesResourceConfig(String filename, ContentType contentType, String content) throws ResourceConfigException {
        super(filename, contentType, content);

        JsonNode contentObjectTree = ContentTreeUtil.createObjectTree(contentType, content);
        this.setResourceId(ContentTreeUtil.readStringValue(ContentTreeUtil.readNodeValue(contentObjectTree, "metadata"), "name"));
        this.resourceType = ContentTreeUtil.readStringValue(contentObjectTree, "kind");
        this.resourceVersion = ContentTreeUtil.readStringValue(contentObjectTree, "apiVersion");
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public String getResourceType() {
        return resourceType;
    }
}
