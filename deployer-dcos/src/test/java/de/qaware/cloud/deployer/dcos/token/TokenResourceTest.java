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

import de.qaware.cloud.deployer.commons.config.cloud.AuthConfig;
import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.dcos.test.DCOSTestEnvironmentUtil;
import junit.framework.TestCase;
import org.junit.Before;

import static de.qaware.cloud.deployer.dcos.logging.DCOSMessageBundle.DCOS_MESSAGE_BUNDLE;

public class TokenResourceTest extends TestCase {

    private EnvironmentConfig environmentConfig;

    @Before
    public void setUp() throws Exception {
        this.environmentConfig = DCOSTestEnvironmentUtil.createEnvironmentConfig("SOFT");
    }

    public void testRetrieveApiTokenWithEmptyToken() throws ResourceException {
        environmentConfig.setAuthConfig(new AuthConfig());
        assertException(environmentConfig, DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_EMPTY_TOKEN"));
    }

    public void testRetrieveApiTokenWithInvalidToken() throws ResourceException {
        String validToken = environmentConfig.getAuthConfig().getToken();
        String invalidToken = validToken.substring(0, validToken.length() - 2);
        environmentConfig.setAuthConfig(new AuthConfig(invalidToken));
        assertException(environmentConfig, DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN"));
    }

    public void testRetrieveApiTokenInvalidAddress() throws ResourceException {
        EnvironmentConfig newEnvironmentConfig = new EnvironmentConfig("http://bla-blub-foobar-bla-12341.xy/", environmentConfig.getUpdateStrategy());
        newEnvironmentConfig.setAuthConfig(environmentConfig.getAuthConfig());
        assertException(newEnvironmentConfig, DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_ESTABLISH_CONNECTION"));

        newEnvironmentConfig = new EnvironmentConfig("http://google.de/mich/gibts/nicht/1234/bla/", environmentConfig.getUpdateStrategy());
        newEnvironmentConfig.setAuthConfig(environmentConfig.getAuthConfig());
        assertException(newEnvironmentConfig, DCOS_MESSAGE_BUNDLE.getMessage("DEPLOYER_DCOS_ERROR_COULD_NOT_RETRIEVE_TOKEN"));
    }

    public void testRetrieveApiToken() throws ResourceException, EnvironmentConfigException {
        TokenResource tokenResource = new TokenResource(environmentConfig);
        String token = tokenResource.retrieveApiToken();
        assertFalse(token.isEmpty());
    }

    private void assertException(EnvironmentConfig environmentConfig, String exceptionMessage) throws ResourceException {
        boolean exceptionThrown = false;
        TokenResource tokenResource = new TokenResource(environmentConfig);
        try {
            tokenResource.retrieveApiToken();
        } catch (EnvironmentConfigException e) {
            exceptionThrown = true;
            assertEquals(exceptionMessage, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
