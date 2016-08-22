package de.qaware.cloud.deployer.kubernetes.resource.service;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class ServiceResource extends BaseResource implements Resource {

    private final ServiceClient serviceClient;

    public ServiceResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        this.serviceClient = createClient(ServiceClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        try {
            Call<ResponseBody> request = serviceClient.get(getId(), getNamespace());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public boolean create() throws ResourceException {
        try {
            Call<ResponseBody> request = serviceClient.create(getNamespace(), createRequestBody());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public String toString() {
        return "ServiceResource: " + getNamespace() + "/" + getId();
    }
}
