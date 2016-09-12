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

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesCloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.resource.KubernetesResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceUtil;
import de.qaware.cloud.deployer.kubernetes.update.KubernetesUpdateStrategy;
import de.qaware.cloud.deployer.kubernetes.update.KubernetesUpdateStrategyFactory;

import java.io.File;
import java.util.List;

/**
 * Offers the possibility to deploy a list of kubernetes config files to a kubernetes cloud.
 */
public class KubernetesDeployer {

    /**
     * Deletes the specified namespace in the specified cloud.
     *
     * @param cloudConfig The config which describes the cloud.
     * @throws ResourceConfigException If a problem during config parsing and interpretation occurs.
     * @throws ResourceException       If a problem during resource deletion/creation occurs.
     */
    public void delete(KubernetesCloudConfig cloudConfig) throws ResourceConfigException, ResourceException {
        // 1. Create a resource factory for the specified namespace
        KubernetesResourceFactory resourceFactory = new KubernetesResourceFactory(cloudConfig);

        // 2. Create the namespace resource
        NamespaceResource namespaceResource = resourceFactory.getNamespaceResource();

        // 3. Delete the namespace
        NamespaceUtil.safeDeleteNamespace(namespaceResource);
    }

    /**
     * Deploys the list of kubernetes config files to the specified cloud in the specified namespace.
     *
     * @param cloudConfig The config which describes the cloud.
     * @throws ResourceConfigException If a problem during config parsing and interpretation occurs.
     * @throws ResourceException       If a problem during resource deletion/creation occurs.
     */
    public void deploy(KubernetesCloudConfig cloudConfig, List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Read and create resource configs
        KubernetesResourceConfigFactory resourceConfigFactory = new KubernetesResourceConfigFactory();
        List<KubernetesResourceConfig> resourceConfigs = resourceConfigFactory.createConfigs(files);

        // 2. Create a resource factory for the specified namespace
        KubernetesResourceFactory resourceFactory = new KubernetesResourceFactory(cloudConfig);

        // 3. Create the resources for the configs out of step 1.
        List<KubernetesResource> resources = resourceFactory.createResources(resourceConfigs);

        // 4. Create the namespace resource
        NamespaceResource namespaceResource = resourceFactory.getNamespaceResource();

        // 5. Retrieve a update strategy
        KubernetesUpdateStrategy updateStrategy = KubernetesUpdateStrategyFactory.create(cloudConfig.getUpdateStrategy());

        // 6. Deploy the resources using the strategy
        updateStrategy.deploy(namespaceResource, resources);
    }
}
