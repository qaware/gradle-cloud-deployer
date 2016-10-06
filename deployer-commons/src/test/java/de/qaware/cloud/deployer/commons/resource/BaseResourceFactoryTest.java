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
package de.qaware.cloud.deployer.commons.resource;

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author sjahreis
 */
public class BaseResourceFactoryTest {

    private BaseResourceFactory<BaseResource<BaseResourceConfig>, BaseResourceConfig> baseResourceFactory;
    private BasePingResource basePingResource;
    private BaseResourceConfig config1;
    private BaseResourceConfig config2;
    private BaseResource<BaseResourceConfig> baseResource1;
    private BaseResource<BaseResourceConfig> baseResource2;

    @Before
    public void setup() throws ResourceException {
        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", "http://localhost", Strategy.REPLACE);
        config1 = mock(BaseResourceConfig.class);
        config2 = mock(BaseResourceConfig.class);
        baseResource1 = mock(BaseResource.class);
        baseResource2 = mock(BaseResource.class);
        basePingResource = mock(BasePingResource.class);

        baseResourceFactory = new BaseResourceFactory<BaseResource<BaseResourceConfig>, BaseResourceConfig>(environmentConfig) {
            @Override
            public BaseResource<BaseResourceConfig> createResource(BaseResourceConfig resourceConfig) throws ResourceException {
                return null;
            }

            @Override
            public BasePingResource createPingResource(EnvironmentConfig environmentConfig) throws ResourceException {
                return basePingResource;
            }
        };
        baseResourceFactory = spy(baseResourceFactory);
        when(baseResourceFactory.createResource(config1)).thenReturn(baseResource1);
        when(baseResourceFactory.createResource(config2)).thenReturn(baseResource2);
        when(baseResourceFactory.createPingResource(environmentConfig)).thenReturn(basePingResource);
    }

    @Test
    public void testPingOnBeginning() throws ResourceException {
        verify(basePingResource, times(1)).ping();
    }

    @Test
    public void testCreateResources() throws ResourceException {
        List<BaseResource<BaseResourceConfig>> resources = new ArrayList<>();
        resources.add(baseResource1);
        resources.add(baseResource2);

        List<BaseResourceConfig> resourceConfigs = new ArrayList<>();
        resourceConfigs.add(config1);
        resourceConfigs.add(config2);

        List<BaseResource<BaseResourceConfig>> createdResources = baseResourceFactory.createResources(resourceConfigs);

        assertEquals(resources, createdResources);
        verify(baseResourceFactory, times(1)).createResource(config1);
        verify(baseResourceFactory, times(1)).createResource(config2);
    }
}
