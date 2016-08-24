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
package de.qaware.cloud.deployer.kubernetes.resource;

import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ResourceTestEnvironment {

    private NamespaceResource namespaceResource;
    private ClientFactory clientFactory;
    private KubernetesClient kubernetesClient;

    public ResourceTestEnvironment(NamespaceResource namespaceResource, ClientFactory clientFactory, KubernetesClient kubernetesClient) {
        this.namespaceResource = namespaceResource;
        this.clientFactory = clientFactory;
        this.kubernetesClient = kubernetesClient;
    }

    public NamespaceResource getNamespaceResource() {
        return namespaceResource;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public KubernetesClient getKubernetesClient() {
        return kubernetesClient;
    }
}
