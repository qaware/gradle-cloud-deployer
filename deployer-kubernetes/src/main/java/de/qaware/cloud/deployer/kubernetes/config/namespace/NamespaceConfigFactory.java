package de.qaware.cloud.deployer.kubernetes.config.namespace;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qaware.cloud.deployer.kubernetes.config.resource.ContentType;
import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceConfigException;

import java.io.IOException;

public class NamespaceConfigFactory {

    private NamespaceConfigFactory() {
    }

    public static ResourceConfig create(String name) throws ResourceConfigException {
        NamespaceDescription namespaceDescription = new NamespaceDescription(name);
        String namespaceDescriptionContent = "";
        try {
            namespaceDescriptionContent = new ObjectMapper(new JsonFactory()).writeValueAsString(namespaceDescription);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ResourceConfig(ContentType.JSON, namespaceDescriptionContent);
    }
}
