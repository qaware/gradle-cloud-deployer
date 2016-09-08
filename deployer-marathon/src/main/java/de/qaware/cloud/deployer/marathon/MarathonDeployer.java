package de.qaware.cloud.deployer.marathon;

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfigFactory;
import de.qaware.cloud.deployer.marathon.resource.MarathonResourceFactory;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import de.qaware.cloud.deployer.marathon.update.MarathonUpdateStrategy;
import de.qaware.cloud.deployer.marathon.update.MarathonUpdateStrategyFactory;

import java.io.File;
import java.util.List;

/**
 * Offers the possibility to deploy a list of marathon config files to a marathon cloud.
 */
public class MarathonDeployer {

    /**
     * Deploys the list of marathon config files to the specified cloud.
     *
     * @param cloudConfig The config which describes the cloud.
     * @param files       The marathon config files to deploy.
     * @throws ResourceConfigException If a problem during config parsing and interpretation occurs.
     * @throws ResourceException       If a problem during resource deletion/creation occurs.
     */
    public void deploy(CloudConfig cloudConfig, List<File> files) throws ResourceConfigException, ResourceException {
        // 1. Read and create resource configs
        MarathonResourceConfigFactory resourceConfigFactory = new MarathonResourceConfigFactory();
        List<MarathonResourceConfig> resourceConfigs = resourceConfigFactory.createConfigs(files);

        // 2. Create a resource factory for the specified namespace
        MarathonResourceFactory resourceFactory = new MarathonResourceFactory(cloudConfig);

        // 3. Create the resources for the configs out of step 1.
        List<MarathonResource> resources = resourceFactory.createResources(resourceConfigs);

        // 4. Retrieve a update strategy
        MarathonUpdateStrategy updateStrategy = MarathonUpdateStrategyFactory.create(cloudConfig.getUpdateStrategy());

        // 5. Deploy the resources using the strategy
        updateStrategy.deploy(resources);
    }
}
