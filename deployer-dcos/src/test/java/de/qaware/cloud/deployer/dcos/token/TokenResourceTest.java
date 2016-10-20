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
package de.qaware.cloud.deployer.dcos.token;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.environment.AuthConfig;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.environment.SSLConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import org.junit.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static de.qaware.cloud.deployer.dcos.logging.DcosMessageBundle.DCOS_MESSAGE_BUNDLE;
import static org.junit.Assert.*;

public class TokenResourceTest {

    private static final String SERVER_ADDRESS = "http://localhost";
    private static final String OPEN_ID_TOKEN = "validToken";
    private static final String AUTHENTICATION_TOKEN = "tokenResponse";
    private static final UrlPattern TOKEN_PATTERN = urlEqualTo("/acs/api/v1/auth/login");

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(WireMockConfiguration.options().dynamicPort());

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    private EnvironmentConfig environmentConfig;

    @Before
    public void setUp() {
        environmentConfig = createEnvironmentConfig();
    }

    @After
    public void reset() {
        instanceRule.resetMappings();
    }

    @Test
    public void testRetrieveAuthenticationTokenWithNullToken() throws ResourceException {
        assertExceptionOnRetrieveAuthenticationToken(
                environmentConfig,
                null,
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_EMPTY_TOKEN")
        );
    }

    @Test
    public void testRetrieveAuthenticationTokenWithEmptyToken() throws ResourceException {
        assertExceptionOnRetrieveAuthenticationToken(
                environmentConfig,
                "",
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_EMPTY_TOKEN")
        );
    }

    @Test
    public void testRetrieveAuthenticationTokenWithInvalidToken() throws ResourceException {
        instanceRule.stubFor(post(TOKEN_PATTERN)
                .willReturn(aResponse().withStatus(401)));

        assertExceptionOnRetrieveAuthenticationToken(
                environmentConfig,
                OPEN_ID_TOKEN,
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN")
        );

        verify(1, postRequestedFor(TOKEN_PATTERN));
        verify(postRequestedFor(TOKEN_PATTERN).withRequestBody(equalTo("{\"token\":\"" + OPEN_ID_TOKEN + "\"}")));
    }

    @Test
    public void testRetrieveAuthenticationTokenWithEmptyTokenResponse() throws ResourceException {
        instanceRule.stubFor(post(TOKEN_PATTERN)
                .willReturn(aResponse().withStatus(200)));

        assertExceptionOnRetrieveAuthenticationToken(
                environmentConfig,
                OPEN_ID_TOKEN,
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN")
        );

        verify(1, postRequestedFor(TOKEN_PATTERN));
        verify(postRequestedFor(TOKEN_PATTERN).withRequestBody(equalTo("{\"token\":\"" + OPEN_ID_TOKEN + "\"}")));
    }

    @Test
    public void testRetrieveAuthenticationTokenWithInvalidAddress() throws ResourceException {
        EnvironmentConfig newEnvironmentConfig = new EnvironmentConfig("test", "http://bla-blub-foobar-bla-12341.xy/", environmentConfig.getStrategy());
        assertExceptionOnRetrieveAuthenticationToken(
                newEnvironmentConfig,
                OPEN_ID_TOKEN,
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN")
        );

        newEnvironmentConfig = new EnvironmentConfig("test", "http://google.de/mich/gibts/nicht/1234/bla/", environmentConfig.getStrategy());
        assertExceptionOnRetrieveAuthenticationToken(
                newEnvironmentConfig,
                OPEN_ID_TOKEN,
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN")
        );
    }

    @Test
    public void testRetrieveAuthenticationToken() throws ResourceException, EnvironmentConfigException {
        instanceRule.stubFor(post(urlEqualTo("/acs/api/v1/auth/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"token\":\"tokenResponse\"}"))
        );

        TokenResource tokenResource = new TokenResource(environmentConfig);
        String authenticationToken = tokenResource.retrieveAuthenticationToken(OPEN_ID_TOKEN);
        assertFalse(authenticationToken.isEmpty());
        assertEquals(AUTHENTICATION_TOKEN, authenticationToken);

        verify(1, postRequestedFor(TOKEN_PATTERN));
        verify(postRequestedFor(TOKEN_PATTERN).withRequestBody(equalTo("{\"token\":\"" + OPEN_ID_TOKEN + "\"}")));
    }

    private void assertExceptionOnRetrieveAuthenticationToken(EnvironmentConfig environmentConfig,
                                                              String token,
                                                              String exceptionMessage) throws ResourceException {
        boolean exceptionThrown = false;
        TokenResource tokenResource = new TokenResource(environmentConfig);
        try {
            tokenResource.retrieveAuthenticationToken(token);
        } catch (EnvironmentConfigException e) {
            exceptionThrown = true;
            assertEquals(exceptionMessage, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private EnvironmentConfig createEnvironmentConfig() {
        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", SERVER_ADDRESS + ":" + instanceRule.port(), Strategy.REPLACE);
        environmentConfig.setSslConfig(new SSLConfig());
        environmentConfig.setAuthConfig(new AuthConfig());
        return environmentConfig;
    }
}
