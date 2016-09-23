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
package de.qaware.cloud.deployer.plugin.extension;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.plugin.token.DcosAuthTokenInitializer;
import de.qaware.cloud.deployer.plugin.token.OpenIdConnectIdToken;
import de.qaware.cloud.deployer.plugin.token.DefaultTokenInitializer;
import de.qaware.cloud.deployer.plugin.token.TokenInitializer;

import java.io.File;

/**
 * Contains the authorization configuration for a environment.
 */
public class AuthExtension {

    /**
     * The username used for authorization.
     */
    private String username;

    /**
     * The password used for authorization.
     */
    private String password;

    /**
     * The token used for authorization.
     */
    private TokenInitializer token;

    /**
     * Creates a OpenId Connect Id token initializer which uses the specified file.
     *
     * @param tokenFile The file which contains the OpenId Connect Id token.
     * @return The OpenId Connect Id token initializer.
     * @throws ResourceConfigException If the token can't be initialized.
     */
    public TokenInitializer openId(File tokenFile) throws ResourceConfigException {
        return new OpenIdConnectIdToken(tokenFile);
    }

    /**
     * Creates a dcos auth token initializer.
     *
     * @return The dcos auth token initializer.
     * @throws ResourceConfigException If the token can't be initialized.
     */
    public TokenInitializer dcosAuthToken() throws ResourceConfigException {
        return new DcosAuthTokenInitializer();
    }

    /**
     * Creates a default token initializer.
     *
     * @param tokenFile The file which contains the token.
     * @return The default token initializer.
     * @throws ResourceConfigException If the token can't be initialized.
     */
    public TokenInitializer defaultToken(File tokenFile) throws ResourceConfigException {
        return new DefaultTokenInitializer(tokenFile);
    }

    /**
     * Returns the username used for authorization.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username used for authorization.
     *
     * @param username The username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password used for authorization.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password used for authorization.
     *
     * @param password The password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the token initializer used for authorization.
     *
     * @return The token initializer.
     */
    public TokenInitializer getToken() {
        return token;
    }

    /**
     * Sets the token initializer used for authorization.
     *
     * @param token The token initializer.
     */
    public void setToken(TokenInitializer token) {
        this.token = token;
    }
}
