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
package de.qaware.cloud.deployer.plugin;

import de.qaware.cloud.deployer.plugin.extension.DeployerExtension;
import de.qaware.cloud.deployer.plugin.task.DeleteAllTask;
import de.qaware.cloud.deployer.plugin.task.DeleteTask;
import de.qaware.cloud.deployer.plugin.task.DeployAllTask;
import de.qaware.cloud.deployer.plugin.task.DeployTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * Specifies all tasks offered and the extension used by the gradle cloud deployer plugin.
 */
public class DeployerPlugin implements Plugin<Project> {

    /**
     * Apply this plugin and it's tasks and extension.
     *
     * @param project The project this plugin is applied to.
     */
    @Override
    public void apply(Project project) {
        project.getExtensions().create("deployer", DeployerExtension.class, project);

        // Deploy tasks
        DeployAllTask deployAllTask = project.getTasks().create("deployAll", DeployAllTask.class);
        deployAllTask.setDescription(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_TASK_DESCRIPTION_DEPLOY_ALL"));
        deployAllTask.setGroup(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_PLUGIN_GROUP"));

        DeployTask deployTask = project.getTasks().create("deploy", DeployTask.class);
        deployTask.setDescription(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_TASK_DESCRIPTION_DEPLOY"));
        deployTask.setGroup(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_PLUGIN_GROUP"));

        // Delete tasks
        DeleteAllTask deleteAllTask = project.getTasks().create("deleteAll", DeleteAllTask.class);
        deleteAllTask.setDescription(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_TASK_DESCRIPTION_DELETE_ALL"));
        deleteAllTask.setGroup(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_PLUGIN_GROUP"));

        DeleteTask deleteTask = project.getTasks().create("delete", DeleteTask.class);
        deleteTask.setDescription(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_TASK_DESCRIPTION_DELETE"));
        deleteTask.setGroup(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_MESSAGES_PLUGIN_GROUP"));
    }
}
