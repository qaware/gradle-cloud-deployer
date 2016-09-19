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
package de.qaware.cloud.deployer.marathon.update;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.update.UpdateStrategy;
import junit.framework.TestCase;

import static de.qaware.cloud.deployer.marathon.logging.MarathonMessageBundle.MARATHON_MESSAGE_BUNDLE;

public class MarathonUpdateStrategyFactoryTest extends TestCase {

    public void testCreateWithSoftUpdateStrategy() throws ResourceException {
        MarathonUpdateStrategy soft = MarathonUpdateStrategyFactory.create(UpdateStrategy.REPLACE);
        assertTrue(soft instanceof MarathonReplaceUpdateStrategy);
    }

    public void testCreateWithUnknownUpdateStrategy() {
        boolean exceptionThrown = false;
        try {
            MarathonUpdateStrategyFactory.create(UpdateStrategy.RESET);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_ERROR_UNSUPPORTED_UPDATE_STRATEGY", "RESET"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
