package de.qaware.cloud.deployer.kubernetes.test;

import de.qaware.cloud.deployer.kubernetes.resource.base.KubernetesResource;
import de.qaware.cloud.deployer.kubernetes.resource.deployment.DeploymentResource;
import de.qaware.cloud.deployer.kubernetes.resource.namespace.NamespaceResource;
import de.qaware.cloud.deployer.kubernetes.resource.service.ServiceResource;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class BaseKubernetesStrategyTest {

    // Resources
    protected NamespaceResource namespaceResource;
    protected DeploymentResource deploymentResource;
    protected ServiceResource serviceResource;
    protected List<KubernetesResource> resources;

    @Before
    public void setup() {
        namespaceResource = mock(NamespaceResource.class);
        deploymentResource = mock(DeploymentResource.class);
        serviceResource = mock(ServiceResource.class);

        resources = new ArrayList<>();
        resources.add(deploymentResource);
        resources.add(serviceResource);
    }
}
