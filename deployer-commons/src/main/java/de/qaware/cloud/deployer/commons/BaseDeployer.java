package de.qaware.cloud.deployer.commons;

import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;

import java.io.File;
import java.util.List;

/**
 * The base implementation of a deployer.
 *
 * @param <ConfigType> The type of config this deployer consumes.
 */
public abstract class BaseDeployer<ConfigType extends EnvironmentConfig> {

    /**
     * The environment this deployer is defined for.
     */
    private final ConfigType environmentConfig;

    /**
     * Creates a new base deployer for the specified environment.
     *
     * @param environmentConfig The environment this deployer is defined for.
     */
    public BaseDeployer(ConfigType environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    /**
     * Returns the environment config of this deployer.
     *
     * @return The environment.
     */
    public ConfigType getEnvironmentConfig() {
        return environmentConfig;
    }

    /**
     * Deploys the list of config files.
     *
     * @throws ResourceConfigException If a problem during config parsing and interpretation occurs.
     * @throws ResourceException       If a problem during resource deletion/creation occurs.
     */
    public abstract void deploy(List<File> files) throws ResourceConfigException, ResourceException;
}
