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
package de.qaware.cloud.deployer.plugin.config.cloud;

import de.qaware.cloud.deployer.commons.config.cloud.AuthConfig;
import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.plugin.extension.AuthExtension;
import de.qaware.cloud.deployer.plugin.extension.EnvironmentExtension;
import de.qaware.cloud.deployer.plugin.extension.SSLExtension;
import de.qaware.cloud.deployer.plugin.token.TokenInitializer;

import java.io.File;
import java.util.*;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * This factory maps environment extensions to environment configs and validates them.
 */
public final class EnvironmentConfigFactory {

    /**
     * The default strategy.
     */
    private static final Strategy DEFAULT_STRATEGY = Strategy.REPLACE;

    /**
     * UTILITY.
     */
    private EnvironmentConfigFactory() {
    }

    /**
     * Creates a map of kubernetes environment configs and their files out of the specified extensions.
     *
     * @param extensions THe extensions which are used to create the kubernetes environment configs.
     * @return The map of kubernetes environment configs and their files.
     * @throws EnvironmentConfigException If necessary parameters are missing.
     */
    public static Map<EnvironmentConfig, List<File>> createKubernetesEnvironmentConfigs(Collection<EnvironmentExtension> extensions) throws EnvironmentConfigException {
        Map<EnvironmentConfig, List<File>> configs = new LinkedHashMap<>();
        for (EnvironmentExtension extension : extensions) {
            configs.put(createKubernetesEnvironmentConfig(extension), extension.getFiles());
        }
        return configs;
    }

    /**
     * Creates a map of environment configs and their files out of the specified extensions.
     *
     * @param extensions The extensions which are used to create the environment configs.
     * @return The map of environment configs and their files.
     * @throws EnvironmentConfigException If necessary parameters are missing.
     */
    public static Map<EnvironmentConfig, List<File>> createEnvironmentConfigs(Collection<EnvironmentExtension> extensions) throws EnvironmentConfigException {
        Map<EnvironmentConfig, List<File>> configs = new LinkedHashMap<>();
        for (EnvironmentExtension extension : extensions) {
            configs.put(createEnvironmentConfig(extension), extension.getFiles());
        }
        return configs;
    }

    /**
     * Creates a new environment config using the specified extension.
     *
     * @param extension The extension which contains the configuration.
     * @return The created environment config.
     * @throws EnvironmentConfigException If necessary parameters are missing.
     */
    private static KubernetesEnvironmentConfig createKubernetesEnvironmentConfig(EnvironmentExtension extension) throws EnvironmentConfigException {
        String id = extractId(extension);
        String baseUrl = extractBaseUrl(extension);
        Strategy strategy = extractStrategy(extension);
        String namespace = extractNamespace(extension);
        KubernetesEnvironmentConfig environmentConfig = new KubernetesEnvironmentConfig(id, baseUrl, strategy, namespace);

        // Set authorization config
        AuthConfig authConfig = extractAuthConfig(extension);
        environmentConfig.setAuthConfig(authConfig);

        // Set ssl config
        SSLConfig sslConfig = extractSSLConfig(extension);
        environmentConfig.setSslConfig(sslConfig);

        // Initialize the token
        initializeToken(extension, environmentConfig, authConfig);

        return environmentConfig;
    }

    /**
     * Creates a new kubernetes environment config using the specified extension.
     *
     * @param extension The extension which contains the configuration.
     * @return The created kubernetes config.
     * @throws EnvironmentConfigException If necessary parameters are missing.
     */
    private static EnvironmentConfig createEnvironmentConfig(EnvironmentExtension extension) throws EnvironmentConfigException {
        String id = extractId(extension);
        String baseUrl = extractBaseUrl(extension);
        Strategy strategy = extractStrategy(extension);
        EnvironmentConfig environmentConfig = new EnvironmentConfig(id, baseUrl, strategy);

        // Set authorization config
        AuthConfig authConfig = extractAuthConfig(extension);
        environmentConfig.setAuthConfig(authConfig);

        // Set ssl config
        SSLConfig sslConfig = extractSSLConfig(extension);
        environmentConfig.setSslConfig(sslConfig);

        // Initialize the token
        initializeToken(extension, environmentConfig, authConfig);

        return environmentConfig;
    }

