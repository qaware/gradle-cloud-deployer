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

import java.io.File;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * Initializes a token from the specified file.
 */
public class DefaultTokenInitializer implements TokenInitializer {

    /**
     * The file which contains the token.
     */
    private final File tokenFile;

    /**
     * Creates a new default token initializer which uses the specified file.
     *
     * @param tokenFile The file which contains the token.
     */
    public DefaultTokenInitializer(File tokenFile) {
        this.tokenFile = tokenFile;
    }

    @Override
    public String initialize(EnvironmentConfig environmentConfig) throws EnvironmentConfigException {
        try {
            return FileUtil.readFileContent(tokenFile);
        } catch (ResourceConfigException e) {
            String filePath = tokenFile == null ? "null" : tokenFile.getPath();
            throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_TOKEN_FROM_FILE", filePath), e);
        }
    }
}
