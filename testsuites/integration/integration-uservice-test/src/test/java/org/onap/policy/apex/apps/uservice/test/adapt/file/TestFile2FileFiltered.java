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

package org.onap.policy.apex.apps.uservice.test.adapt.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;

public class TestFile2FileFiltered {

    @Test
    public void testJsonFilteredFileInOutEvents() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/File2FileFilteredInOutJsonEvent.json"};

        final String[] outFilePaths =
                {"src/test/resources/events/Events0004Out.json", "src/test/resources/events/Events0104Out.json"};

        final long[] expectedFileSizes = {25949, 23007};

        testFilteredFileEvents(args, outFilePaths, expectedFileSizes);
    }

    @Test
    public void testJsonFilteredFileOutEvents() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/File2FileFilteredOutJsonEvent.json"};

        final String[] outFilePaths =
                {"src/test/resources/events/Events0004Out.json", "src/test/resources/events/Events0104Out.json"};

        final long[] expectedFileSizes = {25949, 23007};

        testFilteredFileEvents(args, outFilePaths, expectedFileSizes);
    }

    @Test
    public void testJsonFilteredFileInEvents() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/File2FileFilteredInJsonEvent.json"};

        final String[] outFilePaths = {"src/test/resources/events/Events0004Out.json"};

        final long[] expectedFileSizes = {25949};

        testFilteredFileEvents(args, outFilePaths, expectedFileSizes);
    }

    private void testFilteredFileEvents(final String[] args, final String[] outFilePaths,
            final long[] expectedFileSizes) throws MessagingException, ApexException, IOException {
        final ApexMain apexMain = new ApexMain(args);

        final File outFile0 = new File(outFilePaths[0]);

        while (!outFile0.exists()) {
            ThreadUtilities.sleep(500);
        }

        // Wait for the file to be filled
        long outFile0Size = 0;
        for (int i = 0; i < 4; i++) {
            final String fileString = TextFileUtils.getTextFileAsString(outFilePaths[0]).replaceAll("\\s+", "");
            outFile0Size = fileString.length();
            if (outFile0Size > 0 && outFile0Size >= expectedFileSizes[0]) {
                break;
            }
            ThreadUtilities.sleep(500);
        }

        ThreadUtilities.sleep(500);
        apexMain.shutdown();

        final long[] actualFileSizes = new long[expectedFileSizes.length];

        for (int i = 0; i < outFilePaths.length; i++) {
            final String fileString = TextFileUtils.getTextFileAsString(outFilePaths[i]).replaceAll("\\s+", "");
            actualFileSizes[i] = fileString.length();
            new File(outFilePaths[i]).delete();
        }

        for (int i = 0; i < actualFileSizes.length; i++) {
            assertEquals(actualFileSizes[i], expectedFileSizes[i]);
        }
    }
}
