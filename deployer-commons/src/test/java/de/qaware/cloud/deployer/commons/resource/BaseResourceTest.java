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

import de.qaware.cloud.deployer.commons.config.environment.EnvironmentConfig;
import de.qaware.cloud.deployer.commons.config.resource.BaseResourceConfig;
import de.qaware.cloud.deployer.commons.config.resource.ContentType;
import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static de.qaware.cloud.deployer.commons.logging.CommonsMessageBundle.COMMONS_MESSAGE_BUNDLE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author sjahreis
 */
@SuppressWarnings("unchecked")
public class BaseResourceTest {

    private BaseResource<BaseResourceConfig> baseResource;

    @Before
    public void setup() throws ResourceException {
        String BASE_URL = "http://localhost";

        EnvironmentConfig environmentConfig = new EnvironmentConfig("test", BASE_URL, Strategy.REPLACE);

        ClientFactory clientFactory = new ClientFactory(environmentConfig);

        BaseResourceConfig resourceConfig = new BaseResourceConfig("", ContentType.JSON, "") {
        };

        baseResource = new BaseResource<BaseResourceConfig>(resourceConfig, clientFactory) {
            @Override
            public String toString() {
                return null;
            }

            @Override
            protected MediaType createMediaType() throws ResourceException {
                return null;
            }

            @Override
            public boolean exists() throws ResourceException {
                return false;
            }

            @Override
            public void create() throws ResourceException {
            }

            @Override
            public void delete() throws ResourceException {
            }

            @Override
            public void update() throws ResourceException {
            }
        };
    }

