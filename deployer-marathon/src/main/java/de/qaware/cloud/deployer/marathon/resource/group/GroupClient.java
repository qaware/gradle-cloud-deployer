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
package de.qaware.cloud.deployer.marathon.resource.group;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Group interface which will be used by retrofit to create a group client.
 */
interface GroupClient {

    /**
     * Returns the http response for a request to the group resource with the specified id.
     *
     * @param groupId The groups's id.
     * @return The server's http response.
     */
    @GET("/service/marathon/v2/groups/{groupId}")
    Call<ResponseBody> get(@Path("groupId") String groupId);

    /**
     * Creates the specified group.
     *
     * @param groupDescription The request body which contains the group.
     * @return The server's http response.
     */
    @POST("/service/marathon/v2/groups?force=true")
    Call<ResponseBody> create(@Body RequestBody groupDescription);

    /**
     * Deletes the group resource with the specified id.
     *
     * @param groupId The group's id.
     * @return The server's http response.
     */
    @DELETE("/service/marathon/v2/groups/{groupId}?force=true")
    Call<ResponseBody> delete(@Path("groupId") String groupId);
}
