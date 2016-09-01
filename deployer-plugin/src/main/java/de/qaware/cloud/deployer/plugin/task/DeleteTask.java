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

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.KubernetesDeployer;
import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.plugin.DeployerExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DeleteTask extends DefaultTask {

    @TaskAction
    public void delete() throws ResourceException, ResourceConfigException {
        DeployerExtension extension = getProject().getExtensions().findByType(DeployerExtension.class);
        SSLConfig sslConfig = new SSLConfig(extension.isTrustAll(), extension.getCertificate());
        CloudConfig cloudConfig = new CloudConfig(extension.getBaseUrl(),
                extension.getUsername(),
                extension.getPassword(),
                extension.getUpdateStrategy(),
                sslConfig);
        String namespace = extension.getNamespace();

        KubernetesDeployer deployer = new KubernetesDeployer();
        deployer.delete(cloudConfig, namespace);
    }
}
