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
package de.qaware.cloud.deployer.kubernetes.resource.ping;

import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BasePingResource;
import de.qaware.cloud.deployer.commons.test.BasePingResourceTest;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class KubernetesPingResourceTest extends BasePingResourceTest {

    private static final UrlPattern PING_PATTERN = urlEqualTo("/api/v1/namespaces");

    @Override
    public BasePingResource createPingResource() throws ResourceException {
        return new KubernetesPingResource(environmentConfig);
    }

    @Test
    public void testFailingPing() throws ResourceException {
        testFailingPing(PING_PATTERN);
    }

    @Test
    public void testPing() throws ResourceException {
        testPing(PING_PATTERN);
    }
}
