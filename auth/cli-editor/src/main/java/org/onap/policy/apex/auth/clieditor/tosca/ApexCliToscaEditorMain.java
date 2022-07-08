/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2022 Nordix Foundation.
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain;
import org.onap.policy.apex.auth.clieditor.CommandLineParameters;
import org.onap.policy.apex.auth.clieditor.utils.CliUtils;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class initiates an Apex CLI Tosca editor.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexCliToscaEditorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexCliToscaEditorMain.class);

    @Getter
    private boolean failure;
    private ApexCliToscaParameters parameters;
    private ApexCommandLineEditorMain apexCliEditor;

    /**
     * Instantiates the Apex CLI Tosca editor.
     *
     * @param args the command line arguments
     */
    public ApexCliToscaEditorMain(final String[] args) {
        final var argumentString = Arrays.toString(args);
        LOGGER.info("Starting Apex CLI Tosca editor with arguments - {}", argumentString);

        final var parser = new ApexCliToscaParameterParser();
        parameters = parser.parse(args);
        if (parameters.isHelpSet()) {
            CliUtils.help(ApexCliToscaEditorMain.class.getName(), parser.getOptions());
            return;
        }
        parameters.validate();

        String policyModelFilePath = null;
        try {
            final var tempModelFile = TextFileUtils.createTempFile("policyModel", ".json");
            policyModelFilePath = tempModelFile.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error("Cannot create the policy model temp file.", e);
        }

        List<String> cliArgsList = CliUtils.generateArgumentsForCliEditor(parameters, parser.getOptionVariableMap(),
            CommandLineParameters.class);
        cliArgsList.add("-o");
        cliArgsList.add(policyModelFilePath);
        String[] cliArgs = cliArgsList.toArray(new String[cliArgsList.size()]);

        // Generate policy models if command file is provided
        if (parameters.getCommandFileName() != null) {
            apexCliEditor = new ApexCommandLineEditorMain(cliArgs);
            if (apexCliEditor.getErrorCount() == 0) {
                LOGGER.info("Policy model created by APEX CLI editor.");
            } else {
                failure = true;
                LOGGER.error("Execution of Apex command line editor failed: {} command execution failure(s) occurred",
                        apexCliEditor.getErrorCount());
                return;
            }
        }
        // Create the Tosca service template using the tosca template skeleton file, config file, and
        // policy model created.
        try {
            if (parameters.getApexConfigFileName() != null) {
                CliUtils.createToscaPolicy(parameters, policyModelFilePath);
                LOGGER.info("Apex CLI Tosca editor completed execution for Tosca policy.");
            } else {
                // Create node template if apex config file is not provided in the argument
                CliUtils.createToscaMetadataSet(parameters, policyModelFilePath);
                LOGGER.info("Apex CLI Tosca editor completed execution for Tosca node template.");
            }

        } catch (IOException | CoderException e) {
            failure = true;
            LOGGER.error("Failed to create the Tosca template using the generated policy model, apex config file and"
                    + " the tosca template skeleton file ." + e);
        }

    }

    /**
     * The main method, kicks off the cli tosca editor.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        new ApexCliToscaEditorMain(args);
    }
}
