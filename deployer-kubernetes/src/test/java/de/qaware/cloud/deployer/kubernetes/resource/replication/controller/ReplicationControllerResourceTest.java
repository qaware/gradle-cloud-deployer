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
package de.qaware.cloud.deployer.kubernetes.resource.replication.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.api.delete.options.DeleteOptions;
import de.qaware.cloud.deployer.kubernetes.test.BaseKubernetesResourceTest;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

public class ReplicationControllerResourceTest extends BaseKubernetesResourceTest {

    private static final String BASE_PATH = "/api/v1/namespaces/" + NAMESPACE;
    private static final UrlPattern REPLICATION_CONTROLLERS_PATTERN = urlEqualTo(BASE_PATH + "/replicationcontrollers");
    private static final UrlPattern REPLICATION_CONTROLLER_PATTERN = urlEqualTo(BASE_PATH + "/replicationcontrollers/nginx");
    private static final UrlPattern SCALE_PATTERN = urlEqualTo(BASE_PATH + "/replicationcontrollers/nginx/scale");

    @Override
    public BaseResource createResource() throws ResourceException, ResourceConfigException {
        String controllerDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/kubernetes/resource/replication/controller/replication-controller.yml");
        KubernetesResourceConfig resourceConfigV1 = new KubernetesResourceConfig("test", ContentType.YAML, controllerDescriptionV1);
        return new ReplicationControllerResource(NAMESPACE, resourceConfigV1, clientFactory);
    }

    @Test
    public void testExists() throws ResourceException, InterruptedException {
        testExists(REPLICATION_CONTROLLER_PATTERN);
    }

    @Test
    public void testCreate() throws ResourceException, InterruptedException {
        testCreate(REPLICATION_CONTROLLERS_PATTERN, REPLICATION_CONTROLLER_PATTERN);
    }

    @Test
    public void testDelete() throws ResourceException, InterruptedException, TimeoutException, JsonProcessingException {
        String scenarioName = "testDelete";

        // Scale pods down
        instanceRule.stubFor(put(SCALE_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("scaledDown"));

        // Delete replication controller
        instanceRule.stubFor(delete(REPLICATION_CONTROLLER_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("scaledDown")
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("replicationControllerHalfDeleted"));

        // Simulate deleting
        instanceRule.stubFor(get(REPLICATION_CONTROLLER_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("replicationControllerHalfDeleted")
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("replicationControllerDeleted"));

        // Deleted replicationController
        instanceRule.stubFor(get(REPLICATION_CONTROLLER_PATTERN)
                .inScenario(scenarioName)
                .whenScenarioStateIs("replicationControllerDeleted")
                .willReturn(aResponse().withStatus(404)));

        // Test
        resource.delete();

        // Verify calls
        instanceRule.verify(1, putRequestedFor(SCALE_PATTERN));
        instanceRule.verify(1, deleteRequestedFor(REPLICATION_CONTROLLER_PATTERN));
        instanceRule.verify(2, getRequestedFor(REPLICATION_CONTROLLER_PATTERN));

        // Check if delete options are specified
        String jsonDeleteOptions = new ObjectMapper(new JsonFactory()).writeValueAsString(new DeleteOptions(0));
        instanceRule.verify(deleteRequestedFor(REPLICATION_CONTROLLER_PATTERN).withRequestBody(equalTo(jsonDeleteOptions)));
    }

    @Test
    public void testUpdate() {
        testMissingUpdate();
    }
}
