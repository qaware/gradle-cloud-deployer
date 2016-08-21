package de.qaware.cloud.deployer.plugin.task;

import de.qaware.cloud.deployer.kubernetes.KubernetesDeployer;
import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.plugin.DeployerExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DeployTask extends DefaultTask {

    @TaskAction
    public void deploy() {
        DeployerExtension extension = getProject().getExtensions().findByType(DeployerExtension.class);
        SSLConfig sslConfig = new SSLConfig(extension.isTrustAll(), extension.getCertificate());
        CloudConfig cloudConfig = new CloudConfig(extension.getBaseUrl(), extension.getUsername(), extension.getPassword(), sslConfig);
        String namespace = extension.getNamespace();

        List<File> files = Arrays.asList(extension.getFiles());
        KubernetesDeployer deployer = new KubernetesDeployer();
        deployer.deploy(cloudConfig, namespace, files);
    }
}
