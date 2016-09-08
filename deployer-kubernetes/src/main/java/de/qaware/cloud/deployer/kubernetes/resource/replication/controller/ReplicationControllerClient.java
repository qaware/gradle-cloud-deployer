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
package de.qaware.cloud.deployer.kubernetes.resource.replication.controller;

import de.qaware.cloud.deployer.kubernetes.resource.scale.Scale;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Replication controller interface which will be used by retrofit to create a replication controller client.
 */
interface ReplicationControllerClient {

    /**
     * Returns the http response for a request to the replication controller resource with the specified name and namespace.
     *
     * @param name      The replication controller name.
     * @param namespace The replication controller's namespace.
     * @return The server's http response.
     */
    @GET("/api/v1/namespaces/{namespace}/replicationcontrollers/{name}")
    Call<ResponseBody> get(@Path("name") String name, @Path("namespace") String namespace);

    /**
     * Creates the specified replication controller.
     *
     * @param namespace                        The namespace of the new replication controller.
     * @param replicationControllerDescription The request body which contains the replication controller.
     * @return The server's http response.
     */
    @POST("/api/v1/namespaces/{namespace}/replicationcontrollers")
    Call<ResponseBody> create(@Path("namespace") String namespace, @Body RequestBody replicationControllerDescription);

    /**
     * Deletes the replication controller resource with the specified name.
     *
     * @param name      The replication controller's name.
     * @param namespace The namespace of the replication controller.
     * @return The server's http response.
     */
    @DELETE("/api/v1/namespaces/{namespace}/replicationcontrollers/{name}")
    Call<ResponseBody> delete(@Path("name") String name, @Path("namespace") String namespace);

    /**
     * Updates the scale of the replication controller resource with the specified name.
     *
     * @param name      The replication controller's name.
     * @param namespace The namespace of the replication controller.
     * @param scale     The new scale object.
     * @return The server's http response.
     */
    @PUT("/api/v1/namespaces/{namespace}/replicationcontrollers/{name}/scale")
    Call<ResponseBody> updateScale(@Path("name") String name, @Path("namespace") String namespace, @Body Scale scale);
}
