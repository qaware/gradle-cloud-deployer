package de.qaware.cloud.deployer.kubernetes.error;

public class ResourceException extends Exception {

    public ResourceException(Throwable cause) {
        super(cause);
    }

    public ResourceException(String message) {
        super(message);
    }

    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
