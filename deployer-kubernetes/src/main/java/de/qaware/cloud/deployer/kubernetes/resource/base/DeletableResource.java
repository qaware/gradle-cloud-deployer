package de.qaware.cloud.deployer.kubernetes.resource.base;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;

public interface DeletableResource extends Resource {

    boolean delete() throws ResourceException;
}
