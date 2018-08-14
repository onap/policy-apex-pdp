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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * This class reads and handles command line parameters to the Apex CLI editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CLIParameters {
    // Default location of the command definition meta data in JSON
    private static final String JSON_COMMAND_METADATA_RESOURCE = "etc/editor/Commands.json";
    private static final String APEX_MODEL_PROPERTIES_RESOURCE = "etc/editor/ApexModelProperties.json";

    // The editor parameters
    private boolean helpSet = false;
    private String metadataFileName = null;
    private String apexPropertiesFileName = null;
    private String commandFileName = null;
    private String inputModelFileName = null;
    private String outputModelFileName = null;
    private String workingDirectory = null;
    private String logFileName = null;
    private boolean echo = false;
    private boolean suppressLog = false;
    private boolean suppressModelOutput = false;
    private boolean ignoreCommandFailuresSet = false;
    private boolean ignoreCommandFailures = false;

    /**
     * Validates the command line parameters.
     */
    public void validate() {
        validateReadableFile("Metadata File", metadataFileName);
        validateReadableFile("Properties File", apexPropertiesFileName);
        validateReadableFile("Command File", commandFileName);
        validateReadableFile("Input Model File", inputModelFileName);
        validateWritableFile("Output Model File", outputModelFileName);
        validateWritableFile("Log File", logFileName);
        validateWritableDirectory("Working Directory", workingDirectory);

        if (isSuppressLogSet()) {
            setEcho(false);
        } else {
            if (checkSetCommandFileName()) {
                setEcho(true);
                if (!checkSetIgnoreCommandFailures()) {
                    setIgnoreCommandFailures(false);
                }
            } else {
                setEcho(false);
                if (!checkSetIgnoreCommandFailures()) {
                    setIgnoreCommandFailures(true);
                }
            }
        }
    }

    /**
     * Gets the command metadata for the editor commands as a stream.
     *
     * @return the command metadata for the editor commands as a stream.
     * @throws IOException the IO exception
     */
    public InputStream getMetadataStream() throws IOException {
        if (metadataFileName == null) {
            return ResourceUtils.getResourceAsStream(JSON_COMMAND_METADATA_RESOURCE);
        } else {
            return new FileInputStream(new File(metadataFileName));
        }
    }

    /**
     * Gets the location of command metadata for the editor commands.
     *
     * @return the location of command metadata for the editor commands
     */
    public String getMetadataLocation() {
        if (metadataFileName == null) {
            return "resource: \"" + JSON_COMMAND_METADATA_RESOURCE + "\"";
        } else {
            return "file: \"" + metadataFileName + "\"";
        }
    }

    /**
     * Gets the properties that are used for command default values as a stream.
     *
     * @return the properties that are used for command default values as a stream
     * @throws IOException the IO exception
     */
    public InputStream getApexPropertiesStream() throws IOException {
        if (apexPropertiesFileName == null) {
            return ResourceUtils.getResourceAsStream(APEX_MODEL_PROPERTIES_RESOURCE);
        } else {
            return new FileInputStream(new File(apexPropertiesFileName));
        }
    }

    /**
     * Gets the location of the properties that are used for command default values.
     *
     * @return the location of the properties that are used for command default values
     */
    public String getApexPropertiesLocation() {
        if (metadataFileName == null) {
            return "resource: \"" + APEX_MODEL_PROPERTIES_RESOURCE + "\"";
        } else {
            return "file: \"" + apexPropertiesFileName + "\"";
        }
    }

    /**
     * Gets the input stream on which commands are being received.
     *
     * @return the input stream on which commands are being received
     * @throws IOException the IO exception
     */
    public InputStream getCommandInputStream() throws IOException {
        if (commandFileName == null) {
            return System.in;
        } else {
            return new FileInputStream(new File(commandFileName));
        }
    }

    /**
     * Gets the output stream on which command result messages are being output.
     *
     * @return the output stream on which command result messages are being output
     * @throws IOException the IO exception
     */
    public OutputStream getOutputStream() throws IOException {
        // Check if log suppression is active, if so, consume all output on a byte array output stream
        if (isSuppressLogSet()) {
            return new ByteArrayOutputStream();

        }
        if (logFileName == null) {
            return System.out;
        } else {
            return new FileOutputStream(new File(logFileName), true);
        }
    }

    /**
     * Validate that a file is readable.
     *
     * @param fileTag the file tag, a tag used for information and error messages
     * @param fileName the file name to check
     */
    private void validateReadableFile(final String fileTag, final String fileName) {
        if (fileName == null) {
            return;
        }
        final File theFile = new File(fileName);
        final String prefixExceptionMessage = "File " + fileName + "of type " + fileTag;

        if (!theFile.exists()) {
            throw new CLIException(prefixExceptionMessage + " does not exist");
        }
        if (!theFile.isFile()) {
            throw new CLIException(prefixExceptionMessage + " is not a normal file");
        }
        if (!theFile.canRead()) {
            throw new CLIException(prefixExceptionMessage + " is ureadable");
        }
    }

    /**
     * Validate that a file is writable.
     *
     * @param fileTag the file tag, a tag used for information and error messages
     * @param fileName the file name to check
     */
    private void validateWritableFile(final String fileTag, final String fileName) {
        if (fileName == null) {
            return;
        }
        final File theFile = new File(fileName);
        final String prefixExceptionMessage = "File " + fileName + "of type " + fileTag;
        if (theFile.exists()) {
            if (!theFile.isFile()) {
                throw new CLIException(prefixExceptionMessage + " is not a normal file");
            }
            if (!theFile.canWrite()) {
                throw new CLIException(prefixExceptionMessage + " cannot be written");
            }
        } else {
            try {
                theFile.createNewFile();
            } catch (final IOException e) {
                throw new CLIException(prefixExceptionMessage + " cannot be created: ", e);
            }
        }
    }

    /**
     * Validate that a directory exists and is writable.
     *
     * @param directoryTag the directory tag, a tag used for information and error messages
     * @param directoryName the directory name to check
     */
    private void validateWritableDirectory(final String directoryTag, final String directoryName) {
        if (directoryName == null) {
            return;
        }
        final File theDirectory = new File(directoryName);
        final String prefixExceptionMessage = "directory " + directoryName + "of type " + directoryTag;

        if (theDirectory.exists()) {
            if (!theDirectory.isDirectory()) {
                throw new CLIException(prefixExceptionMessage + " is not a directory");
            }
            if (!theDirectory.canWrite()) {
                throw new CLIException(prefixExceptionMessage + " cannot be written");
            }
        }
    }

    /**
     * Checks if help is set.
     *
     * @return true, if help is set
     */
    public boolean isHelpSet() {
        return helpSet;
    }

    /**
     * Sets whether the help flag is set or not.
     *
     * @param isHelpSet the value of the help flag
     */
    public void setHelp(final boolean isHelpSet) {
        this.helpSet = isHelpSet;
    }

    /**
     * Gets the file name of the command metadata file for the editor commands.
     *
     * @return the file name of the command metadata file for the editor commands
     */
    public String getMetadataFileName() {
        return metadataFileName;
    }

    /**
     * Sets the file name of the command metadata file for the editor commands.
     *
     * @param metadataFileName the file name of the command metadata file for the editor commands
     */
    public void setMetadataFileName(final String metadataFileName) {
        this.metadataFileName = metadataFileName.trim();
    }

    /**
     * Check if the file name of the command metadata file for the editor commands is set.
     *
     * @return true, if the file name of the command metadata file for the editor commands is set
     */
    public boolean checkSetMetadataFileName() {
        return metadataFileName != null && metadataFileName.length() > 0;
    }

    /**
     * Gets the file name of the file containing properties that are used for command default
     * values.
     *
     * @return the file name of the file containing properties that are used for command default
     *         values
     */
    public String getApexPorpertiesFileName() {
        return apexPropertiesFileName;
    }

    /**
     * Sets the file name of the file containing properties that are used for command default
     * values.
     *
     * @param apexPorpertiesFileName the file name of the file containing properties that are used
     *        for command default values
     */
    public void setApexPorpertiesFileName(final String apexPorpertiesFileName) {
        apexPropertiesFileName = apexPorpertiesFileName.trim();
    }

    /**
     * Check if the file name of the file containing properties that are used for command default
     * values is set.
     *
     * @return true, if the file name of the file containing properties that are used for command
     *         default values is set
     */
    public boolean checkSetApexPropertiesFileName() {
        return apexPropertiesFileName != null && apexPropertiesFileName.length() > 0;
    }

    /**
     * Gets the name of the file containing commands to be streamed into the CLI editor.
     *
     * @return the name of the file containing commands to be streamed into the CLI editor
     */
    public String getCommandFileName() {
        return commandFileName;
    }

    /**
     * Sets the name of the file containing commands to be streamed into the CLI editor.
     *
     * @param commandFileName the name of the file containing commands to be streamed into the CLI
     *        editor
     */
    public void setCommandFileName(final String commandFileName) {
        this.commandFileName = commandFileName.trim();
    }

    /**
     * Check if the name of the file containing commands to be streamed into the CLI editor is set.
     *
     * @return true, if the name of the file containing commands to be streamed into the CLI editor
     *         is set
     */
    public boolean checkSetCommandFileName() {
        return commandFileName != null && commandFileName.length() > 0;
    }

    /**
     * Gets the name of the file containing the Apex model that will be used to initialize the Apex
     * model in the CLI editor.
     *
     * @return the name of the file containing the Apex model that will be used to initialize the
     *         Apex model in the CLI editor
     */
    public String getInputModelFileName() {
        return inputModelFileName;
    }

    /**
     * Sets the name of the file containing the Apex model that will be used to initialize the Apex
     * model in the CLI editor.
     *
     * @param inputModelFileName the name of the file containing the Apex model that will be used to
     *        initialize the Apex model in the CLI editor
     */
    public void setInputModelFileName(final String inputModelFileName) {
        this.inputModelFileName = inputModelFileName.trim();
    }

    /**
     * Check if the name of the file containing the Apex model that will be used to initialize the
     * Apex model in the CLI editor is set.
     *
     * @return true, if the name of the file containing the Apex model that will be used to
     *         initialize the Apex model in the CLI editor is set
     */
    public boolean checkSetInputModelFileName() {
        return inputModelFileName != null && inputModelFileName.length() > 0;
    }

    /**
     * Gets the name of the file that the Apex CLI editor will save the Apex model to when it exits.
     *
     * @return the name of the file that the Apex CLI editor will save the Apex model to when it
     *         exits
     */
    public String getOutputModelFileName() {
        return outputModelFileName;
    }

    /**
     * Sets the name of the file that the Apex CLI editor will save the Apex model to when it exits.
     *
     * @param outputModelFileName the name of the file that the Apex CLI editor will save the Apex
     *        model to when it exits
     */
    public void setOutputModelFileName(final String outputModelFileName) {
        this.outputModelFileName = outputModelFileName.trim();
    }

    /**
     * Check if the name of the file that the Apex CLI editor will save the Apex model to when it
     * exits is set.
     *
     * @return true, if the name of the file that the Apex CLI editor will save the Apex model to
     *         when it exits is set
     */
    public boolean checkSetOutputModelFileName() {
        return outputModelFileName != null && outputModelFileName.length() > 0;
    }

    /**
     * Gets the working directory that is the root for CLI editor macro includes.
     *
     * @return the CLI editor working directory
     */
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Sets the working directory that is the root for CLI editor macro includes.
     *
     * @param workingDirectory the CLI editor working directory
     */
    public void setWorkingDirectory(final String workingDirectory) {
        this.workingDirectory = workingDirectory.trim();
    }

    /**
     * Gets the name of the file to which the Apex CLI editor will log commands and responses.
     *
     * @return the name of the file to which the Apex CLI editor will log commands and responses
     */
    public String getLogFileName() {
        return logFileName;
    }

    /**
     * Sets the name of the file to which the Apex CLI editor will log commands and responses.
     *
     * @param logFileName the name of the file to which the Apex CLI editor will log commands and
     *        responses
     */
    public void setLogFileName(final String logFileName) {
        this.logFileName = logFileName.trim();
    }

    /**
     * Check if the name of the file to which the Apex CLI editor will log commands and responses is
     * set.
     *
     * @return true, if the name of the file to which the Apex CLI editor will log commands and
     *         responses is set
     */
    public boolean checkSetLogFileName() {
        return logFileName != null;
    }

    /**
     * Checks if the Apex CLI editor is set to echo commands that have been entered.
     *
     * @return true, if the Apex CLI editor is set to echo commands that have been entered
     */
    public boolean isEchoSet() {
        return echo;
    }

    /**
     * Sets whether the Apex CLI editor should echo commands that have been entered.
     *
     * @param echo true, if the Apex CLI editor should echo commands that have been entered
     */
    public void setEcho(final boolean echo) {
        this.echo = echo;
    }

    /**
     * Checks whether the Apex CLI editor is set to suppress logging of command output.
     *
     * @return true, if the Apex CLI editor is set to suppress logging of command output.
     */
    public boolean isSuppressLogSet() {
        return suppressLog;
    }

    /**
     * Sets whether the Apex CLI editor should suppress logging of command output.
     *
     * @param suppressLog true, if the Apex CLI editor should suppress logging of command output
     */
    public void setSuppressLog(final boolean suppressLog) {
        this.suppressLog = suppressLog;
    }

    /**
     * Checks whether the Apex CLI editor is set to suppress output of its Apex model on exit.
     *
     * @return true, if checks if the Apex CLI editor is set to suppress output of its Apex model on
     *         exit
     */
    public boolean isSuppressModelOutputSet() {
        return suppressModelOutput;
    }

    /**
     * Sets whether the Apex CLI editor should suppress output of its Apex model on exit.
     *
     * @param suppressModelOutput true, if the Apex CLI editor should suppress output of its Apex
     *        model on exit
     */
    public void setSuppressModelOutput(final boolean suppressModelOutput) {
        this.suppressModelOutput = suppressModelOutput;
    }

    /**
     * Check if the command failures flag is set.
     * 
     * @return true if the command failures flag has been set
     */
    public boolean checkSetIgnoreCommandFailures() {
        return ignoreCommandFailuresSet;
    }

    /**
     * Checks if the command failures flag is set.
     * 
     * @param ignoreCommandFailuresSet true if the command failures flag has been set
     */
    public void setIgnoreCommandFailuresSet(final boolean ignoreCommandFailuresSet) {
        this.ignoreCommandFailuresSet = ignoreCommandFailuresSet;
    }

    /**
     * Checks if command failures should be ignored and command execution continue.
     * 
     * @return true if command failures should be ignored
     */
    public boolean isIgnoreCommandFailures() {
        return ignoreCommandFailures;
    }

    /**
     * Sets if command errors should be ignored and command execution continue.
     * 
     * @param ignoreCommandFailures true if command errors should be ignored
     */
    public void setIgnoreCommandFailures(final boolean ignoreCommandFailures) {
        this.ignoreCommandFailures = ignoreCommandFailures;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CLIParameters [helpSet=" + helpSet + ", metadataFileName=" + metadataFileName
                + ", apexPropertiesFileName=" + apexPropertiesFileName + ", commandFileName=" + commandFileName
                + ", inputModelFileName=" + inputModelFileName + ", outputModelFileName=" + outputModelFileName
                + ", logFileName=" + logFileName + ", echo=" + echo + ", suppressLog=" + suppressLog
                + ", suppressModelOutput=" + suppressModelOutput + "]";
    }
}
