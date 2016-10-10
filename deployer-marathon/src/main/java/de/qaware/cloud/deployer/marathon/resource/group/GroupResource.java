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

import static de.qaware.cloud.deployer.marathon.logging.MarathonMessageBundle.MARATHON_MESSAGE_BUNDLE;

/**
 * Represents a marathon group and offers methods for deletion and creation.
 */
public class GroupResource extends MarathonResource {

    /**
     * The client which is used for communication with the backend.
     */
    private final GroupClient groupClient;

    /**
     * Creates a marathon group resource for the specified config which uses the specified client factory to create a client for communication.
     *
     * @param resourceConfig The config which contains the group's metadata.
     * @param clientFactory  The factory which is used to create a client for communication.
     */
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
    public void update() throws ResourceException {
        Call<ResponseBody> updateCall = groupClient.update(getId(), createRequestBody());
        executeCall(updateCall);
    }

    @Override
    public String toString() {
        return MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_MESSAGE_GROUP", getId());
    }
}
