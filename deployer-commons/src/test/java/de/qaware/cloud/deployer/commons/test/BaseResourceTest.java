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

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.environment.AuthConfig;
import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.environment.SSLConfig;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class BaseResourceTest {

    private static final String ENVIRONMENT_ID = "test-env";
    private static final String SERVER_ADDRESS = "http://localhost";

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(WireMockConfiguration.options().dynamicPort());

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    protected BaseResource resource;

    protected EnvironmentConfig environmentConfig;

    protected ClientFactory clientFactory;

    public abstract BaseResource createResource() throws ResourceException, ResourceConfigException;

    @Before
    public void setup() throws ResourceException, ResourceConfigException {
        // Create test environment
        environmentConfig = new EnvironmentConfig(ENVIRONMENT_ID, SERVER_ADDRESS + ":" + instanceRule.port(), Strategy.REPLACE);
        environmentConfig.setAuthConfig(new AuthConfig());
        environmentConfig.setSslConfig(new SSLConfig());
        clientFactory = new ClientFactory(environmentConfig);

        // Create resource
        resource = createResource();
    }

    @After
    public void reset() {
        instanceRule.resetMappings();
        instanceRule.resetScenarios();
    }

    protected void testExists(UrlPattern instancePattern) throws ResourceException {
        String scenarioName = "testExists";

        // Doesn't exist
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(404))
                .willSetStateTo("existsTrue"));

        // Check exists
        assertFalse(resource.exists());

        // Exists
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("existsTrue")
                .willReturn(aResponse().withStatus(200)));

        // Check exists
        assertTrue(resource.exists());

        // Verify calls
        instanceRule.verify(2, getRequestedFor(instancePattern));
    }
}
