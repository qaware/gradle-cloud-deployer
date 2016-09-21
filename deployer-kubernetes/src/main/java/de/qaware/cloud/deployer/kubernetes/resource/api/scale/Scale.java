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
package de.qaware.cloud.deployer.kubernetes.resource.api.scale;

import de.qaware.cloud.deployer.kubernetes.resource.api.KubernetesApiObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a kubernetes scale as specified in the kubernetes api.
 */
public class Scale extends KubernetesApiObject {

    /**
     * The inner scale specification.
     */
    private final ScaleSpec spec;

    /**
     * A map containing the metadata.
     */
    private final Map<String, String> metadata = new HashMap<>();

    /**
     * Creates a new scale object using the specified params.
     *
     * @param apiVersion The api version to use.
     * @param kind       The kind of the scale.
     * @param name       The name of the scale.
     * @param namespace  The namespace of the scale.
     * @param replicas   The number of replicas to use.
     */
    public Scale(String apiVersion, String kind, String name, String namespace, int replicas) {
        super(apiVersion, kind);
        spec = new ScaleSpec(replicas);
        metadata.put("name", name);
        metadata.put("namespace", namespace);
    }

    /**
     * Returns the metadata.
     *
     * @return The metadata.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Returns the spec.
     *
     * @return The spec.
     */
    public ScaleSpec getSpec() {
        return spec;
    }
}
