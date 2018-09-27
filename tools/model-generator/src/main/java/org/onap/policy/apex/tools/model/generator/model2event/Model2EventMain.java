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

package org.onap.policy.apex.tools.model.generator.model2event;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.tools.common.CliOptions;
import org.onap.policy.apex.tools.common.CliParser;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model 2 event generator with main method.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public final class Model2EventMain {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(Model2EventMain.class);

    /** The name of the application. */
    public static final String APP_NAME = "gen-model2event";

    /** The description 1-liner of the application. */
    public static final String APP_DESCRIPTION = "generates JSON templates for events generated from a policy model";

    /**
     * Constructor, run the command.
     * 
     * @param args the command line arguments
     * @param outStream the stream for command output
     */
    Model2EventMain(final String[] args, final PrintStream outStream) {
        SchemaParameters schemaParameters = new SchemaParameters();
        ParameterService.register(schemaParameters, true);
        
        final CliParser cli = new CliParser();
        cli.addOption(CliOptions.HELP);
        cli.addOption(CliOptions.VERSION);
        cli.addOption(CliOptions.MODELFILE);
        cli.addOption(CliOptions.TYPE);

        final CommandLine cmd = cli.parseCli(args);

        // help is an exit option, print usage and exit
        if (cmd == null || cmd.hasOption('h') || cmd.hasOption("help")) {
            outStream.println(getHelpString(cli));
            outStream.println();
            return;
        }

        // version is an exit option, print version and exit
        if (cmd.hasOption('v') || cmd.hasOption("version")) {
            outStream.println(APP_NAME + " " + cli.getAppVersion());
            outStream.println();
            return;
        }

        generateJsonEventSchema(cmd, outStream);
    }

    /**
     * Generate the JSON event schema.
     * 
     * @param cmd the command to run
     * @param outStream the output stream for output
     */
    private static void generateJsonEventSchema(final CommandLine cmd, final PrintStream outStream) {
        String modelFile = cmd.getOptionValue('m');
        if (modelFile == null) {
            modelFile = cmd.getOptionValue("model");
        }
        if (modelFile == null) {
            outStream.println(APP_NAME + ": no model file given, cannot proceed (try -h for help)");
            return;
        }

        String type = cmd.getOptionValue('t');
        if (type == null) {
            type = cmd.getOptionValue("type");
        }
        if (type == null) {
            outStream.println(APP_NAME + ": no event type given, cannot proceed (try -h for help)");
            return;
        }
        if (!"stimuli".equals(type) && !"response".equals(type) && !"internal".equals(type)) {
            outStream.println(APP_NAME + ": unknown type <" + type + ">, cannot proceed (try -h for help)");
            return;
        }

        outStream.println();
        outStream.println(APP_NAME + ": starting Event generator");
        outStream.println(" --> model file: " + modelFile);
        outStream.println(" --> type: " + type);
        outStream.println();
        outStream.println();

        try {
            final Model2JsonEventSchema app = new Model2JsonEventSchema(modelFile, type, APP_NAME);
            app.runApp();
        } catch (final ApexException aex) {
            String message = APP_NAME + ": caught APEX exception with message: " + aex.getMessage();
            outStream.println(message);
            LOGGER.warn(message, aex);
        }
    }

    /**
     * Get the help string for the application.
     * 
     * @param cli the command line options
     * @return the help string
     */
    private String getHelpString(final CliParser cli) {
        HelpFormatter formatter = new HelpFormatter();

        final StringWriter helpStringWriter = new StringWriter();
        final PrintWriter helpPrintWriter = new PrintWriter(helpStringWriter);

        formatter.printHelp(helpPrintWriter, 120, APP_NAME, APP_DESCRIPTION, cli.getOptions(), 2, 4, "");

        return helpStringWriter.toString();
    }

    /**
     * Main method to start the application.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        new Model2EventMain(args, System.out);
    }
}
