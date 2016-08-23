package de.qaware.cloud.deployer.kubernetes.update;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;

public class UpdateStrategyFactory {

    private UpdateStrategyFactory() {
    }

    public static UpdateStrategy create(String updateStrategy) throws ResourceException {
        switch (updateStrategy) {
            case "HARD":
                return new HardUpdateStrategy();
            case "SOFT":
                return new SoftUpdateStrategy();
            default:
                throw new ResourceException("Unknown strategy type specified");
        }
    }
}
