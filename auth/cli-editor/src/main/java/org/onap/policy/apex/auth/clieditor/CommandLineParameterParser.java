/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This class reads and handles command line parameters to the Apex CLI editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
public class CommandLineParameterParser {

    // Apache Commons CLI options
    private final Options options;
    private Properties optionVariableMap;

    /**
     * Construct the options for the CLI editor.
     */
    public CommandLineParameterParser() {
        options = new Options();
        optionVariableMap = new Properties();

        options.addOption(Option.builder("h").longOpt("help").desc("outputs the usage of this command").required(false)
                        .type(Boolean.class).build());
        options.addOption(Option.builder("m").longOpt("metadata-file").desc("name of the command metadata file to use")
                        .hasArg().argName("CMD_METADATA_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("a").longOpt("model-props-file")
                        .desc("name of the apex model properties file to use").hasArg().argName("MODEL_PROPS_FILE")
                        .required(false).type(String.class).build());
        options.addOption(Option.builder("c").longOpt("command-file")
                        .desc("name of a file containing editor commands to run into the editor").hasArg()
                        .argName("COMMAND_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("l").longOpt("log-file").desc(
                        "name of a file that will contain command logs from the editor, will log to standard output "
                                        + "if not specified or suppressed with \"-nl\" flag")
                        .hasArg().argName("LOG_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("nl").longOpt("no-log").desc(
                        "if specified, no logging or output of commands to standard output or log file is carried out")
                        .required(false).type(Boolean.class).build());
        options.addOption(Option.builder("nm").longOpt("no-model-output").desc(
                        "if specified, no output of a model to standard output or model output file is carried out, "
                                        + "the user can use the \"save\" command in a script to save a model")
                        .required(false).type(Boolean.class).build());
        options.addOption(Option.builder("i").longOpt("input-model-file")
                        .desc("name of a file that contains an input model for the editor").hasArg()
                        .argName("INPUT_MODEL_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("o").longOpt("output-model-file")
                        .desc("name of a file that will contain the output model for the editor, "
                                        + "will output model to standard output if not specified "
                                        + "or suppressed with \"-nm\" flag")
                        .hasArg().argName("OUTPUT_MODEL_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("if").longOpt("ignore-failures")
                        .desc("true or false, ignore failures of commands in command files and continue executing the "
                                        + "command file")
                        .hasArg().argName("IGNORE_FAILURES_FLAG").required(false).type(Boolean.class).build());
        options.addOption(Option.builder("wd").longOpt("working-directory")
                        .desc("the working directory that is the root for the CLI editor and is the root from which to "
                                        + "look for included macro files")
                        .hasArg().argName("WORKING_DIRECTORY").required(false).type(String.class).build());
    }

    /**
     * Parse the command line options.
     *
     * @param args The arguments
     * @return the CLI parameters
     */
    public CommandLineParameters parse(final String[] args) {
        var commandLine = parseDefault(args);
        final var parameters = new CommandLineParameters();
        parseSingleLetterOptions(commandLine, parameters);
        parseDoubleLetterOptions(commandLine, parameters);

        return parameters;
    }

    /**
     * Parse the command line options using default parser.
     *
     * @param args The arguments
     * @return the CLI parameters
     */
    protected CommandLine parseDefault(final String[] args) {
        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (final ParseException e) {
            throw new CommandLineException("invalid command line arguments specified : " + e.getMessage());
        }

        final String[] remainingArgs = commandLine.getArgs();

        if (remainingArgs.length > 0) {
            throw new CommandLineException(
                            "too many command line arguments specified : " + Arrays.toString(remainingArgs));
        }
        return commandLine;
    }

    /**
     * Parse options with just a single letter.
     *
     * @param commandLine the command line
     * @param parameters the parsed parameters
     */
    protected void parseDoubleLetterOptions(CommandLine commandLine, final CommandLineParameters parameters) {
        if (commandLine.hasOption("nl")) {
            parameters.setSuppressLog(true);
            optionVariableMap.setProperty("nl", "suppressLog");
        }
        if (commandLine.hasOption("nm")) {
            parameters.setSuppressModelOutput(true);
            optionVariableMap.setProperty("nm", "suppressModelOutput");
        }
        if (commandLine.hasOption("if")) {
            parameters.setIgnoreCommandFailuresSet(true);
            parameters.setIgnoreCommandFailures(Boolean.valueOf(commandLine.getOptionValue("if")));
            optionVariableMap.setProperty("if", "ignoreCommandFailures");
        } else {
            parameters.setIgnoreCommandFailuresSet(false);
        }
        if (commandLine.hasOption("wd")) {
            parameters.setWorkingDirectory(commandLine.getOptionValue("wd"));
            optionVariableMap.setProperty("wd", "workingDirectory");
        } else {
            parameters.setWorkingDirectory(Paths.get("").toAbsolutePath().toString());
        }
    }

    /**
     * Parse options with two letters.
     *
     * @param commandLine the command line
     * @param parameters the parsed parameters
     */
    protected void parseSingleLetterOptions(CommandLine commandLine, final CommandLineParameters parameters) {
        if (commandLine.hasOption('h')) {
            parameters.setHelpSet(true);
            optionVariableMap.setProperty("h", "helpSet");
        }
        if (commandLine.hasOption('m')) {
            parameters.setMetadataFileName(commandLine.getOptionValue('m'));
            optionVariableMap.setProperty("m", "metadataFileName");
        }
        if (commandLine.hasOption('a')) {
            parameters.setApexPropertiesFileName(commandLine.getOptionValue('a'));
            optionVariableMap.setProperty("a", "apexPropertiesFileName");
        }
        if (commandLine.hasOption('c')) {
            parameters.setCommandFileName(commandLine.getOptionValue('c'));
            optionVariableMap.setProperty("c", "commandFileName");
        }
        if (commandLine.hasOption('l')) {
            parameters.setLogFileName(commandLine.getOptionValue('l'));
            optionVariableMap.setProperty("l", "logFileName");
        }
        if (commandLine.hasOption('i')) {
            parameters.setInputModelFileName(commandLine.getOptionValue('i'));
            optionVariableMap.setProperty("i", "inputModelFileName");
        }
        if (commandLine.hasOption('o')) {
            parameters.setOutputModelFileName(commandLine.getOptionValue('o'));
            optionVariableMap.setProperty("o", "outputModelFileName");
        }
    }

}
