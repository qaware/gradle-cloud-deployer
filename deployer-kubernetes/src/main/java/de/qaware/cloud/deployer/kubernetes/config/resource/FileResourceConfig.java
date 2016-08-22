package de.qaware.cloud.deployer.kubernetes.config.resource;

import com.fasterxml.jackson.databind.JsonNode;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;

public class FileResourceConfig extends ResourceConfig {

    private final String filename;

    public FileResourceConfig(String filename, ContentType contentType, String content) throws ResourceConfigException {
        super(contentType, content);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public JsonNode createObjectTree(ContentType contentType, String content) throws ResourceConfigException {
        try {
            return super.createObjectTree(contentType, content);
        } catch (ResourceConfigException ex) {
            throw new ResourceConfigException(createAdvancedExceptionMessage(ex), ex);
        }
    }

    @Override
    public JsonNode readNodeValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        try {
            return super.readNodeValue(contentObjectTree, key);
        } catch (ResourceConfigException ex) {
            throw new ResourceConfigException(createAdvancedExceptionMessage(ex), ex);
        }
    }

    @Override
    public String readStringValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        try {
            return super.readStringValue(contentObjectTree, key);
        } catch (ResourceConfigException ex) {
            throw new ResourceConfigException(createAdvancedExceptionMessage(ex), ex);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " (File: " + filename + ")";
    }

    private String createAdvancedExceptionMessage(ResourceConfigException ex) {
        return ex.getMessage() + " (File: " + this.filename + ")";
    }
}
