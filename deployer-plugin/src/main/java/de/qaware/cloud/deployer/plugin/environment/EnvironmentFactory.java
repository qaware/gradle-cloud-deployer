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
package de.qaware.cloud.deployer.plugin.environment;

import de.qaware.cloud.deployer.commons.Deployer;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.plugin.extension.DeployerExtension;
import de.qaware.cloud.deployer.plugin.extension.EnvironmentExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory that creates environments.
 */
public final class EnvironmentFactory {

    /**
     * UTILITY.
     */
    private EnvironmentFactory() {
    }

    /**
     * Creates a list of environments as specified in the deployer extension.
     *
     * @param deployerExtension A extension containing all environments to initialize.
     * @return The list of created environments.
     * @throws EnvironmentConfigException If an error during environment creation occurs.
     */
    public static List<Environment> create(DeployerExtension deployerExtension) throws EnvironmentConfigException {
        List<Environment> environments = new ArrayList<>();
        for (EnvironmentExtension environmentExtension : deployerExtension.getConfigs()) {
            Environment environment = createEnvironment(environmentExtension);
            environments.add(environment);
        }
        return environments;
    }

    /**
     * Creates a single environment as specified in the environment extension.
     *
     * @param extension A extension which contains all environment information.
     * @return The created environment.
     * @throws EnvironmentConfigException If an error during environment creation occurs.
     */
    private static Environment createEnvironment(EnvironmentExtension extension) throws EnvironmentConfigException {
        EnvironmentConfig config = EnvironmentConfigFactory.create(extension);
        Deployer deployer = DeployerFactory.create(extension, config);
        return new Environment(config, deployer, extension.getFiles());
    }
}
