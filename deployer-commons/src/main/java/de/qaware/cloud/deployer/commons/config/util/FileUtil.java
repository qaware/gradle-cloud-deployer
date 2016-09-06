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
package de.qaware.cloud.deployer.commons.config.util;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Offers functionality to read the content of a file into a string.
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * Reads the content of the file with the specified filename into a string.
     *
     * @param filename The name of the file whose content will be returned.
     * @return The content of the file.
     * @throws ResourceConfigException If a problem with the file occurs.
     */
    public static String readFileContent(String filename) throws ResourceConfigException {
        return readFileContent(new File(FileUtil.class.getResource(filename).getPath()));
    }

    /**
     * Reads the content of the specified file into a string.
     *
     * @param file The file whose content will be returned.
     * @return The content of the file.
     * @throws ResourceConfigException If a problem with the file occurs.
     */
    public static String readFileContent(File file) throws ResourceConfigException {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset()).trim();
        } catch (IOException e) {
            throw new ResourceConfigException(e.getMessage(), e);
        }
    }
}