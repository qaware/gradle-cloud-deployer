package de.qaware.cloud.deployer.kubernetes;

import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.update.HardUpdateStrategy;
import de.qaware.cloud.deployer.kubernetes.update.UpdateStrategy;
import de.qaware.cloud.deployer.kubernetes.update.UpdateStrategyFactory;

import java.io.File;
import java.util.List;

public class KubernetesDeployer {

    public void delete(CloudConfig cloudConfig, String namespace) throws ResourceConfigException, ResourceException {
        // 1. Create a resource factory for the specified namespace
        ResourceFactory resourceFactory = new ResourceFactory(namespace, cloudConfig);

        // 2. Create the namespace resource
        NamespaceResource namespaceResource = resourceFactory.getNamespaceResource();

        // 3. Delete the namespace
        HardUpdateStrategy.deleteNamespace(namespaceResource);
    }

    public void deploy(CloudConfig cloudConfig, String namespace, List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Read and create resource configs
        List<ResourceConfig> resourceConfigs = ResourceConfigFactory.getConfigs(files);

        // 2. Create a resource factory for the specified namespace
        ResourceFactory resourceFactory = new ResourceFactory(namespace, cloudConfig);

        // 3. Create the resources for the configs out of step 1.
        List<Resource> resources = resourceFactory.createResources(resourceConfigs);

        // 4. Create the namespace resource
        NamespaceResource namespaceResource = resourceFactory.getNamespaceResource();

        // 5. Retrieve a update strategy
        UpdateStrategy updateStrategy = UpdateStrategyFactory.create(cloudConfig.getUpdateStrategy());

        // 6. Deploy the resources using the strategy
        updateStrategy.deploy(namespaceResource, resources);
    }
}
