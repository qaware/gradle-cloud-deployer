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
package de.qaware.cloud.deployer.commons;

import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;

/**
 * The base implementation of a deployer.
 *
 * @param <ConfigType> The type of config this deployer consumes.
 */
public abstract class BaseDeployer<ConfigType extends EnvironmentConfig> implements Deployer {

    /**
     * The environment this deployer is defined for.
     */
    private final ConfigType environmentConfig;

    /**
     * Creates a new base deployer for the specified environment.
     *
     * @param environmentConfig The environment this deployer is defined for.
     */
    public BaseDeployer(ConfigType environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    /**
     * Returns the environment config of this deployer.
     *
     * @return The environment.
     */
    public ConfigType getEnvironmentConfig() {
        return environmentConfig;
    }
}
