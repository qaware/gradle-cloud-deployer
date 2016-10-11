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
package de.qaware.cloud.deployer.plugin.token;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class OpenIdConnectIdTokenInitializerTest {

    private static final String TOKEN_DIR = "/de/qaware/cloud/deployer/plugin/token/";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    @Test
    public void testInitialize() throws EnvironmentConfigException {
        String authToken = "TOKEN";
        String authTokenRequestBody = createTokenBody(authToken);
        String correctToken = "TOKEN_RESPONSE";
        String tokenResponseBody = createTokenBody(correctToken);

        wireMockRule.stubFor(post(urlEqualTo("/acs/api/v1/auth/login"))
                .withRequestBody(equalTo(authTokenRequestBody))
                .willReturn(aResponse().withStatus(200).withBody(tokenResponseBody)));

        File tokenFile = new File(this.getClass().getResource(TOKEN_DIR + "token.txt").getPath());
        OpenIdConnectIdTokenInitializer openIdConnectIdTokenInitializer = new OpenIdConnectIdTokenInitializer(tokenFile);

        EnvironmentConfig environmentConfig = mock(EnvironmentConfig.class);
        when(environmentConfig.getBaseUrl()).thenReturn("http://localhost:" + wireMockRule.port());

        String token = openIdConnectIdTokenInitializer.initialize(environmentConfig);
        assertEquals(correctToken, token);
    }

    @Test
    public void testInitializeWithInvalidToken() throws EnvironmentConfigException {
        wireMockRule.stubFor(post(urlEqualTo("/acs/api/v1/auth/login"))
                .willReturn(aResponse().withStatus(401)));

        File tokenFile = new File(this.getClass().getResource(TOKEN_DIR + "token.txt").getPath());
        OpenIdConnectIdTokenInitializer openIdConnectIdTokenInitializer = new OpenIdConnectIdTokenInitializer(tokenFile);

        EnvironmentConfig environmentConfig = mock(EnvironmentConfig.class);
        when(environmentConfig.getBaseUrl()).thenReturn("http://localhost:" + wireMockRule.port());

        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_DCOS_API_TOKEN");
        TokenInitializerTestHelper.assertExceptionOnInitialize(openIdConnectIdTokenInitializer, mock(EnvironmentConfig.class), message);
    }

    @Test
    public void testInitializeWithNotExistingFile() {
        File tokenFile = new File(TOKEN_DIR + "token-not-existing.txt");
        OpenIdConnectIdTokenInitializer openIdConnectIdTokenInitializer = new OpenIdConnectIdTokenInitializer(tokenFile);
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_TOKEN_FROM_FILE", tokenFile.getPath());
        TokenInitializerTestHelper.assertExceptionOnInitialize(openIdConnectIdTokenInitializer, mock(EnvironmentConfig.class), message);
    }

    @Test
    public void testInitializeWithEmptyFile() {
        File tokenFile = new File(TOKEN_DIR + "token-empty.txt");
        OpenIdConnectIdTokenInitializer openIdConnectIdTokenInitializer = new OpenIdConnectIdTokenInitializer(tokenFile);
        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_TOKEN_FROM_FILE", tokenFile.getPath());
        TokenInitializerTestHelper.assertExceptionOnInitialize(openIdConnectIdTokenInitializer, mock(EnvironmentConfig.class), message);
    }

    private String createTokenBody(String token) {
        return String.format("{\"token\":\"%s\"}", token);
    }
}
