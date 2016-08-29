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

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.scale.ScaleDescription;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ReplicationControllerResource extends BaseResource {

    private final ReplicationControllerClient replicationControllerClient;

    public ReplicationControllerResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        replicationControllerClient = createClient(ReplicationControllerClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = replicationControllerClient.get(getId(), getNamespace());
        return executeExistsCall(call);
    }

    @Override
    public void create() throws ResourceException {
        Call<ResponseBody> request = replicationControllerClient.create(getNamespace(), createRequestBody());
        executeCreateCallAndBlock(request);
    }

    @Override
    public void delete() throws ResourceException {
        Call<ResponseBody> updateScaleCall = replicationControllerClient.updateScale(getId(), getNamespace(), new ScaleDescription(getId(), getNamespace(), 0));
        executeCall(updateScaleCall);
        Call<ResponseBody> deleteCall = replicationControllerClient.delete(getId(), getNamespace());
        executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return "ReplicationControllerResource: " + getNamespace() + "/" + getId();
    }
}
