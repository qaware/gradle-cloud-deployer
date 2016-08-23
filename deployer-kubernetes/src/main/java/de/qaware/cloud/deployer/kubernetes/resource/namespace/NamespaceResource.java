package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class NamespaceResource extends BaseResource {

    private final NamespaceClient namespaceClient;

    public NamespaceResource(ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(resourceConfig.getResourceId(), resourceConfig, clientFactory);
        this.namespaceClient = createClient(NamespaceClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = namespaceClient.get(getId());
        return executeExistsCall(call);
    }

    @Override
    public boolean create() throws ResourceException {
        Call<ResponseBody> request = namespaceClient.create(createRequestBody());
        return executeCreateCallAndBlock(request);
    }

    @Override
    public boolean delete() throws ResourceException {
        Call<ResponseBody> deleteCall = namespaceClient.delete(getId());
        return executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return "NamespaceResource: " + getNamespace();
    }
}
