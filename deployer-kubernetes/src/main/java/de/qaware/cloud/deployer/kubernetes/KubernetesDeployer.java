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
package de.qaware.cloud.deployer.kubernetes;

import de.qaware.cloud.deployer.commons.BaseDeployer;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.resource.KubernetesResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.strategy.KubernetesStrategy;
import de.qaware.cloud.deployer.kubernetes.strategy.KubernetesStrategyFactory;

import java.io.File;
import java.util.List;

/**
 * Offers the possibility to deploy a list of kubernetes config files to a kubernetes cloud.
 */
public class KubernetesDeployer extends BaseDeployer<KubernetesEnvironmentConfig> {

    /**
     * Creates a new kubernetes deployer for the specified environment.
     *
     * @param environmentConfig The environment.
     */
    public KubernetesDeployer(KubernetesEnvironmentConfig environmentConfig) {
        super(environmentConfig);
    }

    @Override
    public void deploy(List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Create resources
        EnvironmentResourceContainer resourceContainer = createResources(files);
        NamespaceResource namespaceResource = resourceContainer.namespaceResource;
        List<KubernetesResource> resources = resourceContainer.resources;

        // 2. Retrieve a strategy
        KubernetesStrategy strategy = createStrategy();

        // 3. Deploy the resources using the strategy
        strategy.deploy(namespaceResource, resources);
    }

    @Override
    public void delete(List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Create resources
        EnvironmentResourceContainer resourceContainer = createResources(files);
        NamespaceResource namespaceResource = resourceContainer.namespaceResource;
        List<KubernetesResource> resources = resourceContainer.resources;

        // 2. Retrieve a strategy
        KubernetesStrategy strategy = createStrategy();

        // 3. Delete resources using the strategy
        strategy.delete(namespaceResource, resources);
    }

    /**
     * Creates all resources as defined in the specified config files.
     *
     * @param files The files which contain the resource configuration.
     * @return A container which contains all resources.
     * @throws ResourceConfigException If an error during config parsing and interpretation occurs.
     * @throws ResourceException       If an error during resource creation occurs.
     */
    private EnvironmentResourceContainer createResources(List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Read and create resource configs
        KubernetesEnvironmentConfig environmentConfig = getEnvironmentConfig();
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        List<KubernetesResourceConfig> resourceConfigs = resourceConfigFactory.createConfigs(files);

        // 2. Create a resource factory for the specified namespace
        KubernetesResourceFactory resourceFactory = new KubernetesResourceFactory(environmentConfig);

        // 3. Create the resources for the configs out of step 1.
        List<KubernetesResource> resources = resourceFactory.createResources(resourceConfigs);

        // 4. Create the namespace resource
        NamespaceResource namespaceResource = resourceFactory.getNamespaceResource();

        return new EnvironmentResourceContainer(namespaceResource, resources);
    }

    /**
     * Creates the strategy which is defined in the environment config.
     *
     * @return The created strategy.
     * @throws ResourceException If an error during strategy creation occurs.
     */
    private KubernetesStrategy createStrategy() throws ResourceException {
        return KubernetesStrategyFactory.create(getEnvironmentConfig().getStrategy());
    }

    /**
     * A container which contains all necessary resources for this environment.
     */
    private static final class EnvironmentResourceContainer {
        /**
         * The namespace resource of the environment.
         */
        private final NamespaceResource namespaceResource;

        /**
         * The list of resources which belong to this environment.
         */
        private final List<KubernetesResource> resources;

        /**
         * Creates a new container using the specified params.
         *
         * @param namespaceResource The namespace resource of the environment.
         * @param resources         The resources which belong to this environment.
         */
        private EnvironmentResourceContainer(NamespaceResource namespaceResource, List<KubernetesResource> resources) {
            this.namespaceResource = namespaceResource;
            this.resources = resources;
        }
    }
}
