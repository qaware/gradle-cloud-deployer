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
package de.qaware.cloud.deployer.marathon.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;

import static de.qaware.cloud.deployer.marathon.logging.MarathonMessageBundle.MARATHON_MESSAGE_BUNDLE;

/**
 * Factory which returns a marathon strategy.
 */
public final class MarathonStrategyFactory {

    /**
     * UTILITY.
     */
    private MarathonStrategyFactory() {
    }

    /**
     * Accepts an strategy and instantiates a new object of this strategy.
     *
     * @param strategy The string representation of the strategy.
     * @return A new object of the specified strategy.
     * @throws ResourceException If the string specifies a not existing strategy.
     */
    public static MarathonStrategy create(Strategy strategy) throws ResourceException {
        switch (strategy) {
            case REPLACE:
                return new MarathonReplaceStrategy();
            default:
                throw new ResourceException(MARATHON_MESSAGE_BUNDLE.getMessage("DEPLOYER_MARATHON_ERROR_UNSUPPORTED_STRATEGY", strategy));
        }
    }
}
