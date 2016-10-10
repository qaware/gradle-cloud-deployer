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

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.api.delete.options.DeleteOptions;
import de.qaware.cloud.deployer.kubernetes.resource.api.scale.Scale;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * Represents a kubernetes replication controller. It offers methods for deletion and creation.
 */
public class ReplicationControllerResource extends KubernetesResource {

    /**
     * The kind of the replica set scale object as specified in the kubernetes api.
     */
    private static final String SCALE_KIND = "Scale";

    /**
     * The api version of the replica set scale object as specified in the kubernetes api.
     */
    private static final String SCALE_VERSION = "autoscaling/v1";

    /**
     * The client which is used for backend communication.
     */
    private final ReplicationControllerClient replicationControllerClient;

    /**
     * Creates a new replication controller resource as specified in the config.
     *
     * @param namespace      The namespace the replication controller is located in.
     * @param resourceConfig The config which describes the replication controller.
     * @param clientFactory  The factory which is used to create the client for backend communication.
     */
    public ReplicationControllerResource(String namespace, KubernetesResourceConfig resourceConfig, ClientFactory clientFactory) {
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
        // Scale pods down
        Scale scale = new Scale(SCALE_VERSION, SCALE_KIND, getId(), getNamespace(), 0);
        Call<ResponseBody> updateScaleCall = replicationControllerClient.updateScale(getId(), getNamespace(), scale);
        executeCall(updateScaleCall);

        // Delete controller
        Call<ResponseBody> deleteCall = replicationControllerClient.delete(getId(), getNamespace(), new DeleteOptions(0));
        executeDeleteCallAndBlock(deleteCall);
    }

    @Override
    public String toString() {
        return KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_MESSAGE_REPLICATION_CONTROLLER", getNamespace(), getId());
    }
}
