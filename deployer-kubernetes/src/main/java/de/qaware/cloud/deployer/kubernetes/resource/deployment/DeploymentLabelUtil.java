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
import de.qaware.cloud.deployer.commons.config.util.ContentTreeUtil;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;

import java.util.Objects;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * A utility which adds a label to the specified deployment.
 */
final class DeploymentLabelUtil {

    /**
     * UTILITY.
     */
    private DeploymentLabelUtil() {
    }

    /**
     * Adds a label to the specified deployment resource.
     *
     * @param resourceConfig The config of the deployment.
     * @param label          The label name.
     * @param value          The label value.
     * @throws ResourceException If the deployment config doesn't contain the path /spec/template/metadata/labels or if
     *                           the specified config doesn't belong to a deployment.
     */
    static void addLabel(KubernetesResourceConfig resourceConfig, String label, String value) throws ResourceException {
        if (Objects.equals(resourceConfig.getResourceVersion(), "extensions/v1beta1") && Objects.equals(resourceConfig.getResourceType(), "Deployment")) {
            try {
                ContentType contentType = resourceConfig.getContentType();
                JsonNode objectTree = ContentTreeUtil.createObjectTree(contentType, resourceConfig.getContent());
                JsonNode specNode = ContentTreeUtil.readNodeValue(objectTree, "spec");
                JsonNode templateNode = ContentTreeUtil.readNodeValue(specNode, "template");
                JsonNode metadataNode = ContentTreeUtil.readNodeValue(templateNode, "metadata");
                JsonNode labelsNode = ContentTreeUtil.readNodeValue(metadataNode, "labels");
                ContentTreeUtil.addField(labelsNode, label, value);
                resourceConfig.setContent(ContentTreeUtil.writeAsString(contentType, objectTree));
            } catch (ResourceConfigException e) {
                throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_DURING_LABEL_MARKING_INVALID_PATH", resourceConfig.getFilename()), e);
            }
        } else {
            throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_DURING_LABEL_MARKING_INVALID_CONFIG", resourceConfig.getFilename()));
        }
    }
}
