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
package de.qaware.cloud.deployer.kubernetes.strategy;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.strategy.Strategy;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * Factory which returns a kubernetes strategy.
 */
public final class KubernetesStrategyFactory {

    /**
     * UTILITY.
     */
    private KubernetesStrategyFactory() {
    }

    /**
     * Accepts an strategy and instantiates a new object of this strategy.
     *
     * @param strategy The strategy.
     * @return A new object of the specified strategy.
     * @throws ResourceException If the string specifies a not existing strategy.
     */
    public static KubernetesStrategy create(Strategy strategy) throws ResourceException {

        // Check if the strategy is defined
        if (strategy == null) {
            throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNSUPPORTED_STRATEGY", "null"));
        }

        switch (strategy) {
            case RESET:
                return new KubernetesResetStrategy();
            case REPLACE:
                return new KubernetesReplaceStrategy();
            case UPDATE:
                return new KubernetesUpdateStrategy();
            default:
                throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNSUPPORTED_STRATEGY", strategy));
        }
    }
}
