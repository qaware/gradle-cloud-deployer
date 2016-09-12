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
package de.qaware.cloud.deployer.kubernetes.config.namespace;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a kubernetes namespace as specified in the kubernetes api.
 */
class Namespace implements Serializable {

    /**
     * The api version of the namespace.
     */
    private final String apiVersion;

    /**
     * The kind of the namespace.
     */
    private final String kind;

    /**
     * A map containing the metadata.
     */
    private final Map<String, String> metadata = new HashMap<>();

    /**
     * Creates a new namespace.
     *
     * @param name       The name of the new namespace.
     * @param apiVersion The api version of the namespace.
     * @param kind       The kind of the namespace.
     */
    Namespace(final String name, String apiVersion, String kind) {
        this.apiVersion = apiVersion;
        this.kind = kind;
        metadata.put("name", name);
    }

    /**
     * Returns the api version of the namespace.
     *
     * @return The api version.
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Returns the kind of namespace.
     *
     * @return The kind of namespace.
     */
    public String getKind() {
        return kind;
    }

    /**
     * Returns the metadata.
     *
     * @return The metadata.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }
}
