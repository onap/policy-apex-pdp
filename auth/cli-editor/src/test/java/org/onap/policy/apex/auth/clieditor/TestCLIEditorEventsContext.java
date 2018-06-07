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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * The Class TestCLIEditorEventsContext.
 */
public class TestCLIEditorEventsContext {
    // CHECKSTYLE:OFF: MagicNumber

    private static final Path SRC_MAIN_FOLDER = Paths.get("src/main/resources/");
    private static final Path SRC_TEST_FOLDER = Paths.get("src/test/resources/");

    private static final Path SUB_FOLDER = SRC_MAIN_FOLDER.resolve("examples/scripts/");

    private static final String SPACES = "\\s+";
    private static final String EMPTY_STRING = "";

    private static final Path APEX_AVRO_POLICY_FILE = SUB_FOLDER.resolve("TestPolicyAvroEventContext.apex");
    private static final Path APEX_JAVA_POLICY_FILE = SUB_FOLDER.resolve("TestPolicyJavaEventContext.apex");

    private static final String FILE_NAME = "TestPolicyJavaEventsAndContext";
    private static final String JSON_FILE = FILE_NAME + ".json";
    private static final String LOG_FILE = FILE_NAME + ".log";


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Test java context model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if an Apex error happens
     */
    @Test
    public void testJavaContextModel() throws IOException, ApexModelException {

        final File tempLogFile = temporaryFolder.newFile(LOG_FILE);
        final File tempModelFile = temporaryFolder.newFile(JSON_FILE);

        final String[] cliArgs = new String[] {"-c", APEX_JAVA_POLICY_FILE.toString(), "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll(SPACES, EMPTY_STRING).length();
        final int modelCharCount = modelString.replaceAll(SPACES, EMPTY_STRING).length();

        assertEquals(25911, logCharCount);
        assertEquals(46138, modelCharCount);
    }

    /**
     * Test avro context model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if an Apex error happens
     */
    @Test
    public void testAvroContextModel() throws IOException, ApexModelException {

        final File tempLogFile = temporaryFolder.newFile(LOG_FILE);
        final File tempModelFile = temporaryFolder.newFile(JSON_FILE);

        final String[] cliArgs = new String[] {"-c", APEX_AVRO_POLICY_FILE.toString(), "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non white space characters in log and model files
        final int logCharCount = logString.replaceAll(SPACES, EMPTY_STRING).length();
        final int modelCharCount = modelString.replaceAll(SPACES, EMPTY_STRING).length();

        assertEquals(30315, logCharCount);
        assertEquals(52930, modelCharCount);

    }

    @Test
    public void test_metadataCommandFileWithEmptyCommandListTag_errorcountGreaterThanOne() throws IOException {

        final File tempLogFile = temporaryFolder.newFile(LOG_FILE);
        final File tempModelFile = temporaryFolder.newFile(JSON_FILE);

        final String modelFile = SRC_TEST_FOLDER.resolve("model").resolve("empty_commands.json").toString();
        final String apexPropertiesLocation =
                SRC_MAIN_FOLDER.resolve("etc/editor").resolve("ApexModelProperties.json").toString();

        final String[] cliArgs =
                new String[] {"-c", APEX_AVRO_POLICY_FILE.toString(), "-l", tempLogFile.getAbsolutePath(), "-o",
                        tempModelFile.getAbsolutePath(), "-m", modelFile, "-a", apexPropertiesLocation};

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(cliArgs);
        assertEquals(1, cliEditor.getErrorCount());

    }

    @Test
    public void test_metadataCommandFile_errorcountGreaterThanOne() throws IOException {

        final File tempLogFile = temporaryFolder.newFile(LOG_FILE);
        final File tempModelFile = temporaryFolder.newFile(JSON_FILE);

        final File modelFile = temporaryFolder.newFile("empty_commands.json");

        final String apexPropertiesLocation =
                SRC_MAIN_FOLDER.resolve("etc/editor").resolve("ApexModelProperties.json").toString();

        final String[] cliArgs = new String[] {"-c", APEX_AVRO_POLICY_FILE.toString(), "-l",
                tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath(), "-m", modelFile.getAbsolutePath(),
                "-a", apexPropertiesLocation};

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(cliArgs);
        assertEquals(1, cliEditor.getErrorCount());

    }

    @Test
    public void test_modelPropertyFile_errorcountGreaterThanOne() throws IOException {

        final File tempLogFile = temporaryFolder.newFile(LOG_FILE);
        final File tempModelFile = temporaryFolder.newFile(JSON_FILE);

        final File apexPropertiesLocation = temporaryFolder.newFile("empty_modelProperties.json");

        final String[] cliArgs =
                new String[] {"-c", APEX_AVRO_POLICY_FILE.toString(), "-l", tempLogFile.getAbsolutePath(), "-o",
                        tempModelFile.getAbsolutePath(), "-a", apexPropertiesLocation.getAbsolutePath()};

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(cliArgs);
        assertEquals(1, cliEditor.getErrorCount());

    }
}
