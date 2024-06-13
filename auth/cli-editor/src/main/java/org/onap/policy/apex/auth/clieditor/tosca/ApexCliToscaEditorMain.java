/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2022, 2024 Nordix Foundation.
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
@Getter
public class ApexCliToscaEditorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexCliToscaEditorMain.class);

    private boolean failure;

    /**
     * Instantiates the Apex CLI Tosca editor.
     *
     * @param args the command line arguments
     */
    public ApexCliToscaEditorMain(final String[] args) {
        final var argumentString = Arrays.toString(args);
        LOGGER.info("Starting Apex CLI Tosca editor with arguments - {}", argumentString);

        final var parser = new ApexCliToscaParameterParser();
        ApexCliToscaParameters parameters = parser.parse(args);
        if (parameters.isHelpSet()) {
            CliUtils.help(ApexCliToscaEditorMain.class.getName(), parser.getOptions());
            return;
        }
        parameters.validate();

        String policyModelFilePath = null;
        String nodeType = parameters.getNodeType();
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

        ApexCommandLineEditorMain apexCliEditor = new ApexCommandLineEditorMain(cliArgs);
        if (apexCliEditor.getErrorCount() == 0) {
            LOGGER.info("Apex CLI editor completed execution. Creating the ToscaPolicy using the tosca template"
                + "skeleton file, config file, and policy model created.");

            // Create the ToscaPolicy using the tosca template skeleton file, config file, and policy model created.
            try {
                CliUtils.createToscaPolicy(parameters, policyModelFilePath, nodeType);
                LOGGER.info("Apex CLI Tosca editor completed execution.");
            } catch (IOException | CoderException e) {
                failure = true;
                LOGGER.error("Failed to create the Tosca template using the generated policy model,"
                    + "apex config file and the tosca template skeleton file. {}", e.getMessage());
            }

        } else {
            failure = true;
            LOGGER.error("execution of Apex command line editor failed: {} command execution failure(s) occurred",
                apexCliEditor.getErrorCount());
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
