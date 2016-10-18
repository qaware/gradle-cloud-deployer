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
package de.qaware.cloud.deployer.plugin;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.utils.MarathonException;
import mesosphere.marathon.client.utils.ModelUtils;

class AuthorizedMarathonClient {

    static Marathon createInstance(String endpoint, String token) {
        GsonDecoder decoder = new GsonDecoder(ModelUtils.GSON);
        GsonEncoder encoder = new GsonEncoder(ModelUtils.GSON);
        return Feign.builder().encoder(encoder).decoder(decoder).errorDecoder(new MarathonErrorDecoder()).requestInterceptor(new MarathonHeadersInterceptor(token)).target(Marathon.class, endpoint);
    }

    private static class MarathonErrorDecoder implements ErrorDecoder {
        MarathonErrorDecoder() {
        }

        public Exception decode(String methodKey, Response response) {
            return new MarathonException(response.status(), response.reason());
        }
    }

    private static class MarathonHeadersInterceptor implements RequestInterceptor {

        private final String token;

        MarathonHeadersInterceptor(String token) {
            this.token = token;
        }

        public void apply(RequestTemplate template) {
            template.header("Accept", "application/json");
            template.header("Content-Type", "application/json");
            template.header("Authorization", "token=" + token);
        }
    }
}
