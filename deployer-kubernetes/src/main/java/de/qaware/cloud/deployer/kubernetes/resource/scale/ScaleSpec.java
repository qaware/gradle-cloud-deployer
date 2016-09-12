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
package de.qaware.cloud.deployer.kubernetes.resource.scale;

import java.io.Serializable;

/**
 * Represents a scale spec object as specified in the kubernetes api.
 */
class ScaleSpec implements Serializable {

    /**
     * The number of replicas.
     */
    private final int replicas;

    /**
     * Creates a new scale spec object using the specified number of replicas.
     *
     * @param replicas The number of replicas.
     */
    ScaleSpec(int replicas) {
        this.replicas = replicas;
    }

    /**
     * Returns the number of replicas.
     *
     * @return The number of replicas.
     */
    public int getReplicas() {
        return replicas;
    }
}
