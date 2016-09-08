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
package de.qaware.cloud.deployer.kubernetes.resource.deployment;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.scale.Scale;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Represents a kubernetes deployment which offers methods for deletion and creation.
 */
public class DeploymentResource extends KubernetesResource {

    /**
     * The name of the label which is used for marking deployments.
     */
    private static final String DEPLOYMENT_MARKER_LABEL = "deployment-id";

    /**
     * The kind of the deployment scale object as specified in the kubernetes api.
     */
    private static final String SCALE_KIND = "Scale";

    /**
     * The api version of the deployment scale object as specified in the kubernetes api.
     */
    private static final String SCALE_VERSION = "extensions/v1beta1";

    /**
     * The client which is used for backend communication concerning deployments.
     */
    private final DeploymentClient deploymentClient;

    /**
     * The client which is used for backend communication concerning replica sets.
     */
    private final ReplicaSetClient replicaSetClient;

    /**
     * Creates a new deployment resource as specified in the config.
     *
     * @param namespace      The deployment resource's namespace.
     * @param resourceConfig The config which describes the deployment.
     * @param clientFactory  The factory which is used to create the clients for the backend communication.
     * @throws ResourceException If a problem occurs during deployment label marking.
     */
    public DeploymentResource(String namespace, KubernetesResourceConfig resourceConfig, ClientFactory clientFactory) throws ResourceException {
        super(namespace, resourceConfig, clientFactory);

        // Replace the config content with a new marked version
        DeploymentLabelUtil.addLabel(getResourceConfig(), DEPLOYMENT_MARKER_LABEL, getId());

        // Create the clients
        this.deploymentClient = createClient(DeploymentClient.class);
        this.replicaSetClient = createClient(ReplicaSetClient.class);
    }

    @Override
    public boolean exists() throws ResourceException {
        Call<ResponseBody> call = deploymentClient.get(getId(), getNamespace());
        return executeExistsCall(call);
    }

    @Override
    public void create() throws ResourceException {
        Call<ResponseBody> request = deploymentClient.create(getNamespace(), createRequestBody());
        executeCreateCallAndBlock(request);
    }

    @Override
    public void delete() throws ResourceException {
        // Scale down pods
        Scale scale = new Scale(getId(), getNamespace(), 0, SCALE_VERSION, SCALE_KIND);
        Call<ResponseBody> updateScaleCall = deploymentClient.updateScale(getId(), getNamespace(), scale);
        executeCall(updateScaleCall);

        // Delete deployment
        Call<ResponseBody> deploymentDeleteCall = deploymentClient.delete(getId(), getNamespace());
        executeDeleteCallAndBlock(deploymentDeleteCall);

        // Delete the replica set
        Call<ResponseBody> replicaSetDeleteCall = replicaSetClient.delete(getNamespace(), createLabelSelector());
        executeDeleteCallAndBlock(replicaSetDeleteCall);
    }

    @Override
    public String toString() {
        return "Deployment: " + getNamespace() + "/" + getId();
    }

    /**
     * Creates the label selector for this deployment.
     *
     * @return The label selector for this deployment.
     */
    private String createLabelSelector() {
        return DEPLOYMENT_MARKER_LABEL + "=" + getId();
    }
}
