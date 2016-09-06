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

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.marathon.config.resource.MarathonResourceConfig;
import de.qaware.cloud.deployer.marathon.resource.base.MarathonResource;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class GroupResource extends MarathonResource {

    private final GroupClient groupClient;

    public GroupResource(MarathonResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(resourceConfig, clientFactory);
        groupClient = createClient(GroupClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = groupClient.get(getId());
        return executeExistsCall(call);
    }

    @Override
    public void create() throws ResourceException {
        Call<ResponseBody> call = groupClient.create(createRequestBody());
        executeCreateCallAndBlock(call);
    }

    @Override
    public void delete() throws ResourceException {
        Call<ResponseBody> deleteCall = groupClient.delete(getId());
        executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return "Group: " + getId();
    }
}
