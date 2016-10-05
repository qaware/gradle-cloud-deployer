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

/**
 * Blocks for specified amount of time and throws an exception if the maximum duration is exceeded. It's block method is
 * intended to be called in a loop.
 */
class Blocker {

    private final int timeout;
    private final int blockTime;
    private final String errorMessage;
    private int timeoutCounter = 0;

    /**
     * Creates a new blocker using the specified parameters.
     *
     * @param timeout      The maximum duration in seconds until an exception is thrown.
     * @param blockTime     The time in seconds this blocker blocks when the block function is called.
     * @param errorMessage The error message which will be used in the exception if the maximum duration is exceeded.
     */
    Blocker(double timeout, double blockTime, String errorMessage) {
        this.timeout = (int) timeout * 1000;
        this.blockTime = (int) (blockTime * 1000);
        this.errorMessage = errorMessage;
    }

    /**
     * Blocks the execution for a amount of time.
     *
     * @throws ResourceException If the maximum duration is exceeded.
     */
    void block() throws ResourceException {
        try {
            timeoutCounter += blockTime;
            if (timeoutCounter > timeout) {
                throw new ResourceException(errorMessage);
            }
            Thread.sleep(blockTime);
        } catch (InterruptedException e) {
            throw new ResourceException(e);
        }
    }
}
