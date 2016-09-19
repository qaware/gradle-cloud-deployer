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
package de.qaware.cloud.deployer.kubernetes.update;

import de.qaware.cloud.deployer.commons.error.ResourceException;
import de.qaware.cloud.deployer.commons.update.UpdateStrategy;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

/**
 * Factory which returns a kubernetes update strategy.
 */
public final class KubernetesUpdateStrategyFactory {

    /**
     * UTILITY.
     */
    private KubernetesUpdateStrategyFactory() {
    }

    /**
     * Accepts an update strategy and instantiates a new object of this strategy.
     *
     * @param updateStrategy The update strategy.
     * @return A new object of the specified strategy.
     * @throws ResourceException If the string specifies a not existing update strategy.
     */
    public static KubernetesUpdateStrategy create(UpdateStrategy updateStrategy) throws ResourceException {
        switch (updateStrategy) {
            case RESET:
                return new KubernetesResetUpdateStrategy();
            case REPLACE:
                return new KubernetesReplaceUpdateStrategy();
            default:
                throw new ResourceException(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNSUPPORTED_UPDATE_STRATEGY", updateStrategy));
        }
    }
}
