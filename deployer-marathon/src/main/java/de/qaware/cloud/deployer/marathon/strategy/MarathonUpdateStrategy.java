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
package de.qaware.cloud.deployer.marathon.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.Resource;
import de.qaware.cloud.deployer.commons.strategy.BaseUpdateStrategy;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.qaware.cloud.deployer.marathon.logging.MarathonMessageBundle.MARATHON_MESSAGE_BUNDLE;

/**
 * Implements the update strategy. Meaning that all resources not included in the resources list stay untouched.
 * All included resources are updated or created.
 */
class MarathonUpdateStrategy extends BaseUpdateStrategy implements MarathonStrategy {

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MarathonUpdateStrategy.class);

    @Override
    public void deploy(List<MarathonResource> resources) throws ResourceException {
        LOGGER.info(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_MESSAGE_UPDATING_RESOURCES_STARTED"));

        // Update existing resources and create new ones
        for (Resource resource : resources) {
            super.deploy(resource);
        }

        LOGGER.info(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_MESSAGE_UPDATING_RESOURCES_DONE"));
    }
}
