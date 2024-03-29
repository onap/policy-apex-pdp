/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.auth.clieditor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.auth.clieditor.utils.CliUtils;
import org.onap.policy.apex.model.utilities.json.JsonHandler;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class initiates an Apex CLI editor from a java main method.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexCommandLineEditorMain {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexCommandLineEditorMain.class);

    // The editor parameters
    private CommandLineParameters parameters;

    // The CLI commands read in from JSON
    private CommandLineCommands commands;

    // The Apex model properties read in from JSON
    private ApexModelProperties apexModelProperties;

    // The number of errors encountered in command processing
    @Getter
    @Setter
    private int errorCount = 0;

    /**
     * Instantiates the Apex CLI editor.
     *
     * @param args the command line arguments
     */
    public ApexCommandLineEditorMain(final String[] args) {
        String startMessage = "Starting Apex CLI editor " + Arrays.toString(args) + " . . .";
        LOGGER.info(startMessage);

        try {
            final var parser = new CommandLineParameterParser();
            parameters = parser.parse(args);

            if (parameters.isHelpSet()) {
                CliUtils.help(ApexCommandLineEditorMain.class.getName(), parser.getOptions());
                return;
            }
            parameters.validate();
        } catch (final Exception e) {
            LOGGER.error("start of Apex command line editor failed, ", e);
            errorCount++;
            return;
        }

        String message = "parameters are: " + parameters.toString();
        LOGGER.debug(message);

        // Read the command definitions
        try {
            commands = new JsonHandler<CommandLineCommands>().read(CommandLineCommands.class,
                            parameters.getMetadataStream());
        } catch (final Exception e) {
            LOGGER.error("start of Apex command line editor failed, error reading command metadata from {}",
                            parameters.getMetadataLocation(), e);
            errorCount++;
            return;
        }

        // The JSON processing returns null if there is an empty file
        if (commands == null || commands.getCommandSet().isEmpty()) {
            LOGGER.error("start of Apex command line editor failed, no commands found in {}",
                            parameters.getApexPropertiesLocation());
            errorCount++;
            return;
        }

        LOGGER.debug("found {} commands", commands.getCommandSet().size());

        // Read the Apex properties
        try {
            apexModelProperties = new JsonHandler<ApexModelProperties>().read(ApexModelProperties.class,
                            parameters.getApexPropertiesStream());
        } catch (final Exception e) {
            LOGGER.error("start of Apex command line editor failed, error reading Apex model properties from "
                            + parameters.getApexPropertiesLocation(), e);
            errorCount++;
            return;
        }

        // The JSON processing returns null if there is an empty file
        if (apexModelProperties == null) {
            LOGGER.error("start of Apex command line editor failed, no Apex model properties found in {}",
                            parameters.getApexPropertiesLocation());
            errorCount++;
            return;
        }

        var modelPropertiesString = "model properties are: " + apexModelProperties.toString();
        LOGGER.debug(modelPropertiesString);

        // Find the system commands
        final Set<KeywordNode> systemCommandNodes = new TreeSet<>();
        for (final CommandLineCommand command : commands.getCommandSet()) {
            if (command.isSystemCommand()) {
                systemCommandNodes.add(new KeywordNode(command.getName(), command));
            }
        }

        // Read in the command hierarchy, this builds a tree of commands
        final var rootKeywordNode = new KeywordNode("root");
        for (final CommandLineCommand command : commands.getCommandSet()) {
            rootKeywordNode.processKeywords(command.getKeywordlist(), command);
        }
        rootKeywordNode.addSystemCommandNodes(systemCommandNodes);

        // Create the model we will work towards
        ApexModelHandler modelHandler = null;
        try {
            modelHandler = new ApexModelHandler(apexModelProperties.getProperties(),
                            parameters.getInputModelFileName());
        } catch (final Exception e) {
            LOGGER.error("execution of Apex command line editor failed: ", e);
            errorCount++;
            return;
        }

        final var cliEditorLoop = new CommandLineEditorLoop(apexModelProperties.getProperties(),
                        modelHandler, rootKeywordNode);
        try {
            errorCount = cliEditorLoop.runLoop(parameters.getCommandInputStream(), parameters.getOutputStream(),
                            parameters);

            if (errorCount == 0) {
                LOGGER.info("Apex CLI editor completed execution");
            } else {
                LOGGER.error("execution of Apex command line editor failed: {} command execution failure(s) occurred",
                                errorCount);
            }
        } catch (final IOException e) {
            LOGGER.error("execution of Apex command line editor failed: " + e.getMessage(), e);
        }
    }

    /**
     * The main method, kicks off the editor.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        final var cliEditor = new ApexCommandLineEditorMain(args);

        // Only call system.exit on errors as it brings the JVM down
        if (cliEditor.getErrorCount() > 0) {
            System.exit(cliEditor.getErrorCount());
        }
    }
}
