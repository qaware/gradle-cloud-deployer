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

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author sjahreis
 */
public class DeployerPluginTest {

    @Test
    public void testApply() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().add(new DeployerPlugin());

        Object deployerExtension = project.getExtensions().getByName("deployer");
        assertNotNull(deployerExtension);

        Task deployAll = project.getTasks().findByName("deployAll");
        assertNotNull(deployAll);

        Task deploy = project.getTasks().findByName("deploy");
        assertNotNull(deploy);

        Task deleteAll = project.getTasks().findByName("deleteAll");
        assertNotNull(deleteAll);

        Task delete = project.getTasks().findByName("delete");
        assertNotNull(delete);
    }
}
