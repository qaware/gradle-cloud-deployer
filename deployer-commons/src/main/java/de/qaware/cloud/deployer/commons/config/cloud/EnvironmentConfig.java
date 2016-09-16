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
package de.qaware.cloud.deployer.commons.config.cloud;

/**
 * Represents a configuration for a environment.
 */
public class EnvironmentConfig {

    /**
     * The base url of the environment.
     */
    private final String baseUrl;

    /**
     * The update strategy which is used in this environment.
     */
    private final String updateStrategy;

    /**
     * The authorization configuration for this environment.
     */
    private AuthConfig authConfig;

    /**
     * The ssl configuration for this environment.
     */
    private SSLConfig sslConfig;

    /**
     * Creates a new environment config.
     *
     * @param baseUrl        The base url of this environment.
     * @param updateStrategy The update strategy which is used for this environment.
     */
    public EnvironmentConfig(String baseUrl, String updateStrategy) {
        this.baseUrl = baseUrl;
        this.updateStrategy = updateStrategy;
    }

    /**
     * Returns the base url.
     *
     * @return The base url.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the update strategy.
     *
     * @return The update strategy.
     */
    public String getUpdateStrategy() {
        return updateStrategy;
    }

    /**
     * Returns the authorization configuration.
     *
     * @return The authorization configuration.
     */
    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    /**
     * Sets the authorization configuration.
     *
     * @param authConfig The authorization configuration.
     */
    public void setAuthConfig(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    /**
     * Returns the environment's ssl config.
     *
     * @return The environment's ssl config.
     */
    public SSLConfig getSslConfig() {
        return sslConfig;
    }

    /**
     * Sets the environment's ssl config.
     *
     * @param sslConfig The environment's ssl config.
     */
    public void setSslConfig(SSLConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}
