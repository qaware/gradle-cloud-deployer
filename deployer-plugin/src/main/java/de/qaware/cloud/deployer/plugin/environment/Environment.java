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

import java.io.File;
import java.util.List;

/**
 * Represents a environment.
 */
public class Environment {

    /**
     * The environments id.
     */
    private final String id;

    /**
     * The environments config.
     */
    private final EnvironmentConfig config;

    /**
     * The deployer for this environment.
     */
    private final Deployer deployer;

    /**
     * The list of files which belong to this environment.
     */
    private final List<File> files;

    /**
     * Creates a new deployer using the specified parameters.
     *
     * @param config   The environment's config.
     * @param deployer The environment's deployer.
     * @param files    The environment's files.
     */
    Environment(EnvironmentConfig config, Deployer deployer, List<File> files) {
        this.id = config.getId();
        this.config = config;
        this.deployer = deployer;
        this.files = files;
    }

    /**
     * Returns the id of this environment.
     *
     * @return The id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the config of this environment.
     *
     * @return The config.
     */
    public EnvironmentConfig getConfig() {
        return config;
    }

    /**
     * Returns the deployer of this environment.
     *
     * @return The deployer.
     */
    public Deployer getDeployer() {
        return deployer;
    }

    /**
     * Returns the files of this environment.
     *
     * @return The files.
     */
    public List<File> getFiles() {
        return files;
    }
}
