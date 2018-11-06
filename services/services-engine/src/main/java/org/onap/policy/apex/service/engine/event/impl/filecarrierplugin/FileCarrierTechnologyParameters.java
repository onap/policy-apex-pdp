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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin;

import java.io.File;

import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer.ApexFileEventProducer;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;

/**
 * This class holds the parameters that allows transport of events into and out of Apex using files and standard input
 * and output.
 *
 * <p>The following parameters are defined: <ol> <li>fileName: The full path to the file from which to read events or to
 * which to write events. <li>standardIO: If this flag is set to true, then standard input is used to read events in or
 * standard output is used to write events and the fileName parameter is ignored if present <li>standardError: If this
 * flag is set to true, then standard error is used to write events <li>streamingMode: If this flag is set to true, then
 * streaming mode is set for reading events and event handling will wait on the input stream for events until the stream
 * is closed. If streaming model is off, then event reading completes when the end of input is detected. <li>startDelay:
 * The amount of milliseconds to wait at startup startup before processing the first event. </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class FileCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    /** The label of this carrier technology. */
    public static final String FILE_CARRIER_TECHNOLOGY_LABEL = "FILE";

    /** The producer plugin class for the FILE carrier technology. */
    public static final String FILE_EVENT_PRODUCER_PLUGIN_CLASS = ApexFileEventProducer.class.getCanonicalName();

    /** The consumer plugin class for the FILE carrier technology. */
    public static final String FILE_EVENT_CONSUMER_PLUGIN_CLASS = ApexFileEventConsumer.class.getCanonicalName();

    // Recurring strings
    private static final String FILE_NAME_TOKEN = "fileName";

    private String fileName;
    private boolean standardIo = false;
    private boolean standardError = false;
    private boolean streamingMode = false;
    private long startDelay = 0;
    // @formatter:on

    /**
     * Constructor to create a file carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public FileCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the FILE carrier technology
        this.setLabel(FILE_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(FILE_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(FILE_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /**
     * Gets the file name from which to read or to which to write events.
     *
     * @return the file name from which to read or to which to write events
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Checks if is standard IO should be used for input or output.
     *
     * @return true, if standard IO should be used for input or output
     */
    public boolean isStandardIo() {
        return standardIo;
    }

    /**
     * Checks if is standard error should be used for output.
     *
     * @return true, if standard error should be used for output
     */
    public boolean isStandardError() {
        return standardError;
    }

    /**
     * Checks if is streaming mode is on.
     *
     * @return true, if streaming mode is on
     */
    public boolean isStreamingMode() {
        return streamingMode;
    }

    /**
     * Sets the file name from which to read or to which to write events.
     *
     * @param fileName the file name from which to read or to which to write events
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets if standard IO should be used for event input or output.
     *
     * @param standardIo if standard IO should be used for event input or output
     */
    public void setStandardIo(final boolean standardIo) {
        this.standardIo = standardIo;
    }

    /**
     * Sets if standard error should be used for event output.
     *
     * @param standardError if standard error should be used for event output
     */
    public void setStandardError(final boolean standardError) {
        this.standardError = standardError;
    }

    /**
     * Sets streaming mode.
     *
     * @param streamingMode the streaming mode value
     */
    public void setStreamingMode(final boolean streamingMode) {
        this.streamingMode = streamingMode;
    }

    /**
     * Gets the delay in milliseconds before the plugin starts processing.
     * 
     * @return the delay
     */
    public long getStartDelay() {
        return startDelay;
    }

    /**
     * Sets the delay in milliseconds before the plugin starts processing.
     * 
     * @param startDelay the delay
     */
    public void setStartDelay(final long startDelay) {
        this.startDelay = startDelay;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters# toString()
     */
    @Override
    public String toString() {
        return "FILECarrierTechnologyParameters [fileName=" + fileName + ", standardIO=" + standardIo
                        + ", standardError=" + standardError + ", streamingMode=" + streamingMode + ", startDelay="
                        + startDelay + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.common.parameters.ParameterGroup#getName()
     */
    @Override
    public String getName() {
        return this.getLabel();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        if (!standardIo && !standardError) {
            validateFileName(result);
        }

        if (standardIo || standardError) {
            streamingMode = true;
        }

        if (startDelay < 0) {
            result.setResult("startDelay", ValidationStatus.INVALID,
                            "startDelay must be zero or a positive number of milliseconds");
        }

        return result;
    }
    

    /**
     * Validate the file name parameter.
     * 
     * @param result the variable in which to store the result of the validation
     */
    private void validateFileName(final GroupValidationResult result) {
        if (!ParameterValidationUtils.validateStringParameter(fileName)) {
            result.setResult(FILE_NAME_TOKEN, ValidationStatus.INVALID,
                            "\"" + fileName + "\" invalid, must be specified as a non-empty string");
            return;
        }

        String absoluteFileName = null;

        // Resolve the file name if it is a relative file name
        File theFile = new File(fileName);
        if (theFile.isAbsolute()) {
            absoluteFileName = fileName;
        } else {
            absoluteFileName = System.getProperty("APEX_RELATIVE_FILE_ROOT") + File.separator + fileName;
            theFile = new File(absoluteFileName);
        }

        // Check if the file exists, the file should be a regular file and should be readable
        if (theFile.exists()) {
            validateExistingFile(result, absoluteFileName, theFile);
        }
        // The path to the file should exist and should be writable
        else {
            validateNewFileParent(result, absoluteFileName, theFile);
        }
    }

    /**
     * Validate an existing file is OK.
     * 
     * @param result the result of the validation
     * @param absoluteFileName the absolute file name of the file
     * @param theFile the file that exists
     */
    private void validateExistingFile(final GroupValidationResult result, String absoluteFileName, File theFile) {
        // Check that the file is a regular file
        if (!theFile.isFile()) {
            result.setResult(FILE_NAME_TOKEN, ValidationStatus.INVALID, "is not a plain file");
        }
        else {
            fileName = absoluteFileName;

            if (!theFile.canRead()) {
                result.setResult(FILE_NAME_TOKEN, ValidationStatus.INVALID, "is not readable");
            }
        }
    }

    /**
     * Validate the parent of a new file is OK.
     * 
     * @param result the result of the validation
     * @param absoluteFileName the absolute file name of the file
     * @param theFile the file that exists
     */
    private void validateNewFileParent(final GroupValidationResult result, String absoluteFileName, File theFile) {
        // Check that the parent of the file is a directory
        if (!theFile.getParentFile().exists()) {
            result.setResult(FILE_NAME_TOKEN, ValidationStatus.INVALID, "parent of file does not exist");
        }
        // Check that the parent of the file is a directory
        else if (!theFile.getParentFile().isDirectory()) {
            result.setResult(FILE_NAME_TOKEN, ValidationStatus.INVALID, "parent of file is not directory");
        }
        else {
            fileName = absoluteFileName;

            if (!theFile.getParentFile().canRead()) {
                result.setResult(FILE_NAME_TOKEN, ValidationStatus.INVALID, "is not readable");
            }
        }
    }
}
