package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.DeletableResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class NamespaceResource extends BaseResource implements DeletableResource {

    private final NamespaceClient namespaceClient;

    public NamespaceResource(ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(resourceConfig.getResourceId(), resourceConfig, clientFactory);
        this.namespaceClient = createClient(NamespaceClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        try {
            Call<ResponseBody> request = namespaceClient.get(getId());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public boolean create() throws ResourceException {
        try {
            Call<ResponseBody> request = namespaceClient.create(createRequestBody());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public boolean delete() throws ResourceException {
        try {
            Call<ResponseBody> request = namespaceClient.delete(getId());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public String toString() {
        return "NamespaceResource: " + getNamespace();
    }
}
