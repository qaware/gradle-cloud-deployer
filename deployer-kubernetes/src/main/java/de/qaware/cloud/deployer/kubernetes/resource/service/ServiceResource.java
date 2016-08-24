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

import de.qaware.cloud.deployer.kubernetes.config.resource.ResourceConfig;
import de.qaware.cloud.deployer.kubernetes.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.resource.base.BaseResource;
import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ServiceResource extends BaseResource {

    private final ServiceClient serviceClient;

    public ServiceResource(String namespace, ResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        this.serviceClient = createClient(ServiceClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = serviceClient.get(getId(), getNamespace());
        return executeExistsCall(call);
    }

    @Override
    public void create() throws ResourceException {
        Call<ResponseBody> request = serviceClient.create(getNamespace(), createRequestBody());
        executeCreateCallAndBlock(request);
    }

    @Override
    public void delete() throws ResourceException {
        Call<ResponseBody> deleteCall = serviceClient.delete(getId(), getNamespace());
        executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return "ServiceResource: " + getNamespace() + "/" + getId();
    }
}
