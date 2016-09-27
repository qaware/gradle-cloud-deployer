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
package de.qaware.cloud.deployer.kubernetes.test;

import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.resource.service.ServiceResource;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public abstract class BaseKubernetesStrategyTest {

    // Resources
    protected NamespaceResource namespaceResource;
    protected DeploymentResource deploymentResource;
    protected ServiceResource serviceResource;
    protected List<KubernetesResource> resources;

    @Before
    public void setup() {
        namespaceResource = mock(NamespaceResource.class);
        deploymentResource = mock(DeploymentResource.class);
        serviceResource = mock(ServiceResource.class);

        resources = new ArrayList<>();
        resources.add(deploymentResource);
        resources.add(serviceResource);
    }
}
