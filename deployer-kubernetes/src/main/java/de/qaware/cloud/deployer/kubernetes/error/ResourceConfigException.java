package de.qaware.cloud.deployer.kubernetes.error;

public class ResourceConfigException extends Exception {

    public ResourceConfigException(String message) {
        super(message);
    }

    public ResourceConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
