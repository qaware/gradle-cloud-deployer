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

import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BasePingResource;
import de.qaware.cloud.deployer.commons.resource.BaseResourceFactory;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.resource.ping.KubernetesPingResource;
import de.qaware.cloud.deployer.kubernetes.resource.pod.PodResource;
import de.qaware.cloud.deployer.kubernetes.resource.replication.controller.ReplicationControllerResource;
import de.qaware.cloud.deployer.kubernetes.resource.service.ServiceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

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
     * @param environmentConfig The config which describes the cloud.
     * @throws ResourceConfigException If a problem during namespace creation occurs.
     * @throws ResourceException       If a problem during client factory creation occurs.
     */
    public KubernetesResourceFactory(KubernetesEnvironmentConfig environmentConfig) throws ResourceConfigException, ResourceException {
        super(environmentConfig);
        KubernetesResourceConfig namespaceResourceConfig = NamespaceResourceConfigFactory.create(environmentConfig.getNamespace());
        this.namespaceResource = new NamespaceResource(namespaceResourceConfig, getClientFactory());
    }

    /**
     * Returns the namespace resource.
     *
     * @return The namespace resource.
     */
    public NamespaceResource getNamespaceResource() {
        return namespaceResource;
    }

    @Override
    public List<KubernetesResource> createResources(List<KubernetesResourceConfig> resourceConfigs) throws ResourceException {
        LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_CREATING_RESOURCES_STARTED"));
        List<KubernetesResource> resources = super.createResources(resourceConfigs);
        LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_CREATING_RESOURCES_DONE"));
        return resources;
    }

    @Override
    public KubernetesResource createResource(KubernetesResourceConfig resourceConfig) throws ResourceException {
        String resourceVersion = resourceConfig.getResourceVersion();
        String resourceType = resourceConfig.getResourceType();
        KubernetesResource resource;

        // Is the content empty?
        if (resourceConfig.getContent().isEmpty()) {
            throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_EMPTY_CONFIG", resourceConfig.getFilename()));
        }

        switch (resourceVersion) {
            case "extensions/v1beta1":
                switch (resourceType) {
                    case "Deployment":
                        resource = new DeploymentResource(namespaceResource.getNamespace(), resourceConfig, getClientFactory());
                        break;
                    default:
                        throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_RESOURCE_TYPE", resourceConfig.getFilename()));
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
                        throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_RESOURCE_TYPE", resourceConfig.getFilename()));
                }
                break;
            default:
                throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNKNOWN_API_VERSION", resourceConfig.getFilename()));
        }

        LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_CREATING_RESOURCES_SINGLE_RESOURCE", resource));

        return resource;
    }

    @Override
    public BasePingResource createPingResource(EnvironmentConfig environmentConfig) throws ResourceException {
        return new KubernetesPingResource(environmentConfig);
    }
}
