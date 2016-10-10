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

import de.qaware.cloud.deployer.commons.Deployer;
import de.qaware.cloud.deployer.commons.config.environment.AuthConfig;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.environment.SSLConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.kubernetes.KubernetesDeployer;
import de.qaware.cloud.deployer.kubernetes.config.cloud.KubernetesEnvironmentConfig;
import de.qaware.cloud.deployer.marathon.MarathonDeployer;
import de.qaware.cloud.deployer.plugin.extension.*;
import de.qaware.cloud.deployer.plugin.token.TokenInitializer;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class EnvironmentFactoryTest {

    @Test
    public void testCreate() throws EnvironmentConfigException {
        EnvironmentExtension environmentExtension1 = createEnvironmentExtension(1);
        EnvironmentExtension environmentExtension2 = createEnvironmentExtension(2);

        List<EnvironmentExtension> environmentExtensions = new ArrayList<>();
        environmentExtensions.add(environmentExtension1);
        environmentExtensions.add(environmentExtension2);

        DeployerExtension deployerExtension = mock(DeployerExtension.class);
        when(deployerExtension.getConfigs()).thenReturn(environmentExtensions);

        List<Environment> environments = EnvironmentFactory.create(deployerExtension);
        assertEquals(environmentExtensions.size(), environments.size());
        for (int i = 0; i < environmentExtensions.size(); i++) {
            Environment environment = environments.get(i);
            EnvironmentExtension environmentExtension = environmentExtensions.get(i);
            assertEquals(environment.getId(), environmentExtension.getId());
            testEquality(environmentExtension, environment.getConfig());
            assertEquals(environmentExtension.getFiles(), environment.getFiles());
            testEquality(environmentExtension, environment.getDeployer());
        }
    }

    private EnvironmentExtension createEnvironmentExtension(int number) {
        SSLExtension sslExtension = mock(SSLExtension.class);
        AuthExtension authExtension = mock(AuthExtension.class);
        List<File> files = new ArrayList<>();
        files.add(mock(File.class));
        files.add(mock(File.class));

        EnvironmentExtension environmentExtension = mock(EnvironmentExtension.class);
        when(environmentExtension.getDeployerType()).thenReturn(DeployerType.values()[RandomUtils.nextInt(0, 2)]);
        when(environmentExtension.getId()).thenReturn("id" + number);
        when(environmentExtension.getBaseUrl()).thenReturn("baseUrl" + number);
        when(environmentExtension.getStrategy()).thenReturn("REPLACE");
        when(environmentExtension.getSslExtension()).thenReturn(sslExtension);
        when(environmentExtension.getAuthExtension()).thenReturn(authExtension);
        when(environmentExtension.getFiles()).thenReturn(files);
        when(environmentExtension.getNamespace()).thenReturn("namespace" + number);

        return environmentExtension;
    }

    private void testEquality(EnvironmentExtension environmentExtension, EnvironmentConfig config) throws EnvironmentConfigException {
        assertEquals(environmentExtension.getStrategy(), config.getStrategy().toString());
        assertEquals(environmentExtension.getId(), config.getId());
        assertEquals(environmentExtension.getBaseUrl(), config.getBaseUrl());
        if (config instanceof KubernetesEnvironmentConfig) {
            String namespace = ((KubernetesEnvironmentConfig) config).getNamespace();
            assertEquals(environmentExtension.getNamespace(), namespace);
        }
        testEquality(environmentExtension.getAuthExtension(), config.getAuthConfig());
        testEquality(environmentExtension.getSslExtension(), config.getSslConfig());
    }

    private void testEquality(AuthExtension authExtension, AuthConfig authConfig) throws EnvironmentConfigException {
        TokenInitializer tokenInitializer = authExtension.getToken();
        String token = tokenInitializer == null ? null : tokenInitializer.initialize(mock(EnvironmentConfig.class));
        assertEquals(authExtension.getPassword(), authConfig.getPassword());
        assertEquals(authExtension.getUsername(), authConfig.getUsername());
        assertEquals(token, authConfig.getToken());
    }

    private void testEquality(SSLExtension sslExtension, SSLConfig sslConfig) {
        String certificate = sslExtension.getCertificate();
        certificate = certificate == null ? "" : certificate;
        assertEquals(certificate, sslConfig.getCertificate());
        assertEquals(!certificate.isEmpty(), sslConfig.hasCertificate());
        assertEquals(sslExtension.isTrustAll(), sslConfig.isTrustAll());
    }

    private void testEquality(EnvironmentExtension environmentExtension, Deployer deployer) {
        switch (environmentExtension.getDeployerType()) {
            case KUBERNETES:
                assertTrue(deployer instanceof KubernetesDeployer);
                break;
            case MARATHON:
                assertTrue(deployer instanceof MarathonDeployer);
                break;
            default:
                throw new IllegalArgumentException("This kind of deployer type is not supported");
        }
    }
}
