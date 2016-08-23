package de.qaware.cloud.deployer.kubernetes.resource;

import de.qaware.cloud.deployer.kubernetes.resource.base.ClientFactory;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ResourceTestEnvironment {

    private NamespaceResource namespaceResource;
    private ClientFactory clientFactory;
    private KubernetesClient kubernetesClient;

    public ResourceTestEnvironment(NamespaceResource namespaceResource, ClientFactory clientFactory, KubernetesClient kubernetesClient) {
        this.namespaceResource = namespaceResource;
        this.clientFactory = clientFactory;
        this.kubernetesClient = kubernetesClient;
    }

    public NamespaceResource getNamespaceResource() {
        return namespaceResource;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public KubernetesClient getKubernetesClient() {
        return kubernetesClient;
    }
}
