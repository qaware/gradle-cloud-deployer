package de.qaware.cloud.deployer.kubernetes.resource.base;

public interface DeletableResource extends Resource {

    boolean delete();
}
