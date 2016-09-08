package de.qaware.cloud.deployer.marathon.update;

import de.qaware.cloud.deployer.commons.error.ResourceException;

/**
 * Factory which returns a marathon update strategy.
 */
public class MarathonUpdateStrategyFactory {

    /**
     * Accepts a string representation of an update strategy and instantiates a new object of this strategy.
     *
     * @param updateStrategy The string representation of the update strategy.
     * @return A new object of the specified strategy.
     * @throws ResourceException If the string specifies a not existing update strategy.
     */
    public static MarathonUpdateStrategy create(String updateStrategy) throws ResourceException {
        switch (updateStrategy) {
            case "SOFT":
                return new MarathonSoftUpdateStrategy();
            default:
                throw new ResourceException("Unknown strategy type specified");
        }
    }
}
