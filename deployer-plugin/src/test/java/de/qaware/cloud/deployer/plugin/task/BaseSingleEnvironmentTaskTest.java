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
import de.qaware.cloud.deployer.plugin.environment.Environment;
import org.gradle.api.Project;
import org.junit.Test;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author sjahreis
 */
public class BaseSingleEnvironmentTaskTest extends BaseAllTaskTestHelper {

    @Override
    public BaseAllEnvironmentsTask createTask(Project project) {
        // Need to use the DeleteTask because BaseSingleEnvironmentTask is abstract.
        return project.getTasks().create("delete", DeleteTask.class);
    }

    @Test
    public void testWithValidEnvironmentId() throws EnvironmentConfigException {
        BaseSingleEnvironmentTask singleEnvironmentTask = (BaseSingleEnvironmentTask) task;

        Environment environment = environments.get(0);
        singleEnvironmentTask.setEnvironmentId(environment.getId());
        singleEnvironmentTask.setupEnvironment();

        Environment filteredEnvironment = singleEnvironmentTask.getEnvironment();
        assertEquals(environment, filteredEnvironment);
    }

    @Test
    public void testWithEmptyEnvironmentId() throws EnvironmentConfigException {
        BaseSingleEnvironmentTask singleEnvironmentTask = (BaseSingleEnvironmentTask) task;

        singleEnvironmentTask.setEnvironmentId("");

        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_EMPTY_ID");
        assertExceptionOnSetup(singleEnvironmentTask, message);
    }

    @Test
    public void testWithNullEnvironmentId() throws EnvironmentConfigException {
        BaseSingleEnvironmentTask singleEnvironmentTask = (BaseSingleEnvironmentTask) task;

        singleEnvironmentTask.setEnvironmentId(null);

        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_EMPTY_ID");
        assertExceptionOnSetup(singleEnvironmentTask, message);
    }

    @Test
    public void testWithInvalidEnvironmentId() throws EnvironmentConfigException {
        String invalidId = "bla";

        BaseSingleEnvironmentTask singleEnvironmentTask = (BaseSingleEnvironmentTask) task;

        singleEnvironmentTask.setEnvironmentId(invalidId);

        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_ID_DOES_NOT_EXIST", invalidId);
        assertExceptionOnSetup(singleEnvironmentTask, message);
    }

    @Test
    public void testWithDuplicatedEnvironmentId() throws EnvironmentConfigException {
        BaseSingleEnvironmentTask singleEnvironmentTask = (BaseSingleEnvironmentTask) task;

        Environment environment = environments.get(0);
        environments.add(environment);
        String id = environment.getId();
        singleEnvironmentTask.setEnvironmentId(id);

        String message = PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_DEPLOY_ERROR_ID_DOES_EXIST_SEVERAL_TIMES", id);
        assertExceptionOnSetup(singleEnvironmentTask, message);
    }

    private void assertExceptionOnSetup(BaseSingleEnvironmentTask task, String message) {
        boolean exceptionThrown = false;
        try {
            task.setupEnvironment();
        } catch (EnvironmentConfigException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}