package de.qaware.cloud.deployer.commons.test;

import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.resource.BasePingResource;
import de.qaware.cloud.deployer.commons.resource.BaseResource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class BasePingResourceTest extends BaseResourceTest {

    private BasePingResource pingResource;

    @Override
    public BaseResource createResource() throws ResourceException, ResourceConfigException {
        pingResource = createPingResource();
        return null;
    }

    public abstract BasePingResource createPingResource() throws ResourceException;

    protected void testFailingPing(UrlPattern pingPattern) throws ResourceException {
        instanceRule.stubFor(get(pingPattern)
                .willReturn(aResponse().withStatus(401)));

        boolean exceptionThrown = false;
        try {
            pingResource.ping();
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_PING_FAILED", environmentConfig.getId(), 401), e.getMessage());
        }
        assertTrue(exceptionThrown);

        // Verify calls
        instanceRule.verify(1, getRequestedFor(pingPattern));
    }

    protected void testPing(UrlPattern pingPattern) throws ResourceException {
        instanceRule.stubFor(get(pingPattern)
                .willReturn(aResponse().withStatus(200)));

        pingResource.ping();

        // Verify calls
        instanceRule.verify(1, getRequestedFor(pingPattern));
    }
}
