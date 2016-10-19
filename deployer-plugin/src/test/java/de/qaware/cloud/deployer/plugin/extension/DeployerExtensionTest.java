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
package de.qaware.cloud.deployer.plugin.extension;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class DeployerExtensionTest {

    private DeployerExtension deployerExtension;
    private Project project;

    @Before
    public void setup() {
        project = mock(Project.class);
        deployerExtension = new DeployerExtension(project);
    }

    @Test
    public void testMarathon() {
        EnvironmentExtension environmentExtension = mock(EnvironmentExtension.class);
        when(project.configure(any(EnvironmentExtension.class), any(Closure.class))).thenReturn(environmentExtension);

        Closure closure = mock(Closure.class);
        deployerExtension.marathon(closure);

        Collection<EnvironmentExtension> configs = deployerExtension.getConfigs();
        assertEquals(1, configs.size());
        assertEquals(environmentExtension, configs.iterator().next());
    }

    @Test
    public void testKubernetes() {
        EnvironmentExtension environmentExtension = mock(EnvironmentExtension.class);
        when(project.configure(any(EnvironmentExtension.class), any(Closure.class))).thenReturn(environmentExtension);

        Closure closure = mock(Closure.class);
        deployerExtension.kubernetes(closure);

        Collection<EnvironmentExtension> configs = deployerExtension.getConfigs();
        assertEquals(1, configs.size());
        assertEquals(environmentExtension, configs.iterator().next());
    }

    @Test
    public void testKubernetesAndMarathon() {
        Closure closure1 = mock(Closure.class);
        Closure closure2 = mock(Closure.class);

        EnvironmentExtension environmentExtension1 = mock(EnvironmentExtension.class);
        EnvironmentExtension environmentExtension2 = mock(EnvironmentExtension.class);

        when(project.configure(any(EnvironmentExtension.class), eq(closure1))).thenReturn(environmentExtension1);
        when(project.configure(any(EnvironmentExtension.class), eq(closure2))).thenReturn(environmentExtension2);

        deployerExtension.kubernetes(closure1);
        deployerExtension.kubernetes(closure2);

        Collection<EnvironmentExtension> configs = deployerExtension.getConfigs();
        assertEquals(2, configs.size());
        Iterator<EnvironmentExtension> iterator = configs.iterator();
        assertEquals(environmentExtension1, iterator.next());
        assertEquals(environmentExtension2, iterator.next());
    }
}
