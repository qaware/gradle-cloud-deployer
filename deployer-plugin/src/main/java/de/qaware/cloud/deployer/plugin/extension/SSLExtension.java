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

/**
 * Contains the ssl configuration for a environment.
 */
public class SSLExtension {

    /**
     * Indicates whether all certs are trusted.
     */
    private boolean trustAll;

    /**
     * Specifies a custom certificate which is trusted.
     */
    private String certificate;

    /**
     * Indicates whether all certificates are trusted.
     *
     * @return TRUE if all certs are trusted, FALSE otherwise.
     */
    public boolean isTrustAll() {
        return trustAll;
    }

    /**
     * Sets whether to trust all certificates or not.
     *
     * @param trustAll TRUE if all certs are trusted, FALSE otherwise.
     */
    public void setTrustAll(boolean trustAll) {
        this.trustAll = trustAll;
    }

    /**
     * Returns the custom certificate which is trusted.
     *
     * @return The trusted certificate.
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets a custom certificate to trust.
     *
     * @param certificate The certificate.
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
}
