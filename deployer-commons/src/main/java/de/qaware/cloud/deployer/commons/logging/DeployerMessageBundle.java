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
package de.qaware.cloud.deployer.commons.logging;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * A wrapper around resource bundle which adds more functionality.
 */
public class DeployerMessageBundle {

    /**
     * A bundle which contains all messageBundle.
     */
    private ResourceBundle messageBundle;

    /**
     * Creates a new deployer message bundle.
     *
     * @param bundleName The name of the bundle which contains the messageBundle.
     */
    public DeployerMessageBundle(String bundleName) {
        this.messageBundle = ResourceBundle.getBundle(bundleName);
    }

    /**
     * Returns the log message with the specified id.
     *
     * @param id The id of the log message to load.
     * @return The log message.
     */
    public String getMessage(String id) {
        return messageBundle.getString(id);
    }

    /**
     * Returns the log message with the specified id and additionally replaces a placeholder within the string with the
     * specified value.
     *
     * @param id    The id of the log message.
     * @param value The value of the placeholder.
     * @return The log message.
     */
    public String getMessage(String id, String value) {
        String originalMessage = messageBundle.getString(id);
        return MessageFormat.format(originalMessage, value);
    }


    /**
     * Returns the log message with the specified id and additionally replaces a placeholder within the string with the
     * specified value.
     *
     * @param id    The id of the log message.
     * @param value The value of the placeholder.
     * @return The log message.
     */
    public String getMessage(String id, Object value) {
        String originalMessage = messageBundle.getString(id);
        return MessageFormat.format(originalMessage, value.toString());
    }
}
