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

import de.qaware.cloud.deployer.commons.error.EnvironmentConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author sjahreis
 */
public class SSLExtensionTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testSetCertificate() throws IOException, EnvironmentConfigException, ResourceConfigException {
        String cert = "CERT";
        File certFile = folder.newFile();
        FileUtils.writeStringToFile(certFile, "CERT", Charset.defaultCharset());

        SSLExtension extension = new SSLExtension();
        extension.setCertificate(certFile);
        assertEquals(cert, extension.getCertificate());
    }

    @Test
    public void testSetCertificateWithEmptyFile() throws IOException, ResourceConfigException {
        boolean exceptionThrown = false;
        File certFile = folder.newFile();
        SSLExtension extension = new SSLExtension();
        try {
            extension.setCertificate(certFile);
        } catch (EnvironmentConfigException e) {
            assertEquals(PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_CERTIFICATE_FROM_FILE", certFile.getName()), e.getMessage());
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testSetCertificateWithNullFile() throws IOException, EnvironmentConfigException {
        boolean exceptionThrown = false;
        SSLExtension extension = new SSLExtension();
        try {
            extension.setCertificate(null);
        } catch (ResourceConfigException e) {
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_INVALID_FILENAME"), e.getMessage());
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
