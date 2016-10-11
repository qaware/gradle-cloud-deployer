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
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import org.junit.Test;

import java.io.File;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author sjahreis
 */
public class DefaultTokenInitializerTest {

    private static final String TOKEN_DIR = "/de/qaware/cloud/deployer/plugin/token/";

    @Test
    public void testInitialize() throws EnvironmentConfigException {
        File tokenFile = new File(this.getClass().getResource(TOKEN_DIR + "token.txt").getPath());
        DefaultTokenInitializer defaultTokenInitializer = new DefaultTokenInitializer(tokenFile);
        String token = defaultTokenInitializer.initialize(mock(EnvironmentConfig.class));
        String correctToken = "TOKEN";
        assertEquals(correctToken, token);
    }

    @Test
    public void testInitializeWithNotExistingFile() {
        File tokenFile = new File(TOKEN_DIR + "token-not-existing.txt");
        DefaultTokenInitializer defaultTokenInitializer = new DefaultTokenInitializer(tokenFile);
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_TOKEN_FROM_FILE", tokenFile.getPath());
        assertExceptionOnInitialize(defaultTokenInitializer, mock(EnvironmentConfig.class), message);
    }

    @Test
    public void testInitializeWithEmptyFile() {
        File tokenFile = new File(TOKEN_DIR + "token-empty.txt");
        DefaultTokenInitializer defaultTokenInitializer = new DefaultTokenInitializer(tokenFile);
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_TOKEN_FROM_FILE", tokenFile.getPath());
        assertExceptionOnInitialize(defaultTokenInitializer, mock(EnvironmentConfig.class), message);
    }

    private void assertExceptionOnInitialize(TokenInitializer tokenInitializer, EnvironmentConfig environmentConfig, String message) {
        boolean exceptionThrown = false;
        try {
            tokenInitializer.initialize(environmentConfig);
        } catch (EnvironmentConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
