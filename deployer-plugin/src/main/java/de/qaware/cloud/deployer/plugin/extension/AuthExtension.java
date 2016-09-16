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
    private String token;

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
     * Returns the token used for authorization.
     *
     * @return The token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token used for authorization.
     *
     * @param token The token.
     */
    public void setToken(String token) {
        this.token = token;
    }
}
