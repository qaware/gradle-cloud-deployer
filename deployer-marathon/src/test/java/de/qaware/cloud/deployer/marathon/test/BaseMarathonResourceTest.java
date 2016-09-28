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
package de.qaware.cloud.deployer.marathon.test;

import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.test.BaseResourceTest;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

public abstract class BaseMarathonResourceTest extends BaseResourceTest {

    private static final String FORCE_PARAM = "?force=true";

    protected void testCreate(UrlPattern creationPattern, UrlPattern instancePattern) throws ResourceException {
        String scenarioName = "testCreate";

        // Add force param to url pattern
        creationPattern = urlEqualTo(creationPattern.getExpected() + FORCE_PARAM);

        // Create
        instanceRule.stubFor(post(creationPattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .withRequestBody(equalTo(resource.getResourceConfig().getContent()))
                .withQueryParam("force", equalTo("true"))
                .willReturn(aResponse().withStatus(201))
                .willSetStateTo("halfCreated"));

        // Simulate creating
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("halfCreated")
                .willReturn(aResponse().withStatus(404))
                .willSetStateTo("created"));

        // Created
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("created")
                .willReturn(aResponse().withStatus(200)));

        // Test
        resource.create();

        // Verify body
        instanceRule.verify(postRequestedFor(creationPattern)
                .withRequestBody(equalTo(resource.getResourceConfig().getContent())));

        // Verify calls
        instanceRule.verify(1, postRequestedFor(creationPattern));
        instanceRule.verify(2, getRequestedFor(instancePattern));
    }

    protected void testCreateRetry(UrlPattern creationPattern, UrlPattern instancePattern) throws ResourceException {
        String scenarioName = "testCreateRetry";

        // Add force param to url pattern
        creationPattern = urlEqualTo(creationPattern.getExpected() + FORCE_PARAM);

        // Simulate busy cloud
        instanceRule.stubFor(post(creationPattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(409))
                .willSetStateTo("cloudNotBusy"));

        // Create
        instanceRule.stubFor(post(creationPattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("cloudNotBusy")
                .withRequestBody(equalTo(resource.getResourceConfig().getContent()))
                .willReturn(aResponse().withStatus(201))
                .willSetStateTo("halfCreated"));

        // Simulate creating
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("halfCreated")
                .willReturn(aResponse().withStatus(404))
                .willSetStateTo("created"));

        // Created
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("created")
                .willReturn(aResponse().withStatus(200)));

        // Test
        resource.create();

        // Verify body
        instanceRule.verify(postRequestedFor(creationPattern)
                .withRequestBody(equalTo(resource.getResourceConfig().getContent())));

        // Verify calls
        instanceRule.verify(2, postRequestedFor(creationPattern));
        instanceRule.verify(2, getRequestedFor(instancePattern));
    }

    protected void testDelete(UrlPattern instancePattern) throws ResourceException {
        String scenarioName = "testDelete";

        // Create a delete pattern with force param
        UrlPattern deleteInstancePattern = urlEqualTo(instancePattern.getExpected() + FORCE_PARAM);

        // Delete
        instanceRule.stubFor(delete(deleteInstancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("deletionStarted"));

        // Simulate deleting
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deletionStarted")
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("deletionEnded"));

        // Deleted
        instanceRule.stubFor(get(instancePattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deletionEnded")
                .willReturn(aResponse().withStatus(404)));

        // Test
        resource.delete();

        // Verify
        instanceRule.verify(1, deleteRequestedFor(deleteInstancePattern));
        instanceRule.verify(2, getRequestedFor(instancePattern));
    }

    protected void testUpdate(UrlPattern instancePattern) throws ResourceException, IOException {
        // Create pattern
        UrlPattern createInstancePattern = urlEqualTo(instancePattern.getExpected() + FORCE_PARAM);

        // Update deployment
        instanceRule.stubFor(put(createInstancePattern)
                .withRequestBody(equalTo(resource.getResourceConfig().getContent()))
                .willReturn(aResponse().withStatus(200)));

        resource.update();

        // Verify calls
        instanceRule.verify(1, putRequestedFor(createInstancePattern));
    }
}
