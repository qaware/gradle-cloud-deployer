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
package de.qaware.cloud.deployer.kubernetes.config.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;

import java.io.IOException;

public class ContentTreeUtil {

    private ContentTreeUtil() {
    }

    public static JsonNode createObjectTree(ContentType contentType, String content) throws ResourceConfigException {
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

    public static JsonNode readNodeValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        if (contentObjectTree.hasNonNull(key)) {
            return contentObjectTree.get(key);
        } else {
            throw new ResourceConfigException("Could not find attribute '" + key + "' in config content");
        }
    }

    public static String readStringValue(JsonNode contentObjectTree, String key) throws ResourceConfigException {
        if (contentObjectTree.hasNonNull(key)) {
            return contentObjectTree.get(key).textValue();
        } else {
            throw new ResourceConfigException("Could not find attribute '" + key + "' in config content");
        }
    }

    public static JsonNode addField(JsonNode contentObjectTree, String fieldName, String value) {
        ObjectNode objectNode = (ObjectNode) contentObjectTree;
        return objectNode.put(fieldName, value);
    }
}
