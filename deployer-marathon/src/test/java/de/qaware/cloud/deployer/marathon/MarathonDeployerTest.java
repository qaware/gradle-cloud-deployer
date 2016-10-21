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
package de.qaware.cloud.deployer.marathon;

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfigFactory;
import de.qaware.cloud.deployer.marathon.resource.MarathonResourceFactory;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * @author sjahreis
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MarathonDeployer.class)
public class MarathonDeployerTest {

    private EnvironmentConfig environmentConfig;
    private List<File> files;
    private MarathonResourceConfigFactory marathonResourceConfigFactory;
    private List<MarathonResourceConfig> configs;
    private MarathonResourceFactory marathonResourceFactory;
    private MarathonResource resource1;
    private MarathonResource resource2;

    @Before
    public void setup() throws Exception {
        // Mock environment config
        environmentConfig = mock(EnvironmentConfig.class);
        when(environmentConfig.getStrategy()).thenReturn(Strategy.UPDATE);

        // Mock files
        files = new ArrayList<>();
        files.add(mock(File.class));
        files.add(mock(File.class));

        // Mock configs
        configs = new ArrayList<>();
        configs.add(mock(MarathonResourceConfig.class));
        configs.add(mock(MarathonResourceConfig.class));
        marathonResourceConfigFactory = mock(MarathonResourceConfigFactory.class);
        when(marathonResourceConfigFactory.createConfigs(files)).thenReturn(configs);

        // Mock resources
        resource1 = mock(MarathonResource.class);
        resource2 = mock(MarathonResource.class);
        List<MarathonResource> resources = new ArrayList<>();
        resources.add(resource1);
        when(resource1.exists()).thenReturn(true);
        resources.add(resource2);
        when(resource2.exists()).thenReturn(true);
        marathonResourceFactory = mock(MarathonResourceFactory.class);
        when(marathonResourceFactory.createResources(configs)).thenReturn(resources);

        // Return mock factories
        whenNew(MarathonResourceFactory.class).withArguments(environmentConfig).thenReturn(marathonResourceFactory);
        whenNew(MarathonResourceConfigFactory.class).withNoArguments().thenReturn(marathonResourceConfigFactory);
    }

    @Test
    public void testDeploy() throws Exception {
        // Start test
        MarathonDeployer deployer = new MarathonDeployer(environmentConfig);
        deployer.deploy(files);

        // Verify resources were called correctly
        verify(environmentConfig, times(1)).getStrategy();
        verify(marathonResourceConfigFactory, times(1)).createConfigs(any());
        verify(marathonResourceConfigFactory, times(1)).createConfigs(files);
        verify(marathonResourceFactory, times(1)).createResources(any());
        verify(marathonResourceFactory, times(1)).createResources(configs);
        verify(resource1, times(1)).exists();
        verify(resource2, times(1)).exists();
        verify(resource1, times(1)).update();
        verify(resource2, times(1)).update();
    }

    @Test
    public void testDelete() throws Exception {
        // Start test
        MarathonDeployer deployer = new MarathonDeployer(environmentConfig);
        deployer.delete(files);

        // Verify resources were called correctly
        verify(environmentConfig, times(1)).getStrategy();
        verify(marathonResourceConfigFactory, times(1)).createConfigs(any());
        verify(marathonResourceConfigFactory, times(1)).createConfigs(files);
        verify(marathonResourceFactory, times(1)).createResources(any());
        verify(marathonResourceFactory, times(1)).createResources(configs);
        verify(resource1, times(1)).exists();
        verify(resource2, times(1)).exists();
        verify(resource1, times(1)).delete();
        verify(resource2, times(1)).delete();
    }
}
