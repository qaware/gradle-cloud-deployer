package de.qaware.cloud.deployer.kubernetes.resource.base;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;

public interface Resource {

    boolean exists() throws ResourceException;

    boolean create() throws ResourceException;

    boolean delete() throws ResourceException;
}
