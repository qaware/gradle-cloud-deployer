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
package de.qaware.cloud.deployer.kubernetes.resource.service;

import de.qaware.cloud.deployer.kubernetes.resource.api.delete.options.DeleteOptions;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Service interface which will be used by retrofit to create a service client.
 */
interface ServiceClient {

    /**
     * Returns the http response for a request to the service resource with the specified name and namespace.
     *
     * @param name      The service name.
     * @param namespace The service's namespace.
     * @return The server's http response.
     */
    @GET("/api/v1/namespaces/{namespace}/services/{name}")
    Call<ResponseBody> get(@Path("name") String name, @Path("namespace") String namespace);

    /**
     * Creates the specified service.
     *
     * @param namespace          The namespace of the new service.
     * @param serviceDescription The request body which contains the service.
     * @return The server's http response.
     */
    @POST("/api/v1/namespaces/{namespace}/services")
    Call<ResponseBody> create(@Path("namespace") String namespace, @Body RequestBody serviceDescription);

    /**
     * Deletes the service resource with the specified name.
     *
     * @param name          The service's name.
     * @param namespace     The namespace of the service.
     * @param deleteOptions The delete options.
     * @return The server's http response.
     */
    @HTTP(method = "DELETE", path = "/api/v1/namespaces/{namespace}/services/{name}", hasBody = true)
    Call<ResponseBody> delete(@Path("name") String name, @Path("namespace") String namespace, @Body DeleteOptions deleteOptions);
}
