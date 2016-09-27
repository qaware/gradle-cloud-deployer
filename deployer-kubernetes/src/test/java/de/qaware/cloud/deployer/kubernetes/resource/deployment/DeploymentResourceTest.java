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

import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.BaseKubernetesResourceTest;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

public class DeploymentResourceTest extends BaseKubernetesResourceTest {

    private static final String BASE_PATH = "/apis/extensions/v1beta1/namespaces/" + NAMESPACE;
    private static final UrlPattern DEPLOYMENTS_PATTERN = urlEqualTo(BASE_PATH + "/deployments");
    private static final UrlPattern DEPLOYMENT_PATTERN = urlEqualTo(BASE_PATH + "/deployments/zwitscher-eureka");
    private static final UrlPattern SCALE_PATTERN = urlEqualTo(BASE_PATH + "/deployments/zwitscher-eureka/scale");
    private static final UrlPattern REPLICA_SETS_PATTERN = urlEqualTo(BASE_PATH + "/replicasets?labelSelector=deployment-id%3Dzwitscher-eureka");

    @Override
    public BaseResource createResource() throws ResourceException, ResourceConfigException {
        String deploymentDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/kubernetes/resource/deployment/deployment.yml");
        KubernetesResourceConfig resourceConfigV1 = new KubernetesResourceConfig("test", ContentType.YAML, deploymentDescriptionV1);
        return new DeploymentResource(NAMESPACE, resourceConfigV1, clientFactory);
    }

    @Test
    public void testExists() throws ResourceException {
        testExists(DEPLOYMENT_PATTERN);
    }

    @Test
    public void testCreate() throws ResourceException {
        testCreate(DEPLOYMENTS_PATTERN, DEPLOYMENT_PATTERN);
    }

    @Test
    public void testCreateRetry() throws ResourceException {
        testCreateRetry(DEPLOYMENTS_PATTERN, DEPLOYMENT_PATTERN);
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

        resource.delete();

        // Verify calls
        instanceRule.verify(1, putRequestedFor(SCALE_PATTERN));
        instanceRule.verify(1, deleteRequestedFor(DEPLOYMENT_PATTERN));
        instanceRule.verify(1, deleteRequestedFor(REPLICA_SETS_PATTERN));
        instanceRule.verify(3, getRequestedFor(DEPLOYMENT_PATTERN));
    }

    @Test
    public void testUpdate() throws ResourceException, TimeoutException, InterruptedException, IOException {
        testUpdate(DEPLOYMENT_PATTERN);
    }
}
