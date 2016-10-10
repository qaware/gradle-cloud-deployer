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
package de.qaware.cloud.deployer.plugin.task;

import de.qaware.cloud.deployer.commons.Deployer;
import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.plugin.environment.Environment;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author sjahreis
 */
abstract class BaseAllTaskTestHelper {

    BaseAllEnvironmentsTask task;
    List<Environment> environments;

    public abstract BaseAllEnvironmentsTask createTask(Project project);

    @Before
    public void setup() throws EnvironmentConfigException {
        Project project = ProjectBuilder.builder().build();
        task = createTask(project);

        Environment environment0 = createEnvironment(0);
        Environment environment1 = createEnvironment(1);
        environments = new ArrayList<>();
        environments.add(environment0);
        environments.add(environment1);

        assertNotNull(task);
        task = spy(task);
        when(task.getEnvironments()).thenReturn(environments);
        doNothing().when(task).setupEnvironments();
    }

    private Environment createEnvironment(int idSuffix) {
        File file0 = mock(File.class);
        File file1 = mock(File.class);
        List<File> files = new ArrayList<>();
        files.add(file0);
        files.add(file1);
        Deployer deployer = mock(Deployer.class);
        Environment environment = mock(Environment.class);
        when(environment.getId()).thenReturn("environment" + idSuffix);
        when(environment.getFiles()).thenReturn(files);
        when(environment.getDeployer()).thenReturn(deployer);
        return environment;
    }
}
