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
package de.qaware.cloud.deployer.plugin;

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import de.qaware.cloud.deployer.commons.test.TestEnvironmentUtil;
import de.qaware.cloud.deployer.dcos.token.TokenResource;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.utils.MarathonException;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.*;

/**
 * @author sjahreis
 */
public class MarathonDeployerIntegrationTest {

    private static final String TEST_CONFIG_DIR = "marathon";
    private static final String APP_ONE = "/temp/zwitscher-eureka";
    private static final String APP_TWO = "/temp/zwitscher-config";
    private static final String ENVIRONMENT_ONE = "marathon-zwitscher-one";
    private static final String ENVIRONMENT_TWO = "marathon-zwitscher-two";

    // Environment variables.
    private static final String MARATHON_URL_ENV = "MARATHON_URL";
    private static final String MARATHON_TOKEN_ENV = "MARATHON_TOKEN";

    private Marathon marathonClient;

    @Before
    public void setup() throws IOException, ResourceException, EnvironmentConfigException {
        executeTask(TEST_CONFIG_DIR, "deleteAll");

        Map<String, String> environmentVariables = TestEnvironmentUtil.loadEnvironmentVariables(
                MARATHON_TOKEN_ENV,
                MARATHON_URL_ENV
        );

        TokenResource tokenResource = new TokenResource(new EnvironmentConfig("test", environmentVariables.get(MARATHON_URL_ENV), Strategy.REPLACE));
        String apiToken = tokenResource.retrieveAuthenticationToken(environmentVariables.get(MARATHON_TOKEN_ENV));
        marathonClient = AuthorizedMarathonClient.createInstance(environmentVariables.get(MARATHON_URL_ENV) + "/service/marathon", apiToken);
    }

    @After
    public void tearDown() {
        executeTask(TEST_CONFIG_DIR, "deleteAll");
    }

    @Test
    public void testDeployAll() throws MarathonException {
        String task = "deployAll";
        BuildResult result = executeTask(TEST_CONFIG_DIR, task);

        assertEquals(result.task(":" + task).getOutcome(), SUCCESS);
        checkEnvironment(APP_ONE);
        checkEnvironment(APP_TWO);
    }

    @Test
    public void testDeploy() throws MarathonException {
        String task = "deploy";
        String params = "--environmentId=" + ENVIRONMENT_ONE;
        BuildResult result = executeTask(TEST_CONFIG_DIR, task, params);

        assertEquals(result.task(":" + task).getOutcome(), SUCCESS);
        checkEnvironment(APP_ONE);
        checkEmptyEnvironment(APP_TWO);
    }

    @Test
    public void testDeleteAll() {
        String deployTask = "deployAll";
        executeTask(TEST_CONFIG_DIR, deployTask);

        String deleteTask = "deleteAll";
        BuildResult result = executeTask(TEST_CONFIG_DIR, deleteTask);
        assertEquals(result.task(":" + deleteTask).getOutcome(), SUCCESS);
        checkEmptyEnvironment(APP_ONE);
        checkEmptyEnvironment(APP_TWO);
    }

    @Test
    public void testDelete() throws MarathonException {
        String deployTask = "deployAll";
        executeTask(TEST_CONFIG_DIR, deployTask);

        String deleteTask = "delete";
        String params = "--environmentId=" + ENVIRONMENT_ONE;
        BuildResult result = executeTask(TEST_CONFIG_DIR, deleteTask, params);
        assertEquals(result.task(":" + deleteTask).getOutcome(), SUCCESS);
        checkEmptyEnvironment(APP_ONE);
        checkEnvironment(APP_TWO);
    }

    private void checkEnvironment(String appName) throws MarathonException {
        App app = marathonClient.getApp(appName).getApp();
        assertNotNull(app);
        assertEquals(appName, app.getId());
    }

    private void checkEmptyEnvironment(String appName) {
        boolean exceptionThrown = false;
        try {
            marathonClient.getApp(appName).getApp();
        } catch (MarathonException e) {
            assertEquals("Not Found (http status: 404)", e.getMessage());
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    private BuildResult executeTask(String buildDir, String... arguments) {
        File projectDir = new File(this.getClass().getResource(buildDir).getPath());
        return GradleRunner.create()
                .withDebug(true)
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .withArguments(arguments)
                .build();
    }
}
