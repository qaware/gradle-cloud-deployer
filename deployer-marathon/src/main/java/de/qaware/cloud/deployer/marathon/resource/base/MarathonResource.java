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
package de.qaware.cloud.deployer.marathon.resource.base;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import okhttp3.MediaType;

/**
 * Represents a marathon resource. It offers functionality for deletion and creation.
 */
public abstract class MarathonResource extends BaseResource<MarathonResourceConfig> {

    /**
     * Creates a new marathon resource using the specified config and client factory.
     *
     * @param resourceConfig The config this resource belongs to.
     * @param clientFactory The factory which is used to build the clients for backend communication.
     */
    public MarathonResource(MarathonResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(resourceConfig, clientFactory);
    }

    @Override
    protected MediaType createMediaType() throws ResourceException {
        // Override because only json is support.
        switch (getResourceConfig().getContentType()) {
            case JSON:
                return MediaType.parse("application/json");
            default:
                throw new ResourceException("Unknown type " + getResourceConfig().getContentType());
        }
    }
}
