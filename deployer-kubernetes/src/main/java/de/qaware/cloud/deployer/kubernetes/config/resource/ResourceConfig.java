package de.qaware.cloud.deployer.kubernetes.config.resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;

import java.io.IOException;

public class ResourceConfig {

    private final String content;
    private final ContentType contentType;
    private final String resourceVersion;
    private final String resourceId;
    private final String resourceType;

    public ResourceConfig(ContentType contentType, String content) throws ResourceConfigException {
        this.content = content;
        this.contentType = contentType;

        JsonNode contentObjectTree = createObjectTree(contentType, content);
        this.resourceId = readStringValue(readNodeValue(contentObjectTree, "metadata"), "name");
        this.resourceType = readStringValue(contentObjectTree, "kind");
        this.resourceVersion = readStringValue(contentObjectTree, "apiVersion");
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

    public JsonNode createObjectTree(ContentType contentType, String content) throws ResourceConfigException {
        ObjectMapper mapper;
        switch (contentType) {
            case YAML:
                mapper = new ObjectMapper(new YAMLFactory());
                break;
            case JSON:
                mapper = new ObjectMapper(new JsonFactory());
                break;
            default:
                throw new ResourceConfigException("Unknown config type " + contentType);
        }
        try {
            return mapper.readTree(content);
        } catch (JsonProcessingException ex) {
            throw new ResourceConfigException("Could not parse config content", ex);
        } catch (IOException ex) {
            throw new ResourceConfigException(ex.getMessage(), ex);
        }
    }

    public JsonNode readNodeValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        if (contentObjectTree.hasNonNull(key)) {
            return contentObjectTree.get(key);
        } else {
            throw new ResourceConfigException("Could not find attribute '" + key + "' in config content");
        }
    }

    public String readStringValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        if (contentObjectTree.hasNonNull(key)) {
            return contentObjectTree.get(key).textValue();
        } else {
            throw new ResourceConfigException("Could not find attribute '" + key + "' in config content");
        }
    }

    @Override
    public String toString() {
        return "ResourceConfig: " + getResourceId() + " - " + getResourceType();
    }
}
