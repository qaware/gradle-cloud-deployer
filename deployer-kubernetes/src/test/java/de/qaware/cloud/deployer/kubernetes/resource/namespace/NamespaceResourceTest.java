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
package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.kubernetes.config.namespace.NamespaceResourceConfigFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.test.BaseKubernetesResourceTest;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class NamespaceResourceTest extends BaseKubernetesResourceTest {

    private static final UrlPattern NAMESPACES_PATTERN = urlEqualTo("/api/v1/namespaces");
    private static final UrlPattern NAMESPACE_PATTERN = urlEqualTo("/api/v1/namespaces/" + NAMESPACE);

    @Override
    public BaseResource createResource() throws ResourceException, ResourceConfigException {
        KubernetesResourceConfig resourceConfig = NamespaceResourceConfigFactory.create(NAMESPACE);
        return new NamespaceResource(resourceConfig, clientFactory);
    }

    @Test
    public void testExists() throws ResourceException {
        testExists(NAMESPACE_PATTERN);
    }

    @Test
    public void testDelete() throws ResourceException, JsonProcessingException {
        testDelete(NAMESPACE_PATTERN);
    }

    @Test
    public void testCreate() throws ResourceException {
        testCreate(NAMESPACES_PATTERN, NAMESPACE_PATTERN);
    }

    @Test
    public void testUpdate() {
        testMissingUpdate();
    }
}
