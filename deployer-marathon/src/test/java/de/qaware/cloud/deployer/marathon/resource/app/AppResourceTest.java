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
package de.qaware.cloud.deployer.marathon.resource.app;

import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BaseResource;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.test.BaseMarathonResourceTest;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class AppResourceTest extends BaseMarathonResourceTest {

    private static final String APPS_PATH = "/service/marathon/v2/apps";
    private static final UrlPattern APPS_PATTERN = urlEqualTo(APPS_PATH);
    private static final UrlPattern APP_PATTERN = urlEqualTo(APPS_PATH + "/zwitscher-eureka");

    @Override
    public BaseResource createResource() throws ResourceException, ResourceConfigException {
        String appDescription = FileUtil.readFileContent("/de/qaware/cloud/deployer/marathon/resource/app/app.json");
        MarathonResourceConfig resourceConfig = new MarathonResourceConfig("test", ContentType.JSON, appDescription);
        return new AppResource(resourceConfig, clientFactory);
    }

    @Test
    public void testExists() throws ResourceException {
        testExists(APP_PATTERN);
    }

    @Test
    public void testCreate() throws ResourceException {
        testCreate(APPS_PATTERN, APP_PATTERN);
    }

    @Test
    public void testCreateRetry() throws ResourceException {
        testCreateRetry(APPS_PATTERN, APP_PATTERN);
    }

    @Test
    public void testDelete() throws ResourceException {
        testDelete(APP_PATTERN);
    }

    @Test
    public void testUpdate() throws ResourceException, IOException {
        testUpdate(APP_PATTERN);
    }
}
