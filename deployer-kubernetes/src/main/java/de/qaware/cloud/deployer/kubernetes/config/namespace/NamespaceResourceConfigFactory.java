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

import de.qaware.cloud.deployer.commons.config.resource.ContentTreeUtil;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * A factory which creates a resource config for a namespace.
 */
public final class NamespaceResourceConfigFactory {

    /**
     * The namespace's filename that will be used for creation.
     */
    private static final String NAMESPACE_DEFAULT_FILENAME = "temporary";

    /**
     * The kind of the namespace as specified in the kubernetes api.
     */
    private static final String NAMESPACE_KIND = "Namespace";

    /**
     * The api version of the namespace as specified in the kubernetes api.
     */
    private static final String NAMESPACE_VERSION = "v1";

    /**
     * UTILITY.
     */
    private NamespaceResourceConfigFactory() {
    }

    /**
     * Creates a resource config for a namespace with the specified name.
     *
     * @param name The name of the namespace.
     * @return The resource config for the namespace with the specified name.
     * @throws ResourceConfigException If the namespace name isn't valid or a error occurred during namespace creation.
     */
    public static KubernetesResourceConfig create(String name) throws ResourceConfigException {

        if (name == null || name.isEmpty()) {
            throw new ResourceConfigException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_INVALID_NAMESPACE"));
        }

        try {
            Namespace namespace = new Namespace(name, NAMESPACE_VERSION, NAMESPACE_KIND);
            String namespaceDescriptionContent = ContentTreeUtil.writeAsString(ContentType.JSON, namespace);
            return new KubernetesResourceConfig(NAMESPACE_DEFAULT_FILENAME, ContentType.JSON, namespaceDescriptionContent);
        } catch (ResourceConfigException e) {
            throw new ResourceConfigException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_DURING_NAMESPACE_CREATION"), e);
        }
    }
}
