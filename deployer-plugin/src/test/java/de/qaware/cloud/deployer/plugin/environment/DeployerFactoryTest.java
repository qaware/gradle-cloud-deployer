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
package de.qaware.cloud.deployer.plugin.environment;

import de.qaware.cloud.deployer.commons.Deployer;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.kubernetes.KubernetesDeployer;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.marathon.MarathonDeployer;
import de.qaware.cloud.deployer.plugin.extension.DeployerType;
import de.qaware.cloud.deployer.plugin.extension.EnvironmentExtension;
import org.junit.Before;
import org.junit.Test;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class DeployerFactoryTest {

    private EnvironmentConfig environmentConfig;
    private EnvironmentExtension environmentExtension;

    @Before
    public void setup() {
        this.environmentConfig = new EnvironmentConfig("test", "http://test.org", Strategy.REPLACE);
        this.environmentExtension = mock(EnvironmentExtension.class);
    }

    @Test
    public void testCreateKubernetesDeployer() throws EnvironmentConfigException {
        this.environmentConfig = mock(KubernetesEnvironmentConfig.class);
        when(environmentExtension.getDeployerType()).thenReturn(DeployerType.KUBERNETES);
        Deployer deployer = DeployerFactory.create(environmentExtension, environmentConfig);
        assertTrue(deployer instanceof KubernetesDeployer);
    }

    @Test
    public void testCreateMarathonDeployer() throws EnvironmentConfigException {
        when(environmentExtension.getDeployerType()).thenReturn(DeployerType.MARATHON);
        Deployer deployer = DeployerFactory.create(environmentExtension, environmentConfig);
        assertTrue(deployer instanceof MarathonDeployer);
    }

    @Test
    public void testCreateDeployerTypeNull() {
        when(environmentExtension.getDeployerType()).thenReturn(null);
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_DEPLOYER_TYPE_NULL");
        assertExceptionOnCreate(environmentExtension, environmentConfig, message);
    }

    @Test
    public void testCreateExtensionNull() {
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EXTENSION_OR_CONFIG_NULL");
        assertExceptionOnCreate(null, environmentConfig, message);
    }

    @Test
    public void testCreateConfigNull() {
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EXTENSION_OR_CONFIG_NULL");
        assertExceptionOnCreate(environmentExtension, null, message);
    }

    @Test
    public void testCreateKubernetesDeployerWithInvalidConfig() {
        when(environmentExtension.getDeployerType()).thenReturn(DeployerType.KUBERNETES);
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_INVALID_KUBERNETES_CONFIG");
        assertExceptionOnCreate(environmentExtension, environmentConfig, message);
    }

    private void assertExceptionOnCreate(EnvironmentExtension extension, EnvironmentConfig config, String message) {
        boolean exceptionThrown = false;
        try {
            DeployerFactory.create(extension, config);
        } catch (EnvironmentConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
