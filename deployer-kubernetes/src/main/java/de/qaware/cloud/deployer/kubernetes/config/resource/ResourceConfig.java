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
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;

public class ResourceConfig {

    private final ContentType contentType;
    private final String resourceVersion;
    private final String resourceId;
    private final String resourceType;
    private String content;

    public ResourceConfig(ContentType contentType, String content) throws ResourceConfigException {
        this.content = content;
        this.contentType = contentType;

        JsonNode contentObjectTree = ContentTreeUtil.createObjectTree(contentType, content);
        this.resourceId = ContentTreeUtil.readStringValue(ContentTreeUtil.readNodeValue(contentObjectTree, "metadata"), "name");
        this.resourceType = ContentTreeUtil.readStringValue(contentObjectTree, "kind");
        this.resourceVersion = ContentTreeUtil.readStringValue(contentObjectTree, "apiVersion");
    }

    public String getContent() {
        return content;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ResourceConfig: " + getResourceId() + " - " + getResourceType();
    }
}
