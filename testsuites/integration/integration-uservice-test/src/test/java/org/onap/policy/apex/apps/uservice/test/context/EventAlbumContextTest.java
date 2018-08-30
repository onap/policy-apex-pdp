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

package org.onap.policy.apex.apps.uservice.test.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.apex.auth.clieditor.ApexCLIEditorMain;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.ResourceUtils;

public class EventAlbumContextTest {
    private File tempCommandFile;
    private File tempLogFile;
    private File tempModelFile;
    private String eventContextString;
    private String configFile;
    private String outputFile;
    private String compareFile;

    @Rule
    public TemporaryFolder tempTestDir = new TemporaryFolder();

    @Test
    public void testJavaEventAlbumContextTest() throws IOException, ApexException {
        tempCommandFile = tempTestDir.newFile("TestPolicyJavaEventContext.apex");
        tempLogFile = tempTestDir.newFile("TestPolicyJavaEventContext.log");
        tempModelFile = tempTestDir.newFile("TestPolicyJavaEventContext.json");

        eventContextString = ResourceUtils.getResourceAsString("examples/scripts/TestPolicyJavaEventContext.apex");

        configFile = "src/test/resources/prodcons/Context_JavaEventAlbum_file2file.json";
        outputFile = "src/test/resources/events/Context_JavaEventAlbum_EventOut.json";
        compareFile = "src/test/resources/events/Context_JavaEventAlbum_EventOutCompare.json";
        
        testEventAlbumContextTest();
    }

    @Test
    public void testAvroEventAlbumContextTest() throws IOException, ApexException {
        tempCommandFile = tempTestDir.newFile("TestPolicyAvroEventContext.apex");
        tempLogFile = tempTestDir.newFile("TestPolicyAvroEventContext.log");
        tempModelFile = tempTestDir.newFile("TestPolicyAvroEventContext.json");

        eventContextString = ResourceUtils.getResourceAsString("examples/scripts/TestPolicyAvroEventContext.apex");

        configFile = "src/test/resources/prodcons/Context_AvroEventAlbum_file2file.json";
        outputFile = "src/test/resources/events/Context_AvroEventAlbum_EventOut.json";
        compareFile = "src/test/resources/events/Context_AvroEventAlbum_EventOutCompare.json";
        
        testEventAlbumContextTest();
    }

    private void testEventAlbumContextTest() throws IOException, ApexException {
        TextFileUtils.putStringAsFile(eventContextString, tempCommandFile);

        final String[] cliArgs = new String[] { "-c", tempCommandFile.getCanonicalPath(), "-l",
                        tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath() };

        ModelService.clear();

        new ApexCLIEditorMain(cliArgs);

        ModelService.clear();

        final String[] args = new String[] { "-m", tempModelFile.getAbsolutePath(), "-c", configFile };
        final ApexMain apexMain = new ApexMain(args);

        // The output event will be in this file
        final File outputEventFile = new File(outputFile);
        String receivedApexOutputString = "";
        for (int tenthsOfSecondsToWait = 100; tenthsOfSecondsToWait > 0; tenthsOfSecondsToWait--) {
            if (outputEventFile.exists() && outputEventFile.length() > 0) {
                // The output event is in this file
                receivedApexOutputString = TextFileUtils.getTextFileAsString(outputEventFile.getCanonicalPath())
                                .replaceAll("\\s+", "");
                break;
            }

            ThreadUtilities.sleep(100);
        }

        // Shut down Apex
        apexMain.shutdown();
        ParameterService.clear();

        assertTrue("Test failed, the output event file was not created", outputEventFile.exists());
        outputEventFile.delete();

        assertTrue("Test failed, the output event file was empty", receivedApexOutputString.length() > 0);

        // We compare the output to what we expect to get
        final String expectedFileContent = TextFileUtils
                        .getTextFileAsString(compareFile);
        final String outputEventCompareString = expectedFileContent.replaceAll("\\s+", "");

        assertEquals(outputEventCompareString, receivedApexOutputString);
    }

}
