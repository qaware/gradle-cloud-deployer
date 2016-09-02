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

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResourceFactory;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarathonResourceFactory extends BaseResourceFactory<MarathonResource, MarathonResourceConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarathonResourceFactory.class);

    public MarathonResourceFactory(CloudConfig cloudConfig) {
        super(LOGGER, new ClientFactory(cloudConfig));
    }

    @Override
    public MarathonResource createResource(MarathonResourceConfig resourceConfig) throws ResourceException {
        return null;
    }
}
