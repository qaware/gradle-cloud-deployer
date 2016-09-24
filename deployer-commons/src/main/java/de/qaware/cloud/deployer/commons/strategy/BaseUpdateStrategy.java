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
 * Implements a basic version of the update strategy. Meaning that all resources not included in the resources list
 * stay untouched. All included resources are updated or created.
 */
public abstract class BaseUpdateStrategy {

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUpdateStrategy.class);

    /**
     * Updates the specified resources. If a resource already exists, it will be updated. If it doesn't exist, it will
     * be created.
     *
     * @param resources      The resources to update.
     * @param <ResourceType> The type of the resources.
     * @throws ResourceException If an error during updating or deployment occurs.
     */
    protected <ResourceType extends Resource> void update(List<ResourceType> resources) throws ResourceException {
        for (Resource resource : resources) {
            if (resource.exists()) {
                LOGGER.info(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_UPDATING_SINGLE_RESOURCE", resource));
                resource.update();
            } else {
                LOGGER.info(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_CREATING_SINGLE_RESOURCE", resource));
                resource.create();
            }
        }
    }
}
