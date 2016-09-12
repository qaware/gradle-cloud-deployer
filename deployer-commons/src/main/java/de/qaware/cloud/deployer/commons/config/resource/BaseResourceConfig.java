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

/**
 * Represents a basic resource config which contains common fields independent of the target cloud system.
 */
public abstract class BaseResourceConfig {

    /**
     * The name of the file this config was read from.
     */
    private final String filename;

    /**
     * The content type of the config file (json, yml, ...).
     */
    private final ContentType contentType;

    /**
     * The id of the resource which is specified in the config file.
     */
    private String resourceId;

    /**
     * The config file content.
     */
    private String content;

    /**
     * Creates a new base resource config.
     *
     * @param filename    The name of the file this config belongs to.
     * @param contentType The content type of the file.
     * @param content     The content of the config file.
     */
    public BaseResourceConfig(String filename, ContentType contentType, String content) {
        this.filename = filename;
        this.content = content;
        this.contentType = contentType;
    }

    /**
     * Returns the filename.
     *
     * @return The filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns the content type.
     *
     * @return The content type.
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Returns the resource's id.
     *
     * @return The resource's id.
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the resource's id.
     *
     * @param resourceId The resource's id.
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Returns the content.
     *
     * @return The content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content The content.
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Config: " + getResourceId() + " (File: " + filename + ")";
    }
}
