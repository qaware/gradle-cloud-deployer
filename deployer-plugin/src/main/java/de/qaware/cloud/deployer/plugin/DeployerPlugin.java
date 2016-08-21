package de.qaware.cloud.deployer.plugin;

import de.qaware.cloud.deployer.plugin.task.DeployTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DeployerPlugin implements Plugin<Project> {

    public void apply(Project project) {
        project.getExtensions().create("deployer", DeployerExtension.class);
        project.getTasks().create("deploy", DeployTask.class);
    }
}
