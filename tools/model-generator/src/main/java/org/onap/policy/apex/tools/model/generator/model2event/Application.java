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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.tools.common.CliOptions;
import org.onap.policy.apex.tools.common.CliParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model 2 event generator with main method.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public final class Application {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    /** The name of the application. */
    public static final String APP_NAME = "gen-model2event";

    /** The description 1-liner of the application. */
    public static final String APP_DESCRIPTION = "generates JSON templates for events generated from a policy model";

    /** Private constructor to prevent instantiation. */
    private Application() {
    }

    // Input and output streams
    private static final PrintStream OUT_STREAM = System.out;
    private static final PrintStream ERR_STREAM = System.err;

    /**
     * Main method to start the application.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        final CliParser cli = new CliParser();
        cli.addOption(CliOptions.HELP);
        cli.addOption(CliOptions.VERSION);
        cli.addOption(CliOptions.MODELFILE);
        cli.addOption(CliOptions.TYPE);

        final CommandLine cmd = cli.parseCli(args);

        // help is an exit option, print usage and exit
        if (cmd.hasOption('h') || cmd.hasOption("help")) {
            final HelpFormatter formatter = new HelpFormatter();
            OUT_STREAM.println(APP_NAME + " v" + cli.getAppVersion() + " - " + APP_DESCRIPTION);
            formatter.printHelp(APP_NAME, cli.getOptions());
            OUT_STREAM.println();
            return;
        }

        // version is an exit option, print version and exit
        if (cmd.hasOption('v') || cmd.hasOption("version")) {
            OUT_STREAM.println(APP_NAME + " " + cli.getAppVersion());
            OUT_STREAM.println();
            return;
        }

        generateJsonEventScheam(cmd);
    }

    /**
     * Generate the JSON event schema.
     * 
     * @param cmd the command to run
     */
    private static void generateJsonEventScheam(final CommandLine cmd) {
        String modelFile = cmd.getOptionValue('m');
        if (modelFile == null) {
            modelFile = cmd.getOptionValue("model");
        }
        if (modelFile == null) {
            ERR_STREAM.println(APP_NAME + ": no model file given, cannot proceed (try -h for help)");
            return;
        }

        String type = cmd.getOptionValue('t');
        if (type == null) {
            type = cmd.getOptionValue("type");
        }
        if (type == null) {
            ERR_STREAM.println(APP_NAME + ": no event type given, cannot proceed (try -h for help)");
            return;
        }
        if (!"stimuli".equals(type) && !"response".equals(type) && !"internal".equals(type)) {
            ERR_STREAM.println(APP_NAME + ": unknown type <" + type + ">, cannot proceed (try -h for help)");
            return;
        }

        OUT_STREAM.println();
        OUT_STREAM.println(APP_NAME + ": starting Event generator");
        OUT_STREAM.println(" --> model file: " + modelFile);
        OUT_STREAM.println(" --> type: " + type);
        OUT_STREAM.println();
        OUT_STREAM.println();

        try {
            final Model2JsonEventSchema app = new Model2JsonEventSchema(modelFile, type, APP_NAME);
            app.runApp();
        } catch (final ApexException aex) {
            String message = APP_NAME + ": caught APEX exception with message: " + aex.getMessage();
            ERR_STREAM.println(message);
            LOGGER.warn(message, aex);
        }
    }
}
