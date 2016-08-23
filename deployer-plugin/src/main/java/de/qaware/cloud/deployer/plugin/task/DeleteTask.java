package de.qaware.cloud.deployer.plugin.task;

import de.qaware.cloud.deployer.kubernetes.KubernetesDeployer;
import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
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
