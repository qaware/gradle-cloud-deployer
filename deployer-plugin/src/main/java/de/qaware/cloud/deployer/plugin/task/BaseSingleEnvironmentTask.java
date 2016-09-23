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
import org.gradle.api.internal.tasks.options.Option;

import java.util.List;
import java.util.stream.Collectors;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * Implements basic functionality of a task which is executed for a single environment.
 */
abstract class BaseSingleEnvironmentTask extends BaseAllEnvironmentsTask {

    /**
     * The selected environment.
     */
    private String environmentId;

    /**
     * The environment config of the selected environment.
     */
    private Environment environment;

    /**
     * Creates a new base single environment task object and instantiates the selected environment.
     *
     * @throws EnvironmentConfigException If the selected environment doesn't exist.
     */
    BaseSingleEnvironmentTask() throws EnvironmentConfigException {
        super();
        setupEnvironment();
    }

    /**
     * Sets the environment id. This method may also be called when
     * the environment id is set via the command line option.
     *
     * @param environmentId The environment id.
     */
    @Option(option = "environment", description = "The environment that will be used.")
    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    /**
     * Returns the environment this task belongs to.
     *
     * @return The environment.
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Sets up the selected environment.
     *
     * @throws EnvironmentConfigException If an error during environment creation or environment retrieving occurs.
     */
    private void setupEnvironment() throws EnvironmentConfigException {

        // Retrieve the id of the environment to deploy
        if (environmentId == null || environmentId.isEmpty()) {
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_EMPTY_ID"));
        }

        // Build environments
        List<Environment> environments = getEnvironments();

        // Find environment
        environment = retrieveEnvironment(environments, environmentId);
    }

    /**
     * Retrieves a single environment using the specified id.
     *
     * @param environments  A list containing multiple environments.
     * @param environmentId The environment out of the list with the specified id.
     * @return The environment out of the list with the specified id.
     * @throws EnvironmentConfigException If the environment doesn't exist or multiple environments with the same ids exist.
     */
    private Environment retrieveEnvironment(List<Environment> environments, String environmentId) throws EnvironmentConfigException {
        List<Environment> foundEnvironments = environments.stream().filter(environment1 -> environment1.getId().equals(environmentId)).collect(Collectors.toList());
        if (foundEnvironments.isEmpty()) {
            // Throw an error if an environment with the specified id doesn't exist
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_ID_DOES_NOT_EXIST", environmentId));
        } else if (foundEnvironments.size() > 1) {
            // Throw an error if multiple environments with the same id exist
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_ID_DOES_EXIST_SEVERAL_TIMES", environmentId));
        } else {
            // Return environment
            return foundEnvironments.get(0);
        }
    }
}
