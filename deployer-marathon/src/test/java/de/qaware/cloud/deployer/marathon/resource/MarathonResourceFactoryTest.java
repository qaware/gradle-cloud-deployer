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
package de.qaware.cloud.deployer.marathon.resource;

import de.qaware.cloud.deployer.commons.config.cloud.CloudConfig;
import de.qaware.cloud.deployer.commons.config.cloud.SSLConfig;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.resource.app.AppResource;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import de.qaware.cloud.deployer.marathon.resource.group.GroupResource;
import junit.framework.TestCase;

public class MarathonResourceFactoryTest extends TestCase {

    private CloudConfig cloudConfig;

    @Override
    public void setUp() throws Exception {
        cloudConfig = new CloudConfig("http://test", "", "", "", new SSLConfig(true, ""));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateResourceWithValidApp() throws ResourceException, ResourceConfigException {
        MarathonResourceConfig config = new MarathonResourceConfig("", ContentType.JSON, FileUtil.readFileContent("/resource/app.json"));
        MarathonResourceFactory resourceFactory = new MarathonResourceFactory(cloudConfig);
        MarathonResource resource = resourceFactory.createResource(config);
        assertTrue(resource instanceof AppResource);
    }

    public void testCreateResourceWithValidGroup() throws ResourceException, ResourceConfigException {
        MarathonResourceConfig config = new MarathonResourceConfig("", ContentType.JSON, FileUtil.readFileContent("/resource/group.json"));
        MarathonResourceFactory resourceFactory = new MarathonResourceFactory(cloudConfig);
        MarathonResource resource = resourceFactory.createResource(config);
        assertTrue(resource instanceof GroupResource);
    }

    public void testCreateResourceWithUnknownType() throws ResourceException, ResourceConfigException {
        boolean exceptionThrown = false;
        MarathonResourceConfig config = new MarathonResourceConfig("", ContentType.JSON, FileUtil.readFileContent("/resource/unknown.json"));
        MarathonResourceFactory resourceFactory = new MarathonResourceFactory(cloudConfig);
        try {
            resourceFactory.createResource(config);
        } catch (ResourceException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
