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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import junit.framework.TestCase;

import java.io.IOException;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

public class DeploymentLabelUtilTest extends TestCase {

    private KubernetesResourceConfig resourceConfig;

    @Override
    public void setUp() throws Exception {
        String deploymentFileName = getTestFilePath("deployment.yml");
        String deploymentDescription = FileUtil.readFileContent(deploymentFileName);
        resourceConfig = new KubernetesResourceConfig("test", ContentType.YAML, deploymentDescription);
    }

    public void testAddLabel() throws IOException, ResourceException, ResourceConfigException {
        // Add new label
        DeploymentLabelUtil.addLabel(resourceConfig, "test", "test");

        // Read marked deployment
        String markedDeployment = FileUtil.readFileContent(getTestFilePath("deployment-marked.yml"));

        // Check result
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode contentTree = mapper.readTree(resourceConfig.getContent());
        JsonNode expectedContentTree = mapper.readTree(markedDeployment);
        assertEquals(expectedContentTree, contentTree);
    }

    public void testAddLabelWithAPod() throws ResourceConfigException {
        boolean exceptionThrown = false;
        KubernetesResourceConfig podConfig =
                new KubernetesResourceConfig("test", ContentType.YAML, FileUtil.readFileContent(getTestFilePath("pod.json")));
        try {
            DeploymentLabelUtil.addLabel(podConfig, "test", "test");
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_DURING_LABEL_MARKING_INVALID_CONFIG", "test"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    public void testAddLabelWithMissingPath() throws ResourceConfigException {
        boolean exceptionThrown = false;
        KubernetesResourceConfig podConfig =
                new KubernetesResourceConfig("test", ContentType.YAML, FileUtil.readFileContent(getTestFilePath("deployment-missing-labels.yml")));
        try {
            DeploymentLabelUtil.addLabel(podConfig, "test", "test");
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_DURING_LABEL_MARKING_INVALID_PATH", "test"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private String getTestFilePath(String filename) {
        return "/de/qaware/cloud/deployer/kubernetes/resource/deployment/" + filename;
    }
}
