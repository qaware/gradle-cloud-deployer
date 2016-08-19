package de.qaware.cloud.deployer.kubernetes.resource.pod;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface PodClient {

    @GET("/api/v1/namespaces/{namespace}/pods/{name}")
    Call<ResponseBody> get(@Path("name") String name, @Path("namespace") String namespace);

    @POST("/api/v1/namespaces/{namespace}/pods")
    Call<ResponseBody> create(@Path("namespace") String namespace, @Body RequestBody podDescription);
}
