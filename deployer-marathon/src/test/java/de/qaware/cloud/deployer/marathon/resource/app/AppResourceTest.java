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

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironment;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironmentUtil;
import junit.framework.TestCase;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.utils.MarathonException;

public class AppResourceTest extends TestCase {

    private AppResource appResource;
    private Marathon marathonClient;

    @Override
    public void setUp() throws Exception {
        MarathonTestEnvironment testEnvironment = MarathonTestEnvironmentUtil.createTestEnvironment();
        marathonClient = testEnvironment.getMarathonClient();

        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String appDescription = FileUtil.readFileContent("/resource/app.json");
        MarathonResourceConfig resourceConfig = new MarathonResourceConfig("test", ContentType.JSON, appDescription);
        appResource = new AppResource(resourceConfig, clientFactory);
    }

    @Override
    public void tearDown() throws Exception {
        try {
            marathonClient.deleteApp(appResource.getId());
        } catch (MarathonException e) {
        }
    }

    public void testExists() throws ResourceException, MarathonException {

        // Check that the app doesn't exist already
        assertNotFound();

        // Test exists method
        assertFalse(appResource.exists());

        // Create app
        appResource.create();

        // Check that the app exists
        App app = marathonClient.getApp(appResource.getId()).getApp();
        assertNotNull(app);

        // Test exists method
        assertTrue(appResource.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException, MarathonException {

        // Check that the app doesn't exist already
        assertNotFound();

        // Create app
        appResource.create();

        // Check that the app exists
        App app = marathonClient.getApp(appResource.getId()).getApp();
        assertNotNull(app);

        // Compare app ids
        assertEquals(app.getId(), "/" + appResource.getId());
    }

    public void testDelete() throws ResourceException, MarathonException {

        // Create app
        appResource.create();

        // Check that the app exists
        App app = marathonClient.getApp(appResource.getId()).getApp();
        assertNotNull(app);

        // Delete app
        appResource.delete();

        // Check that app doesn't exist anymore
        assertNotFound();
    }

    private void assertNotFound() {
        boolean exceptionThrown = false;
        try {
            marathonClient.getApp(appResource.getId()).getApp();
        } catch (MarathonException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().contains("404"));
        }
        assertTrue(exceptionThrown);
    }
}