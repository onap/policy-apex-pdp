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

import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This class reads and handles command line parameters to the Apex CLI editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CLIParameterParser {
    private static final int MAX_HELP_LINE_LENGTH = 120;

    // Apache Commons CLI options
    private final Options options;

    /**
     * Construct the options for the CLI editor.
     */
    public CLIParameterParser() {
        options = new Options();
        options.addOption(Option.builder("h").longOpt("help").desc("outputs the usage of this command").required(false)
                .type(Boolean.class).build());
        options.addOption(Option.builder("m").longOpt("metadata-file").desc("name of the command metadata file to use")
                .hasArg().argName("CMD_METADATA_FILE").required(false).type(String.class).build());
        options.addOption(
                Option.builder("a").longOpt("model-props-file").desc("name of the apex model properties file to use")
                        .hasArg().argName("MODEL_PROPS_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("c").longOpt("command-file")
                .desc("name of a file containing editor commands to run into the editor").hasArg()
                .argName("COMMAND_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("l").longOpt("log-file")
                .desc("name of a file that will contain command logs from the editor, will log to standard output "
                        + "if not specified or suppressed with \"-nl\" flag")
                .hasArg().argName("LOG_FILE").required(false).type(String.class).build());
        options.addOption(Option.builder("nl").longOpt("no-log")
                .desc("if specified, no logging or output of commands to standard output or log file is carried out")
                .required(false).type(Boolean.class).build());
        options.addOption(Option.builder("nm").longOpt("no-model-output")
                .desc("if specified, no output of a model to standard output or model output file is carried out, "
                        + "the user can use the \"save\" command in a script to save a model")
                .required(false).type(Boolean.class).build());
        options.addOption(Option.builder("i").longOpt("input-model-file")
                .desc("name of a file that contains an input model for the editor").hasArg().argName("INPUT_MODEL_FILE")
                .required(false).type(String.class).build());
        options.addOption(Option.builder("o").longOpt("output-model-file")
                .desc("name of a file that will contain the output model for the editor, "
                        + "will output model to standard output if not specified or suppressed with \"-nm\" flag")
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
    public CLIParameters parse(final String[] args) {
        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (final ParseException e) {
            throw new CLIException("invalid command line arguments specified : " + e.getMessage());
        }

        final CLIParameters parameters = new CLIParameters();
        final String[] remainingArgs = commandLine.getArgs();

        if (remainingArgs.length > 0) {
            throw new CLIException("too many command line arguments specified : " + Arrays.toString(remainingArgs));
        }

        if (commandLine.hasOption('h')) {
            parameters.setHelp(true);
        }
        if (commandLine.hasOption('m')) {
            parameters.setMetadataFileName(commandLine.getOptionValue('m'));
        }
        if (commandLine.hasOption('a')) {
            parameters.setApexPorpertiesFileName(commandLine.getOptionValue('a'));
        }
        if (commandLine.hasOption('c')) {
            parameters.setCommandFileName(commandLine.getOptionValue('c'));
        }
        if (commandLine.hasOption('l')) {
            parameters.setLogFileName(commandLine.getOptionValue('l'));
        }
        if (commandLine.hasOption("nl")) {
            parameters.setSuppressLog(true);
        }
        if (commandLine.hasOption("nm")) {
            parameters.setSuppressModelOutput(true);
        }
        if (commandLine.hasOption('i')) {
            parameters.setInputModelFileName(commandLine.getOptionValue('i'));
        }
        if (commandLine.hasOption('o')) {
            parameters.setOutputModelFileName(commandLine.getOptionValue('o'));
        }
        if (commandLine.hasOption("if")) {
            parameters.setIgnoreCommandFailuresSet(true);
            parameters.setIgnoreCommandFailures(Boolean.valueOf(commandLine.getOptionValue("if")));
        } else {
            parameters.setIgnoreCommandFailuresSet(false);
        }
        if (commandLine.hasOption("wd")) {
            parameters.setWorkingDirectory(commandLine.getOptionValue("wd"));
        } else {
            parameters.setWorkingDirectory(Paths.get("").toAbsolutePath().toString());
        }

        return parameters;
    }

    /**
     * Print help information.
     *
     * @param mainClassName the main class name
     */
    public void help(final String mainClassName) {
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(MAX_HELP_LINE_LENGTH, mainClassName + " [options...]", "options", options, "");
    }
}
