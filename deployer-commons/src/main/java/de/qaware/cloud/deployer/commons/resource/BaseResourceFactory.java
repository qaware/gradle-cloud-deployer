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

import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseResourceFactory<T extends BaseResource, X extends BaseResourceConfig> {

    private final ClientFactory clientFactory;
    private final Logger logger;

    public BaseResourceFactory(Logger logger, ClientFactory clientFactory) {
        this.logger = logger;
        this.clientFactory = clientFactory;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public List<T> createResources(List<X> resourceConfigs) throws ResourceException {

        logger.info("Creating resources...");

        List<T> resources = new ArrayList<>();
        for (X resourceConfig : resourceConfigs) {
            resources.add(createResource(resourceConfig));
        }

        logger.info("Finished creating resources...");

        return resources;
    }

    public abstract T createResource(X resourceConfig) throws ResourceException;
}
