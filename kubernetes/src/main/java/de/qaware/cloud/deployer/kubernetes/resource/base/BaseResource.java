package de.qaware.cloud.deployer.kubernetes.resource.base;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public abstract class BaseResource {

    private final String namespace;
    private final ResourceConfig resourceConfig;
    private final ClientFactory clientFactory;

    public BaseResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        this.namespace = namespace;
        this.resourceConfig = resourceConfig;
        this.clientFactory = clientFactory;
    }

    public String getId() {
        return resourceConfig.getResourceId();
    }

    public String getNamespace() {
        return namespace;
    }

    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public RequestBody createRequestBody() {
        return RequestBody.create(createMediaType(), resourceConfig.getContent());
    }

    public MediaType createMediaType() {
        switch (resourceConfig.getContentType()) {
            case JSON:
                return MediaType.parse("application/json");
            case YAML:
                return MediaType.parse("application/yaml");
            default:
                throw new IllegalArgumentException("Unknown type " + resourceConfig.getContentType());
        }
    }

    public boolean isSuccessResponse(retrofit2.Response<ResponseBody> response) {
        return response.code() == 200 || response.code() == 201;
    }

    public <T> T createClient(Class<T> serviceClass) {
        return clientFactory.create(serviceClass);
    }
}
