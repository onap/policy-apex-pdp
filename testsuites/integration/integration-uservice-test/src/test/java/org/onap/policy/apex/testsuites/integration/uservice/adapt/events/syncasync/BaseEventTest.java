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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.events.syncasync;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;

public class BaseEventTest {
    private static final long TIME_OUT_IN_MS = 10000;

    private void waitForOutFiles(final String[] expectedFileNames, final long expectedFileSize) throws IOException {
        final long totalExpectedSize = expectedFileSize * expectedFileNames.length;

        long startWaitTime = System.currentTimeMillis();
        long lastTotalSize = 0;
        
        do {
            long totalSize = 0;

            for (String expectedFileName : expectedFileNames) {
                totalSize += getEventCount(expectedFileName);
            }

            if (totalSize >= totalExpectedSize) {
                return;
            }
        
            // We're making progress, extend the timeout
            if (totalSize > lastTotalSize) {
                lastTotalSize = totalSize;
                startWaitTime = System.currentTimeMillis();
            }
            
            ThreadUtilities.sleep(100);
        }
        while (TIME_OUT_IN_MS >= System.currentTimeMillis() - startWaitTime);
    }

    private int getEventCount(final String expectedFileName) throws IOException {
        File expectedFile = new File(expectedFileName);
        
        if (!expectedFile.exists()) {
            return 0;
        }

        String expectedFileContents = TextFileUtils.getTextFileAsString(expectedFileName);
        
        return StringUtils.countMatches(expectedFileContents, "{");
    }

    protected void testFileEvents(final String[] args, final String[] expectedFileNames, final long expectedFileSize)
                    throws Exception {
        final ApexMain apexMain = new ApexMain(args);

        waitForOutFiles(expectedFileNames, expectedFileSize);

        apexMain.shutdown();

        for (final String expectedFileName : expectedFileNames) {
            assertEquals(expectedFileSize, getEventCount(expectedFileName));
        }
    }
}
