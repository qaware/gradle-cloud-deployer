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
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.Group;
import mesosphere.marathon.client.utils.MarathonException;

import java.util.concurrent.atomic.AtomicInteger;

public class GroupResourceIntegrationTest extends TestCase {

    private static AtomicInteger testCounter = new AtomicInteger(0);

    private GroupResource groupResourceV1;
    private GroupResource groupResourceV2;
    private Marathon marathonClient;

    @Override
    public void setUp() throws Exception {
        MarathonTestEnvironment testEnvironment = MarathonTestEnvironmentUtil.createTestEnvironment();
        marathonClient = testEnvironment.getMarathonClient();

        ClientFactory clientFactory = testEnvironment.getClientFactory();
        int idSuffix = testCounter.getAndIncrement();
        String groupDescriptionV1 = FileUtil.readFileContent("/de/qaware/cloud/deployer/marathon/resource/group/group-v1.json");
        groupDescriptionV1 = groupDescriptionV1.replace("group-test", "group-test-" + idSuffix);
        String groupDescriptionV2 = FileUtil.readFileContent("/de/qaware/cloud/deployer/marathon/resource/group/group-v2.json");
        groupDescriptionV2 = groupDescriptionV2.replace("group-test", "group-test-" + idSuffix);

        MarathonResourceConfig resourceConfigV1 = new MarathonResourceConfig("test", ContentType.JSON, groupDescriptionV1);
        groupResourceV1 = new GroupResource(resourceConfigV1, clientFactory);

        MarathonResourceConfig resourceConfigV2 = new MarathonResourceConfig("test", ContentType.JSON, groupDescriptionV2);
        groupResourceV2 = new GroupResource(resourceConfigV2, clientFactory);

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
        assertFalse(groupResourceV1.exists());

        // Create group
        groupResourceV1.create();

        // Check that the group exists
        assertExists();

        // Test exists method
        assertTrue(groupResourceV1.exists());
    }

    public void testCreate() throws ResourceException, InterruptedException, MarathonException {

        // Check that the group doesn't exist already
        assertNotFound();

        // Create group
        groupResourceV1.create();

        // Check that the group exists
        Group group = assertExists();

        // Compare group ids
        assertEquals(group.getId(), "/" + groupResourceV1.getId());
    }

    public void testDelete() throws ResourceException, MarathonException, InterruptedException {

        // Create group
        groupResourceV1.create();

        // Check that the group exists
        assertExists();

        // Delete group
        groupResourceV1.delete();

        // Check that group doesn't exist anymore
        assertNotFound();
    }

    public void testUpdate() throws MarathonException, ResourceException {

        // Create group
        groupResourceV1.create();

        // Check that the group exists
        assertExists();

        // Update the group
        groupResourceV2.update();

        // Check that the group was updated correctly
        Group group = marathonClient.getGroup(groupResourceV1.getId());

        // Check app
        assertEquals(1, group.getApps().size());
        App app = group.getApps().toArray(new App[1])[0];
        assertEquals(new Integer(1), app.getInstances());
        assertEquals(1, group.getGroups().size());

        // Check subgroup
        Group subGroup = group.getGroups().toArray(new Group[1])[0];
        assertEquals(2, subGroup.getApps().size());
        App[] subGroupApps = subGroup.getApps().toArray(new App[2]);
        App subGroupApp1 = subGroupApps[0];
        assertEquals(new Integer(1), subGroupApp1.getInstances());
        App subGroupApp2 = subGroupApps[0];
        assertEquals(new Integer(1), subGroupApp2.getInstances());
    }

    private Group assertExists() throws MarathonException {
        Group group = marathonClient.getGroup(groupResourceV1.getId());
        assertNotNull(group);
        return group;
    }

    private void assertNotFound() {
        boolean exceptionThrown = false;
        try {
            marathonClient.getGroup(groupResourceV1.getId());
        } catch (MarathonException e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().contains("404"));
        }
        assertTrue(exceptionThrown);
    }

    private void removeGroup() throws ResourceException {
        if (groupResourceV1.exists()) {
            groupResourceV1.delete();
        }
    }
}
