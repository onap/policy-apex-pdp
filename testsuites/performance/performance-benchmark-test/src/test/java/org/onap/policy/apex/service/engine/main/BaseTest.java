/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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
package org.onap.policy.apex.service.engine.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.utilities.TextFileUtils;

public class BaseTest {
    protected final static long TIME_OUT_IN_MS = TimeUnit.SECONDS.toMillis(10);

    protected long getFileLength(final String file, final long expectedFileSize) throws IOException {
        return getFileLength(10, file, expectedFileSize);
    }

    protected long getFileLength(final int loopTime, final String file, final long expectedFileSize)
            throws IOException {
        long length = 0;
        for (int i = 0; i < loopTime; i++) {
            final String fileString = TextFileUtils.getTextFileAsString(file).replaceAll("\\s+", "");
            length = fileString.length();
            if (length > 0 && length >= expectedFileSize) {
                break;
            }
            ThreadUtilities.sleep(500);
        }
        return length;
    }

    protected void waitForOutFiles(final File... files) {
        final long timeInMillis = System.currentTimeMillis();
        while (!isFilesExist(files)) {
            if (System.currentTimeMillis() - timeInMillis > TIME_OUT_IN_MS) {
                break;
            }
            ThreadUtilities.sleep(500);
        }

    }

    private void assertFilesExists(final File... files) {
        for (final File file : files) {
            assertTrue("Test failed, the output " + file + "event file was not created", file.exists());
        }
    }

    private void deleteFiles(final File... files) throws IOException {
        for (final File file : files) {
            Files.deleteIfExists(file.toPath());
        }
    }

    private boolean isFilesExist(final File[] files) {
        for (final File file : files) {
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    protected File[] toFile(final String[] filesPath) {
        if (filesPath == null || filesPath.length == 0) {
            return new File[] {};
        }
        final File[] files = new File[filesPath.length];

        for (int index = 0; index < filesPath.length; index++) {
            files[index] = new File(filesPath[index]);
        }

        return files;
    }

    protected void testFileEvents(final String[] args, final String[] expectedFilesPath, final long expectedFileSize)
            throws Exception {
        final ApexMain apexMain = new ApexMain(args);

        final File[] expectedFiles = toFile(expectedFilesPath);
        try {

            waitForOutFiles(expectedFiles);
            assertFilesExists(expectedFiles);

            for (final String filePath : expectedFilesPath) {
                assertEquals(expectedFileSize, getFileLength(filePath, expectedFileSize));
            }

            apexMain.shutdown();

        } finally {
            deleteFiles(expectedFiles);
        }
    }

}
