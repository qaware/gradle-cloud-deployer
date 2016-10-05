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
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author sjahreis
 */
public class BlockerTest {

    private static final double TIMEOUT = 5;
    private static final double BLOCK_TIME = 0.5;
    private static final String MESSAGE = "put your error here!";

    @Test
    public void testBlockerExceeding() {
        Blocker blocker = new Blocker(TIMEOUT, BLOCK_TIME, MESSAGE);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int callCounter = 0;
        while (true) {
            try {
                blocker.block();
                callCounter++;
            } catch (ResourceException e) {
                stopWatch.stop();
                break;
            }
        }

        assertEquals(TIMEOUT / BLOCK_TIME, callCounter, 0.1);
        assertEquals(TIMEOUT, stopWatch.getTime() / 1000, 0.1);
    }

    @Test
    public void testBlocker() throws ResourceException {
        Blocker blocker = new Blocker(TIMEOUT, BLOCK_TIME, MESSAGE);

        int callCounter = 0;
        for (int i = 1; i < TIMEOUT / BLOCK_TIME; i++) {
            blocker.block();
            callCounter++;
        }

        assertEquals((TIMEOUT / BLOCK_TIME) - BLOCK_TIME, callCounter, 0.5);
    }
}
