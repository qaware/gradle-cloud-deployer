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
package de.qaware.cloud.deployer.marathon;

import de.qaware.cloud.deployer.commons.BaseDeployer;
import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfigFactory;
import de.qaware.cloud.deployer.marathon.resource.MarathonResourceFactory;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import de.qaware.cloud.deployer.marathon.update.MarathonUpdateStrategy;
import de.qaware.cloud.deployer.marathon.update.MarathonUpdateStrategyFactory;

import java.io.File;
import java.util.List;

/**
 * Offers the possibility to deploy a list of marathon config files to a marathon cloud.
 */
public class MarathonDeployer extends BaseDeployer<EnvironmentConfig> {

    /**
     * Creates a new marathon deployer for the specified environment.
     *
     * @param environmentConfig The environment.
     */
    public MarathonDeployer(EnvironmentConfig environmentConfig) {
        super(environmentConfig);
    }

    /**
     * Deploys the list of marathon config files.
     *
     * @param files The marathon config files to deploy.
     * @throws ResourceConfigException If a problem during config parsing and interpretation occurs.
     * @throws ResourceException       If a problem during resource deletion/creation occurs.
     */
    public void deploy(List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Read and create resource configs
        EnvironmentConfig environmentConfig = getEnvironmentConfig();
        MarathonResourceConfigFactory resourceConfigFactory = new MarathonResourceConfigFactory();
        List<MarathonResourceConfig> resourceConfigs = resourceConfigFactory.createConfigs(files);

        // 2. Create a resource factory for the specified namespace
        MarathonResourceFactory resourceFactory = new MarathonResourceFactory(environmentConfig);

        // 3. Create the resources for the configs out of step 1.
        List<MarathonResource> resources = resourceFactory.createResources(resourceConfigs);

        // 4. Retrieve a update strategy
        MarathonUpdateStrategy updateStrategy = MarathonUpdateStrategyFactory.create(environmentConfig.getUpdateStrategy());

        // 5. Deploy the resources using the strategy
        updateStrategy.deploy(resources);
    }
}
