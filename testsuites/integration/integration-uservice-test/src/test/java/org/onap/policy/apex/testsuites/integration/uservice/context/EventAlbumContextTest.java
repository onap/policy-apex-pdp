/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.uservice.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.common.utils.resources.TextFileUtils;

class EventAlbumContextTest {
    private File tempCommandFile;
    private File tempLogFile;
    private File tempPolicyFile;
    private String eventContextString;
    private String configFile;
    private String outputFile;
    private String compareFile;

    @TempDir
    Path tempTestDir;

    /**
     * Clear relative file root environment variable.
     */
    @BeforeEach
    void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    @Test
    void testJavaEventAlbumContextTest() throws IOException, ApexException {
        tempCommandFile = tempTestDir.resolve("TestPolicyJavaEventContext.apex").toFile();
        tempLogFile = tempTestDir.resolve("TestPolicyJavaEventContext.log").toFile();
        tempPolicyFile = tempTestDir.resolve("TestPolicyJavaEventContext.json").toFile();

        eventContextString = ResourceUtils.getResourceAsString("examples/scripts/TestPolicyJavaEventContext.apex");

        configFile = "src/test/resources/prodcons/Context_JavaEventAlbum_file2file.json";
        outputFile = "target/Context_JavaEventAlbum_EventOut.json";
        compareFile = "src/test/resources/events/Context_JavaEventAlbum_EventOutCompare.json";

        testEventAlbumContextTest();
    }

    @Test
    void testAvroEventAlbumContextTest() throws IOException, ApexException {
        tempCommandFile = tempTestDir.resolve("TestPolicyAvroEventContext.apex").toFile();
        tempLogFile = tempTestDir.resolve("TestPolicyAvroEventContext.log").toFile();
        tempPolicyFile = tempTestDir.resolve("TestPolicyAvroEventContext.json").toFile();

        eventContextString = ResourceUtils.getResourceAsString("examples/scripts/TestPolicyAvroEventContext.apex");

        configFile = "src/test/resources/prodcons/Context_AvroEventAlbum_file2file.json";
        outputFile = "target/Context_AvroEventAlbum_EventOut.json";
        compareFile = "src/test/resources/events/Context_AvroEventAlbum_EventOutCompare.json";

        testEventAlbumContextTest();
    }

    private void testEventAlbumContextTest() throws IOException, ApexException {
        TextFileUtils.putStringAsFile(eventContextString, tempCommandFile);

        final String[] cliArgs = new String[] {"-c", tempCommandFile.getCanonicalPath(), "-l",
            tempLogFile.getAbsolutePath(), "-ac", configFile, "-t", "src/test/resources/tosca/ToscaTemplate.json",
            "-ot", tempPolicyFile.getAbsolutePath()};

        new ApexCliToscaEditorMain(cliArgs);

        final String[] args = new String[] {"-p", tempPolicyFile.getAbsolutePath()};
        final ApexMain apexMain = new ApexMain(args);

        // The output event will be in this file
        final File outputEventFile = new File(outputFile);
        String receivedApexOutputString = "";
        for (int tenthsOfSecondsToWait = 100; tenthsOfSecondsToWait > 0; tenthsOfSecondsToWait--) {
            if (outputEventFile.exists() && outputEventFile.length() > 0) {
                // The output event is in this file
                receivedApexOutputString =
                    TextFileUtils.getTextFileAsString(outputEventFile.getCanonicalPath()).replaceAll("\\s+", "");
                break;
            }

            ThreadUtilities.sleep(100);
        }

        // Shut down Apex
        apexMain.shutdown();

        assertTrue(outputEventFile.exists(), "Test failed, the output event file was not created");
        assertTrue(outputEventFile.delete());

        assertFalse(receivedApexOutputString.isEmpty(), "Test failed, the output event file was empty");

        // We compare the output to what we expect to get
        final String expectedFileContent = TextFileUtils.getTextFileAsString(compareFile);
        final String outputEventCompareString = expectedFileContent.replaceAll("\\s+", "");

        assertEquals(outputEventCompareString, receivedApexOutputString);
    }

}
