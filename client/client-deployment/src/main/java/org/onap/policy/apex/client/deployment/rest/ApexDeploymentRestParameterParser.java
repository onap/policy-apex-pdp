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

package org.onap.policy.apex.client.deployment.rest;

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
 * This class reads and handles command line parameters to the Apex RESTful services.
 *
 * @author Michael Watkins (michael.watkins@ericsson.com)
 */
public class ApexDeploymentRestParameterParser {
    // Apache Commons CLI options
    Options options;

    /**
     * Construct the options for the CLI RESTful services.
     */
    public ApexDeploymentRestParameterParser() {
        options = new Options();
        options.addOption("h", "help", false, "outputs the usage of this command");
        options.addOption(Option.builder("p").longOpt("port").desc("port to use for the Apex Services REST calls")
                .hasArg().argName("PORT").required(false).type(Number.class).build());
        options.addOption(Option.builder("t").longOpt("time-to-live")
                .desc("the amount of time in seconds that the server will run for before terminating").hasArg()
                .argName("TIME_TO_LIVE").required(false).type(Number.class).build());
    }

    /**
     * Parse the command line options.
     *
     * @param args the arguments
     * @return the parsed arguments
     */
    public ApexDeploymentRestParameters parse(final String[] args) {
        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (final ParseException e) {
            throw new ApexDeploymentRestParameterException(
                    "invalid command line arguments specified : " + e.getMessage());
        }

        final ApexDeploymentRestParameters parameters = new ApexDeploymentRestParameters();
        final String[] remainingArgs = commandLine.getArgs();

        if (commandLine.getArgs().length > 0) {
            throw new ApexDeploymentRestParameterException(
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
            throw new ApexDeploymentRestParameterException("error parsing argument \"port\" :" + e.getMessage(), e);
        }
        try {
            if (commandLine.hasOption('t')) {
                parameters.setTimeToLive(((Number) commandLine.getParsedOptionValue("time-to-live")).longValue());
            }
        } catch (final ParseException e) {
            throw new ApexDeploymentRestParameterException("error parsing argument \"time-to-live\" :" + e.getMessage(),
                    e);
        }

        return parameters;
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
        helpFormatter.printHelp(stringPrintWriter, 120, mainClassName + " [options...] ", "", options, 0, 0, "");

        return stringWriter.toString();
    }
}
