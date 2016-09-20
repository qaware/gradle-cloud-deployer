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
package de.qaware.cloud.deployer.marathon.resource.group;

import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.config.util.FileUtil;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironment;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironmentUtil;
import junit.framework.TestCase;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.model.v2.Group;
import mesosphere.marathon.client.utils.MarathonException;

import java.util.concurrent.atomic.AtomicInteger;

public class GroupResourceTest extends TestCase {

    private static AtomicInteger testCounter = new AtomicInteger(0);

    private GroupResource groupResource;
    private Marathon marathonClient;

    @Override
    public void setUp() throws Exception {
        MarathonTestEnvironment testEnvironment = MarathonTestEnvironmentUtil.createTestEnvironment();
        marathonClient = testEnvironment.getMarathonClient();

        ClientFactory clientFactory = testEnvironment.getClientFactory();
        String groupDescription = FileUtil.readFileContent("/resource/group/group.json");
        groupDescription = groupDescription.replace("group-test", "group-test-" + testCounter.getAndIncrement());

        MarathonResourceConfig resourceConfig = new MarathonResourceConfig("test", ContentType.JSON, groupDescription);
        groupResource = new GroupResource(resourceConfig, clientFactory);

        removeGroup();
    }

    @Override
    public void tearDown() throws Exception {
        removeGroup();
    }

    public void testExists() throws ResourceException, MarathonException {

        // Check that the group doesn't exist already
        assertNotFound();

        // Test exists method
        assertFalse(groupResource.exists());

        // Create group
        groupResource.create();

        // Check that the group exists
        assertExists();

        // Test exists method
        assertTrue(groupResource.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException, MarathonException {

        // Check that the group doesn't exist already
        assertNotFound();

        // Create group
        groupResource.create();

        // Check that the group exists
        Group group = assertExists();

        // Compare group ids
        assertEquals(group.getId(), "/" + groupResource.getId());
    }

    public void testDelete() throws ResourceException, MarathonException, InterruptedException {

        // Create group
        groupResource.create();

        // Check that the group exists
        assertExists();

        // Delete group
        groupResource.delete();

        // Check that group doesn't exist anymore
        assertNotFound();
    }

    private Group assertExists() throws MarathonException {
        Group group = marathonClient.getGroup(groupResource.getId());
        assertNotNull(group);
        return group;
    }

    private void assertNotFound() {
        boolean exceptionThrown = false;
        try {
            marathonClient.getGroup(groupResource.getId());
        } catch (MarathonException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().contains("404"));
        }
        assertTrue(exceptionThrown);
    }

    private void removeGroup() throws ResourceException {
        if (groupResource.exists()) {
            groupResource.delete();
        }
    }
}
