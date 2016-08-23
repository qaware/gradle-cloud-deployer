package de.qaware.cloud.deployer.kubernetes.resource.service;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ServiceResource extends BaseResource {

    private final ServiceClient serviceClient;

    public ServiceResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        this.serviceClient = createClient(ServiceClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = serviceClient.get(getId(), getNamespace());
        return executeExistsCall(call);
    }

    @Override
    public boolean create() throws ResourceException {
        Call<ResponseBody> request = serviceClient.create(getNamespace(), createRequestBody());
        return executeCreateCallAndBlock(request);
    }

    @Override
    public String toString() {
        return "ServiceResource: " + getNamespace() + "/" + getId();
    }
}
