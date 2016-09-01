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

public interface GroupClient {

    @GET("/v2/groups/{groupId}")
    Call<ResponseBody> get(@Path("groupId") String groupId);

    @POST("/v2/groups")
    Call<ResponseBody> create(@Body RequestBody groupDescription);

    @DELETE("/v2/groups/{groupId}")
    Call<ResponseBody> delete(@Path("groupId") String groupId);
}
