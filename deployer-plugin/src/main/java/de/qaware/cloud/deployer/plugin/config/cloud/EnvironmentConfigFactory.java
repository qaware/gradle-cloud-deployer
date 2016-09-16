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
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.plugin.extension.AuthExtension;
import de.qaware.cloud.deployer.plugin.extension.EnvironmentExtension;
import de.qaware.cloud.deployer.plugin.extension.SSLExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * This factory maps environment extensions to environment configs and validates them.
 */
public final class EnvironmentConfigFactory {

    /**
     * The default update strategy.
     */
    private static final String DEFAULT_UPDATE_STRATEGY = "SOFT";

    /**
     * UTILITY.
     */
    private EnvironmentConfigFactory() {
    }

    /**
     * Creates a list of kubernetes environment configs out of the specified extensions.
     *
     * @param extensions THe extensions which are used to create the kubernetes environment configs.
     * @return The list of kubernetes environment configs.
     * @throws EnvironmentConfigException If necessary parameters are missing.
     */
    public static List<KubernetesEnvironmentConfig> createKubernetesEnvironmentConfigs(Collection<EnvironmentExtension> extensions) throws EnvironmentConfigException {
        List<KubernetesEnvironmentConfig> configs = new ArrayList<>();
        for (EnvironmentExtension extension : extensions) {
            configs.add(createKubernetesEnvironmentConfig(extension));
        }
        return configs;
    }

    /**
     * Creates a list of environment configs out of the specified extensions.
     *
     * @param extensions The extensions which are used to create the environment configs.
     * @return The list of environment configs.
     * @throws EnvironmentConfigException If necessary parameters are missing.
     */
    public static List<EnvironmentConfig> createEnvironmentConfigs(Collection<EnvironmentExtension> extensions) throws EnvironmentConfigException {
        List<EnvironmentConfig> configs = new ArrayList<>();
        for (EnvironmentExtension extension : extensions) {
            configs.add(createEnvironmentConfig(extension));
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
    public static KubernetesEnvironmentConfig createKubernetesEnvironmentConfig(EnvironmentExtension extension) throws EnvironmentConfigException {
        String baseUrl = extractBaseUrl(extension);
        String updateStrategy = extractUpdateStrategy(extension);
        String namespace = extractNamespace(extension);
        KubernetesEnvironmentConfig environmentConfig = new KubernetesEnvironmentConfig(baseUrl, updateStrategy, namespace);

        // Set authorization config
        AuthConfig authConfig = extractAuthConfig(extension);
        environmentConfig.setAuthConfig(authConfig);

        // Set ssl config
        SSLConfig sslConfig = extractSSLConfig(extension);
        environmentConfig.setSslConfig(sslConfig);

        return environmentConfig;
    }

    /**
     * Creates a new kubernetes environment config using the specified extension.
     *
     * @param extension The extension which contains the configuration.
     * @return The created kubernetes config.
     * @throws EnvironmentConfigException If necessary parameters are missing.
     */
    public static EnvironmentConfig createEnvironmentConfig(EnvironmentExtension extension) throws EnvironmentConfigException {
        String baseUrl = extractBaseUrl(extension);
        String updateStrategy = extractUpdateStrategy(extension);
        EnvironmentConfig environmentConfig = new EnvironmentConfig(baseUrl, updateStrategy);

        // Set authorization config
        AuthConfig authConfig = extractAuthConfig(extension);
        environmentConfig.setAuthConfig(authConfig);

        // Set ssl config
        SSLConfig sslConfig = extractSSLConfig(extension);
        environmentConfig.setSslConfig(sslConfig);

        return environmentConfig;
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
        SSLConfig sslConfig;
        SSLExtension sslExtension = extension.getSsl();
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
        AuthExtension authExtension = extension.getAuth();
        if (authExtension != null) {
            // Set a token if available
            String token = authExtension.getToken();
            if (token != null && !token.isEmpty()) {
                authConfig.setToken(token);
            }

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
     * Extracts the update strategy out of the specified extension, if none is defined the default update strategy is used.
     *
     * @param extension The extension which contains the update strategy.
     * @return The extracted update strategy.
     */
    private static String extractUpdateStrategy(EnvironmentExtension extension) {
        String updateStrategy = extension.getUpdateStrategy();
        if (updateStrategy == null || updateStrategy.isEmpty()) {
            updateStrategy = DEFAULT_UPDATE_STRATEGY;
        }
        return updateStrategy;
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
