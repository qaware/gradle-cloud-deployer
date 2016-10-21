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
package de.qaware.cloud.deployer.kubernetes;

import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.resource.KubernetesResourceFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author sjahreis
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(KubernetesDeployer.class)
public class KubernetesDeployerTest {

    private KubernetesEnvironmentConfig kubernetesEnvironmentConfig;
    private List<File> files;
    private KubernetesResourceConfigFactory kubernetesResourceConfigFactory;
    private List<KubernetesResourceConfig> configs;
    private KubernetesResourceFactory kubernetesResourceFactory;
    private KubernetesResource resource1;
    private KubernetesResource resource2;
    private NamespaceResource namespaceResource;

    @Before
    public void setup() throws Exception {
        // Mock environment config
        kubernetesEnvironmentConfig = mock(KubernetesEnvironmentConfig.class);
        when(kubernetesEnvironmentConfig.getStrategy()).thenReturn(Strategy.UPDATE);

        // Mock files
        files = new ArrayList<>();
        files.add(mock(File.class));
        files.add(mock(File.class));

        // Mock configs
        configs = new ArrayList<>();
        configs.add(mock(KubernetesResourceConfig.class));
        configs.add(mock(KubernetesResourceConfig.class));
        kubernetesResourceConfigFactory = mock(KubernetesResourceConfigFactory.class);
        when(kubernetesResourceConfigFactory.createConfigs(files)).thenReturn(configs);

        // Mock resources
        resource1 = mock(KubernetesResource.class);
        when(resource1.exists()).thenReturn(true);
        resource2 = mock(KubernetesResource.class);
        when(resource2.exists()).thenReturn(true);
        namespaceResource = mock(NamespaceResource.class);
        when(namespaceResource.exists()).thenReturn(true);
        List<KubernetesResource> resources = new ArrayList<>();
        resources.add(resource1);
        resources.add(resource2);
        kubernetesResourceFactory = mock(KubernetesResourceFactory.class);
        when(kubernetesResourceFactory.createResources(configs)).thenReturn(resources);
        when(kubernetesResourceFactory.getNamespaceResource()).thenReturn(namespaceResource);

        // Return mock factories
        whenNew(KubernetesResourceFactory.class).withArguments(kubernetesEnvironmentConfig).thenReturn(kubernetesResourceFactory);
        whenNew(KubernetesResourceConfigFactory.class).withNoArguments().thenReturn(kubernetesResourceConfigFactory);
    }

    @Test
    public void testDeploy() throws Exception {
        // Start test
        KubernetesDeployer deployer = new KubernetesDeployer(kubernetesEnvironmentConfig);
        deployer.deploy(files);

        // Verify resources were called correctly
        verify(kubernetesEnvironmentConfig, times(1)).getStrategy();
        verify(kubernetesResourceConfigFactory, times(1)).createConfigs(files);
        verify(kubernetesResourceFactory, times(1)).createResources(configs);
        verify(resource1, times(1)).exists();
        verify(resource2, times(1)).exists();
        verify(resource1, times(1)).update();
        verify(resource2, times(1)).update();
    }

    @Test
    public void testDelete() throws Exception {
        // Start test
        KubernetesDeployer deployer = new KubernetesDeployer(kubernetesEnvironmentConfig);
        deployer.delete(files);

        // Verify resources were called correctly
        verify(kubernetesEnvironmentConfig, times(1)).getStrategy();
        verify(kubernetesResourceConfigFactory, times(1)).createConfigs(files);
        verify(kubernetesResourceFactory, times(1)).createResources(configs);
        verify(namespaceResource, never()).exists();
        verify(namespaceResource, never()).delete();
        verify(resource1, times(1)).exists();
        verify(resource2, times(1)).exists();
        verify(resource1, times(1)).delete();
        verify(resource2, times(1)).delete();
    }
}
