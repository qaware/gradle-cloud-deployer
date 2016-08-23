package de.qaware.cloud.deployer.kubernetes.update;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseUpdateStrategy implements UpdateStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUpdateStrategy.class);

    public void createNamespace(Resource namespaceResource) throws ResourceException {
        LOGGER.info("Creating namespace...");

        LOGGER.info("- " + namespaceResource);
        namespaceResource.create();

        LOGGER.info("Finished creating namespace...");
    }
}
