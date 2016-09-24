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
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;

import java.util.List;

/**
 * A kubernetes strategy describes how to deploy a number of resources in a kubernetes cloud.
 */
public interface KubernetesStrategy {

    /**
     * Deploys the specified resources to the specified namespace.
     *
     * @param namespaceResource The namespace the resources will be deployed in.
     * @param resources         The resources to deploy.
     * @throws ResourceException If an error during deployment occurs.
     */
    void deploy(NamespaceResource namespaceResource, List<KubernetesResource> resources) throws ResourceException;

    /**
     * Deletes the specified resources.
     *
     * @param namespaceResource The namespace resource the resources are located in.
     * @param resources         The resources to delete.
     * @throws ResourceException If an error during deletion occurs.
     */
    void delete(NamespaceResource namespaceResource, List<KubernetesResource> resources) throws ResourceException;
}
