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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.auth.clieditor.ApexCLIEditorMain;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.model.utilities.ResourceUtils;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * The Class AvroEventAlbumContextTest.
 */
public class AvroEventAlbumContextTest {

    /**
     * Test avro event fields, by starting an engine, send event in, test event out.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexException the apex exception
     */
    @Test
    public void testAvroEventAlbumContextTest() throws IOException, ApexException {
        final File tempCommandFile = File.createTempFile("TestPolicyAvroEventContext", ".apex");
        final File tempLogFile = File.createTempFile("TestPolicyAvroEventContext", ".log");
        final File tempModelFile = File.createTempFile("TestPolicyAvroEventContext", ".json");

        final String javaEventContextString =
                ResourceUtils.getResourceAsString("examples/scripts/TestPolicyAvroEventContext.apex");
        TextFileUtils.putStringAsFile(javaEventContextString, tempCommandFile);

        final String[] cliArgs = new String[] {"-c", tempCommandFile.getCanonicalPath(), "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        ModelService.clear();

        new ApexCLIEditorMain(cliArgs);

        tempCommandFile.delete();
        tempLogFile.delete();
        ModelService.clear();

        final String[] args = new String[] {"-m", tempModelFile.getAbsolutePath(), "-c",
                "src/test/resources/prodcons/Context_AvroEventAlbum_file2file.json"};
        final ApexMain apexMain = new ApexMain(args);
        
        // The output event will be in this file
        final File outputEventFile = new File("src/test/resources/events/Context_AvroEventAlbum_EventOut.json");
        int tenthsOfSecondsToWait = 100; // 10 seconds
        for (; !outputEventFile.exists() || outputEventFile.length() <= 0; tenthsOfSecondsToWait--) {
            ThreadUtilities.sleep(100);
        }

        // Shut down Apex
        apexMain.shutdown();
        ParameterService.clear();
        tempModelFile.delete();
        
        // Check if the test timed out
        if (tenthsOfSecondsToWait > 0) {
            final String outputEventString =
                            TextFileUtils.getTextFileAsString(outputEventFile.getCanonicalPath()).replaceAll("\\s+", "");
            outputEventFile.delete();

            // We compare the output to what we expect to get
            final String outputEventCompareString = TextFileUtils
                            .getTextFileAsString("src/test/resources/events/Context_AvroEventAlbum_EventOutCompare.json")
                            .replaceAll("\\s+", "");

            // Check what we got is what we expected to get
            assertEquals(outputEventCompareString, outputEventString);
        }
        else {
            outputEventFile.delete();
            fail("Test failed, the output event file was not created");
        }
    }
}
