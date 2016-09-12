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

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import okhttp3.MediaType;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * Represents a kubernetes resource. Such a resource offers methods for deletion and creation.
 */
public abstract class KubernetesResource extends BaseResource<KubernetesResourceConfig> {

    /**
     * The resource's namespace.
     */
    private final String namespace;

    /**
     * Creates a new resource using the specified parameters.
     *
     * @param namespace      The namespace of the resource.
     * @param resourceConfig The resource's config.
     * @param clientFactory  The factory which is used for backend communication.
     */
    public KubernetesResource(String namespace, KubernetesResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(resourceConfig, clientFactory);
        this.namespace = namespace;
    }

    /**
     * Returns the namespace.
     *
     * @return The namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    @Override
    protected MediaType createMediaType() throws ResourceException {
        switch (getResourceConfig().getContentType()) {
            case JSON:
                return MediaType.parse("application/json");
            case YAML:
                return MediaType.parse("application/yaml");
            default:
                throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_CONTENT_TYPE", getResourceConfig().getFilename()));
        }
    }
}
