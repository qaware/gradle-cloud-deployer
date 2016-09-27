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
package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.cloud.AuthConfig;
import de.qaware.cloud.deployer.commons.config.cloud.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import org.junit.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeploymentResourceTest {

    private static final String SERVER_ADDRESS = "http://localhost";
    private static final String NAMESPACE = "test-deployment";
    private static final UrlPattern DEPLOYMENTS_PATTERN = urlEqualTo("/apis/extensions/v1beta1/namespaces/test/deployments");
    private static final UrlPattern DEPLOYMENT_PATTERN = urlEqualTo("/apis/extensions/v1beta1/namespaces/test/deployments/zwitscher-eureka");
    private static final UrlPattern SCALE_PATTERN = urlEqualTo("/apis/extensions/v1beta1/namespaces/test/deployments/zwitscher-eureka/scale");
    private static final UrlPattern REPLICA_SETS_PATTERN = urlEqualTo("/apis/extensions/v1beta1/namespaces/test/replicasets?labelSelector=deployment-id%3Dzwitscher-eureka");

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(WireMockConfiguration.options().dynamicPort());

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    private DeploymentResource deploymentResource;

    @Before
    public void setUp() throws Exception {
        // Create test environment
        EnvironmentConfig environmentConfig = new EnvironmentConfig(NAMESPACE, SERVER_ADDRESS + ":" + instanceRule.port(), Strategy.REPLACE);
        environmentConfig.setAuthConfig(new AuthConfig());
        environmentConfig.setSslConfig(new SSLConfig());
        ClientFactory clientFactory = new ClientFactory(environmentConfig);

        // Create the deployment resource
        String deploymentDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/kubernetes/resource/deployment/deployment.yml");
        KubernetesResourceConfig resourceConfigV1 = new KubernetesResourceConfig("test", ContentType.YAML, deploymentDescriptionV1);
        deploymentResource = new DeploymentResource("test", resourceConfigV1, clientFactory);
    }

    @After
    public void reset() {
        instanceRule.resetMappings();
        instanceRule.resetScenarios();
    }

    @Test
    public void testExists() throws ResourceException {
        String scenarioName = "testExists";

        // Doesn't exist
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(404))
                .willSetStateTo("existsTrue"));

        // Check exists
        assertFalse(deploymentResource.exists());

        // Exists
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("existsTrue")
                .willReturn(aResponse().withStatus(200)));

        // Check exists
        assertTrue(deploymentResource.exists());

        // Verify calls
        instanceRule.verify(2, getRequestedFor(DEPLOYMENT_PATTERN));
    }

    @Test
    public void testCreate() throws ResourceException {
        String scenarioName = "testCreate";

        // Create
        instanceRule.stubFor(post(DEPLOYMENTS_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .withRequestBody(equalTo(deploymentResource.getResourceConfig().getContent()))
                .willReturn(aResponse().withStatus(201))
                .willSetStateTo("deploymentHalfCreated"));

        // Simulate creating
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deploymentHalfCreated")
                .willReturn(aResponse().withStatus(404))
                .willSetStateTo("deploymentCreated"));

        // Created
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deploymentCreated")
                .willReturn(aResponse().withStatus(200)));

        // Create deployment
        deploymentResource.create();

        // Verify body
        instanceRule.verify(postRequestedFor(DEPLOYMENTS_PATTERN)
                .withRequestBody(equalTo(deploymentResource.getResourceConfig().getContent())));

        // Verify calls
        instanceRule.verify(1, postRequestedFor(DEPLOYMENTS_PATTERN));
        instanceRule.verify(2, getRequestedFor(DEPLOYMENT_PATTERN));
    }

    @Test
    public void testCreateRetry() throws ResourceException {
        String scenarioName = "testCreateRetry";

        // Simulate busy cloud
        instanceRule.stubFor(post(DEPLOYMENTS_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(409))
                .willSetStateTo("cloudNotBusy"));

        // Create
        instanceRule.stubFor(post(DEPLOYMENTS_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("cloudNotBusy")
                .withRequestBody(equalTo(deploymentResource.getResourceConfig().getContent()))
                .willReturn(aResponse().withStatus(201))
                .willSetStateTo("deploymentHalfCreated"));

        // Simulate creating
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deploymentHalfCreated")
                .willReturn(aResponse().withStatus(404))
                .willSetStateTo("deploymentCreated"));

        // Created
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deploymentCreated")
                .willReturn(aResponse().withStatus(200)));

        // Create deployment
        deploymentResource.create();

        // Verify body
        instanceRule.verify(postRequestedFor(DEPLOYMENTS_PATTERN)
                .withRequestBody(equalTo(deploymentResource.getResourceConfig().getContent())));

        // Verify calls
        instanceRule.verify(2, postRequestedFor(DEPLOYMENTS_PATTERN));
        instanceRule.verify(2, getRequestedFor(DEPLOYMENT_PATTERN));
    }

    @Test
    public void testDelete() throws ResourceException, InterruptedException, TimeoutException {
        String scenarioName = "testDelete";

        // Scale pods down
        instanceRule.stubFor(put(SCALE_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("scaledDown"));

        // Delete deployment
        instanceRule.stubFor(delete(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("scaledDown")
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("deploymentHalfDeleted"));

        // Simulate deleting
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deploymentHalfDeleted")
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("deploymentDeleted"));

        // Deleted deployment
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("deploymentDeleted")
                .willReturn(aResponse().withStatus(404))
                .willSetStateTo("replicaSetDeletion"));

        // Delete replica set
        instanceRule.stubFor(delete(REPLICA_SETS_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("replicaSetDeletion")
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("replicaSetDeleted"));

        // Everything deleted
        instanceRule.stubFor(get(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("replicaSetDeleted")
                .willReturn(aResponse().withStatus(404)));

        deploymentResource.delete();

        // Verify calls
        instanceRule.verify(1, putRequestedFor(SCALE_PATTERN));
        instanceRule.verify(1, deleteRequestedFor(DEPLOYMENT_PATTERN));
        instanceRule.verify(1, deleteRequestedFor(REPLICA_SETS_PATTERN));
        instanceRule.verify(3, getRequestedFor(DEPLOYMENT_PATTERN));
    }

    @Test
    public void testUpdate() throws ResourceException, TimeoutException, InterruptedException, IOException {
        String scenarioName = "testUpdate";
        JsonNode yamlBody = new ObjectMapper(new YAMLFactory()).readTree(deploymentResource.getResourceConfig().getContent());
        String jsonBody = new ObjectMapper(new JsonFactory()).writeValueAsString(yamlBody);

        // Update deployment
        instanceRule.stubFor(patch(DEPLOYMENT_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .withRequestBody(equalTo(jsonBody))
                .withHeader("Content-Type", equalTo("application/merge-patch+json; charset=utf-8"))
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("scaledDown"));

        deploymentResource.update();

        // Verify calls
        instanceRule.verify(1, patchRequestedFor(DEPLOYMENT_PATTERN));
    }
}
