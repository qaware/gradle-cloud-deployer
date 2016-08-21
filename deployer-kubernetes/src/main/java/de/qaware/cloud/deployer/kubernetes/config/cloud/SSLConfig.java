package de.qaware.cloud.deployer.kubernetes.config.cloud;

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
