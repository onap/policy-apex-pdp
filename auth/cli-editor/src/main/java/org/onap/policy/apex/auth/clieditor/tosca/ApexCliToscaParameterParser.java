/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021 Nordix Foundation.
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

import org.apache.commons.cli.Option;
import org.onap.policy.apex.auth.clieditor.CommandLineParameterParser;

/**
 * This class reads and handles command line parameters to the Apex CLI Tosca editor.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexCliToscaParameterParser extends CommandLineParameterParser {

    /**
     * Construct the options for the CLI editor.
     */
    public ApexCliToscaParameterParser() {
        getOptions().addOption(Option.builder("ac").longOpt("apex-config-file")
            .desc("name of the file containing apex configuration details").hasArg()
            .argName("APEX_CONFIG_FILE").required(false).type(String.class).build());
        getOptions().addOption(Option.builder("t").longOpt("tosca-template-file")
            .desc("name of the input file containing tosca template which needs to be updated with policy").hasArg()
            .argName("TOSCA_TEMPLATE_FILE").required(false).type(String.class).build());
        getOptions().addOption(Option.builder("ot").longOpt("output-tosca-file")
            .desc("name of a file that will contain the output model for the editor").hasArg()
            .argName("OUTPUT_TOSCA_FILE").required(false).type(String.class).build());
    }

    /**
     * Parse the command line options.
     *
     * @param args The arguments
     * @return the CLI parameters
     */
    @Override
    public ApexCliToscaParameters parse(final String[] args) {
        var commandLine = parseDefault(args);
        final var parameters = new ApexCliToscaParameters();
        parseSingleLetterOptions(commandLine, parameters);
        parseDoubleLetterOptions(commandLine, parameters);

        if (commandLine.hasOption("ac")) {
            parameters.setApexConfigFileName(commandLine.getOptionValue("ac"));
        }
        if (commandLine.hasOption("t")) {
            parameters.setInputToscaTemplateFileName(commandLine.getOptionValue("t"));
        }
        if (commandLine.hasOption("ot")) {
            parameters.setOutputToscaPolicyFileName(commandLine.getOptionValue("ot"));
        }
        return parameters;
    }

}
