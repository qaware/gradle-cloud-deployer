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

import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;

public abstract class MarathonResource extends BaseResource {

    private final MarathonResourceConfig resourceConfig;

    public MarathonResource(MarathonResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(clientFactory);
        this.resourceConfig = resourceConfig;
    }
}
