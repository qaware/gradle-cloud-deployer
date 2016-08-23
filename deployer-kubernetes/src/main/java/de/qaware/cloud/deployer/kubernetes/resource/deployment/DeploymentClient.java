package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface DeploymentClient {

    @GET("/apis/extensions/v1beta1/namespaces/{namespace}/deployments/{name}")
    Call<ResponseBody> get(@Path("name") String name, @Path("namespace") String namespace);

    @POST("/apis/extensions/v1beta1/namespaces/{namespace}/deployments")
    Call<ResponseBody> create(@Path("namespace") String namespace, @Body RequestBody deploymentDescription);

    @DELETE("/apis/extensions/v1beta1/namespaces/{namespace}/deployments/{name}")
    Call<ResponseBody> delete(@Path("name") String name, @Path("namespace") String namespace);
}
