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
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.dcos.token.TokenResource;

import java.io.File;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * Initializes a dcos authentication token using a OpenId Connect Id token from the specified file.
 */
public class OpenIdConnectIdTokenInitializer implements TokenInitializer {

    /**
     * The file which contains the dcos auth token.
     */
    private final File authTokenFile;

    /**
     * Creates a new OpenId Connect Id token initializer.
     *
     * @param tokenFile The file which contains the OpenId Connect Id token.
     */
    public OpenIdConnectIdTokenInitializer(File tokenFile) {
        this.authTokenFile = tokenFile;
    }

    @Override
    public String initialize(EnvironmentConfig environmentConfig) throws EnvironmentConfigException {
        try {
            String authToken = FileUtil.readFileContent(authTokenFile);
            return new TokenResource(environmentConfig).retrieveAuthenticationToken(authToken);
        } catch (ResourceException e) {
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_DCOS_API_TOKEN"), e);
        } catch (ResourceConfigException e) {
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_TOKEN_FROM_FILE", authTokenFile.getPath()), e);
        }
    }
}
