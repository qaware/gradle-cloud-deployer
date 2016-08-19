package de.qaware.cloud.deployer.kubernetes.resource.pod;

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.Resource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class PodResource extends BaseResource implements Resource {

    private final PodClient podClient;

    public PodResource(String namespace, ResourceConfig resourceConfig) {
        super(namespace, resourceConfig);
        this.podClient = createClient(PodClient.class);
    }

    @Override
    public boolean exists() {
        try {
            Call<ResponseBody> request = podClient.get(getId(), getNamespace());
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
            Call<ResponseBody> request = podClient.create(getNamespace(), createRequestBody());
            Response<ResponseBody> response = request.execute();
            return isSuccessResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
