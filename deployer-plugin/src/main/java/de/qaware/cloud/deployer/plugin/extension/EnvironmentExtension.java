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

import groovy.lang.Closure;
import org.gradle.api.Project;

import java.io.File;
import java.util.List;

/**
 * Contains all relevant configuration for a environment.
 */
public class EnvironmentExtension {

    /**
     * The id of this environment.
     */
    private String id;

    /**
     * The base url of the environment.
     */
    private String baseUrl;

    /**
     * The strategy which is used in this environment.
     */
    private String strategy;

    /**
     * The project this environment belongs to.
     */
    private Project project;

    /**
     * The ssl configuration for this environment.
     */
    private SSLExtension sslExtension;

    /**
     * The authorization configuration for this environment.
     */
    private AuthExtension authExtension;

    /**
     * The list of config files which belong to this environment.
     */
    private List<File> files;

    /**
     * The kubernetes namespace which is used.
     */
    private String namespace;

    /**
     * Creates a new environment extension.
     *
     * @param project The project this extension belongs to.
     */
    public EnvironmentExtension(Project project) {
        this.project = project;
    }

    /**
     * Adds a new ssl configuration to this environment.
     *
     * @param closure The closure which contains the ssl configuration.
     * @return The ssl configuration.
     */
    public SSLExtension ssl(Closure closure) {
        SSLExtension ssl = (SSLExtension) project.configure(new SSLExtension(), closure);
        this.sslExtension = ssl;
        return ssl;
    }

    /**
     * Adds a new authorization configuration to this environment.
     *
     * @param closure The closure which contains the authorization configuration.
     * @return The authorization configuration.
     */
    public AuthExtension auth(Closure closure) {
        AuthExtension auth = (AuthExtension) project.configure(new AuthExtension(), closure);
        this.authExtension = auth;
        return auth;
    }

    /**
     * Returns the id of this environment.
     *
     * @return The id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this environment.
     *
     * @param id The id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the base url of this environment.
     *
     * @return The base url.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the base url of this environment.
     *
     * @param baseUrl The base url.
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns the strategy used in this environment.
     *
     * @return The strategy.
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * Sets the strategy used in this environment.
     *
     * @param strategy The strategy.
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    /**
     * Returns the ssl configuration for this environment.
     *
     * @return The ssl configuration.
     */
    public SSLExtension getSslExtension() {
        return sslExtension;
    }

    /**
     * Returns the authorization configuration for this environment.
     *
     * @return The authorization configuration.
     */
    public AuthExtension getAuthExtension() {
        return authExtension;
    }

    /**
     * Returns the config files for this environment.
     *
     * @return The config files.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Sets the config files for this environment.
     *
     * @param files The config files.
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    /**
     * Returns the kubernetes namespace used for this environment.
     *
     * @return The namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the kubernetes namespace used for this environment.
     *
     * @param namespace The namespace.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
