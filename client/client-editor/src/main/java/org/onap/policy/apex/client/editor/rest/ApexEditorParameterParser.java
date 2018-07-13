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

package org.onap.policy.apex.client.editor.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
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
public class ApexEditorParameterParser {
    // Apache Commons CLI options
    private Options options;

    private static final int COMMAND_HELP_MAX_LINE_WIDTH = 120;

    /**
     * Construct the options for the CLI editor.
     */
    public ApexEditorParameterParser() {
        options = new Options();
        options.addOption("h", "help", false, "outputs the usage of this command");
        options.addOption(
                Option.builder("p").longOpt("port").desc("port to use for the Apex RESTful editor REST calls.").hasArg()
                        .argName("PORT").required(false).type(Number.class).build());
        options.addOption(Option.builder("t").longOpt("time-to-live")
                .desc("the amount of time in seconds that the server will run for before terminating. "
                        + "Default value is " + ApexEditorParameters.INFINITY_TIME_TO_LIVE + " to run indefinitely.")
                .hasArg().argName("TIME_TO_LIVE").required(false).type(Number.class).build());
        options.addOption(Option.builder("l").longOpt("listen").desc("the IP address to listen on.  Default value is "
                + ApexEditorParameters.DEFAULT_SERVER_URI_ROOT + " "
                + "to listen on all available addresses. Use value 'localhost' to restrict access to the local machine only.")
                .hasArg().argName("ADDRESS").required(false).type(String.class).build());
    }

    /**
     * Parse the command line options.
     *
     * @param args The arguments
     * @return the apex editor parameters
     */
    public ApexEditorParameters parse(final String[] args) {
        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (final ParseException e) {
            throw new ApexEditorParameterException("invalid command line arguments specified : " + e.getMessage());
        }

        final ApexEditorParameters parameters = new ApexEditorParameters();
        final String[] remainingArgs = commandLine.getArgs();

        if (commandLine.getArgs().length > 0) {
            throw new ApexEditorParameterException(
                    "too many command line arguments specified : " + Arrays.toString(remainingArgs));
        }

        if (commandLine.hasOption('h')) {
            parameters.setHelp(true);
        }
        try {
            if (commandLine.hasOption('p')) {
                parameters.setRESTPort(((Number) commandLine.getParsedOptionValue("port")).intValue());
            }
        } catch (final ParseException e) {
            throw new ApexEditorParameterException("error parsing argument \"port\" :" + e.getMessage(), e);
        }
        try {
            if (commandLine.hasOption('t')) {
                parameters.setTimeToLive(((Number) commandLine.getParsedOptionValue("time-to-live")).longValue());
            }
        } catch (final ParseException e) {
            throw new ApexEditorParameterException("error parsing argument \"time-to-live\" :" + e.getMessage(), e);
        }
        try {
            if (commandLine.hasOption('l')) {
                parameters.setListenAddress(commandLine.getParsedOptionValue("listen").toString());
            }
        } catch (final ParseException e) {
            throw new ApexEditorParameterException("error parsing argument \"listen-address\" :" + e.getMessage(), e);
        }

        return parameters;
    }

    /**
     * Get help information.
     *
     * @param mainClassName the main class name
     * @return the help
     */
    public String getHelp(final String mainClassName) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter stringPrintWriter = new PrintWriter(stringWriter);

        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(stringPrintWriter, COMMAND_HELP_MAX_LINE_WIDTH, mainClassName + " [options...] ", null,
                options, 0, 1, "");

        return stringWriter.toString();
    }
}
