/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.auth.clieditor.utils.CliUtils;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * This class reads and handles command line parameters to the Apex CLI editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Setter
@Getter
public class CommandLineParameters {

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
        CliUtils.validateReadableFile("Metadata File", metadataFileName);
        CliUtils.validateReadableFile("Properties File", apexPropertiesFileName);
        CliUtils.validateReadableFile("Command File", commandFileName);
        CliUtils.validateReadableFile("Input Model File", inputModelFileName);
        CliUtils.validateWritableFile("Output Model File", outputModelFileName);
        CliUtils.validateWritableFile("Log File", logFileName);
        CliUtils.validateWritableDirectory("Working Directory", workingDirectory);

        if (isSuppressLog()) {
            setEcho(false);
        } else {
            if (checkSetCommandFileName()) {
                setEcho(true);
                if (!isIgnoreCommandFailuresSet()) {
                    setIgnoreCommandFailuresSet(false);
                }
            } else {
                setEcho(false);
                if (!isIgnoreCommandFailuresSet()) {
                    setIgnoreCommandFailuresSet(true);
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
        if (isSuppressLog()) {
            return new ByteArrayOutputStream();

        }
        if (logFileName == null) {
            return System.out;
        } else {
            var logFile = new File(logFileName);
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
            return new FileOutputStream(logFile, true);
        }
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
     * Check if the file name of the file containing properties that are used for command default values is set.
     *
     * @return true, if the file name of the file containing properties that are used for command default values is set
     */
    public boolean checkSetApexPropertiesFileName() {
        return apexPropertiesFileName != null && apexPropertiesFileName.length() > 0;
    }

    /**
     * Check if the name of the file containing commands to be streamed into the CLI editor is set.
     *
     * @return true, if the name of the file containing commands to be streamed into the CLI editor is set
     */
    public boolean checkSetCommandFileName() {
        return commandFileName != null && commandFileName.length() > 0;
    }

    /**
     * Check if the name of the file containing the Apex model that will be used to initialize the Apex model in the CLI
     * editor is set.
     *
     * @return true, if the name of the file containing the Apex model that will be used to initialize the Apex model in
     *         the CLI editor is set
     */
    public boolean checkSetInputModelFileName() {
        return inputModelFileName != null && inputModelFileName.length() > 0;
    }

    /**
     * Check if the name of the file that the Apex CLI editor will save the Apex model to when it exits is set.
     *
     * @return true, if the name of the file that the Apex CLI editor will save the Apex model to when it exits is set
     */
    public boolean checkSetOutputModelFileName() {
        return outputModelFileName != null && outputModelFileName.length() > 0;
    }

    /**
     * Check if the name of the file to which the Apex CLI editor will log commands and responses is set.
     *
     * @return true, if the name of the file to which the Apex CLI editor will log commands and responses is set
     */
    public boolean checkSetLogFileName() {
        return logFileName != null;
    }

    /**
     * {@inheritDoc}.
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
