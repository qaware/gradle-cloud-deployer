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
package de.qaware.cloud.deployer.commons.update;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.Resource;
import org.slf4j.Logger;

import java.util.List;

/**
 * Implements a basic version of the soft update strategy. Meaning that all resources not included in the resources list
 * stay untouched.
 * @param <ResourceType> The type of the resources that are handled by this update strategy.
 */
public abstract class BaseSoftUpdateStrategy<ResourceType extends Resource> {

    /**
     * The logger of this instance.
     */
    private final Logger logger;

    public BaseSoftUpdateStrategy(Logger logger) {
        this.logger = logger;
    }

    /**
     * Deploys the list of resources. If the resource already exists, it will be deleted first.
     *
     * @param resources The resources to deploy.
     * @throws ResourceException If an error during deletion or deployment occurs.
     */
    public void deploy(List<ResourceType> resources) throws ResourceException {
        logger.info("Deploying resources...");

        for (Resource resource : resources) {
            if (resource.exists()) {
                resource.delete();
                resource.create();
                logger.info("- " + resource + " (updated)");
            } else {
                resource.create();
                logger.info("- " + resource + " (created)");
            }
        }

        logger.info("Finished deploying resources...");
    }
}
