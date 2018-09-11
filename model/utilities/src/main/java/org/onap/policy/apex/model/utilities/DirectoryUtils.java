/*
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

package org.onap.policy.apex.model.utilities;

import java.io.File;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This is common utility class with static methods for handling directories. It is an abstract class to prevent any
 * direct instantiation and private constructor to prevent extending this class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class DirectoryUtils {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(DirectoryUtils.class);

    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private DirectoryUtils() {
    }

    /**
     * Method to get an empty temporary directory in the system temporary directory on the local machine that will be
     * deleted on (normal) shutdown.
     *
     * @param nameprefix The prefix of the filename. System.nanoTime() will be appended to the pattern to create a
     *        unique file pattern
     * @return The temporary directory
     */
    public static File getLocalTempDirectory(final String nameprefix) {
        try {
            // Get the name of the temporary directory
            final String tempDirName = System.getProperty("java.io.tmpdir") + "/" + nameprefix + System.nanoTime();
            final File tempDir = new File(tempDirName);

            // Delete the directory if it already exists
            if (tempDir.exists()) {
                return null;
            }

            // Make the directory
            tempDir.mkdirs();

            // Add a shutdown hook that deletes the directory contents when the JVM closes
            Runtime.getRuntime().addShutdownHook(new DirectoryDeleteShutdownHook(tempDir));

            LOGGER.trace("creating temp directory\"{}\" : ", tempDir.getAbsolutePath());
            return tempDir;
        } catch (final Exception e) {
            LOGGER.debug("error creating temp directory\"{}\" : " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to recursively delete all the files in a directory.
     *
     * @param tempDir the directory to empty
     * @return true if the operation succeeds, false otherwise
     */
    public static boolean emptyDirectory(final File tempDir) {
        // Sanity check
        if (!tempDir.exists() || !tempDir.isDirectory()) {
            return false;
        }

        // Walk the directory structure deleting files as we go
        final File[] files = tempDir.listFiles();
        if (files != null) {
            for (final File directoryFile : files) {
                // Check if this is a directory itself
                if (directoryFile.isDirectory()) {
                    // Recurse into the sub directory and empty it
                    emptyDirectory(directoryFile);
                }

                // Delete the directory entry
                directoryFile.delete();
            }
        }

        return true;
    }
}
