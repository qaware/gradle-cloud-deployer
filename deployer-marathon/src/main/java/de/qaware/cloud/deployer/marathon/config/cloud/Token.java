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
package de.qaware.cloud.deployer.marathon.config.cloud;

/**
 * A pojo for a marathon token request.
 */
public class Token {

    /**
     * The token which will be sent.
     */
    private String token;

    /**
     * Creates a new token.
     */
    public Token() {
    }

    /**
     * Creates a new token.
     *
     * @param token The token string.
     */
    public Token(String token) {
        this.token = token;
    }

    /**
     * Returns the token.
     *
     * @return The token string.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the specified token.
     *
     * @param token The token string.
     */
    public void setToken(String token) {
        this.token = token;
    }
}
