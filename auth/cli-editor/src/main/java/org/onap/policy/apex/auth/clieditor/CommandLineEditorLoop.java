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

import static org.onap.policy.apex.model.utilities.TreeMapUtils.findMatchingEntries;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.model.utilities.TreeMapUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class implements the editor loop, the loop of execution that continuously executes commands until the quit
 * command is issued or EOF is detected on input.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CommandLineEditorLoop {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(CommandLineEditorLoop.class);

    // Recurring string constants
    private static final String COMMAND = "command ";
    private static final String COMMAND_LINE_ERROR = "command line error";

    // The model handler that is handling the API towards the Apex model being editied
    private final ApexModelHandler modelHandler;

    // Holds the current location in the keyword hierarchy
    private final ArrayDeque<KeywordNode> keywordNodeDeque = new ArrayDeque<>();

    // Logic block tags
    private final String logicBlockStartTag;
    private final String logicBlockEndTag;

    // File Macro tag
    private final String macroFileTag;

    /**
     * Initiate the loop with the keyword node tree.
     *
     * @param properties The CLI editor properties defined for execution
     * @param modelHandler the model handler that will handle commands
     * @param rootKeywordNode The root keyword node tree
     */
    public CommandLineEditorLoop(final Properties properties, final ApexModelHandler modelHandler,
                    final KeywordNode rootKeywordNode) {
        this.modelHandler = modelHandler;
        keywordNodeDeque.push(rootKeywordNode);

        logicBlockStartTag = properties.getProperty("DEFAULT_LOGIC_BLOCK_START_TAG");
        logicBlockEndTag = properties.getProperty("DEFAULT_LOGIC_BLOCK_END_TAG");
        macroFileTag = properties.getProperty("DEFAULT_MACRO_FILE_TAG");
    }

    /**
     * Run a command loop.
     *
     * @param inputStream The stream to read commands from
     * @param outputStream The stream to write command output and messages to
     * @param parameters The parameters for the CLI editor
     * @return the exit code from command processing
     * @throws IOException Thrown on exceptions on IO
     */
    public int runLoop(final InputStream inputStream, final OutputStream outputStream,
                    final CommandLineParameters parameters) throws IOException {
        // Readers and writers for input and output
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));

        // The parser parses the input lines into commands and arguments
        final CommandLineParser parser = new CommandLineParser();

        // The execution status has the result of the latest command and a cumulative error count
        MutablePair<Result, Integer> executionStatus = new MutablePair<>(Result.SUCCESS, 0);

        // The main loop for command handing, it continues until EOF on the input stream or until a
        // quit command
        while (!endOfCommandExecution(executionStatus, parameters)) {
            processIncomingCommands(parameters, reader, writer, parser, executionStatus);
        }

        // Get the output model
        if (!parameters.isSuppressModelOutputSet()) {
            final String modelString = modelHandler.writeModelToString(writer);

            if (parameters.checkSetOutputModelFileName()) {
                TextFileUtils.putStringAsTextFile(modelString, parameters.getOutputModelFileName());
            } else {
                writer.println(modelString);
            }
        }

        reader.close();
        writer.close();

        return executionStatus.getRight();
    }

    /**
     * Check if the command processing loop has come to an end.
     * 
     * @param executionStatus a pair containing the result of the last command and the accumulated error count
     * @param parameters the input parameters for command execution
     * @return true if the command processing loop should exit
     */
    private boolean endOfCommandExecution(Pair<Result, Integer> executionStatus, CommandLineParameters parameters) {
        if (executionStatus.getLeft() == Result.FINISHED) {
            return true;
        }

        return executionStatus.getRight() > 0 && !parameters.isIgnoreCommandFailures();
    }

    /**
     * Process the incoming commands one by one.
     * 
     * @param parameters the parameters to the CLI editor
     * @param reader the reader to read the logic block from
     * @param writer the writer to write results and error messages on
     * @param executionStatus the status of the logic block read
     */
    private void processIncomingCommands(final CommandLineParameters parameters, final BufferedReader reader,
                    final PrintWriter writer, final CommandLineParser parser,
                    MutablePair<Result, Integer> executionStatus) {

        try {
            // Output prompt and get a line of input
            writer.print(getPrompt());
            writer.flush();
            String line = reader.readLine();
            if (line == null) {
                executionStatus.setLeft(Result.FINISHED);
                return;
            }

            // Expand any macros in the script
            while (line.contains(macroFileTag)) {
                line = expandMacroFile(parameters, line);
            }

            if (parameters.isEchoSet()) {
                writer.println(line);
            }

            String logicBlock = null;
            if (line.trim().endsWith(logicBlockStartTag)) {
                line = line.replace(logicBlockStartTag, "").trim();

                logicBlock = readLogicBlock(parameters, reader, writer, executionStatus);
            }

            // Parse the line into a list of commands and arguments
            final List<String> commandWords = parser.parse(line, logicBlock);

            // Find the command, if the command is null, then we are simply changing position in
            // the hierarchy
            final CommandLineCommand command = findCommand(commandWords);
            if (command != null) {
                // Check the arguments of the command
                final TreeMap<String, CommandLineArgumentValue> argumentValues = getArgumentValues(command,
                                commandWords);

                // Execute the command, a FINISHED result means a command causes the loop to
                // leave execution
                executionStatus.setLeft(executeCommand(command, argumentValues, writer));
                if (ApexApiResult.Result.isNok(executionStatus.getLeft())) {
                    executionStatus.setRight(executionStatus.getRight() + 1);
                }
            }
        }
        // Print any error messages from command parsing and finding
        catch (final CommandLineException e) {
            writer.println(e.getMessage());
            executionStatus.setRight(executionStatus.getRight() + 1);
            LOGGER.debug(COMMAND_LINE_ERROR, e);
        } catch (final Exception e) {
            e.printStackTrace(writer);
            LOGGER.error(COMMAND_LINE_ERROR, e);
        }
    }

    /**
     * Read a logic block, a block of program logic for a policy.
     * 
     * @param parameters the parameters to the CLI editor
     * @param reader the reader to read the logic block from
     * @param writer the writer to write results and error messages on
     * @param executionStatus the status of the logic block read
     * @return the result of the logic block read
     */
    private String readLogicBlock(final CommandLineParameters parameters, final BufferedReader reader,
                    final PrintWriter writer, MutablePair<Result, Integer> executionStatus) {
        String logicBlock;
        logicBlock = "";

        while (true) {
            try {
                String logicLine = reader.readLine();
                if (logicLine == null) {
                    return null;
                }

                while (logicLine.contains(macroFileTag)) {
                    logicLine = expandMacroFile(parameters, logicLine);
                }

                if (parameters.isEchoSet()) {
                    writer.println(logicLine);
                }

                if (logicLine.trim().endsWith(logicBlockEndTag)) {
                    logicBlock += logicLine.replace(logicBlockEndTag, "").trim() + "\n";
                    return logicBlock;
                } else {
                    logicBlock += logicLine + "\n";
                }
            }
            // Print any error messages from command parsing and finding
            catch (final CommandLineException e) {
                writer.println(e.getMessage());
                executionStatus.setRight(executionStatus.getRight() + 1);
                LOGGER.debug(COMMAND_LINE_ERROR, e);
                continue;
            } catch (final Exception e) {
                e.printStackTrace(writer);
                LOGGER.error(COMMAND_LINE_ERROR, e);
            }
        }
    }

    /**
     * Output a prompt that indicates where in the keyword hierarchy we are.
     *
     * @return A string with the prompt
     */
    private String getPrompt() {
        final StringBuilder builder = new StringBuilder();
        final Iterator<KeywordNode> keynodeDequeIter = keywordNodeDeque.descendingIterator();

        while (keynodeDequeIter.hasNext()) {
            builder.append('/');
            builder.append(keynodeDequeIter.next().getKeyword());
        }
        builder.append("> ");

        return builder.toString();
    }

    /**
     * Finds a command for the given input command words. Command words need only ne specified enough to uniquely
     * identify them. Therefore, "p s o c" will find the command "policy state output create"
     *
     * @param commandWords The commands and arguments parsed from the command line by the parser
     * @return The found command
     */

    private CommandLineCommand findCommand(final List<String> commandWords) {
        CommandLineCommand command = null;

        final KeywordNode startKeywordNode = keywordNodeDeque.peek();

        // Go down through the keywords searching for the command
        for (int i = 0; i < commandWords.size(); i++) {
            final KeywordNode searchKeywordNode = keywordNodeDeque.peek();

            // We have got to the arguments, time to stop looking
            if (commandWords.get(i).indexOf('=') >= 0) {
                unwindStack(startKeywordNode);
                throw new CommandLineException("command not found: " + stringAL2String(commandWords));
            }

            // If the node entries found is not equal to one, then we have either no command or more
            // than one command matching
            final List<Entry<String, KeywordNode>> foundNodeEntries = findMatchingEntries(
                            searchKeywordNode.getChildren(), commandWords.get(i));
            if (foundNodeEntries.isEmpty()) {
                unwindStack(startKeywordNode);
                throw new CommandLineException("command not found: " + stringAL2String(commandWords));
            } else if (foundNodeEntries.size() > 1) {
                unwindStack(startKeywordNode);
                throw new CommandLineException("multiple commands matched: " + stringAL2String(commandWords) + " ["
                                + nodeAL2String(foundNodeEntries) + ']');
            }

            // Record the fully expanded command word
            commandWords.set(i, foundNodeEntries.get(0).getKey());

            // Check if there is a command
            final KeywordNode childKeywordNode = foundNodeEntries.get(0).getValue();
            command = childKeywordNode.getCommand();

            // If the command is null, we go into a sub mode, otherwise we unwind the stack of
            // commands and return the found command
            if (command == null) {
                keywordNodeDeque.push(childKeywordNode);
            } else {
                unwindStack(startKeywordNode);
                return command;
            }
        }

        return null;
    }

    /**
     * Unwind the stack of keyword node entries we have placed on the queue in a command search.
     *
     * @param startKeywordNode The point on the queue we want to unwind to
     */
    private void unwindStack(final KeywordNode startKeywordNode) {
        // Unwind the stack
        while (true) {
            if (keywordNodeDeque.peek().equals(startKeywordNode)) {
                return;
            }
            keywordNodeDeque.pop();
        }
    }

    /**
     * Check the arguments of the command.
     *
     * @param command The command to check
     * @param commandWords The command words entered
     * @return the argument values
     */
    private TreeMap<String, CommandLineArgumentValue> getArgumentValues(final CommandLineCommand command,
                    final List<String> commandWords) {
        final TreeMap<String, CommandLineArgumentValue> argumentValues = new TreeMap<>();
        for (final CommandLineArgument argument : command.getArgumentList()) {
            if (argument != null) {
                argumentValues.put(argument.getArgumentName(), new CommandLineArgumentValue(argument));
            }
        }

        // Set the value of the arguments
        for (final Entry<String, String> argument : getCommandArguments(commandWords)) {
            final List<Entry<String, CommandLineArgumentValue>> foundArguments = TreeMapUtils
                            .findMatchingEntries(argumentValues, argument.getKey());
            if (foundArguments.isEmpty()) {
                throw new CommandLineException(COMMAND + stringAL2String(commandWords) + ": " + " argument \""
                                + argument.getKey() + "\" not allowed on command");
            } else if (foundArguments.size() > 1) {
                throw new CommandLineException(COMMAND + stringAL2String(commandWords) + ": " + " argument " + argument
                                + " matches multiple arguments [" + argumentAL2String(foundArguments) + ']');
            }

            // Set the value of the argument, stripping off any quotes
            final String argumentValue = argument.getValue().replaceAll("^\"", "").replaceAll("\"$", "");
            foundArguments.get(0).getValue().setValue(argumentValue);
        }

        // Now check all mandatory arguments are set
        for (final CommandLineArgumentValue argumentValue : argumentValues.values()) {
            // Argument values are null by default so if this argument is not nullable it is
            // mandatory
            if (!argumentValue.isSpecified() && !argumentValue.getCliArgument().isNullable()) {
                throw new CommandLineException(COMMAND + stringAL2String(commandWords) + ": " + " mandatory argument \""
                                + argumentValue.getCliArgument().getArgumentName() + "\" not specified");
            }
        }

        return argumentValues;
    }

    /**
     * Get the arguments of the command, the command words have already been conditioned into an array starting with the
     * command words and ending with the arguments as name=value tuples.
     *
     * @param commandWords The command words entered by the user
     * @return the arguments as an entry array list
     */
    private ArrayList<Entry<String, String>> getCommandArguments(final List<String> commandWords) {
        final ArrayList<Entry<String, String>> arguments = new ArrayList<>();

        // Iterate over the command words, arguments are of the format name=value
        for (final String word : commandWords) {
            final int equalsPos = word.indexOf('=');
            if (equalsPos > 0) {
                arguments.add(new SimpleEntry<>(word.substring(0, equalsPos),
                                word.substring(equalsPos + 1, word.length())));
            }
        }

        return arguments;
    }

    /**
     * Execute system and editor commands.
     *
     * @param command The command to execute
     * @param argumentValues The arguments input on the command line to invoke the command
     * @param writer The writer to use for any output from the command
     * @return the result of execution of the command
     */
    private Result executeCommand(final CommandLineCommand command,
                    final TreeMap<String, CommandLineArgumentValue> argumentValues, final PrintWriter writer) {
        if (command.isSystemCommand()) {
            return exceuteSystemCommand(command, writer);
        } else {
            return modelHandler.executeCommand(command, argumentValues, writer);
        }
    }

    /**
     * Execute system commands.
     *
     * @param command The command to execute
     * @param writer The writer to use for any output from the command
     * @return the result of execution of the command
     */
    private Result exceuteSystemCommand(final CommandLineCommand command, final PrintWriter writer) {
        if ("back".equals(command.getName())) {
            return executeBackCommand();
        } else if ("help".equals(command.getName())) {
            return executeHelpCommand(writer);
        } else if ("quit".equals(command.getName())) {
            return executeQuitCommand();
        } else {
            return Result.SUCCESS;
        }
    }

    /**
     * Execute the "back" command.
     *
     * @return the result of execution of the command
     */
    private Result executeBackCommand() {
        if (keywordNodeDeque.size() > 1) {
            keywordNodeDeque.pop();
        }
        return Result.SUCCESS;
    }

    /**
     * Execute the "quit" command.
     *
     * @return the result of execution of the command
     */
    private Result executeQuitCommand() {
        return Result.FINISHED;
    }

    /**
     * Execute the "help" command.
     *
     * @param writer The writer to use for output from the command
     * @return the result of execution of the command
     */
    private Result executeHelpCommand(final PrintWriter writer) {
        for (final CommandLineCommand command : keywordNodeDeque.peek().getCommands()) {
            writer.println(command.getHelp());
        }
        return Result.SUCCESS;
    }

    /**
     * Helper method to output an array list of keyword node entries to a string.
     *
     * @param nodeEntryArrayList the array list of keyword node entries
     * @return the string
     */
    private String nodeAL2String(final List<Entry<String, KeywordNode>> nodeEntryArrayList) {
        final ArrayList<String> stringArrayList = new ArrayList<>();
        for (final Entry<String, KeywordNode> node : nodeEntryArrayList) {
            stringArrayList.add(node.getValue().getKeyword());
        }

        return stringAL2String(stringArrayList);
    }

    /**
     * Helper method to output an array list of argument entries to a string.
     *
     * @param argumentArrayList the argument array list
     * @return the string
     */
    private String argumentAL2String(final List<Entry<String, CommandLineArgumentValue>> argumentArrayList) {
        final ArrayList<String> stringArrayList = new ArrayList<>();
        for (final Entry<String, CommandLineArgumentValue> argument : argumentArrayList) {
            stringArrayList.add(argument.getValue().getCliArgument().getArgumentName());
        }

        return stringAL2String(stringArrayList);
    }

    /**
     * Helper method to output an array list of strings to a string.
     *
     * @param stringArrayList the array list of strings
     * @return the string
     */
    private String stringAL2String(final List<String> stringArrayList) {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (final String word : stringArrayList) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append(word);
        }

        return builder.toString();
    }

    /**
     * This method reads in the file from a file macro statement, expands the macro, and replaces the Macro tag in the
     * line with the file contents.
     * 
     * @param parameters The parameters for the CLI editor
     * @param line The line with the macro keyword in it
     * @return the expanded line
     */
    private String expandMacroFile(final CommandLineParameters parameters, final String line) {
        final int macroTagPos = line.indexOf(macroFileTag);

        // Get the line before and after the macro tag
        final String lineBeforeMacroTag = line.substring(0, macroTagPos);
        final String lineAfterMacroTag = line.substring(macroTagPos + macroFileTag.length()).replaceAll("^\\s*", "");

        // Get the file name that is the argument of the Macro tag
        final String[] lineWords = lineAfterMacroTag.split("\\s+");

        if (lineWords.length == 0) {
            throw new CommandLineException("no file name specified for Macro File Tag");
        }

        // Get the macro file name and the remainder of the line after the file name
        String macroFileName = lineWords[0];
        final String lineAfterMacroFileName = lineAfterMacroTag.replaceFirst(macroFileName, "");

        if (macroFileName.length() > 2 && macroFileName.startsWith("\"") && macroFileName.endsWith("\"")) {
            macroFileName = macroFileName.substring(1, macroFileName.length() - 1);
        } else {
            throw new CommandLineException("macro file name \"" + macroFileName
                            + "\" must exist and be quoted with double quotes \"\"");
        }

        // Append the working directory to the macro file name
        macroFileName = parameters.getWorkingDirectory() + File.separatorChar + macroFileName;

        // Now, get the text file for the argument of the macro
        String macroFileContents = null;
        try {
            macroFileContents = TextFileUtils.getTextFileAsString(macroFileName);
        } catch (final IOException e) {
            throw new CommandLineException("file \"" + macroFileName + "\" specified in Macro File Tag not found", e);
        }

        return lineBeforeMacroTag + macroFileContents + lineAfterMacroFileName;
    }
}
