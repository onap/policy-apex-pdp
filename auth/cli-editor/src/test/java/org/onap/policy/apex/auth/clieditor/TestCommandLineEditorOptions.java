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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * The Class TestCLIEditorOptions.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestCommandLineEditorOptions {
    // CHECKSTYLE:OFF: MagicNumber

    /**
     * Test script options log model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testScriptOptionsLogModel() throws IOException, ApexModelException {
        final File tempLogFile = File.createTempFile("ShellPolicyModel", ".log");
        final File tempModelFile = File.createTempFile("ShellPolicyModel", ".json");

        final String[] cliArgs = new String[] {"-c", "src/main/resources/examples/scripts/ShellPolicyModel.apex", "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll("\\s+", "").length();
        final int modelCharCount = modelString.replaceAll("\\s+", "").length();

        assertEquals(1204, logCharCount);
        assertEquals(2924, modelCharCount);

        tempLogFile.delete();
        tempModelFile.delete();
    }

    /**
     * Test script options no log no model spec.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testScriptOptionsNoLogNoModelSpec() throws IOException, ApexModelException {
        final File tempLogFile = File.createTempFile("ShellPolicyModel", ".log");
        final File tempModelFile = File.createTempFile("ShellPolicyModel", ".json");

        final String[] cliArgs = new String[] {"-c", "src/main/resources/examples/scripts/ShellPolicyModel.apex", "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath(), "-nl", "-nm"};

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll("\\s+", "").length();
        final int modelCharCount = modelString.replaceAll("\\s+", "").length();

        assertEquals(0, logCharCount);
        assertEquals(0, modelCharCount);

        tempLogFile.delete();
        tempModelFile.delete();
    }

    /**
     * Test script options log no model spec.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testScriptOptionsLogNoModelSpec() throws IOException, ApexModelException {
        final File tempLogFile = File.createTempFile("ShellPolicyModel", ".log");
        final File tempModelFile = File.createTempFile("ShellPolicyModel", ".json");

        final String[] cliArgs = new String[] {"-c", "src/main/resources/examples/scripts/ShellPolicyModel.apex", "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath(), "-nm"};

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        System.err.println(modelString);
        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll("\\s+", "").length();
        final int modelCharCount = modelString.replaceAll("\\s+", "").length();

        assertEquals(1204, logCharCount);
        assertEquals(0, modelCharCount);

        tempLogFile.delete();
        tempModelFile.delete();
    }

    /**
     * Test script options no log model spec.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testScriptOptionsNoLogModelSpec() throws IOException, ApexModelException {
        final File tempLogFile = File.createTempFile("ShellPolicyModel", ".log");
        final File tempModelFile = File.createTempFile("ShellPolicyModel", ".json");

        final String[] cliArgs = new String[] {"-c", "src/main/resources/examples/scripts/ShellPolicyModel.apex", "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath(), "-nl"};

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll("\\s+", "").length();
        final int modelCharCount = modelString.replaceAll("\\s+", "").length();

        assertEquals(0, logCharCount);
        assertEquals(2924, modelCharCount);

        tempLogFile.delete();
        tempModelFile.delete();
    }

    /**
     * Test script options no log no model no spec.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testScriptOptionsNoLogNoModelNoSpec() throws IOException, ApexModelException {
        final String[] cliArgs =
                new String[] {"-c", "src/main/resources/examples/scripts/ShellPolicyModel.apex", "-nl", "-nm"};

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        System.setOut(new PrintStream(baos));
        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Cursor for log
        assertFalse(baos.toString().contains(">"));

        // Curly bracket from JSON model
        assertFalse(baos.toString().contains("{"));
    }

    /**
     * Test script options log model no spec.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testScriptOptionsLogModelNoSpec() throws IOException, ApexModelException {
        final String[] cliArgs = new String[] {"-c", "src/main/resources/examples/scripts/ShellPolicyModel.apex"};

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final PrintStream stdout = System.out;
        System.setOut(new PrintStream(baos));
        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Cursor for log
        assertTrue(baos.toString().contains(">"));

        // Curly bracket from JSON model
        assertTrue(baos.toString().contains("{"));

        System.setOut(stdout);
    }

    /**
     * Test script options input output model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testScriptOptionsInputOutputModel() throws IOException, ApexModelException {
        final File tempLogFileIn = File.createTempFile("ShellPolicyModelIn", ".log");
        final File tempLogFileOut = File.createTempFile("ShellPolicyModelOut", ".log");
        final File tempModelFileIn = File.createTempFile("ShellPolicyModelIn", ".json");
        final File tempModelFileOut = File.createTempFile("ShellPolicyModelOut", ".json");

        // Generate input model
        final String[] cliArgsIn = new String[] {"-c", "src/main/resources/examples/scripts/ShellPolicyModel.apex",
            "-l", tempLogFileIn.getAbsolutePath(), "-o", tempModelFileIn.getAbsolutePath()};

        final ApexCommandLineEditorMain cliEditorIn = new ApexCommandLineEditorMain(cliArgsIn);
        assertEquals(0, cliEditorIn.getErrorCount());

        // Get the model and log into strings
        final String tempLogFileInString = TextFileUtils.getTextFileAsString(tempLogFileIn.getCanonicalPath());
        final String tempModelFileInString = TextFileUtils.getTextFileAsString(tempModelFileIn.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int tempLogFileInCharCount = tempLogFileInString.replaceAll("\\s+", "").length();
        final int tempModelFileInCharCount = tempModelFileInString.replaceAll("\\s+", "").length();

        assertEquals(1204, tempLogFileInCharCount);
        assertEquals(2924, tempModelFileInCharCount);

        final String[] cliArgsOut = new String[] {"-i", tempModelFileIn.getAbsolutePath(), "-c",
            "src/main/resources/examples/scripts/ShellPolicyModelAddSchema.apex", "-l",
            tempLogFileOut.getAbsolutePath(), "-o", tempModelFileOut.getAbsolutePath()};

        final ApexCommandLineEditorMain cliEditorOut = new ApexCommandLineEditorMain(cliArgsOut);
        assertEquals(0, cliEditorOut.getErrorCount());

        // Get the model and log into strings
        final String tempLogFileOutString = TextFileUtils.getTextFileAsString(tempLogFileOut.getCanonicalPath());
        final String tempModelFileOutString = TextFileUtils.getTextFileAsString(tempModelFileOut.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int tempLogFileOutCharCount = tempLogFileOutString.replaceAll("\\s+", "").length();
        final int tempModelFileOutCharCount = tempModelFileOutString.replaceAll("\\s+", "").length();

        assertEquals(1154, tempLogFileOutCharCount);
        assertEquals(3356, tempModelFileOutCharCount);

        tempLogFileIn.delete();
        tempModelFileIn.delete();
        tempLogFileOut.delete();
        tempModelFileOut.delete();
    }
}
