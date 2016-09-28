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
package de.qaware.cloud.deployer.commons.test;

import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BasePingResource;
import de.qaware.cloud.deployer.commons.resource.BaseResource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class BasePingResourceTest extends BaseResourceTest {

    private BasePingResource pingResource;

    @Override
    public BaseResource createResource() throws ResourceException, ResourceConfigException {
        pingResource = createPingResource();
        return null;
    }

    public abstract BasePingResource createPingResource() throws ResourceException;

    protected void testFailingPing(UrlPattern pingPattern) throws ResourceException {
        instanceRule.stubFor(get(pingPattern)
                .willReturn(aResponse().withStatus(401)));

        boolean exceptionThrown = false;
        try {
            pingResource.ping();
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_PING_FAILED", environmentConfig.getId(), 401), e.getMessage());
        }
        assertTrue(exceptionThrown);

        // Verify calls
        instanceRule.verify(1, getRequestedFor(pingPattern));
    }

    protected void testPing(UrlPattern pingPattern) throws ResourceException {
        instanceRule.stubFor(get(pingPattern)
                .willReturn(aResponse().withStatus(200)));

        pingResource.ping();

        // Verify calls
        instanceRule.verify(1, getRequestedFor(pingPattern));
    }
}
