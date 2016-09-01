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

public class CallBlocker {

    private final int timeout;
    private final int interval;
    private final String errorMessage;
    private int timeoutCounter = 0;

    public CallBlocker(int timeout, double interval, String errorMessage) {
        this.timeout = timeout * 1000;
        this.interval = (int) (interval * 1000);
        this.errorMessage = errorMessage;
    }

    public void block() throws ResourceException {
        try {
            timeoutCounter += interval;
            if (timeoutCounter > timeout) {
                throw new ResourceException(errorMessage);
            }
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            throw new ResourceException(e);
        }
    }
}
