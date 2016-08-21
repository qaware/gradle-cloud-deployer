package de.qaware.cloud.deployer.kubernetes.resource.base;

public interface Resource {

    boolean exists();

    boolean create();
}
