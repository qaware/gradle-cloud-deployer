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
package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.kubernetes.resource.base.ScaleSpecDescription;

import java.util.HashMap;
import java.util.Map;

public class DeploymentScaleDescription {

    private final String apiVersion = "extensions/v1beta1";
    private final String kind = "Scale";
    private final ScaleSpecDescription spec;
    private Map<String, String> metadata = new HashMap<>();

    public DeploymentScaleDescription(String name, String namespace, int replicas) {
        spec = new ScaleSpecDescription(replicas);
        metadata.put("name", name);
        metadata.put("namespace", namespace);
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getKind() {
        return kind;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public ScaleSpecDescription getSpec() {
        return spec;
    }
}
