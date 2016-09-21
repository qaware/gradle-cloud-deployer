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
import junit.framework.TestCase;

import static de.qaware.cloud.deployer.kubernetes.logging.KubernetesMessageBundle.KUBERNETES_MESSAGE_BUNDLE;

public class KubernetesStrategyFactoryTest extends TestCase {

    public void testCreateWithResetStrategy() throws ResourceException {
        KubernetesStrategy resetStrategy = KubernetesStrategyFactory.create(Strategy.RESET);
        assertTrue(resetStrategy instanceof KubernetesResetStrategy);
    }

    public void testCreateWithReplaceStrategy() throws ResourceException {
        KubernetesStrategy replaceStrategy = KubernetesStrategyFactory.create(Strategy.REPLACE);
        assertTrue(replaceStrategy instanceof KubernetesReplaceStrategy);
    }

    public void testCreateWithUnknownStrategy() {
        boolean exceptionThrown = false;
        try {
            KubernetesStrategyFactory.create(Strategy.UPDATE);
        } catch (ResourceException e) {
            exceptionThrown = true;
            assertEquals(KUBERNETES_MESSAGE_BUNDLE.getMessage("DEPLOYER_KUBERNETES_ERROR_UNSUPPORTED_STRATEGY", "UPDATE"), e.getMessage());
        }
        assertTrue(exceptionThrown);
    }
}
