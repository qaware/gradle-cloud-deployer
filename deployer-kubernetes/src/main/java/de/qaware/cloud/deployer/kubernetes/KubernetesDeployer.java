package de.qaware.cloud.deployer.kubernetes;

import de.qaware.cloud.deployer.kubernetes.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.DeletableResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class KubernetesDeployer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesDeployer.class);

    public void delete(CloudConfig cloudConfig, String namespace) throws ResourceConfigException, ResourceException {
        // 1. Create a resource factory for the specified namespace
        ResourceFactory resourceFactory = new ResourceFactory(namespace, cloudConfig);

        // 2. Create the namespace resource
        DeletableResource namespaceResource = resourceFactory.getNamespaceResource();

        // 3. Delete the namespace
        resetNamespace(namespaceResource);
    }

    public void deploy(CloudConfig cloudConfig, String namespace, List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Read and create resource configs
        List<ResourceConfig> resourceConfigs = ResourceConfigFactory.getConfigs(files);

        // 2. Create a resource factory for the specified namespace
        ResourceFactory resourceFactory = new ResourceFactory(namespace, cloudConfig);

        // 3. Create the resources for the configs out of step 1.
        List<Resource> resources = resourceFactory.createResources(resourceConfigs);

        // 4. Create the namespace resource
        DeletableResource namespaceResource = resourceFactory.getNamespaceResource();

        // 5a. Delete the namespace if it already exists
        resetNamespace(namespaceResource);

        // 5b. Create the namespace
        createNamespace(namespaceResource);

        // 6. Create the resources
        createResources(resources);
    }

    private static void createResources(List<Resource> resources) throws ResourceException {
        LOGGER.info("Deploying resources...");

        for (Resource resource : resources) {
            LOGGER.info("- " + resource);
            resource.create();
        }

        LOGGER.info("Finished deploying resources...");
    }

    private static void resetNamespace(DeletableResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            LOGGER.info("Removing namespace...");

            LOGGER.info("- " + namespaceResource);
            namespaceResource.delete();

            LOGGER.info("Finished removing namespace...");
        }
    }

    private static void createNamespace(Resource namespaceResource) throws ResourceException {
        LOGGER.info("Deploying namespace...");

        LOGGER.info("- " + namespaceResource);
        namespaceResource.create();

        LOGGER.info("Finished deploying namespace...");
    }
}
