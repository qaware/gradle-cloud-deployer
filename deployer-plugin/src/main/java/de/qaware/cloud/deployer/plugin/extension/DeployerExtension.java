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
package de.qaware.cloud.deployer.plugin.extension;

import groovy.lang.Closure;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.Collection;

public class DeployerExtension {

    /**
     * Contains all configs defined in this deployer extension.
     */
    private Collection<EnvironmentExtension> configs = new ArrayList<>();

    /**
     * The project this deployer extension belongs to.
     */
    private Project project;

    /**
     * Creates a new deployer extension.
     *
     * @param project The project this extension belongs to.
     */
    public DeployerExtension(Project project) {
        this.project = project;
    }

    /**
     * Creates a new marathon environment extension.
     *
     * @param closure The closure which contains the environment configuration.
     * @return The new marathon environment extension.
     */
    public EnvironmentExtension marathon(Closure closure) {
        EnvironmentExtension config = (EnvironmentExtension) project.configure(new EnvironmentExtension(project), closure);
        configs.add(config);
        return config;
    }

    /**
     * Creates a new kubernetes environment extension.
     *
     * @param closure The closure which contains the environment configuration.
     * @return The new kubernetes environment extension.
     */
    public EnvironmentExtension kubernetes(Closure closure) {
        EnvironmentExtension config = (EnvironmentExtension) project.configure(new EnvironmentExtension(project), closure);
        configs.add(config);
        return config;
    }

    /**
     * Returns the configs for this deployer extension.
     *
     * @return The configs.
     */
    public Collection<EnvironmentExtension> getConfigs() {
        return configs;
    }
}
