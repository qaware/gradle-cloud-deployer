package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
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
    public boolean exists() {
        try {
            Call<ResponseBody> request = namespaceClient.get(getId());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean create() {
        try {
            Call<ResponseBody> request = namespaceClient.create(createRequestBody());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete() {
        try {
            Call<ResponseBody> request = namespaceClient.delete(getId());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "NamespaceResource: " + getNamespace();
    }
}
