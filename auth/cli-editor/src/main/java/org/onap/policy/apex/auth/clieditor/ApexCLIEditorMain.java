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

package org.onap.policy.apex.auth.clieditor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.model.utilities.json.JSONHandler;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class initiates an Apex CLI editor from a java main method.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexCLIEditorMain {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexCLIEditorMain.class);

    // The editor parameters
    private CLIParameters parameters;

    // The CLI commands read in from JSON
    private CLICommands commands;

    // The Apex model properties read in from JSON
    private ApexModelProperties apexModelProperties;

    // The number of errors encountered in command processing
    private int errorCount = 0;

    /**
     * Instantiates the Apex CLI editor.
     *
     * @param args the command line arguments
     */
    public ApexCLIEditorMain(final String[] args) {
        LOGGER.info("Starting Apex CLI editor " + Arrays.toString(args) + " . . .");

        try {
            final CLIParameterParser parser = new CLIParameterParser();
            parameters = parser.parse(args);

            if (parameters.isHelpSet()) {
                parser.help(ApexCLIEditorMain.class.getCanonicalName());
                return;
            }
            parameters.validate();
        } catch (final Exception e) {
            LOGGER.error("start of Apex command line editor failed, " + e.getMessage());
            errorCount++;
            return;
        }

        LOGGER.debug("parameters are: " + parameters.toString());

        // Read the command definitions
        try {
            commands = new JSONHandler<CLICommands>().read(CLICommands.class, parameters.getMetadataStream());
        } catch (final Exception e) {
            LOGGER.error("start of Apex command line editor failed, error reading command metadata from "
                    + parameters.getMetadataLocation());
            LOGGER.error(e.getMessage());
            errorCount++;
            return;
        }

        // The JSON processing returns null if there is an empty file
        if (commands == null || commands.getCommandSet().isEmpty()) {
            LOGGER.error("start of Apex command line editor failed, no commands found in "
                    + parameters.getApexPropertiesLocation());
            errorCount++;
            return;
        }

        LOGGER.debug("found " + commands.getCommandSet().size() + " commands");

        // Read the Apex properties
        try {
            apexModelProperties = new JSONHandler<ApexModelProperties>().read(ApexModelProperties.class,
                    parameters.getApexPropertiesStream());
        } catch (final Exception e) {
            LOGGER.error("start of Apex command line editor failed, error reading Apex model properties from "
                    + parameters.getApexPropertiesLocation());
            LOGGER.error(e.getMessage());
            errorCount++;
            return;
        }

        // The JSON processing returns null if there is an empty file
        if (apexModelProperties == null) {
            LOGGER.error("start of Apex command line editor failed, no Apex model properties found in "
                    + parameters.getApexPropertiesLocation());
            errorCount++;
            return;
        }

        LOGGER.debug("model properties are: " + apexModelProperties.toString());

        // Find the system commands
        final Set<KeywordNode> systemCommandNodes = new TreeSet<>();
        for (final CLICommand command : commands.getCommandSet()) {
            if (command.isSystemCommand()) {
                systemCommandNodes.add(new KeywordNode(command.getName(), command));
            }
        }

        // Read in the command hierarchy, this builds a tree of commands
        final KeywordNode rootKeywordNode = new KeywordNode("root");
        for (final CLICommand command : commands.getCommandSet()) {
            rootKeywordNode.processKeywords(command.getKeywordlist(), command);
        }
        rootKeywordNode.addSystemCommandNodes(systemCommandNodes);

        // Create the model we will work towards
        ApexModelHandler modelHandler = null;
        try {
            modelHandler =
                    new ApexModelHandler(apexModelProperties.getProperties(), parameters.getInputModelFileName());
        } catch (final Exception e) {
            LOGGER.error("execution of Apex command line editor failed: " + e.getMessage());
            errorCount++;
            return;
        }

        final CLIEditorLoop cliEditorLoop =
                new CLIEditorLoop(apexModelProperties.getProperties(), modelHandler, rootKeywordNode);
        try {
            errorCount =
                    cliEditorLoop.runLoop(parameters.getCommandInputStream(), parameters.getOutputStream(), parameters);

            if (errorCount == 0) {
                LOGGER.info("Apex CLI editor completed execution");
            } else {
                LOGGER.error("execution of Apex command line editor failed: " + errorCount
                        + " command execution failure(s) occurred");
            }
        } catch (final IOException e) {
            LOGGER.error("execution of Apex command line editor failed: " + e.getMessage());
            return;
        }
    }

    /**
     * Get the number of errors encountered in command processing
     * 
     * @return the number of errors
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * Sets the number of errors encountered in command processing.
     * 
     * @param errorCount the number of errors
     */
    public void setErrorCount(final int errorCount) {
        this.errorCount = errorCount;
    }


    /**
     * The main method, kicks off the editor.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(args);

        // Only call system.exit on errors as it brings the JVM down
        if (cliEditor.getErrorCount() > 0) {
            System.exit(cliEditor.getErrorCount());
        }
    }
}
