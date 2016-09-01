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
import de.qaware.cloud.deployer.kubernetes.config.resource.KubernetesResourceConfig;
import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.commons.resource.ClientFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DeploymentResource extends KubernetesResource {

    public static final String DEPLOYMENT_MARKER_LABEL = "deployment-id";

    private final DeploymentClient deploymentClient;
    private final ReplicaSetClient replicaSetClient;

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
        DeploymentScaleDescription scaleDescription = new DeploymentScaleDescription(getId(), getNamespace(), 0);
        Call<ResponseBody> updateScaleCall = deploymentClient.updateScale(getId(), getNamespace(), scaleDescription);
        executeCall(updateScaleCall);

        // Delete deployment
        Call<ResponseBody> deploymentDeleteCall = deploymentClient.delete(getId(), getNamespace());
        executeDeleteCallAndBlock(deploymentDeleteCall);

        // Delete the replica set
        Call<ResponseBody> replicaSetDeleteCall = replicaSetClient.delete(getNamespace(), DEPLOYMENT_MARKER_LABEL + "=" + getId());
        executeDeleteCallAndBlock(replicaSetDeleteCall);
    }

    @Override
    public String toString() {
        return "Deployment: " + getNamespace() + "/" + getId();
    }
}
