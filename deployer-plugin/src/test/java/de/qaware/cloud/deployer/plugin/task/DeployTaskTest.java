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

import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.plugin.environment.Environment;
import org.gradle.api.Project;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author sjahreis
 */
public class DeployTaskTest extends BaseAllTaskTestHelper {

    @Override
    public BaseAllEnvironmentsTask createTask(Project project) {
        return project.getTasks().create("deploy", DeployTask.class);
    }

    @Test
    public void testDeploy() throws EnvironmentConfigException, ResourceException, ResourceConfigException {
        DeployTask deployTask = (DeployTask) task;
        Environment environment = environments.get(0);
        deployTask.setEnvironmentId(environment.getId());

        deployTask.deploy();

        verify(deployTask, times(1)).setupEnvironments();
        verify(environment.getDeployer(), times(1)).deploy(environment.getFiles());
        verify(environment.getDeployer(), never()).delete(any());

        for (int i = 1; i < environments.size(); i++) {
            Environment curEnvironment = environments.get(i);
            verify(curEnvironment.getDeployer(), never()).deploy(any());
            verify(curEnvironment.getDeployer(), never()).delete(any());
        }
    }
}
