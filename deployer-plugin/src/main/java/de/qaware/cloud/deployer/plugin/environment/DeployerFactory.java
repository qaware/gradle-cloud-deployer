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
import de.qaware.cloud.deployer.kubernetes.KubernetesDeployer;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.marathon.MarathonDeployer;
import de.qaware.cloud.deployer.plugin.extension.DeployerType;
import de.qaware.cloud.deployer.plugin.extension.EnvironmentExtension;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * A factory which creates deployers.
 */
final class DeployerFactory {

    /**
     * UTILITY.
     */
    private DeployerFactory() {
    }

    /**
     * Creates a deployer using the specified extension and config.
     *
     * @param extension         The extension which specifies the type of deployer to create.
     * @param environmentConfig The config which is used to initialize the deployer.
     * @return The created deployer.
     * @throws EnvironmentConfigException If an error during deployer creation occurs.
     */
    static Deployer create(EnvironmentExtension extension, EnvironmentConfig environmentConfig) throws EnvironmentConfigException {
        if (extension == null || environmentConfig == null) {
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EXTENSION_OR_CONFIG_NULL"));
        }

        DeployerType deployerType = extension.getDeployerType();
        if (deployerType == null) {
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_DEPLOYER_TYPE_NULL"));
        }

        Deployer deployer;
        switch (deployerType) {
            case KUBERNETES:
                if (environmentConfig instanceof KubernetesEnvironmentConfig) {
                    deployer = new KubernetesDeployer((KubernetesEnvironmentConfig) environmentConfig);
                } else {
                    throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_INVALID_KUBERNETES_CONFIG"));
                }
                break;
            case MARATHON:
                deployer = new MarathonDeployer(environmentConfig);
                break;
            default:
                throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_INVALID_DEPLOYER_TYPE", deployerType));
        }
        return deployer;
    }
}
