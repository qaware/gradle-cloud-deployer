package de.qaware.cloud.deployer.kubernetes.config.resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class ResourceConfig {

    private final String content;
    private final ContentType contentType;
    private final JsonNode contentObjectTree;

    public ResourceConfig(ContentType contentType, String content) throws IOException {
        this.content = content;
        this.contentType = contentType;
        this.contentObjectTree = createObjectTree();
    }

    public String getContent() {
        return content;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getResourceVersion() {
        return contentObjectTree.get("apiVersion").textValue();
    }

    public String getResourceType() {
        return contentObjectTree.get("kind").textValue();
    }

    public String getResourceId() {
        return contentObjectTree.get("metadata").get("name").textValue();
    }

    private JsonNode createObjectTree() throws IOException {
        ObjectMapper mapper;
        switch (contentType) {
            case YAML:
                mapper = new ObjectMapper(new YAMLFactory());
                break;
            case JSON:
                mapper = new ObjectMapper(new JsonFactory());
                break;
            default:
                throw new IllegalArgumentException("Unknown type " + contentType);
        }
        return mapper.readTree(content);
    }
}
