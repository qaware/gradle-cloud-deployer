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
package de.qaware.cloud.deployer.plugin;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;

/**
 * Represents the extension which is used to configure this plugin.
 */
public class DeployerExtension {

    /**
     * The cloud's base url.
     */
    private String baseUrl;

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
     * The update strategy for this cloud.
     */
    private String updateStrategy;

    /**
     * Whether to trust all certificates (TRUE) or not (FALSE).
     */
    private boolean trustAll;

    /**
     * A custom certificate to trust.
     */
    private String certificate;

    /**
     * The namespace this plugin is applied to (kubernetes only).
     */
    private String namespace;

    /**
     * The list of config files to deploy.
     */
    private File[] files;

    /**
     * Returns the base url.
     *
     * @return The base url.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the base url.
     *
     * @param baseUrl The base url.
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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
     * @param username The username used for authorization.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password used for authorization.
     *
     * @return The password used for authorization.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password used for authorization.
     *
     * @param password The password used for authorization.
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

    /**
     * Indicates whether to trust all certificates or not.
     *
     * @return TRUE if all certs are trusted, FALSE otherwise.
     */
    public boolean isTrustAll() {
        return trustAll;
    }

    /**
     * Whether to trust all certs.
     *
     * @param trustAll A boolean which indicates whether to trust all certs (TRUE) or not (FALSE).
     */
    public void setTrustAll(boolean trustAll) {
        this.trustAll = trustAll;
    }

    /**
     * Returns the custom certificate to trust.
     *
     * @return The custom certificate.
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets a custom certificate to trust.
     *
     * @param certificate The custom certificate.
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * Returns the namespace which is used for this configuration (kubernetes only).
     *
     * @return The namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace which is used for this configuration (kubernetes only).
     *
     * @param namespace The namespace.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Returns the list of configuration files associated with this configuration.
     *
     * @return The configuration files.
     */
    public File[] getFiles() {
        return ArrayUtils.clone(files);
    }

    /**
     * Sets the list of configuration files associated with this configuration.
     *
     * @param files The configuration files.
     */
    public void setFiles(File[] files) {
        this.files = ArrayUtils.clone(files);
    }

    /**
     * Returns the used update strategy.
     *
     * @return The update strategy.
     */
    public String getUpdateStrategy() {
        return updateStrategy;
    }

    /**
     * Sets the update strategy to use.
     *
     * @param updateStrategy The update strategy.
     */
    public void setUpdateStrategy(String updateStrategy) {
        this.updateStrategy = updateStrategy;
    }
}
