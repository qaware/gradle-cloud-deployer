package de.qaware.cloud.deployer.kubernetes.config.cloud;

public class CloudConfig {

    private final String baseUrl;
    private final String username;
    private final String password;
    private final SSLConfig sslConfig;

    public CloudConfig(String baseUrl, String username, String password, SSLConfig sslConfig) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.sslConfig = sslConfig;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public SSLConfig getSslConfig() {
        return sslConfig;
    }
}
