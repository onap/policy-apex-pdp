/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * The Class TestCLIEditorEventsContext.
 */
class CommandLineEditorEventsContextTest {
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

    @TempDir
    static Path temporaryFolder;

    /**
     * Test java context model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testJavaContextModel() throws IOException {

        final File tempLogFile = temporaryFolder.resolve(LOG_FILE).toFile();
        final File tempModelFile = temporaryFolder.resolve(JSON_FILE).toFile();

        final String[] cliArgs = new String[] {"-c", APEX_JAVA_POLICY_FILE.toString(), "-l",
            tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non-white space characters in log and model files
        final int logCharCount = logString.replaceAll(SPACES, EMPTY_STRING).length();
        final int modelCharCount = modelString.replaceAll(SPACES, EMPTY_STRING).length();

        assertThat(logCharCount).isGreaterThan(20000);
        assertThat(modelCharCount).isGreaterThan(30000);
    }

    /**
     * Test avro context model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testAvroContextModel() throws IOException {

        final File tempLogFile = temporaryFolder.resolve(LOG_FILE).toFile();
        final File tempModelFile = temporaryFolder.resolve(JSON_FILE).toFile();

        final String[] cliArgs = new String[] {"-c", APEX_AVRO_POLICY_FILE.toString(), "-l",
            tempLogFile.getAbsolutePath(), "-o", tempModelFile.getAbsolutePath()};

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Get the model and log into strings
        final String logString = TextFileUtils.getTextFileAsString(tempLogFile.getCanonicalPath());
        final String modelString = TextFileUtils.getTextFileAsString(tempModelFile.getCanonicalPath());

        // As a sanity check, count the number of non-white space characters in log and model files
        final int logCharCount = logString.replaceAll(SPACES, EMPTY_STRING).length();
        final int modelCharCount = modelString.replaceAll(SPACES, EMPTY_STRING).length();

        assertThat(logCharCount).isGreaterThan(20000);
        assertThat(modelCharCount).isGreaterThan(30000);

    }

    @Test
    void test_emptyMetadataCommandFileWithEmptyJsonTag_errorCountGreaterThanOne() {

        final File tempLogFile = temporaryFolder.resolve(LOG_FILE).toFile();
        final File tempModelFile = temporaryFolder.resolve(JSON_FILE).toFile();

        final String modelFile = SRC_TEST_FOLDER.resolve("model").resolve("empty_commands.json").toString();
        final String apexPropertiesLocation =
            SRC_MAIN_FOLDER.resolve("etc/editor").resolve("ApexModelProperties.json").toString();

        final String[] cliArgs =
            new String[] {"-c", APEX_AVRO_POLICY_FILE.toString(), "-l", tempLogFile.getAbsolutePath(), "-o",
                tempModelFile.getAbsolutePath(), "-m", modelFile, "-a", apexPropertiesLocation};

        final ApexCommandLineEditorMain objUnderTest = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(1, objUnderTest.getErrorCount());

    }

    @Test
    void test_emptyMetadataCommandFile_errorCountGreaterThanOne() {

        final File tempLogFile = temporaryFolder.resolve(LOG_FILE).toFile();
        final File tempModelFile = temporaryFolder.resolve(JSON_FILE).toFile();

        final File modelFile = temporaryFolder.resolve("empty_commands.json").toFile();

        final String apexPropertiesLocation =
            SRC_MAIN_FOLDER.resolve("etc/editor").resolve("ApexModelProperties.json").toString();

        final String[] cliArgs =
            new String[] {"-c", APEX_AVRO_POLICY_FILE.toString(), "-l", tempLogFile.getAbsolutePath(), "-o",
                tempModelFile.getAbsolutePath(), "-m", modelFile.getAbsolutePath(), "-a", apexPropertiesLocation};

        final ApexCommandLineEditorMain objUnderTest = new ApexCommandLineEditorMain(cliArgs);
        assertEquals(1, objUnderTest.getErrorCount());

    }

}
