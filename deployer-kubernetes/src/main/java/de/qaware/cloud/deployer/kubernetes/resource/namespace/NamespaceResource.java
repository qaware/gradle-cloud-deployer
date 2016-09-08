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
package de.qaware.cloud.deployer.kubernetes.resource.namespace;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Represents a kubernetes namespace. It offers methods for deletion and creation.
 */
public class NamespaceResource extends KubernetesResource {

    /**
     * The client which is used for backend communication.
     */
    private final NamespaceClient namespaceClient;

    /**
     * Creates a new namespace resource as specified in the config.
     *
     * @param resourceConfig The config which describes the namespace.
     * @param clientFactory The factory which is used to create the client for the backend communication.
     */
    public NamespaceResource(KubernetesResourceConfig resourceConfig, ClientFactory clientFactory) {
        super(resourceConfig.getResourceId(), resourceConfig, clientFactory);
        this.namespaceClient = createClient(NamespaceClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = namespaceClient.get(getId());
        return executeExistsCall(call);
    }

    @Override
    public void create() throws ResourceException {
        Call<ResponseBody> request = namespaceClient.create(createRequestBody());
        executeCreateCallAndBlock(request);
    }

    @Override
    public void delete() throws ResourceException {
        Call<ResponseBody> deleteCall = namespaceClient.delete(getId());
        executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return "Namespace: " + getNamespace();
    }
}
