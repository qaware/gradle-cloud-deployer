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
package de.qaware.cloud.deployer.plugin.environment;

import de.qaware.cloud.deployer.commons.config.environment.AuthConfig;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.environment.SSLConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.plugin.extension.AuthExtension;
import de.qaware.cloud.deployer.plugin.extension.DeployerType;
import de.qaware.cloud.deployer.plugin.extension.EnvironmentExtension;
import de.qaware.cloud.deployer.plugin.extension.SSLExtension;
import de.qaware.cloud.deployer.plugin.token.TokenInitializer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class EnvironmentConfigFactoryTest {

    private static final Strategy DEFAULT_STRATEGY = Strategy.REPLACE;

    private EnvironmentExtension environmentExtension;
    private SSLExtension sslExtension;
    private AuthExtension authExtension;
    private TokenInitializer tokenInitializer;

    @Before
    public void setup() {
        List<File> files = new ArrayList<>();

        sslExtension = mock(SSLExtension.class);
        authExtension = mock(AuthExtension.class);
        tokenInitializer =  mock(TokenInitializer.class);

        environmentExtension = mock(EnvironmentExtension.class);
        when(environmentExtension.getDeployerType()).thenReturn(DeployerType.MARATHON);
        when(environmentExtension.getId()).thenReturn("id");
        when(environmentExtension.getBaseUrl()).thenReturn("baseUrl");
        when(environmentExtension.getStrategy()).thenReturn("REPLACE");
        when(environmentExtension.getSslExtension()).thenReturn(sslExtension);
        when(environmentExtension.getAuthExtension()).thenReturn(authExtension);
        when(environmentExtension.getFiles()).thenReturn(files);
        when(environmentExtension.getNamespace()).thenReturn("namespace");
    }

    @Test
    public void testCreateDefault() throws EnvironmentConfigException {
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateDefaultKubernetes() throws EnvironmentConfigException {
        when(environmentExtension.getDeployerType()).thenReturn(DeployerType.KUBERNETES);
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateNull() {
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EXTENSION_OR_CONFIG_NULL");
        assertExceptionOnCreate(null, message);
    }

    @Test
    public void testCreateWithSSLTrustAll() throws EnvironmentConfigException {
        when(sslExtension.isTrustAll()).thenReturn(true);
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithSSLCustomCertificate() throws EnvironmentConfigException, ResourceConfigException {
        when(sslExtension.getCertificate()).thenReturn("CERTIFICATE");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithEmptyCustomCertificate() throws EnvironmentConfigException {
        when(sslExtension.getCertificate()).thenReturn("");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithTokenAuthentication() throws EnvironmentConfigException {
        when(tokenInitializer.initialize(any())).thenReturn("TOKEN");
        when(authExtension.getToken()).thenReturn(tokenInitializer);
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithBasicAuthentication() throws EnvironmentConfigException {
        when(authExtension.getPassword()).thenReturn("PASSWORD");
        when(authExtension.getUsername()).thenReturn("USERNAME");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithBasicAuthenticationMissingPassword() throws EnvironmentConfigException {
        when(authExtension.getUsername()).thenReturn("USERNAME");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);

        // Reset
        when(authExtension.getUsername()).thenReturn(null);

        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithBasicAuthenticationMissingUsername() throws EnvironmentConfigException {
        when(authExtension.getPassword()).thenReturn("PASSWORD");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);

        // Reset
        when(authExtension.getPassword()).thenReturn(null);

        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithNullStrategy() throws EnvironmentConfigException {
        when(environmentExtension.getStrategy()).thenReturn(null);
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);

        // Reset
        when(environmentExtension.getStrategy()).thenReturn(DEFAULT_STRATEGY.toString());

        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithEmptyStrategy() throws EnvironmentConfigException {
        when(environmentExtension.getStrategy()).thenReturn("");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);

        // Reset
        when(environmentExtension.getStrategy()).thenReturn(DEFAULT_STRATEGY.toString());

        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithResetStrategy() throws EnvironmentConfigException {
        when(environmentExtension.getStrategy()).thenReturn("RESET");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithReplaceStrategy() throws EnvironmentConfigException {
        when(environmentExtension.getStrategy()).thenReturn("REPLACE");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithUpdateStrategy() throws EnvironmentConfigException {
        when(environmentExtension.getStrategy()).thenReturn("UPDATE");
        EnvironmentConfig environmentConfig = EnvironmentConfigFactory.create(environmentExtension);
        testEquality(environmentConfig);
    }

    @Test
    public void testCreateWithUnsupportedStrategy() {
        String strategy = "BLA";
        when(environmentExtension.getStrategy()).thenReturn(strategy);
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_UNKNOWN_STRATEGY", strategy);
        assertExceptionOnCreate(environmentExtension, message);
    }

    @Test
    public void testCreateWithEmptyBaseUrl() {
        when(environmentExtension.getBaseUrl()).thenReturn("");
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_EMPTY_BASE_URL", environmentExtension.getId());
        assertExceptionOnCreate(environmentExtension, message);
    }

    private void assertExceptionOnCreate(EnvironmentExtension extension, String message) {
        boolean exceptionThrown = false;
        try {
            EnvironmentConfigFactory.create(extension);
        } catch (EnvironmentConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void testEquality(EnvironmentConfig config) throws EnvironmentConfigException {
        assertEquals(environmentExtension.getStrategy(), config.getStrategy().toString());
        assertEquals(environmentExtension.getId(), config.getId());
        assertEquals(environmentExtension.getBaseUrl(), config.getBaseUrl());
        if (config instanceof KubernetesEnvironmentConfig) {
            String namespace = ((KubernetesEnvironmentConfig) config).getNamespace();
            assertEquals(environmentExtension.getNamespace(), namespace);
        }
        testEquality(config.getAuthConfig());
        testEquality(config.getSslConfig());
    }

    private void testEquality(AuthConfig authConfig) throws EnvironmentConfigException {
        TokenInitializer tokenInitializer = authExtension.getToken();
        String token = tokenInitializer == null ? null : tokenInitializer.initialize(mock(EnvironmentConfig.class));
        assertEquals(authExtension.getPassword(), authConfig.getPassword());
        assertEquals(authExtension.getUsername(), authConfig.getUsername());
        assertEquals(token, authConfig.getToken());
    }

    private void testEquality(SSLConfig sslConfig) {
        String certificate = sslExtension.getCertificate();
        certificate = certificate == null ? "" : certificate;
        assertEquals(certificate, sslConfig.getCertificate());
        assertEquals(!certificate.isEmpty(), sslConfig.hasCertificate());
        assertEquals(sslExtension.isTrustAll(), sslConfig.isTrustAll());
    }
}
