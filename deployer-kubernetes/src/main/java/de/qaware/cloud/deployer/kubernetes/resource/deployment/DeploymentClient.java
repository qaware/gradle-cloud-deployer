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
package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.kubernetes.resource.api.delete.options.DeleteOptions;
import de.qaware.cloud.deployer.kubernetes.resource.api.scale.Scale;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Deployment interface which will be used by retrofit to create a deployment client.
 */
interface DeploymentClient {

    /**
     * Returns the http response for a request to the deployment resource with the specified name and namespace.
     *
     * @param name      The deployment name.
     * @param namespace The deployment's namespace.
     * @return The server's http response.
     */
    @GET("/apis/extensions/v1beta1/namespaces/{namespace}/deployments/{name}")
    Call<ResponseBody> get(@Path("name") String name, @Path("namespace") String namespace);

    /**
     * Creates the specified deployment.
     *
     * @param namespace             The namespace of the new deployment.
     * @param deploymentDescription The request body which contains the deployment.
     * @return The server's http response.
     */
    @POST("/apis/extensions/v1beta1/namespaces/{namespace}/deployments")
    Call<ResponseBody> create(@Path("namespace") String namespace, @Body RequestBody deploymentDescription);

    /**
     * Deletes the deployment resource with the specified name.
     *
     * @param name          The deployment's name.
     * @param namespace     The namespace of the deployment.
     * @param deleteOptions The delete options.
     * @return The server's http response.
     */
    @HTTP(method = "DELETE", path = "/apis/extensions/v1beta1/namespaces/{namespace}/deployments/{name}", hasBody = true)
    Call<ResponseBody> delete(@Path("name") String name, @Path("namespace") String namespace, @Body DeleteOptions deleteOptions);

    /**
     * Updates the deployment resource with the specified name.
     *
     * @param name                  The deployment's name.
     * @param namespace             The namespace of the deployment.
     * @param deploymentDescription The request body which contains the updated deployment.
     * @return The server's http response.
     */
    @PATCH("/apis/extensions/v1beta1/namespaces/{namespace}/deployments/{name}")
    Call<ResponseBody> update(@Path("name") String name, @Path("namespace") String namespace, @Body RequestBody deploymentDescription);

    /**
     * Updates the scale of the deployment resource with the specified name.
     *
     * @param name      The deployment's name.
     * @param namespace The namespace of the deployment.
     * @param scale     The new scale object.
     * @return The server's http response.
     */
    @PUT("/apis/extensions/v1beta1/namespaces/{namespace}/deployments/{name}/scale")
    Call<ResponseBody> updateScale(@Path("name") String name, @Path("namespace") String namespace, @Body Scale scale);
}
