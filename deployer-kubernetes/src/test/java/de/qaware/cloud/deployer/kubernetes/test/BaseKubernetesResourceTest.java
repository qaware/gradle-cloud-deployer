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
package de.qaware.cloud.deployer.kubernetes.test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.test.BaseResourceTest;
import de.qaware.cloud.deployer.kubernetes.resource.api.delete.options.DeleteOptions;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class BaseKubernetesResourceTest extends BaseResourceTest {

    protected static final String NAMESPACE = "test";

    protected void testCreate(UrlPattern creationPattern, UrlPattern instancePattern) throws ResourceException {
        String scenarioName = "testCreate";

        // Create
        instanceRule.stubFor(post(creationPattern)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
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
        instanceRule.verify(1, postRequestedFor(creationPattern));
        instanceRule.verify(2, getRequestedFor(instancePattern));
    }

    protected void testCreateRetry(UrlPattern creationPattern, UrlPattern instancePattern) throws ResourceException {
        String scenarioName = "testCreateRetry";

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

    protected void testDelete(UrlPattern instancePattern) throws ResourceException, JsonProcessingException {
        String scenarioName = "testDelete";

        // Delete
        instanceRule.stubFor(delete(instancePattern)
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
        instanceRule.verify(1, deleteRequestedFor(instancePattern));
        instanceRule.verify(2, getRequestedFor(instancePattern));

        // Check if delete options are specified
        testDeleteOptions(instancePattern);
    }

    protected void testDeleteOptions(UrlPattern instancePattern) throws JsonProcessingException {
        String jsonDeleteOptions = new ObjectMapper(new JsonFactory()).writeValueAsString(new DeleteOptions(0));
        instanceRule.verify(deleteRequestedFor(instancePattern).withRequestBody(equalTo(jsonDeleteOptions)));
    }

    protected void testUpdate(UrlPattern instancePattern) throws ResourceException, IOException {
        // Prepare body
        JsonNode body;
        ContentType contentType = resource.getResourceConfig().getContentType();
        String content = resource.getResourceConfig().getContent();
        if (contentType == ContentType.YAML) {
            body = new ObjectMapper(new YAMLFactory()).readTree(content);
        } else if (contentType == ContentType.JSON) {
            body = new ObjectMapper(new JsonFactory()).readTree(content);
        } else {
            throw new IllegalArgumentException("Can't process this type of content");
        }
        String jsonContent = new ObjectMapper(new JsonFactory()).writeValueAsString(body);

        // Update deployment
        instanceRule.stubFor(patch(instancePattern)
                .withRequestBody(equalTo(jsonContent))
                .withHeader("Content-Type", equalTo("application/merge-patch+json; charset=utf-8"))
                .willReturn(aResponse().withStatus(200)));

        resource.update();

        // Verify calls
        instanceRule.verify(1, patchRequestedFor(instancePattern));
    }

    protected void testMissingUpdate() {
        boolean exceptionThrown = false;
        try {
            resource.update();
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_RESOURCE_SUPPORTS_NO_UPDATES", resource.toString()), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
