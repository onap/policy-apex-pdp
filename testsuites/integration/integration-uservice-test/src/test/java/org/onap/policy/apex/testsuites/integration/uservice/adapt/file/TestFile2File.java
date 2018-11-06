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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;

public class TestFile2File {
    /**
     * Clear relative file root environment variable.
     */
    @Before
    public void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    @Test
    public void testJsonFileEvents() throws MessagingException, ApexException, IOException {
        final String[] args = {"-rfr", "target", "-c", "target/examples/config/SampleDomain/File2FileJsonEvent.json"};

        testFileEvents(args, "target/examples/events/SampleDomain/EventsOut.json", 42200);
    }

    @Test
    public void testXmlFileEvents() throws MessagingException, ApexException, IOException {
        final String[] args = {"-rfr", "target", "-c", "target/examples/config/SampleDomain/File2FileXmlEvent.json"};

        testFileEvents(args, "target/examples/events/SampleDomain/EventsOut.xmlfile", 100000);
    }

    private void testFileEvents(final String[] args, final String outFilePath, final long expectedFileSize)
            throws MessagingException, ApexException, IOException {
        final ApexMain apexMain = new ApexMain(args);

        final File outFile = new File(outFilePath);

        while (!outFile.exists()) {
            ThreadUtilities.sleep(500);
        }

        // Wait for the file to be filled
        long outFileSize = 0;
        while (true) {
            final String fileString = stripVariableLengthText(outFilePath);
            outFileSize = fileString.length();
            if (outFileSize > 0 && outFileSize >= expectedFileSize) {
                break;
            }
            ThreadUtilities.sleep(500);
        }

        apexMain.shutdown();
        assertEquals(expectedFileSize, outFileSize);
    }

    /**
     * Strip variable length text from file string.
     * 
     * @param textFileAsString the file to read and strip
     * @return the stripped string
     * @throws IOException on out file read exceptions
     */
    private String stripVariableLengthText(final String outFile) throws IOException {
        return TextFileUtils.getTextFileAsString(outFile)
                        .replaceAll("\\s+", "")
                        .replaceAll(":\\d*\\.?\\d*,", ":0,")
                        .replaceAll(":\\d*}", ":0}")
                        .replaceAll("<value>\\d*\\.?\\d*</value>", "<value>0</value>");
    }
}


