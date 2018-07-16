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

import org.apache.commons.cli.Option;

/**
 * Standard application CLI options.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public final class CliOptions {

    /** Private constructor to prevent instantiation. */
    private CliOptions() {}

    /** A console option with "-c" and "--console". */
    public static final Option CONSOLE =
            Option.builder("c").longOpt("console").desc("application as console with input from standard in").build();

    /** A help option with "-h" and "--help". */
    public static final Option HELP =
            Option.builder("h").longOpt("help").desc("prints this help and usage screen").build();

    /** A version option with "-v" and "--version". */
    public static final Option VERSION =
            Option.builder("v").longOpt("version").desc("prints the application version").build();

    /** A print-stacktrace option with "--print-stacktrace". */
    public static final Option PRINTSTACKTRACE =
            Option.builder().longOpt("print-stacktrace").desc("prints stack traces for any exception").build();

    /** A check-java-class option with "--check-java-class". */
    public static final Option CHECKJAVACLASS = Option.builder("j").longOpt("check-java-class")
            .desc("switch on checking of Java class references, requires JAR in class path").build();

    /** A quiet option with "-q" and "--quiet". */
    public static final Option QUIET =
            Option.builder("q").longOpt("quiet").desc("application in quiet mode, no output at all").build();

    /** A no-warning option with "--no-warnings". */
    public static final Option NOWARNINGS =
            Option.builder().longOpt("no-warnings").desc("switch off all warnings").build();

    /** A no-error option with "--no-errors". */
    public static final Option NOERRORS =
            Option.builder().longOpt("no-errors").desc("switch off error messages").build();

    /** A no-progress option with "--no-progress". */
    public static final Option SHOWPROGRESS =
            Option.builder().longOpt("no-progress").desc("switch off progress information").build();

    /** A file-in option with "-f" and "--input-file". */
    public static final Option FILEIN =
            Option.builder("f").hasArg().argName("FILE").longOpt("input-file").desc("set the input file").build();

    /** A file-out option with "-o" and "--output-file". */
    public static final Option FILEOUT = Option.builder("o").hasArg().argName("FILE").longOpt("output-file")
            .desc("set the output file").required(false).build();

    /** An overwrite option with "-ow" and "--overwrite". */
    public static final Option OVERWRITE = Option.builder("ow").required(false).longOpt("overwrite")
            .desc("overwrite the output file if it exists. This option can only be used with the -" + FILEOUT.getOpt()
                    + " option")
            .build();

    /** An option for the policy model file with "-m" and "--model". */
    public static final Option MODELFILE = Option.builder("m").hasArg().argName("MODEL-FILE").longOpt("model")
            .desc("set the input policy model file").build();

    /** A type option defining what type is used for events with "-t" and "--type". */
    public static final Option TYPE = Option.builder("t").hasArg().argName("TYPE").longOpt("type").desc(
            "set the event type for generation, one of: stimuli (trigger events), response (action events), internal (events between states)")
            .build();

    /** A server option with "-s" and "--server". */
    public static final Option SERVER = Option.builder("s").hasArg().argName("HOSTNAME").longOpt("server")
            .desc("set the Websocket server hostname, default: localhost").build();

    /** A port option with "-p" and "--port". */
    public static final Option PORT = Option.builder("p").hasArg().argName("PORT").longOpt("port")
            .desc("set the Websocket server port, default: 8887").build();

    /** A skip validation option with "-sv" and "--skip-validation". */
    public static final Option SKIPVALIDATION = Option.builder("sv").longOpt("skip-validation")
            .desc("switch of validation of the input file").required(false).type(boolean.class).build();
}
