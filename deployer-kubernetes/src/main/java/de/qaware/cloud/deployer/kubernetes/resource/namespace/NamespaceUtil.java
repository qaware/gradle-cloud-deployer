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
package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.update.HardUpdateStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NamespaceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HardUpdateStrategy.class);

    private NamespaceUtil() {
    }

    public static void safeDeleteNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (namespaceResource.exists()) {
            LOGGER.info("Removing namespace...");

            LOGGER.info("- " + namespaceResource);
            namespaceResource.delete();

            LOGGER.info("Finished removing namespace...");
        }
    }

    public static void safeCreateNamespace(NamespaceResource namespaceResource) throws ResourceException {
        if (!namespaceResource.exists()) {
            LOGGER.info("Creating namespace...");

            LOGGER.info("- " + namespaceResource);
            namespaceResource.create();

            LOGGER.info("Finished creating namespace...");
        }
    }
}
