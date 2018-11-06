/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class reads and handles command line parameters to the event generator.
 */
public class EventGeneratorParameterHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventGeneratorParameterHandler.class);

    private static final String CONFIGURATION_FILE = "configuration-file";
    private static final String PORT = "port";
    private static final String HOST = "host";
    private static final String HELP = "help";
    private static final String BATCH_SIZE = "batch-size";
    private static final String BATCH_COUNT = "batch-count";
    private static final String BATCH_DELAY = "delay-between-batches";

    private static final int MAX_HELP_LINE_LENGTH = 120;

    // Apache Commons CLI options
    private final Options options;

    /**
     * Construct the options for the CLI editor.
     */
    public EventGeneratorParameterHandler() {
        options = new Options();
        options.addOption(Option.builder("h").longOpt(HELP).desc("outputs the usage of this command").required(false)
                        .type(Boolean.class).build());
        options.addOption(Option.builder("H").longOpt(HOST)
                        .desc("the host name on which to start the event generation server, defaults to \"localhost\"")
                        .hasArg().argName(HOST).required(false).type(String.class).build());
        options.addOption(Option.builder("p").longOpt(PORT)
                        .desc("the port on which to start the event generation server, defaults to 42339").hasArg()
                        .argName(PORT).required(false).type(Number.class).build());
        options.addOption(Option.builder("c").longOpt(CONFIGURATION_FILE)
                        .desc("name of a file containing the parameters for the event generations server, "
                                        + "this option must appear on its own")
                        .hasArg().argName(CONFIGURATION_FILE).required(false).type(String.class).build());
        options.addOption(Option.builder("bc").longOpt(BATCH_COUNT)
                        .desc("the number of batches of events to send, must be 0 or more, "
                                        + "0 means send event batches forever, defaults to 1")
                        .hasArg().argName(BATCH_COUNT).required(false).type(Integer.class).build());
        options.addOption(Option.builder("bs").longOpt(BATCH_SIZE)
                        .desc("the number of events to send in an event batch, must be 1 or more, defaults to 1")
                        .hasArg().argName(BATCH_SIZE).required(false).type(Integer.class).build());
        options.addOption(Option.builder("bd").longOpt(BATCH_DELAY)
                        .desc("the delay in milliseconds between event batches, must be zero or more, "
                                        + "defaults to 10,000 (10 seconds)")
                        .hasArg().argName(BATCH_DELAY).required(false).type(Long.class).build());
    }

    /**
     * Parse the command line options.
     *
     * @param args The arguments
     * @return the CLI parameters
     * @throws ParseException on parse errors
     */
    public EventGeneratorParameters parse(final String[] args) throws ParseException {
        CommandLine commandLine = new DefaultParser().parse(options, args);
        final String[] remainingArgs = commandLine.getArgs();

        if (remainingArgs.length > 0) {
            throw new ParseException("too many command line arguments specified : " + Arrays.toString(remainingArgs));
        }

        if (commandLine.hasOption('h')) {
            return null;
        }

        EventGeneratorParameters parameters = new EventGeneratorParameters();

        if (commandLine.hasOption('c')) {
            if (commandLine.getOptions().length == 1) {
                parameters = getParametersFromJsonFile(commandLine.getOptionValue(CONFIGURATION_FILE));
            } else {
                throw new ParseException("specify either a configuration file or command line parameters, not both");
            }
        } else {
            parseFlags(commandLine, parameters);
        }

        if (!parameters.isValid()) {
            throw new ParseException("specified parameters are not valid: " + parameters.validate().getResult());
        }

        return parameters;
    }

    /**
     * Parse the command flags.
     * @param commandLine the command line to parse
     * @param parameters the parameters we are parsing into
     * @throws ParseException on parse errors
     */
    private void parseFlags(CommandLine commandLine, EventGeneratorParameters parameters) throws ParseException {
        if (commandLine.hasOption('H')) {
            parameters.setHost(commandLine.getOptionValue(HOST));
        }

        if (commandLine.hasOption('p')) {
            parameters.setPort(((Number) commandLine.getParsedOptionValue(PORT)).intValue());
        }

        if (commandLine.hasOption("bc")) {
            parameters.setBatchCount(((Number) commandLine.getParsedOptionValue(BATCH_COUNT)).intValue());
        }

        if (commandLine.hasOption("bs")) {
            parameters.setBatchSize(((Number) commandLine.getParsedOptionValue(BATCH_SIZE)).intValue());
        }

        if (commandLine.hasOption("bd")) {
            parameters.setDelayBetweenBatches(((Number) commandLine.getParsedOptionValue(BATCH_DELAY)).longValue());
        }
    }

    /**
     * Get the parameters from a JSON file.
     * 
     * @param configurationFile the location of the configuration file
     * @return the parameters read from the JSON file
     * @throws ParseException on errors reading the parameters
     */
    private EventGeneratorParameters getParametersFromJsonFile(String configurationFile) throws ParseException {
        String parameterJsonString = null;

        try {
            parameterJsonString = TextFileUtils.getTextFileAsString(configurationFile);
        } catch (IOException ioe) {
            String errorMessage = "Could not read parameters from configuration file \"" + configurationFile + "\": "
                            + ioe.getMessage();
            LOGGER.warn(errorMessage, ioe);
            throw new ParseException(errorMessage);
        }

        if (parameterJsonString == null || parameterJsonString.trim().length() == 0) {
            String errorMessage = "No parameters found in configuration file \"" + configurationFile + "\"";
            LOGGER.warn(errorMessage);
            throw new ParseException(errorMessage);
        }

        try {
            return new Gson().fromJson(parameterJsonString, EventGeneratorParameters.class);
        } catch (Exception ge) {
            String errorMessage = "Error parsing JSON parameters from configuration file \"" + configurationFile
                            + "\": " + ge.getMessage();
            LOGGER.warn(errorMessage, ge);
            throw new ParseException(errorMessage);
        }
    }

    /**
     * Get help information.
     *
     * @param mainClassName the main class name for the help output
     * @return help string
     */
    public String getHelp(final String mainClassName) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter stringPrintWriter = new PrintWriter(stringWriter);

        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(stringPrintWriter, MAX_HELP_LINE_LENGTH, mainClassName + " [options...] ", "", options,
                        0, 0, "");

        return stringWriter.toString();
    }

}
