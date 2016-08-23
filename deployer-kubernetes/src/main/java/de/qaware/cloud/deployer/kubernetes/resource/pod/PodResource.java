package de.qaware.cloud.deployer.kubernetes.resource.pod;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class PodResource extends BaseResource {

    private final PodClient podClient;

    public PodResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        this.podClient = createClient(PodClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = podClient.get(getId(), getNamespace());
        return executeExistsCall(call);
    }

    @Override
    public boolean create() throws ResourceException {
        Call<ResponseBody> call = podClient.create(getNamespace(), createRequestBody());
        return executeCreateCallAndBlock(call);
    }

    @Override
    public boolean delete() throws ResourceException {
        Call<ResponseBody> deleteCall = podClient.delete(getId(), getNamespace());
        return executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return "PodResource: " + getNamespace() + "/" + getId();
    }
}
