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
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Represents a task which deploys the specified configuration to the cloud.
 */
public class DeployAllTask extends DefaultTask {

    /**
     * Deploys all environments in the specified configuration.
     *
     * @throws ResourceException          If a error during resource interaction with the backend occurs.
     * @throws ResourceConfigException    If a error during config creation/parsing occurs.
     * @throws EnvironmentConfigException If a error during config creation occurs.
     */
    @TaskAction
    public void deployAll() throws ResourceException, ResourceConfigException, EnvironmentConfigException {
        // Retrieve the configuration
        DeployerExtension extension = getProject().getExtensions().findByType(DeployerExtension.class);

        // Map the configurations
        Map<EnvironmentConfig, List<File>> marathonConfigs = EnvironmentConfigFactory.createEnvironmentConfigs(extension.getMarathonConfigs());
        Map<EnvironmentConfig, List<File>> kubernetesConfigs = EnvironmentConfigFactory.createKubernetesEnvironmentConfigs(extension.getKubernetesConfigs());

        // Call marathon deployer
        for (Map.Entry<EnvironmentConfig, List<File>> environment : marathonConfigs.entrySet()) {
            EnvironmentConfig environmentConfig = environment.getKey();
            List<File> files = environment.getValue();
            MarathonDeployer deployer = new MarathonDeployer(environmentConfig);
            deployer.deploy(files);
        }

        // Call kubernetes deployer
        for (Map.Entry<EnvironmentConfig, List<File>> environment : kubernetesConfigs.entrySet()) {
            KubernetesEnvironmentConfig environmentConfig = (KubernetesEnvironmentConfig) environment.getKey();
            List<File> files = environment.getValue();
            KubernetesDeployer deployer = new KubernetesDeployer(environmentConfig);
            deployer.deploy(files);
        }
    }
}
