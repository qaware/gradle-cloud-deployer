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
package de.qaware.cloud.deployer.commons;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;

import java.io.File;
import java.util.List;

/**
 * Defines a deployer's functionality.
 */
public interface Deployer {

    /**
     * Deploys the list of config files.
     *
     * @throws ResourceConfigException If a problem during config parsing and interpretation occurs.
     * @throws ResourceException       If a problem during resource deletion/creation occurs.
     */
    void deploy(List<File> files) throws ResourceConfigException, ResourceException;

    /**
     * Deletes the list of resources in the cloud.
     *
     * @param files The files which define the resources to delete.
     * @throws ResourceConfigException If an error during config parsing and interpretation occurs.
     * @throws ResourceException       If an error during resource deletion occurs.
     */
    void delete(List<File> files) throws ResourceConfigException, ResourceException;
}
