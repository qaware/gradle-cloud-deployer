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
import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.ResourceTestUtil;
import de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentLabelUtil;
import junit.framework.TestCase;

import java.io.IOException;

public class DeploymentLabelUtilTest extends TestCase {

    private ResourceConfig resourceConfig;

    @Override
    public void setUp() throws Exception {
        String deploymentDescription = ResourceTestUtil.readFile("/deployment.yml");
        resourceConfig = new ResourceConfig(ContentType.YAML, deploymentDescription);
    }

    public void testAddLabel() throws IOException, ResourceException, ResourceConfigException {
        // Add new label
        DeploymentLabelUtil.addLabel(resourceConfig, "test", "test");

        // Check result
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode nodeTree = mapper.readTree(resourceConfig.getContent());
        JsonNode specNode = nodeTree.get("spec");
        JsonNode templateNode = specNode.get("template");
        JsonNode metadataNode = templateNode.get("metadata");
        JsonNode labelsNode = metadataNode.get("labels");
        JsonNode testNode = labelsNode.get("test");
        String value = testNode.textValue();
        assertEquals("test", value);
    }
}
