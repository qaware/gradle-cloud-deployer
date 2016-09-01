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
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.resource.ContentTreeUtil;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;

import java.util.Objects;

public class DeploymentLabelUtil {

    private DeploymentLabelUtil() {
    }

    public static void addLabel(ResourceConfig resourceConfig, String label, String value) throws ResourceException {
        if (Objects.equals(resourceConfig.getResourceVersion(), "extensions/v1beta1") && Objects.equals(resourceConfig.getResourceType(), "Deployment")) {
            try {
                JsonNode objectTree = ContentTreeUtil.createObjectTree(resourceConfig.getContentType(), resourceConfig.getContent());
                JsonNode specNode = ContentTreeUtil.readNodeValue(objectTree, "spec");
                JsonNode templateNode = ContentTreeUtil.readNodeValue(specNode, "template");
                JsonNode metadataNode = ContentTreeUtil.readNodeValue(templateNode, "metadata");
                JsonNode labelsNode = ContentTreeUtil.readNodeValue(metadataNode, "labels");
                ContentTreeUtil.addField(labelsNode, label, value);
                resourceConfig.setContent(objectTree.toString());
            } catch (ResourceConfigException e) {
                throw new ResourceException("Can't add label to deployment - the path spec/template/metadata/labels doesn't exist in your deployment config (Config: " + resourceConfig.getResourceId() + ")", e);
            }
        } else {
            throw new ResourceException("Can't add label to a " + resourceConfig.getResourceType());
        }
    }
}
