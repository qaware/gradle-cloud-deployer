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
package de.qaware.cloud.deployer.commons.resource;

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;

/**
 * Implementation of a basic resource factory. It creates resources out of configs.
 *
 * @param <ResourceType> The resource type this factory belongs to.
 * @param <ConfigType>   The config type this factory belongs to.
 */
public abstract class BaseResourceFactory<ResourceType extends BaseResource, ConfigType extends BaseResourceConfig> {

    /**
     * The client factory which is used to create the clients for the backend communication.
     */
    private final ClientFactory clientFactory;

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseResourceFactory.class);

    /**
     * Creates a new base resource factory.
     *
     * @param environmentConfig The configuration of the environment this factory belongs to.
     * @throws ResourceException If a error during ping resource creation or connectivity testing occurs.
     */
    public BaseResourceFactory(EnvironmentConfig environmentConfig) throws ResourceException {

        LOGGER.info(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_PINGING_ENVIRONMENT", environmentConfig.getId()));

        // Test connectivity
        BasePingResource pingResource = createPingResource(environmentConfig);
        pingResource.ping();

        // Create a client factory
        this.clientFactory = new ClientFactory(environmentConfig);
    }

    /**
     * Returns the client factory.
     *
     * @return The client factory.
     */
    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    /**
     * Creates a list of resources out of the specified configs.
     *
     * @param resourceConfigs The configs which are the sources for the resources.
     * @return A list of resources.
     * @throws ResourceException If an error during resource creation occurs.
     */
    public List<ResourceType> createResources(List<ConfigType> resourceConfigs) throws ResourceException {
        List<ResourceType> resources = new ArrayList<>();
        for (ConfigType resourceConfig : resourceConfigs) {
            resources.add(createResource(resourceConfig));
        }
        return resources;
    }

    /**
     * Creates a resource out of the specified config.
     *
     * @param resourceConfig The config which is the source for the resource.
     * @return The created resource.
     * @throws ResourceException If a error during resource creation occurs.
     */
    public abstract ResourceType createResource(ConfigType resourceConfig) throws ResourceException;

    /**
     * Creates a ping resource for the specified environment config to test the connectivity.
     *
     * @param environmentConfig The environment config.
     * @return The ping resource.
     * @throws ResourceException If a error during ping resource creation occurs.
     */
    public abstract BasePingResource createPingResource(EnvironmentConfig environmentConfig) throws ResourceException;
}
