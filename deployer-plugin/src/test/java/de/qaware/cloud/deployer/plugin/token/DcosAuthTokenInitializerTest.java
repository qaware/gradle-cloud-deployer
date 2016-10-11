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
package de.qaware.cloud.deployer.plugin.token;

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import org.junit.Test;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author sjahreis
 */
public class DcosAuthTokenInitializerTest {

    private static final String HOME_DIR = "/de/qaware/cloud/deployer/plugin/token";

    @Test
    public void testInitialize() throws Exception {
        // Replace user home property to use test directory as user home
        String homeDir = this.getClass().getResource(HOME_DIR + "/home").getPath();
        System.setProperty("user.home", homeDir);

        DcosAuthTokenInitializer dcosAuthTokenInitializer = new DcosAuthTokenInitializer();

        String token = dcosAuthTokenInitializer.initialize(mock(EnvironmentConfig.class));
        assertEquals("TOKEN", token);
    }

    @Test
    public void testInitializeWithNotExistingFile() {
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_CONFIG_NOT_FOUND");

        // Replace user home property to use test directory as user home
        String homeDir = this.getClass().getResource(HOME_DIR + "/empty").getPath();
        System.setProperty("user.home", homeDir);

        DcosAuthTokenInitializer dcosAuthTokenInitializer = new DcosAuthTokenInitializer();

        TokenInitializerTestHelper.assertExceptionOnInitialize(dcosAuthTokenInitializer, mock(EnvironmentConfig.class), message);
    }

    @Test
    public void testInitializeWithIncompleteFile() {
        // Replace user home property to use test directory as user home
        String homeDir = this.getClass().getResource(HOME_DIR + "/missing").getPath();
        System.setProperty("user.home", homeDir);

        String tokenFile = homeDir + "/.dcos/dcos.toml";
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_TOKEN_FROM_FILE", tokenFile);

        DcosAuthTokenInitializer dcosAuthTokenInitializer = new DcosAuthTokenInitializer();

        TokenInitializerTestHelper.assertExceptionOnInitialize(dcosAuthTokenInitializer, mock(EnvironmentConfig.class), message);
    }
}
