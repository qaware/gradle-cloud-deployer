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
package de.qaware.cloud.deployer.kubernetes.resource.api.delete.options;


import de.qaware.cloud.deployer.kubernetes.resource.api.KubernetesApiObject;

import java.io.Serializable;

/**
 * Represents a delete options object of the kubernetes api.
 */
public class DeleteOptions extends KubernetesApiObject implements Serializable {

    /**
     * The api version.
     */
    private static final String API_VERSION = "v1";

    /**
     * The kind.
     */
    private static final String KIND = "DeleteOptions";

    /**
     * The amount of seconds before the resource will be deleted.
     */
    private final int gracePeriodSeconds;

    /**
     * Creates a new delete options object.
     *
     * @param gracePeriodSeconds The amount of seconds before the resource will be deleted.
     */
    public DeleteOptions(int gracePeriodSeconds) {
        super(API_VERSION, KIND);
        this.gracePeriodSeconds = gracePeriodSeconds;
    }

    /**
     * Returns the grace period seconds.
     *
     * @return The grace period seconds.
     */
    public int getGracePeriodSeconds() {
        return gracePeriodSeconds;
    }
}
