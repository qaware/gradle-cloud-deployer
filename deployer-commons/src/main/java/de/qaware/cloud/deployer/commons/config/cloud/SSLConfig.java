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
package de.qaware.cloud.deployer.commons.config.cloud;

/**
 * Represents a cloud's ssl configuration.
 */
public class SSLConfig {

    /**
     * Indicates whether all certs are trusted.
     */
    private final boolean trustAll;

    /**
     * Specifies a custom certificate which is trusted.
     */
    private final String certificate;

    /**
     * Creates a default ssl config which trusts only the host's certs.
     */
    public SSLConfig(){
        this(false);
    }

    /**
     * Creates a new ssl config which trusts all certs.
     *
     * @param trustAll Indicates whether all certs are trusted.
     */
    public SSLConfig(boolean trustAll) {
        this.trustAll = trustAll;
        this.certificate = "";
    }

    /**
     * Creates a new ssl config which trusts the specified certificate.
     *
     * @param certificate The certificate to trust.
     */
    public SSLConfig(String certificate) {
        this.certificate = certificate;
        this.trustAll = false;
    }

    /**
     * Indicates whether all certs are trusted.
     *
     * @return TRUE if all certs are trusted, FALSE otherwise.
     */
    public boolean isTrustAll() {
        return trustAll;
    }

    /**
     * Returns the certificate to trust.
     *
     * @return The certificate.
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Indicates whether this ssl config has a custom certificate to trust.
     *
     * @return TRUE if a custom certificate exists, FALSE otherwise.
     */
    public boolean hasCertificate() {
        return certificate != null && certificate.length() > 0;
    }
}