    /**
     * Extracts the id out of the specified extension.
     *
     * @param extension The extension which contains the id.
     * @return The id.
     * @throws EnvironmentConfigException If the id is not defined.
     */
    private static String extractId(EnvironmentExtension extension) throws EnvironmentConfigException {
        String id = extension.getId();
        assertNotNullNorEmpty(id, PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EMPTY_ID"));
        return id;
    }

    /**
     * Extracts the namespace out of the specified extension.
     *
     * @param extension The extension which contains the namespace.
     * @return The namespace.
     * @throws EnvironmentConfigException If the namespace is not defined.
     */
    private static String extractNamespace(EnvironmentExtension extension) throws EnvironmentConfigException {
        String namespace = extension.getNamespace();
        assertNotNullNorEmpty(namespace, PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EMPTY_NAMESPACE", extension.getId()));
        return namespace;
    }

    /**
     * Extracts the ssl config out of the specified extension.
     *
     * @param extension The extension which contains the ssl config.
     * @return The extracted ssl config.
     */
    private static SSLConfig extractSSLConfig(EnvironmentExtension extension) {
        SSLConfig sslConfig = new SSLConfig();
        SSLExtension sslExtension = extension.getSslExtension();
        if (sslExtension != null) {
            if (sslExtension.isTrustAll()) {
                sslConfig = new SSLConfig(true);
            } else {
                String certificate = sslExtension.getCertificate();
                if (sslExtension.getCertificate() != null && !certificate.isEmpty()) {
                    sslConfig = new SSLConfig(certificate);
                } else {
                    sslConfig = new SSLConfig();
                }
            }
        }
        return sslConfig;
    }

    /**
     * Extracts the authorization config out the specified extension.
     *
     * @param extension The extension which contains the authorization config.
     * @return The extracted authorization config.
     */
    private static AuthConfig extractAuthConfig(EnvironmentExtension extension) {
        AuthConfig authConfig = new AuthConfig();
        AuthExtension authExtension = extension.getAuthExtension();
        if (authExtension != null) {
            // Set username and password if available
            String username = authExtension.getUsername();
            String password = authExtension.getPassword();
            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                authConfig.setUsername(username);
                authConfig.setPassword(password);
            }
        }
        return authConfig;
    }

    /**
     * Extracts the strategy out of the specified extension, if none is defined the default strategy is used.
     *
     * @param extension The extension which contains the strategy.
     * @return The extracted strategy.
     */
    private static Strategy extractStrategy(EnvironmentExtension extension) throws EnvironmentConfigException {
        String strategyString = extension.getStrategy();

        // Not defined? Return default
        if (strategyString == null || strategyString.isEmpty()) {
            return DEFAULT_STRATEGY;
        }

        // Otherwise try to identify
        Strategy strategy;
        switch (strategyString) {
            case "RESET":
                strategy = Strategy.RESET;
                break;
            case "REPLACE":
                strategy = Strategy.REPLACE;
                break;
            case "UPDATE":
                strategy = Strategy.UPDATE;
                break;
            default:
                throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_UNKNOWN_STRATEGY", strategyString));
        }
        return strategy;
    }

    /**
     * Extracts the base url out of the specified extension.
     *
     * @param extension The extension which contains the base url.
     * @return The extracted base url.
     * @throws EnvironmentConfigException If the base url is not specified.
     */
    private static String extractBaseUrl(EnvironmentExtension extension) throws EnvironmentConfigException {
        String baseUrl = extension.getBaseUrl();
        assertNotNullNorEmpty(baseUrl, PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EMPTY_BASE_URL", extension.getId()));
        return baseUrl;
    }

    /**
     * Uses the extension's token initializer to initialize the token in the auth config, if available.
     *
     * @param extension         The extension which contains the token initializer or null.
     * @param environmentConfig The environment config that will be used to retrieve the token.
     * @param authConfig        The auth config which will contain the retrieved token.
     * @throws EnvironmentConfigException If a error during token initialization occurs.
     */
    private static void initializeToken(EnvironmentExtension extension, EnvironmentConfig environmentConfig, AuthConfig authConfig) throws EnvironmentConfigException {
        TokenInitializer tokenInitializer = extension.getAuthExtension().getToken();
        if (tokenInitializer != null) {
            String token = tokenInitializer.initialize(environmentConfig);
            authConfig.setToken(token);
        }
    }

    /**
     * Checks if the specified string value is defined.
     *
     * @param value   The value which is checked.
     * @param message The error message which will be used to create the exception.
     * @throws EnvironmentConfigException If the value is not defined or empty.
     */
    private static void assertNotNullNorEmpty(String value, String message) throws EnvironmentConfigException {
        if (value == null || value.isEmpty()) {
            throw new EnvironmentConfigException(message);
        }
    }
}
