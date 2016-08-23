package de.qaware.cloud.deployer.kubernetes.update;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SoftUpdateStrategy extends BaseUpdateStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoftUpdateStrategy.class);

    @Override
    public void deploy(NamespaceResource namespaceResource, List<Resource> resources) throws ResourceException {

        // 1. Create the namespace if it doesn't exist
        if (!namespaceResource.exists()) {
            createNamespace(namespaceResource);
        }

        // 2. Update existing resources (delete and create again) and create new ones
        deployResources(resources);
    }

    private static void deployResources(List<Resource> resources) throws ResourceException {
        LOGGER.info("Deploying resources...");

        for (Resource resource : resources) {
            if (resource.exists()) {
                LOGGER.info("- " + resource + " (updated)");
                resource.delete();
            } else {
                LOGGER.info("- " + resource + " (created)");
            }
            resource.create();
        }

        LOGGER.info("Finished deploying resources...");
    }
}
