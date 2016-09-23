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

import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.KubernetesDeployer;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.marathon.MarathonDeployer;
import de.qaware.cloud.deployer.plugin.config.cloud.EnvironmentConfigFactory;
import de.qaware.cloud.deployer.plugin.extension.DeployerExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.internal.tasks.options.Option;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.List;
import java.util.Map;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * Represents a task which deploys one specified environment.
 */
public class DeployTask extends DefaultTask {

    /**
     * The selected environment.
     */
    private String environment;

    /**
     * Sets the environment name. This method may also be called when
     * the environment name is set via the command line option.
     *
     * @param environment The environment name.
     */
    @Option(option = "environment", description = "The environment that will be used.")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * Deploys the environment with the specified id.
     *
     * @throws ResourceException          If a error during resource interaction with the backend occurs.
     * @throws ResourceConfigException    If a error during config creation/parsing occurs.
     * @throws EnvironmentConfigException If a error during config creation occurs.
     */
    @TaskAction
    public void deploy() throws ResourceException, ResourceConfigException, EnvironmentConfigException {

        // Retrieve project
        Project project = getProject();

        // Retrieve the id of the environment to deploy
        if (environment == null || environment.isEmpty()) {
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_EMPTY_ID"));
        }

        // Retrieve the configuration
        DeployerExtension deployerExtension = project.getExtensions().findByType(DeployerExtension.class);

        // Map the configurations
        Map<EnvironmentConfig, List<File>> marathonConfigs = EnvironmentConfigFactory.createEnvironmentConfigs(deployerExtension.getMarathonConfigs());
        Map<EnvironmentConfig, List<File>> kubernetesConfigs = EnvironmentConfigFactory.createKubernetesEnvironmentConfigs(deployerExtension.getKubernetesConfigs());

        // Is it a marathon environment?
        Map.Entry<EnvironmentConfig, List<File>> environmentConfig = retrieveEnvironment(marathonConfigs, environment);
        if (environmentConfig != null) {
            MarathonDeployer deployer = new MarathonDeployer(environmentConfig.getKey());
            deployer.deploy(environmentConfig.getValue());
            return;
        }

        // Is it a kubernetes environment?
        environmentConfig = retrieveEnvironment(kubernetesConfigs, environment);
        if (environmentConfig != null) {
            KubernetesDeployer deployer = new KubernetesDeployer((KubernetesEnvironmentConfig) environmentConfig.getKey());
            deployer.deploy(environmentConfig.getValue());
            return;
        }

        // Throw an error if an environment with the specified id doesn't exist
        throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_ID_DOES_NOT_EXIST", environment));
    }

    /**
     * Retrieves the environment with the specified id out of the map with all environments.
     *
     * @param allEnvironments A map containing all environments.
     * @param environmentId The id of the environment to retrieve.
     * @return The environment with the specified id, or NULL if not found.
     */
    private Map.Entry<EnvironmentConfig, List<File>> retrieveEnvironment(Map<EnvironmentConfig, List<File>> allEnvironments, String environmentId) {
        for (Map.Entry<EnvironmentConfig, List<File>> environment : allEnvironments.entrySet()) {
            if (environment.getKey().getId().equals(environmentId)) {
                return environment;
            }
        }
        return null;
    }
}
