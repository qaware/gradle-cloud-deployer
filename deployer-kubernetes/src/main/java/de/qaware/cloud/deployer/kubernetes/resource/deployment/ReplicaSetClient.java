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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Replica set interface which will be used by retrofit to create a replica set client.
 */
interface ReplicaSetClient {

    /**
     * Deletes the replica set resource(s) with the specified label selector.
     * @param namespace The namespace of the replica set.
     * @param labelSelector The label selector which identifies the replica set.
     * @return The server's http response.
     */
    @DELETE("apis/extensions/v1beta1/namespaces/{namespace}/replicasets")
    Call<ResponseBody> delete(@Path("namespace") String namespace, @Query("labelSelector") String labelSelector);
}
