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

import com.github.tomakehurst.wiremock.WireMockServer;
import de.qaware.cloud.deployer.commons.config.cloud.AuthConfig;
import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.qaware.cloud.deployer.dcos.logging.DcosMessageBundle.DCOS_MESSAGE_BUNDLE;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TokenResourceTest {

    private static final String SERVER_ADDRESS = "http://localhost";
    private static final String OPEN_ID_TOKEN = "validToken";
    private static final String AUTHENTICATION_TOKEN = "tokenResponse";

    private static WireMockServer wireMockServer;

    private EnvironmentConfig environmentConfig;

    @BeforeClass
    public static void setUpClass() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @AfterClass
    public static void tearDownClass() {
        wireMockServer.shutdown();
    }

    @Before
    public void setUp() {
        environmentConfig = createEnvironmentConfig();
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
        String invalidToken = OPEN_ID_TOKEN.substring(0, OPEN_ID_TOKEN.length() - 1);
        assertExceptionOnRetrieveAuthenticationToken(
                environmentConfig,
                invalidToken,
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN")
        );
    }

    @Test
    public void testRetrieveAuthenticationTokenWithInvalidAddress() throws ResourceException {
        EnvironmentConfig newEnvironmentConfig = new EnvironmentConfig("test", "http://bla-blub-foobar-bla-12341.xy/", environmentConfig.getStrategy());
        assertExceptionOnRetrieveAuthenticationToken(
                newEnvironmentConfig,
                OPEN_ID_TOKEN,
                DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_ESTABLISH_CONNECTION")
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
        TokenResource tokenResource = new TokenResource(environmentConfig);
        String authenticationToken = tokenResource.retrieveAuthenticationToken(OPEN_ID_TOKEN);
        assertFalse(authenticationToken.isEmpty());
        assertEquals(AUTHENTICATION_TOKEN, authenticationToken);
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
        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", SERVER_ADDRESS + ":" + wireMockServer.port(), Strategy.REPLACE);
        environmentConfig.setSslConfig(new SSLConfig());
        environmentConfig.setAuthConfig(new AuthConfig());
        return environmentConfig;
    }
}
