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

/**
 * Factory which returns a marathon update strategy.
 */
public class MarathonUpdateStrategyFactory {

    /**
     * UTILITY.
     */
    public MarathonUpdateStrategyFactory() {
    }

    /**
     * Accepts a string representation of an update strategy and instantiates a new object of this strategy.
     *
     * @param updateStrategy The string representation of the update strategy.
     * @return A new object of the specified strategy.
     * @throws ResourceException If the string specifies a not existing update strategy.
     */
    public static MarathonUpdateStrategy create(String updateStrategy) throws ResourceException {
        switch (updateStrategy) {
            case "SOFT":
                return new MarathonSoftUpdateStrategy();
            default:
                throw new ResourceException("Unknown strategy type specified");
        }
    }
}
