/*
 * Copyright 2016 QAware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import de.qaware.cloud.deployer.kubernetes.resource.api.delete.options.DeleteOptions;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Namespace interface which will be used by retrofit to create a namespace client.
 */
interface NamespaceClient {

    /**
     * Returns the http response for a request to the namespace resource with the specified name.
     *
     * @param namespace The namespace's name.
     * @return The server's http response.
     */
    @GET("api/v1/namespaces/{namespace}")
    Call<ResponseBody> get(@Path("namespace") String namespace);

    /**
     * Creates the specified namespace.
     *
     * @param namespaceDescription The request body which contains the namespace.
     * @return The server's http response.
     */
    @POST("api/v1/namespaces")
    Call<ResponseBody> create(@Body RequestBody namespaceDescription);

    /**
     * Deletes the namespace resource with the specified name.
     *
     * @param namespace     The name of the namespace.
     * @param deleteOptions The delete options.
     * @return The server's http response.
     */
    @HTTP(method = "DELETE", path = "api/v1/namespaces/{namespace}", hasBody = true)
    Call<ResponseBody> delete(@Path("namespace") String namespace, @Body DeleteOptions deleteOptions);
}
