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
package de.qaware.cloud.deployer.kubernetes.resource.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.test.BaseKubernetesResourceTest;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class ServiceResourceTest extends BaseKubernetesResourceTest {

    private static final String BASE_PATH = "/api/v1/namespaces/" + NAMESPACE;
    private static final UrlPattern SERVICES_PATTERN = urlEqualTo(BASE_PATH + "/services");
    private static final UrlPattern SERVICE_PATTERN = urlEqualTo(BASE_PATH + "/services/zwitscher-eureka");

    @Override
    public BaseResource createResource() throws ResourceException, ResourceConfigException {
        String serviceDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/kubernetes/resource/service/service.yml");
        KubernetesResourceConfig resourceConfigV1 = new KubernetesResourceConfig("test", ContentType.YAML, serviceDescriptionV1);
        return new ServiceResource(NAMESPACE, resourceConfigV1, clientFactory);
    }

    @Test
    public void testExists() throws ResourceException {
        testExists(SERVICE_PATTERN);
    }

    @Test
    public void testCreate() throws ResourceException {
        testCreate(SERVICES_PATTERN, SERVICE_PATTERN);
    }

    @Test
    public void testDelete() throws ResourceException, JsonProcessingException {
        testDelete(SERVICE_PATTERN);
    }

    @Test
    public void testUpdate() throws ResourceException, IOException {
        testUpdate(SERVICE_PATTERN);
    }
}
