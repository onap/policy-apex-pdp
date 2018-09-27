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

package org.onap.policy.apex.tools.model.generator.model2cli;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.onap.policy.apex.tools.common.CliOptions;
import org.onap.policy.apex.tools.common.CliParser;
import org.onap.policy.apex.tools.common.OutputFile;

/**
 * Process an Apex Policy Model file to generate the CLI commands to generate an equivalent Apex Policy Model.
 *
 * @author Sven van der Meer &lt;sven.van.der.meer@ericsson.com&gt;
 */
public final class Model2CliMain {
    /** The name of the application. */
    public static final String APP_NAME = "gen-model2cli";

    /** The description 1-liner of the application. */
    public static final String APP_DESCRIPTION = "generates CLI Editor Commands from a policy model";

    /**
     * Run the tool.
     * 
     * @param args the command line arguments
     * @param outStream for command output
     * @param errStream for command errors
     */
    Model2CliMain(final String[] args, final PrintStream outStream, final PrintStream errStream) {
        final CliParser cli = new CliParser();
        cli.addOption(CliOptions.HELP);
        cli.addOption(CliOptions.VERSION);
        cli.addOption(CliOptions.SKIPVALIDATION);
        cli.addOption(CliOptions.MODELFILE);
        cli.addOption(CliOptions.FILEOUT);
        cli.addOption(CliOptions.OVERWRITE);

        CommandLine cmd = cli.parseCli(args);

        // help is an exit option, print usage and exit
        if (cmd == null || cmd.hasOption(CliOptions.HELP.getOpt())) {
            outStream.println(getHelpString(cli));
            outStream.println();
            return;
        }

        // version is an exit option, print version and exit
        if (cmd.hasOption(CliOptions.VERSION.getOpt())) {
            outStream.println(APP_NAME + " " + cli.getAppVersion());
            return;
        }

        String modelFile = cmd.getOptionValue(CliOptions.MODELFILE.getOpt());
        if (modelFile == null) {
            errStream.println(APP_NAME + ": no '-" + CliOptions.MODELFILE.getOpt()
                            + "' model file given, cannot proceed (try -h for help)");
            return;
        } else {
            modelFile = cmd.getOptionValue("model");
        }

        OutputFile outfile = null;
        final String of = cmd.getOptionValue(CliOptions.FILEOUT.getOpt());
        final boolean overwrite = cmd.hasOption(CliOptions.OVERWRITE.getOpt());
        if (overwrite && of == null) {
            errStream.println(APP_NAME + ": error with '-" + CliOptions.OVERWRITE.getOpt()
                            + "' option. This option is only valid if a '-" + CliOptions.FILEOUT.getOpt()
                            + "' option is also used. Cannot proceed (try -h for help)");
            return;
        }
        if (of != null) {
            outfile = new OutputFile(of, overwrite);
            final String isoutfileok = outfile.validate();
            if (isoutfileok != null) {
                errStream.println(APP_NAME + ": error with '-" + CliOptions.FILEOUT.getOpt() + "' option: \""
                                + isoutfileok + "\". Cannot proceed (try -h for help)");
                return;
            }
        }

        if (outfile == null) {
            outStream.println();
            outStream.println(APP_NAME + ": starting CLI generator");
            outStream.println(" --> model file: " + modelFile);
            outStream.println();
            outStream.println();
        }

        final Model2Cli app = new Model2Cli(modelFile, outfile, !cmd.hasOption("sv"), APP_NAME);
        app.runApp();
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
        new Model2CliMain(args, System.out, System.err);
    }
}
