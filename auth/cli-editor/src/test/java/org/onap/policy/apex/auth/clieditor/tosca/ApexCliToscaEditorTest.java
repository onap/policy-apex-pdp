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

package org.onap.policy.apex.auth.clieditor.tosca;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Class to perform unit test of Apex cli tosca editor}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
class ApexCliToscaEditorTest {

    private File tempOutputToscaFile;
    private File tempLogFile;
    String[] sampleArgs;
    private File tempNodeTemplateFile;

    /**
     * Initialise args.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeEach
    public void initialiseArgs() throws IOException {

        tempOutputToscaFile = File.createTempFile("ToscaPolicyOutput", ".json");
        tempNodeTemplateFile = File.createTempFile("ToscaNodeTemplate", ".json");
        tempLogFile = File.createTempFile("ApexCliTosca", ".log");
        sampleArgs = new String[] {
            "-c", CommonTestData.COMMAND_FILE_NAME,
            "-ac", CommonTestData.APEX_CONFIG_FILE_NAME,
            "-t", CommonTestData.INPUT_TOSCA_TEMPLATE_FILE_NAME,
            "-ot", tempOutputToscaFile.getAbsolutePath(),
            "-l", tempLogFile.getAbsolutePath()
        };
    }

    /**
     * Removes the generated files.
     */
    @AfterEach
    public void removeGeneratedFiles() {
        assertTrue(tempOutputToscaFile.delete());
        assertTrue(tempLogFile.delete());
        assertTrue(tempNodeTemplateFile.delete());
    }

    @Test
    void testApexCliToscaParameterParser() {
        ApexCliToscaParameters params = new ApexCliToscaParameterParser().parse(sampleArgs);
        assertEquals(CommonTestData.APEX_CONFIG_FILE_NAME, params.getApexConfigFileName());
        assertEquals(CommonTestData.COMMAND_FILE_NAME, params.getCommandFileName());
        assertEquals(CommonTestData.INPUT_TOSCA_TEMPLATE_FILE_NAME, params.getInputToscaTemplateFileName());
        assertEquals(tempOutputToscaFile.getAbsolutePath(), params.getOutputToscaPolicyFileName());
        assertEquals(tempLogFile.getAbsolutePath(), params.getLogFileName());
    }

    @Test
    void testApexCliTosca_success() throws IOException {
        final ApexCliToscaEditorMain cliEditor = new ApexCliToscaEditorMain(sampleArgs);


        String outputTosca = TextFileUtils.getTextFileAsString(tempOutputToscaFile.getAbsolutePath());
        String outputToscaCompare =
            TextFileUtils.getTextFileAsString("src/test/resources/tosca/ToscaPolicyOutput_compare_2.json").trim();

        assertEquals(outputToscaCompare, outputTosca);
        assertFalse(cliEditor.isFailure());
    }

    @Test
    void testApexCliTosca_no_args() {
        String[] noArgs = new String[] {};
        assertThatThrownBy(() -> new ApexCliToscaEditorMain(noArgs)).hasMessage("Insufficient arguments provided.");
    }

    @Test
    void testApexCliTosca_missing_commandFile() {
        assertThatThrownBy(() -> {
            String[] sampleArgs = new String[] {
                "-ac", CommonTestData.APEX_CONFIG_FILE_NAME,
                "-t", CommonTestData.INPUT_TOSCA_TEMPLATE_FILE_NAME,
                "-ot", tempOutputToscaFile.getAbsolutePath(),
                "-l", tempLogFile.getAbsolutePath()
            };
            new ApexCliToscaEditorMain(sampleArgs);
        }).hasMessage("Insufficient arguments provided.");
    }

    @Test
    void testGenerateToscaPolicyMetadataSet() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c", CommonTestData.COMMAND_FILE_NAME,
            "-l", tempLogFile.getAbsolutePath(),
            "-ac", CommonTestData.APEX_CONFIG_FILE_NAME,
            "-t", CommonTestData.INPUT_TOSCA_TEMPLATE_FILE_NAME,
            "-ot", tempOutputToscaFile.getAbsolutePath(),
            "-on", tempNodeTemplateFile.getAbsolutePath(),
            "-nt", CommonTestData.NODE_TYPE
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        assertTrue(tempOutputToscaFile.length() > 0);
        assertTrue(Files.lines(Paths.get(tempOutputToscaFile.toString()))
            .noneMatch(l -> l.contains("policy_type_impl")));
        assertTrue(tempNodeTemplateFile.length() > 0);
        assertTrue(Files.lines(Paths.get(tempNodeTemplateFile.toString()))
            .anyMatch(l -> l.contains("policyModel")));
    }
}
