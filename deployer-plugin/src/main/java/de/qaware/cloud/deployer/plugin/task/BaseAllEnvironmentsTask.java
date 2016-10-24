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

import de.qaware.cloud.deployer.commons.Deployer;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.plugin.environment.Environment;
import de.qaware.cloud.deployer.plugin.environment.EnvironmentFactory;
import de.qaware.cloud.deployer.plugin.extension.DeployerExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static de.qaware.cloud.deployer.plugin.task.ExtendedExceptionMessageUtil.createExtendedMessage;

/**
 * Implements basic functionality for a task which uses all environments.
 */
public abstract class BaseAllEnvironmentsTask extends DefaultTask {

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAllEnvironmentsTask.class);

    /**
     * Contains all environments.
     */
    @Internal
    private List<Environment> environments;

    /**
     * Deletes the specified environment.
     *
     * @param environment The environment that will be deleted.
     * @throws ResourceException       If a error during resource interaction with the backend occurs.
     * @throws ResourceConfigException If a error during config creation/parsing occurs.
     */
    void delete(Environment environment) throws ResourceConfigException, ResourceException {
        Deployer deployer = environment.getDeployer();
        List<File> files = environment.getFiles();

        LOGGER.info(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_DELETING_ENVIRONMENT_STARTED", environment.getId()));
        try {
            deployer.delete(files);
        } catch (ResourceConfigException e) {
            throw new ResourceConfigException(createExtendedMessage(environment, e.getMessage()), e);
        } catch (ResourceException e) {
            throw new ResourceException(createExtendedMessage(environment, e.getMessage()), e);
        }
        LOGGER.info(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_DELETING_ENVIRONMENT_DONE", environment.getId()));
    }

    /**
     * Deploys the specified environment.
     *
     * @param environment The environment that will be deployed.
     * @throws ResourceException       If a error during resource interaction with the backend occurs.
     * @throws ResourceConfigException If a error during config creation/parsing occurs.
     */
    void deploy(Environment environment) throws ResourceConfigException, ResourceException {
        Deployer deployer = environment.getDeployer();
        List<File> files = environment.getFiles();

        LOGGER.info(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_DEPLOYING_ENVIRONMENT_STARTED", environment.getId()));
        try {
            deployer.deploy(files);
        } catch (ResourceConfigException e) {
            throw new ResourceConfigException(createExtendedMessage(environment, e.getMessage()), e);
        } catch (ResourceException e) {
            throw new ResourceException(createExtendedMessage(environment, e.getMessage()), e);
        }
        LOGGER.info(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_DEPLOYING_ENVIRONMENT_DONE", environment.getId()));
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
    void setupEnvironments() throws EnvironmentConfigException {

        // Retrieve the deployer configuration
        DeployerExtension deployerExtension = getProject().getExtensions().findByType(DeployerExtension.class);

        // Build environments
        environments = EnvironmentFactory.create(deployerExtension);
    }
}
