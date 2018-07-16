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

package org.onap.policy.apex.tools.common.docs;

////
//// NOTE: This file contains tags for ASCIIDOC
//// DO NOT REMOVE any of those tag lines, e.g.
//// //tag::**
//// //end::**
////

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.junit.Test;
import org.onap.policy.apex.tools.common.CliOptions;
import org.onap.policy.apex.tools.common.CliParser;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Examples for documentation using {@link CliParser}.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class ExampleCliParser {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ExampleCliParser.class);

    /**
     * Test example parser.
     */
    @Test
    public void testExampleParser() {
        final String[] args = new String[] { "-h" };

        // tag::setApp[]
        final String appName = "test-app";
        final String appDescription = "a test app for documenting how to use the CLI utilities";
        // end::setApp[]

        // tag::setCli[]
        final CliParser cli = new CliParser();
        cli.addOption(CliOptions.HELP);
        cli.addOption(CliOptions.VERSION);
        cli.addOption(CliOptions.MODELFILE);
        // end::setCli[]

        // tag::parseCli[]
        final CommandLine cmd = cli.parseCli(args);
        // end::parseCli[]

        // tag::processCliHelp[]
        // help is an exit option, print usage and exit
        if (cmd.hasOption('h') || cmd.hasOption("help")) {
            final HelpFormatter formatter = new HelpFormatter();
            LOGGER.info(appName + " v" + cli.getAppVersion() + " - " + appDescription);
            formatter.printHelp(appName, cli.getOptions());
            return;
        }
        // end::processCliHelp[]

        // tag::processCliVersion[]
        // version is an exit option, print version and exit
        if (cmd.hasOption('v') || cmd.hasOption("version")) {
            LOGGER.info(appName + " " + cli.getAppVersion());
            return;
        }
        // end::processCliVersion[]

        // tag::processCliModel[]
        String modelFile = cmd.getOptionValue('m');
        if (modelFile == null) {
            modelFile = cmd.getOptionValue("model");
        }
        if (modelFile == null) {
            LOGGER.error(appName + ": no model file given, cannot proceed (try -h for help)");
            return;
        }
        // end::processCliModel[]

        // tag::someStartPrint[]
        LOGGER.info(appName + ": starting");
        LOGGER.info(" --> model file: " + modelFile);
        // end::someStartPrint[]

        // tag::yourApp[]
        // your code for the application here
        // e.g.
        // try {
        // Model2Cli app = new Model2Cli(modelFile, !cmd.hasOption("sv"), appName);
        // app.runApp();
        // }
        // catch(ApexException aex) {
        // LOGGER.error(appName + ": caught APEX exception with message: " + aex.getMessage());
        // }
        // end::yourApp[]
    }
}
