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

package org.onap.policy.apex.tools.common;

////
//// NOTE: This file contains tags for ASCIIDOC
//// DO NOT REMOVE any of those tag lines, e.g.
////// tag::**
////// end::**
////

import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Application CLI parser.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class CliParser {

    /** The local set of CLI options. */
    private final Options options;

    /** The command line, null on start, not null after successful parse. */
    private CommandLine cmd;

    /**
     * Creates a new CLI parser.
     */
    public CliParser() {
        options = new Options();
    }

    /**
     * Adds an option to the parser.
     *
     * @param option the new option, must not be null
     * @return self to allow chaining
     */
    public CliParser addOption(final Option option) {
        if (option == null) {
            throw new IllegalStateException("CLI parser: given option was null");
        }
        options.addOption(option);
        return this;
    }

    /**
     * Parses the arguments with the set options.
     *
     * @param args the arguments to parse
     * @return a command line with parsed arguments, null on parse errors.
     */
    public CommandLine parseCli(final String[] args) {
        final CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, args);
        } catch (final Exception ex) {
            Console.CONSOLE.error("Parsing failed, see reason and cause below");
            Console.CONSOLE.stacktrace(ex);
        }
        return cmd;
    }

    /**
     * Returns the parsed command line.
     *
     * @return the parsed command line, null if nothing parsed
     */
    public CommandLine getCommandLine() {
        return cmd;
    }

    /**
     * Returns the CLI options.
     *
     * @return CLI options
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Returns the version for an application as set by Maven.
     *
     * @return version, null if version file <code>/app-version.txt</code> was not found
     */
    @SuppressWarnings("resource")
    // tag::cliParserVersion[]
    public String getAppVersion() {
        return new Scanner(CliParser.class.getResourceAsStream("/app-version.txt"), "UTF-8").useDelimiter("\\A").next();
    }
    // end::cliParserVersion[]
}
