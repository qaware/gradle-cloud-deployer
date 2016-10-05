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

import com.fasterxml.jackson.databind.JsonNode;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.junit.Test;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentTreeUtilTest {

    @Test
    public void testCreateObjectTreeWithNullContent() {
        boolean exceptionThrown = false;
        try {
            ContentTreeUtil.createObjectTree(ContentType.YAML, null);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_EMPTY_CONTENT"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testCreateObjectTreeWithInvalidJson() {
        boolean exceptionThrown = false;
        try {
            String content = FileUtil.readFileContent("/de/qaware/cloud/deployer/commons/config/util/invalid.json");
            ContentTreeUtil.createObjectTree(ContentType.JSON, content);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_CONTENT_PARSING"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testCreateObjectTreeWithValidJson() throws ResourceConfigException {
        JsonNode objectTree = getJsonTree();
        testJsonNode(objectTree);
    }

    @Test
    public void testCreateObjectTreeWithValidYaml() throws ResourceConfigException {
        JsonNode objectTree = getYamlTree();
        testJsonNode(objectTree);
    }

    @Test
    public void testReadNodeValue() throws ResourceConfigException {
        JsonNode objectTree = getJsonTree();
        JsonNode containerNode = ContentTreeUtil.readNodeValue(objectTree, "container");
        assertEquals("DOCKER", containerNode.get("type").asText());
    }

    @Test
    public void testReadNodeValueWithUnknownKey() throws ResourceConfigException {
        String invalidKey = "empty";
        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", invalidKey);
        assertExceptionOnReadNodeValue(getJsonTree(), invalidKey, message);
    }

    @Test
    public void testReadStringValue() throws ResourceConfigException {
        JsonNode objectTree = getJsonTree();
        assertEquals("zwitscher-eureka", ContentTreeUtil.readStringValue(objectTree, "id"));
    }

    @Test
    public void testReadStringValueWithUnknownKey() throws ResourceConfigException {
        String invalidKey = "empty";
        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_NODE_VALUE", invalidKey);
        assertExceptionOnReadStringValue(getJsonTree(), invalidKey, message);
    }

    @Test
    public void testReadStringValueWithInvalidKey() throws ResourceConfigException {
        String invalidKey = "container";
        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_READING_STRING_VALUE", invalidKey);
        assertExceptionOnReadStringValue(getJsonTree(), invalidKey, message);
    }

    @Test
    public void testAddField() throws ResourceConfigException {
        String key = "test", value = "test-value";
        JsonNode node = getJsonTree();
        ContentTreeUtil.addField(node, key, value);
        assertTrue(node.hasNonNull(key));
        assertEquals(value, node.get(key).asText());
    }

    @Test
    public void testAddFieldOverride() throws ResourceConfigException {
        String key = "id", value = "test-value";
        JsonNode node = getJsonTree();
        ContentTreeUtil.addField(node, key, value);
        assertTrue(node.hasNonNull(key));
        assertEquals(value, node.get(key).asText());
    }

    @Test
    public void testAddFieldWithNull() throws ResourceConfigException {
        String key = "key", value = "value";
        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_EMPTY_CONTENT");
        assertExceptionOnAddField(null, key, value, message);
    }

    @Test
    public void testAddFieldWithEmptyKey() throws ResourceConfigException {
        String key = "", value = "value";
        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_FIELD_ADDING");
        assertExceptionOnAddField(getJsonTree(), key, value, message);
    }

    @Test
    public void testAddFieldWithEmptyValue() throws ResourceConfigException {
        String key = "key", value = "";
        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_FIELD_ADDING");
        assertExceptionOnAddField(getJsonTree(), key, value, message);
    }

    @Test
    public void testWriteAsString() throws ResourceConfigException {
        String correctString = "{\"id\":\"zwitscher-eureka\",\"container\":{\"type\":\"DOCKER\"}}";
        JsonNode objectTree = getJsonTree();
        String objectTreeAsString = ContentTreeUtil.writeAsString(ContentType.JSON, objectTree);
        assertEquals(correctString, objectTreeAsString);
    }

    @Test
    public void testWriteAsStringWithInvalidObject() throws ResourceConfigException {
        Object object = mock(Object.class);
        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_DURING_CONTENT_WRITING");
        boolean exceptionThrown = false;
        try {
            ContentTreeUtil.writeAsString(ContentType.JSON, object);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void testJsonNode(JsonNode objectTree) {
        assertTrue(objectTree.has("id"));
        assertEquals("zwitscher-eureka", objectTree.get("id").asText());
        assertTrue(objectTree.has("container"));

        JsonNode containerNode = objectTree.get("container");
        assertTrue(containerNode.has("type"));
        assertEquals("DOCKER", containerNode.get("type").asText());
    }

    private void assertExceptionOnReadStringValue(JsonNode objectTree, String key, String message) {
        boolean exceptionThrown = false;
        try {
            ContentTreeUtil.readStringValue(objectTree, key);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void assertExceptionOnReadNodeValue(JsonNode objectTree, String key, String message) {
        boolean exceptionThrown = false;
        try {
            ContentTreeUtil.readNodeValue(objectTree, key);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void assertExceptionOnAddField(JsonNode objectTree, String key, String value, String message) {
        boolean exceptionThrown = false;
        try {
            ContentTreeUtil.addField(objectTree, key, value);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private JsonNode getJsonTree() throws ResourceConfigException {
        String content = FileUtil.readFileContent("/de/qaware/cloud/deployer/commons/config/util/app.json");
        return ContentTreeUtil.createObjectTree(ContentType.JSON, content);
    }

    private JsonNode getYamlTree() throws ResourceConfigException {
        String content = FileUtil.readFileContent("/de/qaware/cloud/deployer/commons/config/util/app.yml");
        return ContentTreeUtil.createObjectTree(ContentType.YAML, content);
    }
}
