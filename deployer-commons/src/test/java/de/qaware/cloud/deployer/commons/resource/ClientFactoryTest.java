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
package de.qaware.cloud.deployer.commons.resource;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import de.qaware.cloud.deployer.commons.config.environment.AuthConfig;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.environment.SSLConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import okio.ByteString;
import org.junit.Rule;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;

/**
 * @author sjahreis
 */
public class ClientFactoryTest {

    private static final UrlPattern TEST_PATTERN = urlEqualTo("/test");
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String TOKEN = "TOKEN";
    private static final String KEYSTORE_PATH = ClientFactoryTest.class.getResource("/certs/identity.jks").getPath();
    private static String HTTP_BASE_URL = "http://localhost";
    private static String HTTPS_BASE_URL = "https://localhost";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options()
            .dynamicPort()
            .dynamicHttpsPort()
            .keystorePath(KEYSTORE_PATH)
    );

    @Test
    public void testClientFactoryDefault() throws ResourceException, IOException {
        wireMockRule.stubFor(get(TEST_PATTERN)
                .willReturn(aResponse().withStatus(200)));

        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", HTTP_BASE_URL + ":" + wireMockRule.port(), Strategy.REPLACE);
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        ClientFactoryTestService clientFactoryTestService = clientFactory.create(ClientFactoryTestService.class);

        clientFactoryTestService.test().execute();

        wireMockRule.verify(1, getRequestedFor(TEST_PATTERN));

        List<LoggedRequest> requests = wireMockRule.findAll(getRequestedFor(TEST_PATTERN));
        LoggedRequest request = requests.get(0);

        assertFalse(request.containsHeader("Authorization"));
    }

    @Test
    public void testClientFactoryWithBasicAuthorization() throws ResourceException, IOException {
        wireMockRule.stubFor(get(TEST_PATTERN)
                .withBasicAuth(USERNAME, PASSWORD)
                .willReturn(aResponse().withStatus(200)));

        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", HTTP_BASE_URL + ":" + wireMockRule.port(), Strategy.REPLACE);
        environmentConfig.setAuthConfig(new AuthConfig(USERNAME, PASSWORD));
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        ClientFactoryTestService clientFactoryTestService = clientFactory.create(ClientFactoryTestService.class);

        clientFactoryTestService.test().execute();

        wireMockRule.verify(1, getRequestedFor(TEST_PATTERN));
    }

    @Test
    public void testClientFactoryWithTokenAuthorization() throws ResourceException, IOException {
        wireMockRule.stubFor(get(TEST_PATTERN)
                .withHeader("Authorization", equalTo("token=" + TOKEN))
                .willReturn(aResponse().withStatus(200)));

        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", HTTP_BASE_URL + ":" + wireMockRule.port(), Strategy.REPLACE);
        environmentConfig.setAuthConfig(new AuthConfig(TOKEN));
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        ClientFactoryTestService clientFactoryTestService = clientFactory.create(ClientFactoryTestService.class);

        clientFactoryTestService.test().execute();

        wireMockRule.verify(1, getRequestedFor(TEST_PATTERN));
    }

    @Test(expected = SSLException.class)
    public void testClientFactoryWithoutSSLConfig() throws ResourceException, IOException {
        wireMockRule.stubFor(get(TEST_PATTERN)
                .willReturn(aResponse().withStatus(200)));

        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", HTTPS_BASE_URL + ":" + wireMockRule.httpsPort(), Strategy.REPLACE);
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        ClientFactoryTestService clientFactoryTestService = clientFactory.create(ClientFactoryTestService.class);

        clientFactoryTestService.test().execute();
    }

    @Test
    public void testClientFactoryWithTrustAllSSLConfig() throws ResourceException, IOException {
        wireMockRule.stubFor(get(TEST_PATTERN)
                .willReturn(aResponse().withStatus(200)));

        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", HTTPS_BASE_URL + ":" + wireMockRule.httpsPort(), Strategy.REPLACE);
        environmentConfig.setSslConfig(new SSLConfig(true));
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        ClientFactoryTestService clientFactoryTestService = clientFactory.create(ClientFactoryTestService.class);

        clientFactoryTestService.test().execute();

        wireMockRule.verify(1, getRequestedFor(TEST_PATTERN));
    }

    @Test
    public void testClientFactoryWithTrustOneSSLConfig() throws ResourceException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        wireMockRule.stubFor(get(TEST_PATTERN)
                .willReturn(aResponse().withStatus(200)));


        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", HTTPS_BASE_URL + ":" + wireMockRule.httpsPort(), Strategy.REPLACE);
        environmentConfig.setSslConfig(new SSLConfig(getKey()));
        ClientFactory clientFactory = new ClientFactory(environmentConfig);
        ClientFactoryTestService clientFactoryTestService = clientFactory.create(ClientFactoryTestService.class);

        clientFactoryTestService.test().execute();

        wireMockRule.verify(1, getRequestedFor(TEST_PATTERN));
    }

    private String getKey() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("JKS");

        InputStream readStream = new FileInputStream(KEYSTORE_PATH);
        ks.load(readStream, "password".toCharArray());
        readStream.close();

        X509Certificate cert = (X509Certificate) ks.getCertificate("wiremock");

        ByteString encodedBytes = ByteString.of(cert.getEncoded());
        return encodedBytes.base64();
    }
}
