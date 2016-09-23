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
package de.qaware.cloud.deployer.plugin.task;

import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.plugin.environment.Environment;
import de.qaware.cloud.deployer.plugin.environment.EnvironmentFactory;
import de.qaware.cloud.deployer.plugin.extension.DeployerExtension;
import org.gradle.api.DefaultTask;

import java.util.List;

/**
 * Implements basic functionality for a task which uses all environments.
 */
abstract class BaseAllEnvironmentsTask extends DefaultTask {

    /**
     * Contains all environments.
     */
    private List<Environment> environments;

    /**
     * Creates a new base all environments task. It sets up all environments.
     *
     * @throws EnvironmentConfigException If an error during environment setup occurs.
     */
    BaseAllEnvironmentsTask() throws EnvironmentConfigException {
        setupEnvironments();
    }

    /**
     * Returns all environments.
     *
     * @return All environments.
     */
    List<Environment> getEnvironments() {
        return environments;
    }

    /**
     * Sets up the environments.
     *
     * @throws EnvironmentConfigException If an error during environment setup occurs.
     */
    private void setupEnvironments() throws EnvironmentConfigException {

        // Retrieve the deployer configuration
        DeployerExtension deployerExtension = getProject().getExtensions().findByType(DeployerExtension.class);

        // Build environments
        environments = EnvironmentFactory.create(deployerExtension);
    }
}
