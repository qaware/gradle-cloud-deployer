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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * Initializes a dcos authorization token which is retrieved from the dcos.toml file in the user's home directory.
 */
public class DcosAuthTokenInitializer implements TokenInitializer {

    /**
     * The user's home directory.
     */
    private static final String USER_HOME = System.getProperty("user.home");

    /**
     * The path of the dcos config file relative to the user's home directory.
     */
    private static final String DCOS_CONFIG_FILE = "/.dcos/dcos.toml";

    /**
     * The property which contains the dcos authorization token.
     */
    private static final String DCOS_TOKEN_PROPERTY = "dcos_acs_token";

    @Override
    public String initialize(EnvironmentConfig environmentConfig) throws EnvironmentConfigException {
        try (FileInputStream inputStream = new FileInputStream(USER_HOME + DCOS_CONFIG_FILE)) {
            // Load the token
            Properties properties = new Properties();
            properties.load(inputStream);
            String token = properties.getProperty(DCOS_TOKEN_PROPERTY);

            // Remove "-characters and assign
            return token.substring(1, token.length() - 1);
        } catch (IOException e) {
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_CONFIG_NOT_FOUND"), e);
        }
    }
}