    @Test
    public void testExecuteCall() throws ResourceException, IOException {
        ResponseBody responseBody = mock(ResponseBody.class);
        Response<ResponseBody> response = Response.success(responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        baseResource.executeCall(call);

        verify(call, times(1)).execute();
    }

    @Test
    public void testExecuteFailingCall() throws IOException {
        int errorCode = 404;
        String errorMessage = "NOT FOUND";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> response = Response.error(errorCode, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE", errorCode, errorMessage);
        assertExceptionOnExecuteCall(call, message);

        verify(call, times(1)).execute();
    }

    @Test(expected = ResourceException.class)
    public void testExecuteCallWithIOException() throws ResourceException, IOException {
        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenThrow(IOException.class);
        baseResource.executeCall(call);
    }

    @Test
    public void testExecuteExistsCallPositive() throws IOException, ResourceException {
        ResponseBody responseBody = mock(ResponseBody.class);
        Response<ResponseBody> response = Response.success(responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        boolean exists = baseResource.executeExistsCall(call);

        verify(call, times(1)).execute();
        assertTrue(exists);
    }

    @Test
    public void testExecuteExistsCallNegative() throws IOException, ResourceException {
        ResponseBody responseBody = mock(ResponseBody.class);
        Response<ResponseBody> response = Response.error(404, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        boolean exists = baseResource.executeExistsCall(call);

        verify(call, times(1)).execute();
        assertFalse(exists);
    }

    @Test
    public void testExecuteExistsCallUnhandledCode() throws IOException, ResourceException {
        int errorCode = 401;
        String errorMessage = "NOT AUTHORIZED";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> response = Response.error(errorCode, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE", errorCode, errorMessage);
        assertExceptionOnExecuteExistsCall(call, message);

        verify(call, times(1)).execute();
    }

    @Test
    public void testExecuteExistsCallRetryOnMultipleServerErrors() throws IOException, ResourceException {
        int errorCode = 500;
        String errorMessage = "SERVER ERROR";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> errorResponse = Response.error(errorCode, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(errorResponse);
        when(call.clone()).thenReturn(call);

        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE", errorCode, errorMessage);
        assertExceptionOnExecuteExistsCall(call, message);

        verify(call, times(2)).execute();
    }

    @Test
    public void testExecuteExistsCallRetryOnSingleServerError() throws IOException, ResourceException {
        int errorCode = 500;
        String errorMessage = "SERVER ERROR";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> errorResponse = Response.error(errorCode, responseBody);
        Response<ResponseBody> successResponse = Response.success(responseBody);

        Call<ResponseBody> errorCall = (Call<ResponseBody>) mock(Call.class);
        Call<ResponseBody> successCall = (Call<ResponseBody>) mock(Call.class);
        when(errorCall.execute()).thenReturn(errorResponse);
        when(errorCall.clone()).thenReturn(successCall);
        when(successCall.execute()).thenReturn(successResponse);

        boolean exists = baseResource.executeExistsCall(errorCall);

        verify(errorCall, times(1)).execute();
        verify(successCall, times(1)).execute();
        assertTrue(exists);
    }

    @Test(expected = ResourceException.class)
    public void testExecuteExistsCallWithIOException() throws ResourceException, IOException {
        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenThrow(IOException.class);
        baseResource.executeExistsCall(call);
    }

    @Test(expected = ResourceException.class)
    public void testExecuteExistsCallWithInterruptedException() throws ResourceException, IOException {
        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenThrow(InterruptedException.class);
        baseResource.executeExistsCall(call);
    }

    @Test
    public void testExecuteCreateCallAndBlock() throws ResourceException, IOException {
        ResponseBody responseBody = mock(ResponseBody.class);
        Response<ResponseBody> response = Response.success(responseBody);

        baseResource = spy(baseResource);
        when(baseResource.exists()).thenReturn(true);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        baseResource.executeCreateCallAndBlock(call);

        verify(call, times(1)).execute();
        verify(baseResource, times(1)).exists();
    }

    @Test
    public void testExecuteCreateCallAndBlockWithBlocking() throws IOException, ResourceException {
        ResponseBody responseBody = mock(ResponseBody.class);
        Response<ResponseBody> response = Response.success(responseBody);

        final int[] existCallsCounter = {0};
        baseResource = spy(baseResource);
        doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                existCallsCounter[0] += 1;
                return existCallsCounter[0] > 3;
            }
        }).when(baseResource).exists();

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        baseResource.executeCreateCallAndBlock(call);

        verify(call, times(1)).execute();
        verify(baseResource, times(4)).exists();
    }

    @Test
    public void testExecuteCreateCallAndBlockUnhandledCode() throws IOException, ResourceException {
        int errorCode = 401;
        String errorMessage = "NOT AUTHORIZED";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> response = Response.error(errorCode, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE", errorCode, errorMessage);
        assertExceptionOnExecuteCreateCallAndBlock(call, message);

        verify(call, times(1)).execute();
    }

    @Test
    public void testExecuteCreateCallAndBlockRetryOnSingleServerError() throws ResourceException, IOException {
        int errorCode = 500;
        String errorMessage = "SERVER ERROR";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> errorResponse = Response.error(errorCode, responseBody);
        Response<ResponseBody> successResponse = Response.success(responseBody);

        Call<ResponseBody> errorCall = (Call<ResponseBody>) mock(Call.class);
        Call<ResponseBody> successCall = (Call<ResponseBody>) mock(Call.class);
        when(errorCall.execute()).thenReturn(errorResponse);
        when(errorCall.clone()).thenReturn(successCall);
        when(successCall.execute()).thenReturn(successResponse);

        baseResource = spy(baseResource);
        when(baseResource.exists()).thenReturn(true);

        baseResource.executeCreateCallAndBlock(errorCall);

        verify(errorCall, times(1)).execute();
        verify(successCall, times(1)).execute();
        verify(baseResource, times(1)).exists();
    }

    @Test
    public void testExecuteCreateCallAndBlockRetryOnMultipleServerErrors() throws IOException, ResourceException {
        int errorCode = 500;
        String errorMessage = "SERVER ERROR";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> errorResponse = Response.error(errorCode, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(errorResponse);
        when(call.clone()).thenReturn(call);

        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE", errorCode, errorMessage);
        assertExceptionOnExecuteCreateCallAndBlock(call, message);

        verify(call, times(2)).execute();
    }

    @Test(expected = ResourceException.class)
    public void testExecuteCreateCallAndBlockWithIOException() throws ResourceException, IOException {
        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenThrow(IOException.class);
        baseResource.executeCreateCallAndBlock(call);
    }

    @Test(expected = ResourceException.class)
    public void testExecuteCreateCallAndBlockWithInterruptedException() throws ResourceException, IOException {
        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenThrow(InterruptedException.class);
        baseResource.executeCreateCallAndBlock(call);
    }

    @Test
    public void testExecuteDeleteCallAndBlock() throws ResourceException, IOException {
        ResponseBody responseBody = mock(ResponseBody.class);
        Response<ResponseBody> response = Response.success(responseBody);

        baseResource = spy(baseResource);
        when(baseResource.exists()).thenReturn(false);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        baseResource.executeDeleteCallAndBlock(call);

        verify(call, times(1)).execute();
        verify(baseResource, times(1)).exists();
    }

    @Test
    public void testExecuteDeleteCallAndBlockWithBlocking() throws IOException, ResourceException {
        ResponseBody responseBody = mock(ResponseBody.class);
        Response<ResponseBody> response = Response.success(responseBody);

        final int[] existCallsCounter = {0};
        baseResource = spy(baseResource);
        doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                existCallsCounter[0] += 1;
                return existCallsCounter[0] < 4;
            }
        }).when(baseResource).exists();

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        baseResource.executeDeleteCallAndBlock(call);

        verify(call, times(1)).execute();
        verify(baseResource, times(4)).exists();
    }

    @Test
    public void testExecuteDeleteCallAndBlockUnhandledCode() throws IOException, ResourceException {
        int errorCode = 401;
        String errorMessage = "NOT AUTHORIZED";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> response = Response.error(errorCode, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(response);

        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE", errorCode, errorMessage);
        assertExceptionOnExecuteDeleteCallAndBlock(call, message);

        verify(call, times(1)).execute();
    }

    @Test
    public void testExecuteDeleteCallAndBlockRetryOnSingleServerError() throws ResourceException, IOException {
        int errorCode = 500;
        String errorMessage = "SERVER ERROR";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> errorResponse = Response.error(errorCode, responseBody);
        Response<ResponseBody> successResponse = Response.success(responseBody);

        Call<ResponseBody> errorCall = (Call<ResponseBody>) mock(Call.class);
        Call<ResponseBody> successCall = (Call<ResponseBody>) mock(Call.class);
        when(errorCall.execute()).thenReturn(errorResponse);
        when(errorCall.clone()).thenReturn(successCall);
        when(successCall.execute()).thenReturn(successResponse);

        baseResource = spy(baseResource);
        when(baseResource.exists()).thenReturn(false);

        baseResource.executeDeleteCallAndBlock(errorCall);

        verify(errorCall, times(1)).execute();
        verify(successCall, times(1)).execute();
        verify(baseResource, times(1)).exists();
    }

    @Test
    public void testExecuteDeleteCallAndBlockRetryOnMultipleServerErrors() throws IOException, ResourceException {
        int errorCode = 500;
        String errorMessage = "SERVER ERROR";

        ResponseBody responseBody = RealResponseBody.create(MediaType.parse("text"), errorMessage);
        Response<ResponseBody> errorResponse = Response.error(errorCode, responseBody);

        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenReturn(errorResponse);
        when(call.clone()).thenReturn(call);

        String message = COMMONS_MESSAGE_BUNDLE.getMessage("DEPLOYER_COMMONS_ERROR_UNHANDLED_HTTP_STATUS_CODE", errorCode, errorMessage);
        assertExceptionOnExecuteDeleteCallAndBlock(call, message);

        verify(call, times(2)).execute();
    }

    @Test(expected = ResourceException.class)
    public void testExecuteDeleteCallAndBlockWithIOException() throws ResourceException, IOException {
        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenThrow(IOException.class);
        baseResource.executeDeleteCallAndBlock(call);
    }

    @Test(expected = ResourceException.class)
    public void testExecuteDeleteCallAndBlockWithInterruptedException() throws ResourceException, IOException {
        Call<ResponseBody> call = (Call<ResponseBody>) mock(Call.class);
        when(call.execute()).thenThrow(InterruptedException.class);
        baseResource.executeDeleteCallAndBlock(call);
    }

    private void assertExceptionOnExecuteCall(Call call, String message) {
        boolean exceptionThrown = false;
        try {
            baseResource.executeCall(call);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void assertExceptionOnExecuteExistsCall(Call call, String message) {
        boolean exceptionThrown = false;
        try {
            baseResource.executeExistsCall(call);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void assertExceptionOnExecuteCreateCallAndBlock(Call call, String message) {
        boolean exceptionThrown = false;
        try {
            baseResource.executeCreateCallAndBlock(call);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    private void assertExceptionOnExecuteDeleteCallAndBlock(Call call, String message) {
        boolean exceptionThrown = false;
        try {
            baseResource.executeDeleteCallAndBlock(call);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(message, e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
