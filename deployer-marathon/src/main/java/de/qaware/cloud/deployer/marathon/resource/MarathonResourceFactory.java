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
package de.qaware.cloud.deployer.marathon.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.config.resource.ContentTreeUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResourceFactory;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.resource.app.AppResource;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import de.qaware.cloud.deployer.marathon.resource.group.GroupResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory which creates MarathonResources using the specified MarathonResourceConfigs.
 */
public class MarathonResourceFactory extends BaseResourceFactory<MarathonResource, MarathonResourceConfig> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MarathonResourceFactory.class);

    /**
     * The json schema for an application.
     */
    private static final String APP_JSON_SCHEMA = "AppDefinition.json";

    /**
     * The json schema for a group.
     */
    private static final String GROUP_JSON_SCHEMA = "GroupDefinition.json";

    /**
     * The namespace where the schemas can be found.
     */
    private static final String NAMESPACE = "resource:/schemas/";

    /**
     * Creates a new MarathonResourceConfigFactory for the specified cloud.
     *
     * @param cloudConfig The config of the cloud.
     * @throws ResourceException if an error occurs.
     */
    public MarathonResourceFactory(CloudConfig cloudConfig) throws ResourceException {
        super(LOGGER, new ClientFactory(cloudConfig));
    }

    @Override
    public MarathonResource createResource(MarathonResourceConfig resourceConfig) throws ResourceException {

        // Is the content empty?
        if (resourceConfig.getContent().isEmpty()) {
            throw new ResourceException("Config is empty (File: " + resourceConfig.getFilename() + ")");
        }

        try {
            JsonNode contentObjectTree = ContentTreeUtil.createObjectTree(resourceConfig.getContentType(), resourceConfig.getContent());
            // What type of config is this json tree?
            if (isApp(contentObjectTree)) {
                // App?
                return new AppResource(resourceConfig, getClientFactory());
            } else if (isGroup(contentObjectTree)) {
                // Group?
                return new GroupResource(resourceConfig, getClientFactory());
            } else {
                throw new ResourceException("Unknown marathon resource type");
            }
        } catch (ResourceConfigException e) {
            throw new ResourceException("An error occured during marathon resource creation", e);
        }
    }

    /**
     * Indicates whether the specified json is of type app.
     *
     * @param contentObjectTree The json which is analysed.
     * @return TRUE if the json is of type app, FALSE otherwise.
     * @throws ResourceException if an error occurred.
     */
    private boolean isApp(JsonNode contentObjectTree) throws ResourceException {
        return validateSchema(APP_JSON_SCHEMA, contentObjectTree);
    }

    /**
     * Indicates whether the specified json is of type group.
     *
     * @param contentObjectTree The json which is analysed.
     * @return TRUE if the json is of type group, FALSE otherwise.
     * @throws ResourceException if an error occurred.
     */
    private boolean isGroup(JsonNode contentObjectTree) throws ResourceException {
        return validateSchema(GROUP_JSON_SCHEMA, contentObjectTree);
    }

    /**
     * Checks if the specified json fulfills the specified schema.
     *
     * @param schemaName        The name of the schema.
     * @param contentObjectTree The json which is analysed.
     * @return TRUE if the json fulfills the specified schema, FALSE otherwise.
     * @throws ResourceException if an error occurred.
     */
    private boolean validateSchema(String schemaName, JsonNode contentObjectTree) throws ResourceException {
        try {
            URITranslatorConfiguration translatorCfg = URITranslatorConfiguration.newBuilder().setNamespace(NAMESPACE).freeze();
            LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setURITranslatorConfiguration(translatorCfg).freeze();
            JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
            JsonSchema schema = factory.getJsonSchema(schemaName);
            ProcessingReport report = schema.validate(contentObjectTree);
            return report.isSuccess();
        } catch (ProcessingException e) {
            throw new ResourceException(e);
        }
    }
}
