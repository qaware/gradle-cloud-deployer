package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DeploymentResource extends BaseResource {

    private final DeploymentClient deploymentClient;

    public DeploymentResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        this.deploymentClient = createClient(DeploymentClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = deploymentClient.get(getId(), getNamespace());
        return executeExistsCall(call);
    }

    @Override
    public boolean create() throws ResourceException {
        Call<ResponseBody> request = deploymentClient.create(getNamespace(), createRequestBody());
        return executeCreateCallAndBlock(request);
    }

    @Override
    public String toString() {
        return "DeploymentResource: " + getNamespace() + "/" + getId();
    }
}
