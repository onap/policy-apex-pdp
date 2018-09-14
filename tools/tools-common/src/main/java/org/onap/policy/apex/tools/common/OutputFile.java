/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.tools.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard output file handling and tests.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class OutputFile {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputFile.class);

    /** The output file name. */
    private final String fileName;

    /** The output file name. */
    private final boolean overwrite;

    /**
     * Creates a new object for a given file name.
     *
     * @param fileName the file name
     */
    public OutputFile(final String fileName) {
        this(fileName, false);
    }

    /**
     * Creates a new object for a given file name.
     *
     * @param fileName the file name
     * @param overwrite if the file already exists, can it be overwritten, or should an error be raised
     */
    public OutputFile(final String fileName, final boolean overwrite) {
        Validate.notBlank(fileName);
        this.fileName = fileName;
        this.overwrite = overwrite;
    }

    /**
     * Get a File object for this output file.
     *
     * @return a File object for this output file
     */
    public File toFile() {
        final Path fp = FileSystems.getDefault().getPath(fileName);
        return fp.toFile();
    }

    /**
     * Get a Writer object for this output file.
     *
     * @return a Writer object for this output file
     */
    public Writer toWriter() {
        try {
            return new BufferedWriter(new FileWriter(toFile()));
        } catch (final IOException e) {
            LOGGER.warn("write error", e);
            return null;
        }
    }

    /**
     * Get a OutputStream object for this output file.
     *
     * @return an OutputStream object for this output file
     */
    public OutputStream toOutputStream() {
        try {
            return new FileOutputStream(toFile());
        } catch (final IOException e) {
            LOGGER.warn("stream creation error", e);
            return null;
        }
    }

    /**
     * Validates the output file. Validation tests for file name being blank, file existing, creation, and finally
     * can-write.
     *
     * @return null on success, an error message on error
     */
    public String validate() {
        if (StringUtils.isBlank(fileName)) {
            return "file name was blank";
        }

        final File file = toFile();
        if (file.exists()) {
            if (!overwrite) {
                return "file already exists";
            }
        } else {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                String message = "could not create output file: " + e.getMessage();
                LOGGER.warn(message, e);
                return message;
            }
        }

        if (!file.canWrite()) {
            return "cannot write to file";
        }

        return null;
    }
}
