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
package de.qaware.cloud.deployer.kubernetes.config.namespace;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import junit.framework.TestCase;

public class NamespaceConfigFactoryTest extends TestCase {

    public void testValidNamespace() throws ResourceConfigException {
        // Create config
        ResourceConfig namespaceResource = NamespaceConfigFactory.create("test");

        // Check config
        assertEquals("test", namespaceResource.getResourceId());
        assertEquals("Namespace", namespaceResource.getResourceType());
        assertEquals("v1", namespaceResource.getResourceVersion());
        assertEquals(ContentType.JSON, namespaceResource.getContentType());
        assertEquals("{\"apiVersion\":\"v1\",\"kind\":\"Namespace\",\"metadata\":{\"name\":\"test\"}}", namespaceResource.getContent());
    }

    public void testEmptyNamespace() {
        boolean exceptionThrown = false;
        try {
            NamespaceConfigFactory.create("");
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    public void testNullNamespace() {
        boolean exceptionThrown = false;
        try {
            NamespaceConfigFactory.create(null);
        } catch (ResourceConfigException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
