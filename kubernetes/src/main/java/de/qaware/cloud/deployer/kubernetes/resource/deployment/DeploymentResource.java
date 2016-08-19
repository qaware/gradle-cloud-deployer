package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class DeploymentResource extends BaseResource implements Resource {

    private final DeploymentClient deploymentClient;

    public DeploymentResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        this.deploymentClient = createClient(DeploymentClient.class);
    }

    @Override
    public boolean exists() {
        try {
            Call<ResponseBody> request = deploymentClient.get(getId(), getNamespace());
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
            Call<ResponseBody> request = deploymentClient.create(getNamespace(), createRequestBody());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
