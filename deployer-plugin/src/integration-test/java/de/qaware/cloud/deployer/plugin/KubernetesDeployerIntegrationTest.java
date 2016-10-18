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

import de.qaware.cloud.deployer.commons.test.TestEnvironmentUtil;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;

/**
 * @author sjahreis
 */
public class KubernetesDeployerIntegrationTest {

    private static final String TEST_CONFIG_DIR = "kubernetes";
    private static final String ENVIRONMENT_ONE = "kubernetes-zwitscher-one";
    private static final String ENVIRONMENT_TWO = "kubernetes-zwitscher-two";
    private static final String APP_ONE = "zwitscher-eureka";
    private static final String APP_TWO = "zwitscher-config";

    // Environment variables.
    private static final String KUBERNETES_NAMESPACE_PREFIX_ENV = "KUBERNETES_NAMESPACE_PREFIX";
    private static final String KUBERNETES_URL_ENV = "KUBERNETES_URL";
    private static final String KUBERNETES_USERNAME_ENV = "KUBERNETES_USERNAME";
    private static final String KUBERNETES_PASSWORD_ENV = "KUBERNETES_PASSWORD";

    private KubernetesClient kubernetesClient;
    private String namespaceEnvironmentOne;
    private String namespaceEnvironmentTwo;

    @Before
    public void setup() throws IOException {
        executeTask(TEST_CONFIG_DIR, "deleteAll");

        Map<String, String> environmentVariables = TestEnvironmentUtil.loadEnvironmentVariables(
                KUBERNETES_URL_ENV,
                KUBERNETES_USERNAME_ENV,
                KUBERNETES_PASSWORD_ENV,
                KUBERNETES_NAMESPACE_PREFIX_ENV
        );

        namespaceEnvironmentOne = environmentVariables.get(KUBERNETES_NAMESPACE_PREFIX_ENV) + "-" + ENVIRONMENT_ONE;
        namespaceEnvironmentTwo = environmentVariables.get(KUBERNETES_NAMESPACE_PREFIX_ENV) + "-" + ENVIRONMENT_TWO;

        Config config = new ConfigBuilder()
                .withMasterUrl(environmentVariables.get(KUBERNETES_URL_ENV))
                .withTrustCerts(true)
                .withUsername(environmentVariables.get(KUBERNETES_USERNAME_ENV))
                .withPassword(environmentVariables.get(KUBERNETES_PASSWORD_ENV))
                .build();
        kubernetesClient = new DefaultKubernetesClient(config);
    }

    @After
    public void tearDown() {
        executeTask(TEST_CONFIG_DIR, "deleteAll");
    }

    @Test
    public void testDeployAll() {
        String task = "deployAll";
        BuildResult result = executeTask(TEST_CONFIG_DIR, task);

        assertEquals(result.task(":" + task).getOutcome(), SUCCESS);
        checkEnvironment(namespaceEnvironmentOne, APP_ONE);
        checkEnvironment(namespaceEnvironmentTwo, APP_TWO);
    }

    @Test
    public void testDeploy() {
        String task = "deploy";
        String params = "--environmentId=" + ENVIRONMENT_ONE;
        BuildResult result = executeTask(TEST_CONFIG_DIR, task, params);

        assertEquals(result.task(":" + task).getOutcome(), SUCCESS);
        checkEnvironment(namespaceEnvironmentOne, APP_ONE);
        checkEmptyEnvironment(namespaceEnvironmentTwo);
    }

    @Test
    public void testDeleteAll() {
        String deployTask = "deployAll";
        executeTask(TEST_CONFIG_DIR, deployTask);

        String deleteTask = "deleteAll";
        BuildResult result = executeTask(TEST_CONFIG_DIR, deleteTask);
        assertEquals(result.task(":" + deleteTask).getOutcome(), SUCCESS);
        checkEmptyEnvironment(namespaceEnvironmentOne);
        checkEmptyEnvironment(namespaceEnvironmentTwo);
    }

    @Test
    public void testDelete() {
        String deployTask = "deployAll";
        executeTask(TEST_CONFIG_DIR, deployTask);

        String deleteTask = "delete";
        String params = "--environmentId=" + ENVIRONMENT_ONE;
        BuildResult result = executeTask(TEST_CONFIG_DIR, deleteTask, params);
        assertEquals(result.task(":" + deleteTask).getOutcome(), SUCCESS);
        checkEmptyEnvironment(namespaceEnvironmentOne);
        checkEnvironment(namespaceEnvironmentTwo, APP_TWO);
    }

    private void checkEnvironment(String namespace, String app) {
        List<Deployment> deployments = kubernetesClient.extensions().deployments().inNamespace(namespace).list().getItems();
        assertEquals(1, deployments.size());
        Deployment deployment = deployments.get(0);
        assertEquals(app, deployment.getMetadata().getName());
    }

    private void checkEmptyEnvironment(String namespace) {
        List<Deployment> deployments = kubernetesClient.extensions().deployments().inNamespace(namespace).list().getItems();
        assertEquals(0, deployments.size());
    }

    private BuildResult executeTask(String buildDir, String... arguments) {
        File projectDir = new File(this.getClass().getResource(buildDir).getPath());
        return GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .withArguments(arguments)
                .build();
    }
}
