/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Class to perform unit test of Apex cli tosca editor}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexCliToscaEditorTest {

    private File tempOutputToscaFile;
    private File tempLogFile;
    String[] sampleArgs;

    /**
     * Initialise args.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void initialiseArgs() throws IOException {

        tempOutputToscaFile = File.createTempFile("ToscaPolicyOutput", ".json");
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
    @After
    public void removeGeneratedFiles() {
        tempOutputToscaFile.delete();
        tempLogFile.delete();
    }

    @Test
    public void testApexCliToscaParameterParser() {
        ApexCliToscaParameters params = new ApexCliToscaParameterParser().parse(sampleArgs);
        assertEquals(CommonTestData.APEX_CONFIG_FILE_NAME, params.getApexConfigFileName());
        assertEquals(CommonTestData.COMMAND_FILE_NAME, params.getCommandFileName());
        assertEquals(CommonTestData.INPUT_TOSCA_TEMPLATE_FILE_NAME, params.getInputToscaTemplateFileName());
        assertEquals(tempOutputToscaFile.getAbsolutePath(), params.getOutputToscaPolicyFileName());
        assertEquals(tempLogFile.getAbsolutePath(), params.getLogFileName());
    }

    @Test
    public void testApexCliTosca_success() throws IOException {
        final ApexCliToscaEditorMain cliEditor = new ApexCliToscaEditorMain(sampleArgs);
        String outputTosca = TextFileUtils.getTextFileAsString(tempOutputToscaFile.getAbsolutePath());
        String outputToscaCompare =
            TextFileUtils.getTextFileAsString("src/test/resources/tosca/ToscaPolicyOutput_compare.json").trim();
        assertEquals(outputToscaCompare, outputTosca);
        assertFalse(cliEditor.isFailure());
    }

    @Test
    public void testApexCliTosca_no_args() {
        String[] noArgs = new String[] {};
        assertThatThrownBy(() -> new ApexCliToscaEditorMain(noArgs)).hasMessage("Insufficient arguments provided.");
    }

    @Test
    public void testApexCliTosca_missing_commandfile() {
        String[] sampleArgs = new String[] {
            "-ac", CommonTestData.APEX_CONFIG_FILE_NAME,
            "-t", CommonTestData.INPUT_TOSCA_TEMPLATE_FILE_NAME,
            "-ot", tempOutputToscaFile.getAbsolutePath(),
            "-l", tempLogFile.getAbsolutePath()
            };
        assertThatThrownBy(() -> new ApexCliToscaEditorMain(sampleArgs)).hasMessage("Insufficient arguments provided.");
    }
}
