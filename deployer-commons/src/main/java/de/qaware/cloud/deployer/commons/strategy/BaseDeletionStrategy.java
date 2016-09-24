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
package de.qaware.cloud.deployer.commons.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;

/**
 * Implements the basic delete features for a strategy. Meaning that all resources in the list are deleted. Non existing
 * resources are skipped. All resources which are not in the list stay untouched.
 */
public abstract class BaseDeletionStrategy {

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDeletionStrategy.class);

    /**
     * Deletes the list of specified resources. If the resource doesn't exist it will be skipped.
     *
     * @param resources The list of resources to delete.
     * @throws ResourceException If an error during resource deletion occurs.
     */
    public <ResourceType extends Resource> void deleteResources(List<ResourceType> resources) throws ResourceException {

        LOGGER.info(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_DELETING_RESOURCES_STARTED"));

        for (Resource resource : resources) {
            if (resource.exists()) {
                LOGGER.info(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_DELETING_SINGLE_RESOURCE", resource));
                resource.delete();
            } else {
                LOGGER.info(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_DELETING_SINGLE_RESOURCE_SKIPPED", resource));
            }
        }

        LOGGER.info(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_DELETING_RESOURCES_DONE"));
    }
}
