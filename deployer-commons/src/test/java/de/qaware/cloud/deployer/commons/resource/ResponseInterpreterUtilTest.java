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

import okhttp3.ResponseBody;
import org.junit.Test;
import retrofit2.Response;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author sjahreis
 */
public class ResponseInterpreterUtilTest {

    private ResponseBody body = mock(ResponseBody.class);

    @Test
    public void testIsNotFoundResponse() {
        Response<ResponseBody> response = Response.success(body);
        boolean interpreterResponse = ResponseInterpreterUtil.isNotFoundResponse(response);
        assertFalse(interpreterResponse);

        response = Response.error(401, body);
        interpreterResponse = ResponseInterpreterUtil.isNotFoundResponse(response);
        assertFalse(interpreterResponse);

        response = Response.error(404, body);
        interpreterResponse = ResponseInterpreterUtil.isNotFoundResponse(response);
        assertTrue(interpreterResponse);

        response = Response.error(500, body);
        interpreterResponse = ResponseInterpreterUtil.isNotFoundResponse(response);
        assertFalse(interpreterResponse);
    }

    @Test
    public void testIsSuccessResponse() {
        Response<ResponseBody> response = Response.success(body);
        boolean interpreterResponse = ResponseInterpreterUtil.isSuccessResponse(response);
        assertTrue(interpreterResponse);

        response = Response.error(401, body);
        interpreterResponse = ResponseInterpreterUtil.isSuccessResponse(response);
        assertFalse(interpreterResponse);

        response = Response.error(404, body);
        interpreterResponse = ResponseInterpreterUtil.isSuccessResponse(response);
        assertFalse(interpreterResponse);

        response = Response.error(500, body);
        interpreterResponse = ResponseInterpreterUtil.isSuccessResponse(response);
        assertFalse(interpreterResponse);
    }

    @Test
    public void testIsServerErrorResponse() {
        Response<ResponseBody> response = Response.success(body);
        boolean interpreterResponse = ResponseInterpreterUtil.isServerErrorResponse(response);
        assertFalse(interpreterResponse);

        response = Response.error(401, body);
        interpreterResponse = ResponseInterpreterUtil.isServerErrorResponse(response);
        assertFalse(interpreterResponse);

        response = Response.error(404, body);
        interpreterResponse = ResponseInterpreterUtil.isServerErrorResponse(response);
        assertFalse(interpreterResponse);

        response = Response.error(409, body);
        interpreterResponse = ResponseInterpreterUtil.isServerErrorResponse(response);
        assertTrue(interpreterResponse);

        response = Response.error(500, body);
        interpreterResponse = ResponseInterpreterUtil.isServerErrorResponse(response);
        assertTrue(interpreterResponse);
    }
}
