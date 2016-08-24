/*
 * Copyright 2016 QAware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qaware.cloud.deployer.kubernetes.update;

import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HardUpdateStrategy extends BaseUpdateStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(HardUpdateStrategy.class);

    @Override
    public void deploy(NamespaceResource namespaceResource, List<Resource> resources) throws ResourceException {
        // 1. Delete the old namespace
        deleteNamespace(namespaceResource);

        // 2. Create the new namespace
        createNamespace(namespaceResource);

        // 3. Create resources in the namespace
        deployResources(resources);
    }

    public static void deleteNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            LOGGER.info("Removing namespace...");

            LOGGER.info("- " + namespaceResource);
            namespaceResource.delete();

            LOGGER.info("Finished removing namespace...");
        }
    }

    private static void deployResources(List<Resource> resources) throws ResourceException {
        LOGGER.info("Deploying resources...");

        for (Resource resource : resources) {
            LOGGER.info("- " + resource);
            resource.create();
        }

        LOGGER.info("Finished deploying resources...");
    }
}
