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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;

public final class NamespaceResourceConfigFactory {

    private NamespaceResourceConfigFactory() {
    }

    public static KubernetesResourceConfig create(String name) throws ResourceConfigException {

        if (name == null || name.isEmpty()) {
            throw new ResourceConfigException("Please specify a valid namespace");
        }

        try {
            Namespace namespace = new Namespace(name);
            String namespaceDescriptionContent = new ObjectMapper(new JsonFactory()).writeValueAsString(namespace);
            return new KubernetesResourceConfig("temporary", ContentType.JSON, namespaceDescriptionContent);
        } catch (JsonProcessingException e) {
            throw new ResourceConfigException("Couldn't create namespace content", e);
        }
    }
}
