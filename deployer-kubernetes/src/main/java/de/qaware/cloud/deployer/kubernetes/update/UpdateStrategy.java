package de.qaware.cloud.deployer.kubernetes.update;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;

import java.util.List;

public interface UpdateStrategy {

    void deploy(NamespaceResource namespaceResource, List<Resource> resources) throws ResourceException;
}
