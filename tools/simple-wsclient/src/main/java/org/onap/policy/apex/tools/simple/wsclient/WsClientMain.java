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

package org.onap.policy.apex.tools.simple.wsclient;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.Validate;
import org.onap.policy.apex.tools.common.CliOptions;
import org.onap.policy.apex.tools.common.CliParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple console application with main method.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public final class WsClientMain {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(WsClientMain.class);

    // String constants
    private static final String APP_NAME = "ws-client";
    private static final String APP_DESCRIPTION = "takes events from stdin and sends via WS to APEX"
                    + " and/or receives events from APEX via WS and prints them to standard out";

    /**
     * Run the command.
     * 
     * @param args the command line arguments
     * @param outStream stream for output
     */
    WsClientMain(final String[] args, final PrintStream outStream) {
        boolean console = false;

        final CliParser cli = new CliParser();
        cli.addOption(CliOptions.HELP);
        cli.addOption(CliOptions.VERSION);
        cli.addOption(CliOptions.CONSOLE);
        cli.addOption(CliOptions.SERVER);
        cli.addOption(CliOptions.PORT);

        final CommandLine cmd = cli.parseCli(args);

        // help is an exit option, print usage and exit
        if (cmd == null || cmd.hasOption('h') || cmd.hasOption("help")) {
            outStream.println(getHelpString(cli));
            outStream.println();
            return;
        }

        if (cmd.hasOption('c') || cmd.hasOption("console")) {
            console = true;
        }

        // version is an exit option, print version and exit
        if (cmd.hasOption('v') || cmd.hasOption("version")) {
            outStream.println(APP_NAME + " " + cli.getAppVersion());
            outStream.println();
            return;
        }

        runConsoleOrEcho(console, cmd, outStream);
    }

    /**
     * Run the console or echo.
     * 
     * @param console if true, run the console otherwise run echo
     * @param cmd the command line to run
     * @param outStream stream for output
     */
    private static void runConsoleOrEcho(final boolean console, final CommandLine cmd, final PrintStream outStream) {
        String server = cmd.getOptionValue('s');
        if (server == null) {
            server = cmd.getOptionValue("server");
        }
        if (server == null) {
            server = "localhost";
        }

        String port = cmd.getOptionValue('p');
        if (port == null) {
            port = cmd.getOptionValue("port");
        }
        if (port == null) {
            port = "8887";
        }

        if (console) {
            runConsole(server, port, outStream);
        } else {
            runEcho(server, port, outStream);
        }
    }

    /**
     * Runs the simple echo client.
     *
     * @param server the server, must not be blank
     * @param port the port, must not be blank
     * @param outStream stream for output
     */
    public static void runEcho(final String server, final String port, final PrintStream outStream) {
        Validate.notBlank(server);
        Validate.notBlank(port);

        outStream.println();
        outStream.println(APP_NAME + ": starting simple event echo");
        outStream.println(" --> server: " + server);
        outStream.println(" --> port: " + port);
        outStream.println();
        outStream.println("Once started, the application will simply print out all received events to standard out.");
        outStream.println("Each received event will be prefixed by '---' and suffixed by '===='");
        outStream.println();
        outStream.println();

        try {
            final SimpleEcho simpleEcho = new SimpleEcho(server, port, APP_NAME, outStream, outStream);
            simpleEcho.connect();
        } catch (final URISyntaxException uex) {
            String message = APP_NAME + ": URI exception, could not create URI from server and port settings";
            outStream.println(message);
            LOGGER.warn(message, uex);
        } catch (final NullPointerException nex) {
            String message = APP_NAME + ": null pointer, server or port were null";
            outStream.println(message);
            LOGGER.warn(message, nex);
        } catch (final IllegalArgumentException iex) {
            String message = APP_NAME + ": illegal argument, server or port were blank";
            outStream.println(message);
            LOGGER.warn(message, iex);
        }
    }

    /**
     * Runs the simple console.
     *
     * @param server the server, must not be blank
     * @param port the port, must not be blank
     * @param outStream stream for output
     */
    public static void runConsole(final String server, final String port, final PrintStream outStream) {
        Validate.notBlank(server);
        Validate.notBlank(port);
        Validate.notBlank(APP_NAME);

        outStream.println();
        outStream.println(APP_NAME + ": starting simple event console");
        outStream.println(" --> server: " + server);
        outStream.println(" --> port: " + port);
        outStream.println();
        outStream.println(" - terminate the application typing 'exit<enter>' or using 'CTRL+C'");
        outStream.println(" - events are created by a non-blank starting line and terminated by a blank line");
        outStream.println();
        outStream.println();

        try {
            final SimpleConsole simpleConsole = new SimpleConsole(server, port, APP_NAME, outStream, outStream);
            simpleConsole.runClient();
        } catch (final URISyntaxException uex) {
            String message = APP_NAME + ": URI exception, could not create URI from server and port settings";
            outStream.println(message);
            LOGGER.warn(message, uex);
        } catch (final NullPointerException nex) {
            String message = APP_NAME + ": null pointer, server or port were null";
            outStream.println(message);
            LOGGER.warn(message, nex);
        } catch (final IllegalArgumentException iex) {
            String message = APP_NAME + ": illegal argument, server or port were blank";
            outStream.println(message);
            LOGGER.warn(message, iex);
        } catch (final NotYetConnectedException nex) {
            String message = APP_NAME + ": not yet connected, connection to server took too long";
            outStream.println(message);
            LOGGER.warn(message, nex);
        } catch (final IOException ioe) {
            String message = APP_NAME + ": IO exception, something went wrong on the standard input";
            outStream.println(message);
            LOGGER.warn(message, ioe);
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
     * The main method for the WS applications.
     *
     * @param args command line argument s
     */
    public static void main(final String[] args) {
        new WsClientMain(args, System.out);
    }
}
