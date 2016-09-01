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

public class SSLConfig {

    private final boolean trustAll;
    private final String certificate;

    public SSLConfig(boolean trustAll, String certificate) {
        this.trustAll = trustAll;
        this.certificate = certificate;
    }

    public boolean isTrustAll() {
        return trustAll;
    }

    public String getCertificate() {
        return certificate;
    }

    public boolean hasCertificate() {
        return certificate != null && certificate.length() > 0;
    }
}
