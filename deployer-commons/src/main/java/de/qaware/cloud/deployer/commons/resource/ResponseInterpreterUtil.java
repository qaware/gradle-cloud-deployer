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
import retrofit2.Response;

/**
 * Helps interpreting retrofit responses.
 */
public final class ResponseInterpreterUtil {

    /**
     * UTILITY.
     */
    private ResponseInterpreterUtil() {
    }

    /**
     * Checks if the specified response is a not found response.
     *
     * @param response The response which will be checked.
     * @return TRUE if the response is a not found response, FALSE otherwise.
     */
    public static boolean isNotFoundResponse(Response<ResponseBody> response) {
        return response.code() == 404;
    }

    /**
     * Checks if the specified response is a success response.
     *
     * @param response The response which will be checked.
     * @return TRUE if the response is a success response, FALSE otherwise.
     */
    public static boolean isSuccessResponse(Response<ResponseBody> response) {
        return response.code() == 200 || response.code() == 201;
    }

    /**
     * Indicates whether the server threw an error.
     *
     * @param response The response which contains the response code.
     * @return TRUE if the server threw an error, FALSE otherwise.
     */
    public static boolean isServerErrorResponse(Response<ResponseBody> response) {
        return response.code() == 409 || response.code() == 500;
    }
}
