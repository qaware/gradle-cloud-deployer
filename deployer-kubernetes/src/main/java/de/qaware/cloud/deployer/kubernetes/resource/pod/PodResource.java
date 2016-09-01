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
package de.qaware.cloud.deployer.kubernetes.resource.pod;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class PodResource extends KubernetesResource {

    private final PodClient podClient;

    public PodResource(String namespace, KubernetesResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(namespace, resourceConfig, clientFactory);
        this.podClient = createClient(PodClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = podClient.get(getId(), getNamespace());
        return executeExistsCall(call);
    }

    @Override
    public void create() throws ResourceException {
        Call<ResponseBody> call = podClient.create(getNamespace(), createRequestBody());
        executeCreateCallAndBlock(call);
    }

    @Override
    public void delete() throws ResourceException {
        Call<ResponseBody> deleteCall = podClient.delete(getId(), getNamespace());
        executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return "Pod: " + getNamespace() + "/" + getId();
    }
}
