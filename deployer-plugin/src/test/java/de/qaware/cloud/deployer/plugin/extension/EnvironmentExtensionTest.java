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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class EnvironmentExtensionTest {

    @Test
    public void testAuth() {
        AuthExtension authExtension = new AuthExtension();
        Project project = mock(Project.class);
        when(project.configure(any(AuthExtension.class), any(Closure.class))).thenReturn(authExtension);
        Closure closure = mock(Closure.class);

        EnvironmentExtension environmentExtension = new EnvironmentExtension(DeployerType.MARATHON, project);
        environmentExtension.auth(closure);
        assertEquals(authExtension, environmentExtension.getAuthExtension());
    }

    @Test
    public void testSSL() {
        SSLExtension sslExtension = new SSLExtension();
        Project project = mock(Project.class);
        when(project.configure(any(SSLExtension.class), any(Closure.class))).thenReturn(sslExtension);
        Closure closure = mock(Closure.class);

        EnvironmentExtension environmentExtension = new EnvironmentExtension(DeployerType.MARATHON, project);
        environmentExtension.ssl(closure);
        assertEquals(sslExtension, environmentExtension.getSslExtension());
    }
}
