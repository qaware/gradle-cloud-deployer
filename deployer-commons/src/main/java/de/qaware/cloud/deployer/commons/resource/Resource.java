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
package de.qaware.cloud.deployer.commons.resource;


import de.qaware.cloud.deployer.commons.error.ResourceException;

/**
 * Specifies the operations a resource has to offer.
 */
public interface Resource {

    /**
     * Indicates whether the resource exists in the cloud or not.
     *
     * @return TRUE if the resource already exists, FALSE otherwise.
     * @throws ResourceException If the request wasn't successful.
     */
    boolean exists() throws ResourceException;

    /**
     * Creates the resource.
     *
     * @throws ResourceException If the request wasn't successful.
     */
    void create() throws ResourceException;

    /**
     * Deletes the resource.
     *
     * @throws ResourceException If the request wasn't successful.
     */
    void delete() throws ResourceException;

    /**
     * Updates the resource.
     *
     * @throws ResourceException If an error during updating occurs.
     */
    void update() throws ResourceException;
}
