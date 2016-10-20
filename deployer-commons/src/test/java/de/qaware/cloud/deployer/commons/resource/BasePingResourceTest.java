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
package de.qaware.cloud.deployer.commons.resource;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sjahreis
 */
public class BasePingResourceTest {

    private static final String ENVIRONMENT_ID = "TEST";
    private BasePingResource basePingResource;
    private Call<ResponseBody> call;
    private ResponseBody responseBody;

    @Before
    public void setup() throws IOException {
        basePingResource = new BasePingResource(ENVIRONMENT_ID) {
            @Override
            public void ping() throws ResourceException {
            }
        };
        call = (Call<ResponseBody>) mock(Call.class);
        responseBody = mock(ResponseBody.class);
    }

    @Test
    public void testExecutePingCall() throws ResourceException, IOException {
        when(call.execute()).thenReturn(Response.success(responseBody));

        basePingResource.executePingCall(call);
    }

    @Test
    public void testExecuteFailingPingCall() throws IOException {
        int responseCode = 404;
        when(call.execute()).thenReturn(Response.error(responseCode, responseBody));

        boolean exceptionThrown = false;
        try {
            basePingResource.executePingCall(call);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_PING_FAILED", ENVIRONMENT_ID, responseCode), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testExecutePingCallWithIOException() throws IOException {
        when(call.execute()).thenThrow(new IOException(ENVIRONMENT_ID));

        boolean exceptionThrown = false;
        try {
            basePingResource.executePingCall(call);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(ENVIRONMENT_ID, e.getCause().getMessage());
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testToString() {
        String toString = basePingResource.toString();
        assertEquals(COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_MESSAGES_PING", ENVIRONMENT_ID), toString);
    }
}
