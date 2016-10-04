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
package de.qaware.cloud.deployer.commons.config.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;

import java.io.IOException;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;

/**
 * Utility for json/yaml content.
 */
public final class ContentTreeUtil {

    /**
     * UTILITY.
     */
    private ContentTreeUtil() {
    }

    /**
     * Parses the content and creates a object tree.
     *
     * @param contentType The type of the content (json, yaml, ...)
     * @param content     The content.
     * @return The object tree.
     * @throws ResourceConfigException If the content type isn't supported or an error during parsing occurs.
     */
    public static JsonNode createObjectTree(ContentType contentType, String content) throws ResourceConfigException {
        if (content == null || content.isEmpty()) {
            throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_EMPTY_CONTENT"));
        }
        try {
            ObjectMapper objectMapper = retrieveObjectMapper(contentType);
            return objectMapper.readTree(content);
        } catch (IOException ex) {
            throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_CONTENT_PARSING"), ex);
        }
    }

    /**
     * Reads the node with the specified key in the object tree.
     *
     * @param contentObjectTree The object tree which contains the node identified by the specified key.
     * @param key               The key which identifies the node.
     * @return The value of the key in form of a node.
     * @throws ResourceConfigException If the key isn't available in the object tree.
     */
    public static JsonNode readNodeValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        if (contentObjectTree.hasNonNull(key)) {
            return contentObjectTree.get(key);
        } else {
            throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", key));
        }
    }

    /**
     * Reads the string with the specified key in the object tree.
     *
     * @param contentObjectTree The object tree which contains the string identified by the specified key.
     * @param key               The key which identifies the string.
     * @return The value of the key in form of a string.
     * @throws ResourceConfigException If the key isn't available in the object tree.
     */
    public static String readStringValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        if (contentObjectTree.hasNonNull(key)) {
            JsonNode jsonNode = contentObjectTree.get(key);
            if (jsonNode.isTextual()) {
                return jsonNode.textValue();
            } else {
                throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_STRING_VALUE", key));
            }
        } else {
            throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", key));
        }
    }

    /**
     * Adds a new field with the specified name and value to a object tree.
     *
     * @param contentObjectTree The object tree which will contain the new field.
     * @param fieldName         The name of the field.
     * @param value             The value of the field.
     */
    public static void addField(JsonNode contentObjectTree, String fieldName, String value) throws ResourceConfigException {
        if (contentObjectTree == null) {
            throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_EMPTY_CONTENT"));
        } else if(fieldName == null || fieldName.isEmpty() || value == null || value.isEmpty()) {
            throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_FIELD_ADDING"));
        }
        ObjectNode objectNode = (ObjectNode) contentObjectTree;
        objectNode.put(fieldName, value);
    }

    /**
     * Writes the specified content as a string in the specified format.
     *
     * @param contentType The format the object is written to.
     * @param object      The object that will be written.
     * @return The written object as string.
     * @throws ResourceConfigException If a error during object writing occurs.
     */
    public static String writeAsString(ContentType contentType, Object object) throws ResourceConfigException {
        try {
            ObjectMapper objectMapper = retrieveObjectMapper(contentType);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_CONTENT_WRITING"), ex);
        }
    }

    /**
     * Returns the object mapper for the specified content type.
     *
     * @param contentType The content type.
     * @return The object mapper.
     * @throws ResourceConfigException If the specified content type is unsupported.
     */
    private static ObjectMapper retrieveObjectMapper(ContentType contentType) throws ResourceConfigException {
        ObjectMapper mapper;
        switch (contentType) {
            case YAML:
                mapper = new ObjectMapper(new YAMLFactory());
                break;
            case JSON:
                mapper = new ObjectMapper(new JsonFactory());
                break;
            default:
                throw new ResourceConfigException(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNSUPPORTED_CONTENT_TYPE"));
        }
        return mapper;
    }
}
