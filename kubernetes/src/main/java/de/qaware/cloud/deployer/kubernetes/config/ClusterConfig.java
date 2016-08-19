package de.qaware.cloud.deployer.kubernetes.config;

public class ClusterConfig {

    private final String baseUrl;
    private final String certificate;
    private final String username;
    private final String password;

    public ClusterConfig(String baseUrl, String certificate, String username, String password) {
        this.baseUrl = baseUrl;
        this.certificate = certificate;
        this.username = username;
        this.password = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
