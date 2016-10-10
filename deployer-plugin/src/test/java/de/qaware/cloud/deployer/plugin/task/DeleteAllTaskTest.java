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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author sjahreis
 */
public class DeleteAllTaskTest extends BaseAllTaskTestHelper {

    @Override
    public BaseAllEnvironmentsTask createTask(Project project) {
        return project.getTasks().create("deployAll", DeleteAllTask.class);
    }

    @Test
    public void testDeleteAll() throws ResourceException, ResourceConfigException, EnvironmentConfigException {
        DeleteAllTask deleteAllTask = (DeleteAllTask) task;
        deleteAllTask.deleteAll();
        verify(deleteAllTask, times(1)).setupEnvironments();
        for (Environment environment : environments) {
            verify(environment.getDeployer(), times(1)).delete(environment.getFiles());
            verify(environment.getDeployer(), never()).deploy(any());
        }
    }
}
