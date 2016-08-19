package de.qaware.cloud.deployer.kubernetes.config.namespace;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NamespaceDescription implements Serializable {

    private final String apiVersion = "v1";
    private final String kind = "Namespace";
    private Map<String, String> metadata = new HashMap<>();

    public NamespaceDescription(final String name) {
        metadata.put("name", name);
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getKind() {
        return kind;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
