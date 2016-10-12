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

import java.util.concurrent.atomic.AtomicInteger;

public class AppResourceIntegrationTest extends TestCase {

    private static AtomicInteger testCounter = new AtomicInteger(0);

    private AppResource appResourceV1;
    private AppResource appResourceV2;
    private Marathon marathonClient;

    @Override
    public void setUp() throws Exception {
        MarathonTestEnvironment testEnvironment = MarathonTestEnvironmentUtil.createTestEnvironment();
        marathonClient = testEnvironment.getMarathonClient();

        ClientFactory clientFactory = testEnvironment.getClientFactory();
        int idSuffix = testCounter.getAndIncrement();
        String appDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/marathon/resource/app/app-v1.json");
        appDescriptionV1 = appDescriptionV1.replace("zwitscher-eureka", "zwitscher-eureka-app-" + idSuffix);
        String appDescriptionV2 = FileUtil.readFileContent("/de/qaware/cloud/deployer/marathon/resource/app/app-v2.json");
        appDescriptionV2 = appDescriptionV2.replace("zwitscher-eureka", "zwitscher-eureka-app-" + idSuffix);

        MarathonResourceConfig resourceConfigV1 = new MarathonResourceConfig("test", ContentType.JSON, appDescriptionV1);
        appResourceV1 = new AppResource(resourceConfigV1, clientFactory);

        MarathonResourceConfig resourceConfigV2 = new MarathonResourceConfig("test", ContentType.JSON, appDescriptionV2);
        appResourceV2 = new AppResource(resourceConfigV2, clientFactory);

        removeApp();
    }

    @Override
    public void tearDown() throws Exception {
        removeApp();
    }

    public void testExists() throws ResourceException, MarathonException {

        // Check that the app doesn't exist already
        assertNotFound();

        // Test exists method
        assertFalse(appResourceV1.exists());

        // Create app
        appResourceV1.create();

        // Check that the app exists
        assertExists();

        // Test exists method
        assertTrue(appResourceV1.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException, MarathonException {

        // Check that the app doesn't exist already
        assertNotFound();

        // Create app
        appResourceV1.create();

        // Check that the app exists
        App app = assertExists();

        // Compare app ids
        assertEquals(app.getId(), "/" + appResourceV1.getId());
    }

    public void testDelete() throws ResourceException, MarathonException {

        // Create app
        appResourceV1.create();

        // Check that the app exists
        assertExists();

        // Delete app
        appResourceV1.delete();

        // Check that app doesn't exist anymore
        assertNotFound();
    }

    public void testUpdate() throws ResourceException, MarathonException {

        // Create the app - already tested above
        appResourceV1.create();

        // Check that the app exists
        assertExists();

        // Update the app
        appResourceV2.update();

        // Retrieve the updated app and check if everything was updated correctly
        App appV2 = marathonClient.getApp(appResourceV1.getId()).getApp();
        assertEquals(new Integer(2), appV2.getInstances());
    }

    private App assertExists() throws MarathonException {
        App app = marathonClient.getApp(appResourceV1.getId()).getApp();
        assertNotNull(app);
        return app;
    }

    private void assertNotFound() {
        boolean exceptionThrown = false;
        try {
            marathonClient.getApp(appResourceV1.getId()).getApp();
        } catch (MarathonException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().contains("404"));
        }
        assertTrue(exceptionThrown);
    }

    private void removeApp() {
        try {
            marathonClient.deleteApp(appResourceV1.getId());
        } catch (MarathonException e) {
        }
    }
}
