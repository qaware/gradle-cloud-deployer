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
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.api.delete.options.DeleteOptions;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * Represents a kubernetes pod. It offers methods for deletion and creation.
 */
public class PodResource extends KubernetesResource {

    /**
     * The client which is used for backend communication.
     */
    private final PodClient podClient;

    /**
     * Creates a new pod resource as specified in the config.
     *
     * @param namespace      The namespace the pod is located in.
     * @param resourceConfig The config which describes the pod.
     * @param clientFactory  The factory which is used to create the client for the backend communication.
     */
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
        Call<ResponseBody> deleteCall = podClient.delete(getId(), getNamespace(), new DeleteOptions(0));
        executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_POD", getNamespace(), getId());
    }
}
