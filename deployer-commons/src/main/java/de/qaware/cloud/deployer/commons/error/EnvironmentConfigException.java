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
package de.qaware.cloud.deployer.commons.error;

/**
 * Represents a exception which is thrown because of configuration errors.
 */
public class EnvironmentConfigException extends Exception {

    /**
     * Creates a new environment config exception.
     *
     * @param message The message which explains the exception.
     */
    public EnvironmentConfigException(String message) {
        super(message);
    }

    /**
     * Creates a new environment config exception.
     *
     * @param message The message which explains the exception.
     * @param cause The cause of the exception.
     */
    public EnvironmentConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
