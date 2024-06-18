/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021, 2024 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * The Class TestFile2FileIgnore.
 */
public class TestFile2FileIgnore {
    // This test is used just to bring up an instance of Apex for manual testing and demonstrations
    // It should always be ignored in automated testing because it holds Apex up for a very long
    // time

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(final String[] args) throws ApexException, IOException {
        final String[] apexArgs = {"-rfr", "target", "-c", "examples/config/SampleDomain/File2FileJsonEvent.json"};

        testFileEvents(apexArgs);
    }

    /**
     * Test file events.
     *
     * @param args the args
     * @throws ApexException the apex exception
     * @throws IOException   Signals that an I/O exception has occurred.
     */
    private static void testFileEvents(final String[] args)
        throws ApexException, IOException {
        final ApexMain apexMain = new ApexMain(args);

        final File outFile = new File("target/EventsOut.json");

        while (!outFile.exists()) {
            ThreadUtilities.sleep(500);
        }

        // Wait for the file to be filled
        long outFileSize;
        while (true) {
            final String fileString = stripVariableLengthText();
            outFileSize = fileString.length();
            if (outFileSize > 0 && outFileSize >= (long) 48656) {
                break;
            }
            ThreadUtilities.sleep(500);
        }

        // Here's the long time I was talking about above!
        ThreadUtilities.sleep(100000000);

        apexMain.shutdown();
        assertTrue(outFile.delete());
        assertEquals(48656, outFileSize);
    }

    /**
     * Strip variable length text from file string.
     *
     * @return the stripped string
     * @throws IOException on out file read exceptions
     */
    private static String stripVariableLengthText() throws IOException {
        return TextFileUtils.getTextFileAsString("target/EventsOut.json")
            .replaceAll("\\s+", "").replaceAll(":\\d*\\.?\\d*,", ":0,")
            .replaceAll(":\\d*}", ":0}").replaceAll("<value>\\d*\\.?\\d*</value>", "<value>0</value>");
    }
}
