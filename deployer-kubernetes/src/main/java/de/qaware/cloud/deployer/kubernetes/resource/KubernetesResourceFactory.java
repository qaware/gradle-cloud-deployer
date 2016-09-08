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

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResourceFactory;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.resource.pod.PodResource;
import de.qaware.cloud.deployer.kubernetes.resource.replication.controller.ReplicationControllerResource;
import de.qaware.cloud.deployer.kubernetes.resource.service.ServiceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A resource factory which creates kubernetes resources for a special namespace as described in the resource configs.
 */
public class KubernetesResourceFactory extends BaseResourceFactory<KubernetesResource, KubernetesResourceConfig> {

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceFactory.class);

    /**
     * The namespace this resource factory creates objects for.
     */
    private final NamespaceResource namespaceResource;

    /**
     * Creates a new factory which creates resources for the specified namespace using the specified cloud config.
     *
     * @param namespace The namespace this factory creates objects for.
     * @param cloudConfig The config which describes the cloud.
     * @throws ResourceConfigException If a problem during namespace creation occurs.
     * @throws ResourceException If a problem during client factory creation occurs.
     */
    public KubernetesResourceFactory(String namespace, CloudConfig cloudConfig) throws ResourceConfigException, ResourceException {
        super(LOGGER, new ClientFactory(cloudConfig));
        KubernetesResourceConfig namespaceResourceConfig = NamespaceResourceConfigFactory.create(namespace);
        this.namespaceResource = new NamespaceResource(namespaceResourceConfig, getClientFactory());
    }

    public NamespaceResource getNamespaceResource() {
        return namespaceResource;
    }

    @Override
    public KubernetesResource createResource(KubernetesResourceConfig resourceConfig) throws ResourceException {
        String resourceVersion = resourceConfig.getResourceVersion();
        String resourceType = resourceConfig.getResourceType();
        KubernetesResource resource;
        switch (resourceVersion) {
            case "extensions/v1beta1":
                switch (resourceType) {
                    case "Deployment":
                        resource = new DeploymentResource(namespaceResource.getNamespace(), resourceConfig, getClientFactory());
                        break;
                    default:
                        throw new ResourceException("Unknown Kubernetes resource type for api version " + resourceVersion + "(KubernetesResourceConfig: " + resourceConfig + ")");
                }
                break;
            case "v1":
                switch (resourceType) {
                    case "Pod":
                        resource = new PodResource(namespaceResource.getNamespace(), resourceConfig, getClientFactory());
                        break;
                    case "Service":
                        resource = new ServiceResource(namespaceResource.getNamespace(), resourceConfig, getClientFactory());
                        break;
                    case "ReplicationController":
                        resource = new ReplicationControllerResource(namespaceResource.getNamespace(), resourceConfig, getClientFactory());
                        break;
                    default:
                        throw new ResourceException("Unknown Kubernetes resource type for api version " + resourceVersion + "(KubernetesResourceConfig: " + resourceConfig + ")");
                }
                break;
            default:
                throw new ResourceException("Unknown Kubernetes api version (KubernetesResourceConfig: " + resourceConfig + ")");
        }

        LOGGER.info("- " + resource);

        return resource;
    }
}
