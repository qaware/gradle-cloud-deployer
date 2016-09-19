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
package de.qaware.cloud.deployer.kubernetes.resource.ping;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Ping interface which will be used by retrofit to create a ping client.
 */
interface KubernetesPingClient {

    /**
     * Returns the http response for a request to the ping resource.
     * ATTENTION: as kubernetes offers no ping endpoint we simply do a call against the namespace endpoint!
     *
     * @return The server's http response.
     */
    @GET("/api/v1/namespaces")
    Call<ResponseBody> ping();
}