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
package de.qaware.cloud.deployer.marathon.resource.ping;

import de.qaware.cloud.deployer.commons.config.cloud.AuthConfig;
import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironment;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironmentUtil;
import junit.framework.TestCase;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;

public class MarathonPingResourceIntegrationTest extends TestCase {

    private EnvironmentConfig environmentConfig;

    @Override
    public void setUp() throws Exception {
        MarathonTestEnvironment testEnvironment = MarathonTestEnvironmentUtil.createTestEnvironment();
        environmentConfig = testEnvironment.getEnvironmentConfig();
    }

    public void testPingWithoutCredentials() throws ResourceException {
        boolean exceptionThrown = false;
        environmentConfig.setAuthConfig(new AuthConfig());
        MarathonPingResource pingResource = new MarathonPingResource(environmentConfig);
        try {
            pingResource.ping();
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_PING_FAILED", environmentConfig.getId(), 401), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    public void testPingWithCredentials() throws ResourceException {
        MarathonPingResource pingResource = new MarathonPingResource(environmentConfig);
        pingResource.ping();
    }
}
