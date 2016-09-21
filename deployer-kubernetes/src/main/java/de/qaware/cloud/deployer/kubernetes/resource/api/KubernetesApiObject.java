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
package de.qaware.cloud.deployer.kubernetes.resource.api;

/**
 * Represents a basic kubernetes api object.
 */
public abstract class KubernetesApiObject {

    /**
     * The api version of the object.
     */
    private final String apiVersion;

    /**
     * The kind of the object.
     */
    private final String kind;

    /**
     * Creates a new kubernetes api object.
     *
     * @param apiVersion The api version of this object.
     * @param kind The kind of this object.
     */
    public KubernetesApiObject(String apiVersion, String kind) {
        this.apiVersion = apiVersion;
        this.kind = kind;
    }

    /**
     * Returns the api version.
     *
     * @return The api version.
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Returns the kind.
     *
     * @return The kind.
     */
    public String getKind() {
        return kind;
    }
}
