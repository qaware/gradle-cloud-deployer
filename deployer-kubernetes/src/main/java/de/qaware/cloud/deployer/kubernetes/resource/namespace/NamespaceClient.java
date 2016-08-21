package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface NamespaceClient {

    @GET("/api/v1/namespaces/{namespace}")
    Call<ResponseBody> get(@Path("namespace") String namespace);

    @POST("/api/v1/namespaces")
    Call<ResponseBody> create(@Body RequestBody namespaceDescription);

    @DELETE("/api/v1/namespaces/{namespace}")
    Call<ResponseBody> delete(@Path("namespace") String namespace);
}
