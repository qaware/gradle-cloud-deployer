package de.qaware.cloud.deployer.kubernetes;

import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.DeletableResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class KubernetesDeployer {

    public void deploy(CloudConfig cloudConfig, String namespace, List<File> files) {
        try {
            // 1. Read and create resource configs
            List<ResourceConfig> resourceConfigs = ResourceConfigFactory.getConfigs(files);

            // 2. Create a resource factory for the specified namespace
            ResourceFactory resourceFactory = new ResourceFactory(namespace, cloudConfig);

            // 3. Create the resources for the configs out of step 1.
            List<Resource> resources = resourceFactory.createResources(resourceConfigs);

            // 4. Create the namespace resource
            DeletableResource namespaceResource = resourceFactory.getNamespaceResource();

            // 5a. Delete the namespace if it already exists
            if (namespaceResource.exists()) {
                namespaceResource.delete();
            }

            // 5b. Create the namespace
            namespaceResource.create();

            // 5. Create the resources
            resources.forEach(Resource::create);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
