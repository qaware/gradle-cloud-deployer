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
package de.qaware.cloud.deployer.kubernetes.resource.base;

import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public abstract class KubernetesResource extends BaseResource {

    private final String namespace;
    private final ResourceConfig resourceConfig;

    public KubernetesResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(clientFactory);
        this.namespace = namespace;
        this.resourceConfig = resourceConfig;
    }

    public String getId() {
        return resourceConfig.getResourceId();
    }

    public String getNamespace() {
        return namespace;
    }

    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public RequestBody createRequestBody() {
        return RequestBody.create(createMediaType(), resourceConfig.getContent());
    }

    public MediaType createMediaType() {
        switch (resourceConfig.getContentType()) {
            case JSON:
                return MediaType.parse("application/json");
            case YAML:
                return MediaType.parse("application/yaml");
            default:
                throw new IllegalArgumentException("Unknown type " + resourceConfig.getContentType());
        }
    }
}
