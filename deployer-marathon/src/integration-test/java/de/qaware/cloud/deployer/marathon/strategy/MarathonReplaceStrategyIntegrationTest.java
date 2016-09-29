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
package de.qaware.cloud.deployer.marathon.strategy;

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.Resource;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfigFactory;
import de.qaware.cloud.deployer.marathon.resource.MarathonResourceFactory;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironment;
import de.qaware.cloud.deployer.marathon.test.MarathonTestEnvironmentUtil;
import junit.framework.TestCase;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.Group;
import mesosphere.marathon.client.utils.MarathonException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MarathonReplaceStrategyIntegrationTest extends TestCase {

    private Marathon marathonClient;
    private MarathonStrategy replaceStrategy;
    private List<MarathonResource> resourcesSingle;
    private List<MarathonResource> resourcesMultipleV1;
    private List<MarathonResource> resourcesMultipleV2;

    @Override
    public void setUp() throws Exception {
        // Create test environment
        MarathonTestEnvironment testEnvironment = MarathonTestEnvironmentUtil.createTestEnvironment();
        marathonClient = testEnvironment.getMarathonClient();
        EnvironmentConfig environmentConfig = testEnvironment.getEnvironmentConfig();

        // Create strategy
        replaceStrategy = new MarathonReplaceStrategy();

        // Create config and resource factory
        MarathonResourceConfigFactory resourceConfigFactory = new MarathonResourceConfigFactory();
        MarathonResourceFactory resourceFactory = new MarathonResourceFactory(environmentConfig);

        // Create the resources for the single deployment test
        List<File> filesSingle = new ArrayList<>();
        filesSingle.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-eureka.json")).getPath()));
        filesSingle.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-config.json")).getPath()));
        filesSingle.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-group.json")).getPath()));
        List<MarathonResourceConfig> configsSingle = resourceConfigFactory.createConfigs(filesSingle);
        resourcesSingle = resourceFactory.createResources(configsSingle);

        // Create the resources for the multiple deployment test v1
        List<File> filesMultipleV1 = new ArrayList<>();
        filesMultipleV1.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-v1-eureka.json")).getPath()));
        filesMultipleV1.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-v1-config.json")).getPath()));
        filesMultipleV1.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-v1-group.json")).getPath()));
        List<MarathonResourceConfig> configsMultipleV1 = resourceConfigFactory.createConfigs(filesMultipleV1);
        resourcesMultipleV1 = resourceFactory.createResources(configsMultipleV1);

        // Create the resources for the multiple deployment test v2
        List<File> filesMultipleV2 = new ArrayList<>();
        filesMultipleV2.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-v2-eureka.json")).getPath()));
        filesMultipleV2.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-v2-nginx.json")).getPath()));
        filesMultipleV2.add(new File(this.getClass().getResource(getTestFilePath("replace-strategy-v2-group.json")).getPath()));
        List<MarathonResourceConfig> configsMultipleV2 = resourceConfigFactory.createConfigs(filesMultipleV2);
        resourcesMultipleV2 = resourceFactory.createResources(configsMultipleV2);

        deleteAll();
    }

    @Override
    public void tearDown() throws Exception {
        deleteAll();
    }

    public void testSingleDeployment() throws ResourceException, MarathonException {
        int originalSize = marathonClient.getApps().getApps().size();
        int appsV1 = 5;

        // Deploy v1
        replaceStrategy.deploy(resourcesSingle);

        // Check that everything was deployed correctly
        MarathonResource eurekaAppResource = resourcesSingle.get(0);
        MarathonResource configAppResource = resourcesSingle.get(1);
        MarathonResource groupResource = resourcesSingle.get(2);

        // Check apps
        assertEquals(originalSize + appsV1, marathonClient.getApps().getApps().size());
        App eurekaApp = marathonClient.getApp(eurekaAppResource.getId()).getApp();
        assertEquals(eurekaAppResource.getId(), eurekaApp.getId());
        assertEquals(new Integer(1), eurekaApp.getInstances());
        assertEquals(0.2, eurekaApp.getCpus());
        assertEquals(512.0, eurekaApp.getMem());

        App configApp = marathonClient.getApp(configAppResource.getId()).getApp();
        assertEquals(configAppResource.getId(), configApp.getId());
        assertEquals(new Integer(1), configApp.getInstances());
        assertEquals(0.2, configApp.getCpus());
        assertEquals(512.0, configApp.getMem());

        // Check group
        Group group = marathonClient.getGroup(groupResource.getId());
        assertEquals(groupResource.getId(), group.getId());
        assertEquals(1, group.getApps().size());
        assertEquals(1, group.getGroups().size());
    }

    public void testMultipleDeployments() throws ResourceException, MarathonException {
        int originalSize = marathonClient.getApps().getApps().size();
        int appsV2 = 6;

        // Deploy v1 - already tested above
        replaceStrategy.deploy(resourcesMultipleV1);

        // Deploy v2
        replaceStrategy.deploy(resourcesMultipleV2);

        // Check that everything was deployed correctly
        MarathonResource configAppResource = resourcesMultipleV1.get(1);
        MarathonResource eurekaAppResource = resourcesMultipleV2.get(0);
        MarathonResource nginxAppResource = resourcesMultipleV2.get(1);
        MarathonResource groupResource = resourcesMultipleV2.get(2);

        // Check apps
        assertEquals(originalSize + appsV2, marathonClient.getApps().getApps().size());

        App eurekaApp = marathonClient.getApp(eurekaAppResource.getId()).getApp();
        assertEquals(eurekaAppResource.getId(), eurekaApp.getId());
        assertEquals(new Integer(2), eurekaApp.getInstances());
        assertEquals(0.4, eurekaApp.getCpus());
        assertEquals(256.0, eurekaApp.getMem());

        App nginxApp = marathonClient.getApp(nginxAppResource.getId()).getApp();
        assertEquals(nginxApp.getId(), nginxApp.getId());
        assertEquals(new Integer(1), nginxApp.getInstances());
        assertEquals(0.5, nginxApp.getCpus());
        assertEquals(100.0, nginxApp.getMem());

        App configApp = marathonClient.getApp(configAppResource.getId()).getApp();
        assertEquals(configAppResource.getId(), configApp.getId());
        assertEquals(new Integer(1), configApp.getInstances());
        assertEquals(0.2, configApp.getCpus());
        assertEquals(512.0, configApp.getMem());

        // Check group
        Group group = marathonClient.getGroup(groupResource.getId());
        assertEquals(groupResource.getId(), group.getId());
        assertEquals(3, group.getApps().size());
    }

    private void deleteAll() {
        List<Resource> allResources = new ArrayList<>();
        allResources.addAll(resourcesSingle);
        allResources.addAll(resourcesMultipleV1);
        allResources.addAll(resourcesMultipleV2);
        for (Resource resource : allResources) {
            try {
                resource.delete();
            } catch (ResourceException e) {
            }
        }
    }

    private String getTestFilePath(String fileName) {
        return "/de/qaware/cloud/deployer/marathon/strategy/" + fileName;
    }
}
