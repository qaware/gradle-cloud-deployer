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

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.plugin.token.DcosAuthTokenInitializer;
import de.qaware.cloud.deployer.plugin.token.DefaultTokenInitializer;
import de.qaware.cloud.deployer.plugin.token.OpenIdConnectIdTokenInitializer;
import de.qaware.cloud.deployer.plugin.token.TokenInitializer;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author sjahreis
 */
public class AuthExtensionTest {

    @Test
    public void testOpenId() throws ResourceConfigException {
        TokenInitializer tokenInitializer = new AuthExtension().openId(mock(File.class));
        assertTrue(tokenInitializer instanceof OpenIdConnectIdTokenInitializer);
    }

    @Test
    public void testDcosAuthToken() throws ResourceConfigException {
        TokenInitializer tokenInitializer = new AuthExtension().dcosAuthToken();
        assertTrue(tokenInitializer instanceof DcosAuthTokenInitializer);
    }

    @Test
    public void testDefaultToken() throws ResourceConfigException {
        TokenInitializer tokenInitializer = new AuthExtension().defaultToken(mock(File.class));
        assertTrue(tokenInitializer instanceof DefaultTokenInitializer);
    }
}
