/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modification Copyright (C) 2020-2021 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.main;

import java.io.File;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.common.utils.cmd.CommandLineArgumentsHandler;
import org.onap.policy.common.utils.cmd.CommandLineException;
import org.onap.policy.common.utils.resources.MessageConstants;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;

/**
 * This class reads and handles command line parameters for the Apex main
 * program.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexCommandLineArguments extends CommandLineArgumentsHandler {
    // A system property holding the root directory for relative paths in the
    // configuration file
    public static final String APEX_RELATIVE_FILE_ROOT = "APEX_RELATIVE_FILE_ROOT";
    private static final String RELATIVE_FILE_ROOT = "relative file root \"";

    @Getter
    @Setter
    private String toscaPolicyFilePath = null;

    @Getter
    private String relativeFileRoot = null;

    private CommandLine cmd = null;

    /**
     * Construct the options for the CLI editor.
     */
    public ApexCommandLineArguments() {
        super(ApexMain.class.getName(), MessageConstants.POLICY_APEX_PDP, apexCustomOptions());
    }

    /**
     * Builds Apex custom options.
     */
    private static Options apexCustomOptions() {
        //@formatter:off
        var options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("outputs the usage of this command")
                .required(false)
                .type(Boolean.class)
                .build());
        options.addOption(Option.builder("v")
                .longOpt("version")
                .desc("outputs the version of Apex")
                .required(false)
                .type(Boolean.class)
                .build());
        options.addOption(Option.builder("rfr")
                .longOpt("relative-file-root")
                .desc("the root file path for relative file paths specified in the Apex configuration file, "
                        + "defaults to the current directory from where Apex is executed")
                .hasArg()
                .argName(APEX_RELATIVE_FILE_ROOT)
                .required(false)
                .type(String.class)
                .build());
        options.addOption(Option.builder("p").longOpt("tosca-policy-file")
            .desc("the full path to the ToscaPolicy file to use.").hasArg().argName("TOSCA_POLICY_FILE")
            .required(false)
            .type(String.class).build());
        //@formatter:on
        return options;
    }

    /**
     * Construct the options for the CLI editor and parse in the given arguments.
     *
     * @param args The command line arguments
     */
    public ApexCommandLineArguments(final String[] args) {
        // Set up the options with the default constructor
        this();

        // Parse the arguments
        try {
            parse(args);
        } catch (final CommandLineException e) {
            throw new ApexRuntimeException("parse error on Apex parameters", e);
        }
    }

    @Override
    public String parse(final String[] args) throws CommandLineException {
        // Clear all our arguments
        setToscaPolicyFilePath(null);
        setRelativeFileRoot(null);

        try {
            cmd = new DefaultParser().parse(apexCustomOptions(), args);
        } catch (final ParseException e) {
            throw new CommandLineException("invalid command line arguments specified", e);
        }

        // Arguments left over after Commons CLI does its stuff
        final String[] remainingArgs = cmd.getArgs();

        if (remainingArgs.length > 0 && cmd.hasOption('p') || remainingArgs.length > 1) {
            throw new CommandLineException("too many command line arguments specified: " + Arrays.toString(args));
        }

        if (remainingArgs.length == 1) {
            toscaPolicyFilePath = remainingArgs[0];
        }

        if (cmd.hasOption('h')) {
            return help();
        }

        if (cmd.hasOption('v')) {
            return version();
        }

        if (cmd.hasOption("rfr")) {
            setRelativeFileRoot(cmd.getOptionValue("rfr"));
        }

        if (cmd.hasOption('p')) {
            setToscaPolicyFilePath(cmd.getOptionValue('p'));
        }
        return null;
    }

    @Override
    public CommandLine getCommandLine() {
        return this.cmd;
    }

    /**
     * Validate the command line options.
     *
     * @throws ApexException on command argument validation errors
     */
    public void validateInputFiles() throws ApexException {
        try {
            validateReadableFile("Tosca Policy", toscaPolicyFilePath);
        } catch (CommandLineException e) {
            throw new ApexException(e.getMessage());
        }
        validateRelativeFileRoot();
    }

    /**
     * Sets the root file path for relative file paths in the configuration file.
     *
     * @param relativeFileRoot the configuration file path
     */
    public void setRelativeFileRoot(final String relativeFileRoot) {
        String relativeFileRootValue = relativeFileRoot;

        if (!ParameterValidationUtils.validateStringParameter(relativeFileRoot)) {
            relativeFileRootValue = System.getProperty(APEX_RELATIVE_FILE_ROOT);
        }

        if (!ParameterValidationUtils.validateStringParameter(relativeFileRootValue)) {
            relativeFileRootValue = System.getProperty("user.dir");
        } else if (!(new File(relativeFileRootValue).isAbsolute())) {
            relativeFileRootValue = System.getProperty("user.dir") + File.separator + relativeFileRootValue;
        }

        this.relativeFileRoot = relativeFileRootValue;
        System.setProperty(APEX_RELATIVE_FILE_ROOT, relativeFileRootValue);
    }

    /**
     * Validate the relative file root.
     */
    private void validateRelativeFileRoot() throws ApexException {
        var relativeFileRootPath = new File(relativeFileRoot);
        if (!relativeFileRootPath.isDirectory()) {
            throw new ApexException(RELATIVE_FILE_ROOT + relativeFileRoot + "\" does not exist or is not a directory");
        }

        if (!relativeFileRootPath.canRead()) {
            throw new ApexException(RELATIVE_FILE_ROOT + relativeFileRoot + "\" is not a readable directory");
        }

        if (!relativeFileRootPath.canExecute()) {
            throw new ApexException(RELATIVE_FILE_ROOT + relativeFileRoot + "\" is not an executable directory");
        }
    }

}
