/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2022 Bell Canada. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.utils.resources.TextFileUtils;

class TestFile2File {
    /**
     * Clear relative file root environment variable.
     */
    @BeforeEach
    void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    @Test
    void testJsonFileEvents() throws ApexException, IOException {
        final String[] args = {"-rfr", "target", "-p", "target/examples/config/SampleDomain/File2FileJsonEvent.json"};

        testFileEvents(args);
    }

    private void testFileEvents(final String[] args) throws ApexException, IOException {
        final ApexMain apexMain = new ApexMain(args);

        final File outFile = new File("target/examples/events/SampleDomain/EventsOut.json");

        while (!outFile.exists()) {
            ThreadUtilities.sleep(500);
        }

        // Wait for the file to be filled
        long outFileSize;
        while (true) {
            final String fileString = stripVariableLengthText();
            outFileSize = fileString.length();
            if (outFileSize > 0 && outFileSize >= (long) 44400) {
                break;
            }
            ThreadUtilities.sleep(500);
        }

        apexMain.shutdown();
        assertEquals(44400, outFileSize);
    }

    /**
     * Strip variable length text from file string.
     *
     * @return the stripped string
     * @throws IOException on out file read exceptions
     */
    private String stripVariableLengthText() throws IOException {
        return TextFileUtils.getTextFileAsString("target/examples/events/SampleDomain/EventsOut.json")
            .replaceAll("\\s+", "")
            .replaceAll(":\\d*\\.?\\d*,", ":0,")
            .replaceAll(":\\d*}", ":0}")
            .replaceAll("<value>\\d*\\.?\\d*</value>", "<value>0</value>");
    }
}