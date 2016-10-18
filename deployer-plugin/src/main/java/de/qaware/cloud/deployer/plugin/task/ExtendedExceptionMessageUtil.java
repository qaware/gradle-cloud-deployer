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
package de.qaware.cloud.deployer.plugin.task;

import de.qaware.cloud.deployer.plugin.environment.Environment;

import static de.qaware.cloud.deployer.plugin.logging.PluginMessageBundle.PLUGIN_MESSAGE_BUNDLE;

/**
 * Creates extended exception messages.
 *
 * @author sjahreis
 */
final class ExtendedExceptionMessageUtil {

    /**
     * UTIL
     */
    private ExtendedExceptionMessageUtil() {
    }

    /**
     * Creates a new exception message using the old message and appending the id of the environment the exception occurred.
     * @param environment The environment the exception occurred in.
     * @param message The original exception message.
     * @return The new exception message.
     */
    static String createExtendedMessage(Environment environment, String message) {
        if (isMultiLine(message)) {
            return message + "\n" + PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_SINGLE_ENVIRONMENT", environment.getId());
        } else {
            return message + " " + PLUGIN_MESSAGE_BUNDLE.getMessage("DEPLOYER_PLUGIN_ERROR_SINGLE_ENVIRONMENT", environment.getId());
        }
    }

    private static boolean isMultiLine(String string) {
        return string.contains("\n");
    }
}
