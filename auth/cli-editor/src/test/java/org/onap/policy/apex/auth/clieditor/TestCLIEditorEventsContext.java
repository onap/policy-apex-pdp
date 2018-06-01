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

package org.onap.policy.apex.auth.clieditor;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * The Class TestCLIEditorEventsContext.
 */
public class TestCLIEditorEventsContext {
    // CHECKSTYLE:OFF: MagicNumber

    /**
     * Test java context model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if an Apex error happens
     */
    @Test
    public void testJavaContextModel() throws IOException, ApexModelException {
        final File tempLogFile = File.createTempFile("TestPolicyJavaEventsAndContext", ".log");
        final File tempModelFile = File.createTempFile("TestPolicyJavaEventsAndContext", ".json");

        final String[] cliArgs =
                new String[] {"-c", "src/main/resources/examples/scripts/TestPolicyJavaEventContext.apex", "-l",
                        tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll("\\s+", "").length();
        final int modelCharCount = modelString.replaceAll("\\s+", "").length();

        assertEquals(25911, logCharCount);
        assertEquals(46138, modelCharCount);

        tempLogFile.delete();
        tempModelFile.delete();
    }

    /**
     * Test avro context model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if an Apex error happens
     */
    @Test
    public void testAvroContextModel() throws IOException, ApexModelException {
        final File tempLogFile = File.createTempFile("TestPolicyAvroEventsAndContext", ".log");
        final File tempModelFile = File.createTempFile("TestPolicyAvroEventsAndContext", ".json");

        final String[] cliArgs =
                new String[] {"-c", "src/main/resources/examples/scripts/TestPolicyAvroEventContext.apex", "-l",
                        tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll("\\s+", "").length();
        final int modelCharCount = modelString.replaceAll("\\s+", "").length();

        assertEquals(30315, logCharCount);
        assertEquals(52930, modelCharCount);

        tempLogFile.delete();
        tempModelFile.delete();
    }
}
