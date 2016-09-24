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
package de.qaware.cloud.deployer.kubernetes.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.BaseUpdateStrategy;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * Implements the update strategy. Meaning that all resources not included in the resources list stay untouched.
 * All included resources are updated or created.
 */
class KubernetesUpdateStrategy extends BaseUpdateStrategy implements KubernetesStrategy {

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesUpdateStrategy.class);

    /**
     * The name of the update method.
     */
    private static final String UPDATE_METHOD = "update";

    @Override
    public void deploy(NamespaceResource namespaceResource, List<KubernetesResource> resources) throws ResourceException {

        // 1. Check if all resources support updates
        List<KubernetesResource> resourcesWithoutUpdateSupport = retrieveResourcesWithoutUpdateSupport(resources);
        if (!resourcesWithoutUpdateSupport.isEmpty()) {
            KubernetesResource resource = resourcesWithoutUpdateSupport.get(0);
            throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_RESOURCE_SUPPORTS_NO_UPDATES", resource));
        }

        // 2. Create the namespace if it doesn't exist
        NamespaceUtil.safeCreateNamespace(namespaceResource);

        LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_UPDATING_RESOURCES_STARTED"));

        // 3. Update existing resources and create new ones
        update(resources);

        LOGGER.info(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_UPDATING_RESOURCES_DONE"));
    }

    /**
     * Returns all resources in the specified list which don't support update.
     *
     * @param resources The resource list with all resources.
     * @return A list with all resources that don't support update.
     */
    private List<KubernetesResource> retrieveResourcesWithoutUpdateSupport(List<KubernetesResource> resources) {
        return resources.stream().filter(this::isResourceWithoutUpdateSupport).collect(Collectors.toList());
    }

    /**
     * Checks whether the specified resource supports updates.
     *
     * @param resource The resource to check.
     * @return TRUE if the resource doesn't support update, FALSE otherwise.
     */
    private boolean isResourceWithoutUpdateSupport(KubernetesResource resource) {
        try {
            // It supports no updates if it doesn't override the default update in kubernetes resource.
            Class<?> updateClass = resource.getClass().getMethod(UPDATE_METHOD).getDeclaringClass();
            return updateClass == KubernetesResource.class;
        } catch (NoSuchMethodException e) {
            return true;
        }
    }
}
