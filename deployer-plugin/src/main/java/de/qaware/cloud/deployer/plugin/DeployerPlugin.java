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
import de.qaware.cloud.deployer.plugin.task.DeployTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

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
        project.getTasks().create("deploy", DeployTask.class);
    }
}
