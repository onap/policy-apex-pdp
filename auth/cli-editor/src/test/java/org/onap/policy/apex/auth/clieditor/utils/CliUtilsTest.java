/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019, 2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.auth.clieditor.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaParameterParser;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaParameters;
import org.onap.policy.apex.auth.clieditor.tosca.CommonTestData;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Class to perform unit test of {@link CliUtils}}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
class CliUtilsTest {

    private File tempOutputToscaFile;
    private File tempLogFile;
    private String policyModelFilePath;
    String[] sampleArgs;

    /**
     * Initialise args.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeEach
    public void initialiseArgs() throws IOException {

        tempOutputToscaFile = File.createTempFile("ToscaPolicyOutput", ".json");
        tempLogFile = File.createTempFile("ApexCliTosca", ".log");
        policyModelFilePath = CommonTestData.POLICY_MODEL_FILE_NAME;
        sampleArgs = new String[] {"-c", CommonTestData.COMMAND_FILE_NAME, "-ac", CommonTestData.APEX_CONFIG_FILE_NAME,
            "-t", CommonTestData.INPUT_TOSCA_TEMPLATE_FILE_NAME, "-ot", tempOutputToscaFile.getAbsolutePath(), "-l",
            tempLogFile.getAbsolutePath()};
    }

    /**
     * Removes the generated files.
     */
    @AfterEach
    public void removeGeneratedFiles() {
        assertTrue(tempOutputToscaFile.delete());
        assertTrue(tempLogFile.delete());
    }

    @Test
    void testCreateToscaServiceTemplate() throws IOException, CoderException {
        ApexCliToscaParameters params = new ApexCliToscaParameterParser().parse(sampleArgs);
        CliUtils.createToscaPolicy(params, policyModelFilePath, null);
        String outputTosca = TextFileUtils.getTextFileAsString(tempOutputToscaFile.getAbsolutePath());
        String outputToscaCompare =
            TextFileUtils.getTextFileAsString("src/test/resources/tosca/ToscaPolicyOutput_compare_1.json").trim();
        assertEquals(outputToscaCompare, outputTosca);
    }

    @Test
    void testValidateReadableFile_validFile() {
        CliUtils.validateReadableFile("Apex Config File", CommonTestData.APEX_CONFIG_FILE_NAME);
    }

    @Test
    void testValidateReadableFile_invalidFile() {
        String invalidFileName = "src/test/resources/tosca/ApexConfigxyz.json";
        assertThatThrownBy(() -> CliUtils.validateReadableFile("Apex Config File", invalidFileName))
            .hasMessage("File " + invalidFileName + " of type Apex Config File does not exist");
    }

    @Test
    void testValidateWritableFile_validFile() {
        CliUtils.validateWritableFile("Output Tosca Policy File", tempOutputToscaFile.getAbsolutePath());
    }

    @Test
    void testValidateWritableFile_invalidFile() {
        String invalidFileName = "src/test/resources/tosca";
        assertThatThrownBy(() -> CliUtils.validateWritableFile("Output Tosca Policy File", invalidFileName))
            .hasMessage("File " + invalidFileName + " of type Output Tosca Policy File is not a normal file");
    }

    @Test
    void testValidateWritableDirectory_validDirectory() {
        CliUtils.validateWritableDirectory("Working Directory", "src/test/resources/tosca");
    }

    @Test
    void testValidateWritableDirectory_invalidDirectory() {
        assertThatThrownBy(() -> CliUtils.validateWritableDirectory("Working Directory",
            CommonTestData.APEX_CONFIG_FILE_NAME)).hasMessage("directory " + CommonTestData.APEX_CONFIG_FILE_NAME
            + " of type Working Directory is not a directory");
    }

    @Test
    void testGenerateArgumentsForCliEditor_success() {
        ApexCliToscaParameters params = new ApexCliToscaParameterParser().parse(sampleArgs);
        Properties optionVariableMap = new Properties();
        optionVariableMap.setProperty("c", "commandFileName");
        optionVariableMap.setProperty("ac", "apexConfigFileName");
        optionVariableMap.setProperty("t", "inputToscaTemplateFileName");
        optionVariableMap.setProperty("ot", "outputToscaPolicyFileName");
        optionVariableMap.setProperty("l", "logFileName");
        List<String> cliArgsList =
            CliUtils.generateArgumentsForCliEditor(params, optionVariableMap, ApexCliToscaParameters.class);
        assertTrue(cliArgsList.containsAll(Arrays.asList(sampleArgs)));
    }

    @Test
    void testGenerateArgumentsForCliEditor_invalidVariable() {
        ApexCliToscaParameters params = new ApexCliToscaParameterParser().parse(sampleArgs);
        Properties optionVariableMap = new Properties();
        optionVariableMap.setProperty("c", "invalidFileName");
        List<String> cliArgsList =
            CliUtils.generateArgumentsForCliEditor(params, optionVariableMap, ApexCliToscaParameters.class);
        assertEquals(0, cliArgsList.size());
    }

    @Test
    void testGenerateArgumentsForCliEditor_missingOption() {
        ApexCliToscaParameters params = new ApexCliToscaParameterParser().parse(sampleArgs);
        Properties optionVariableMap = new Properties();
        List<String> cliArgsList =
            CliUtils.generateArgumentsForCliEditor(params, optionVariableMap, ApexCliToscaParameters.class);
        assertEquals(0, cliArgsList.size());
    }
}
