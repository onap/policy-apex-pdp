/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin;

import java.io.File;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer.ApexFileEventProducer;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ObjectValidationResult;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.parameters.annotations.Min;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.models.base.Validated;

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
@Getter
@Setter
@ToString
public class FileCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    /** The label of this carrier technology. */
    public static final String FILE_CARRIER_TECHNOLOGY_LABEL = "FILE";

    /** The producer plugin class for the FILE carrier technology. */
    public static final String FILE_EVENT_PRODUCER_PLUGIN_CLASS = ApexFileEventProducer.class.getName();

    /** The consumer plugin class for the FILE carrier technology. */
    public static final String FILE_EVENT_CONSUMER_PLUGIN_CLASS = ApexFileEventConsumer.class.getName();

    // Recurring strings
    private static final String FILE_NAME_TOKEN = "fileName";

    private String fileName;
    private boolean standardIo = false;
    private boolean standardError = false;
    private boolean streamingMode = false;
    private @Min(0) long startDelay = 0;
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
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        return this.getLabel();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult result = super.validate();

        if (!standardIo && !standardError) {
            result.addResult(validateFileName());
        }

        if (standardIo || standardError) {
            streamingMode = true;
        }

        return result;
    }


    /**
     * Validate the file name parameter.
     *
     * @return the result of the validation
     */
    private ValidationResult validateFileName() {
        if (!ParameterValidationUtils.validateStringParameter(fileName)) {
            return new ObjectValidationResult(FILE_NAME_TOKEN, fileName, ValidationStatus.INVALID, Validated.IS_BLANK);
        }

        String absoluteFileName = null;

        // Resolve the file name if it is a relative file name
        var theFile = new File(fileName);
        if (theFile.isAbsolute()) {
            absoluteFileName = fileName;
        } else {
            absoluteFileName = System.getProperty("APEX_RELATIVE_FILE_ROOT") + File.separator + fileName;
            theFile = new File(absoluteFileName);
        }

        // Check if the file exists, the file should be a regular file and should be readable
        if (theFile.exists()) {
            return validateExistingFile(absoluteFileName, theFile);
        } else {
            // The path to the file should exist and should be writable
            return validateNewFileParent(absoluteFileName, theFile);
        }
    }

    /**
     * Validate an existing file is OK.
     *
     * @param absoluteFileName the absolute file name of the file
     * @param theFile the file that exists
     * @return the result of the validation
     */
    private ValidationResult validateExistingFile(String absoluteFileName, File theFile) {
        // Check that the file is a regular file
        if (!theFile.isFile()) {
            return new ObjectValidationResult(FILE_NAME_TOKEN, absoluteFileName, ValidationStatus.INVALID,
                            "is not a plain file");

        } else {
            fileName = absoluteFileName;

            if (!theFile.canRead()) {
                return new ObjectValidationResult(FILE_NAME_TOKEN, absoluteFileName, ValidationStatus.INVALID,
                                "is not readable");
            }

            return null;
        }
    }

    /**
     * Validate the parent of a new file is OK.
     *
     * @param absoluteFileName the absolute file name of the file
     * @param theFile the file that exists
     * @return the result of the validation
     */
    private ValidationResult validateNewFileParent(String absoluteFileName, File theFile) {
        // Check that the parent of the file is a directory
        if (!theFile.getParentFile().exists()) {
            return new ObjectValidationResult(FILE_NAME_TOKEN, absoluteFileName, ValidationStatus.INVALID,
                            "parent of file does not exist");

        } else if (!theFile.getParentFile().isDirectory()) {
            // Check that the parent of the file is a directory
            return new ObjectValidationResult(FILE_NAME_TOKEN, absoluteFileName, ValidationStatus.INVALID,
                            "parent of file is not directory");

        } else {
            fileName = absoluteFileName;

            if (!theFile.getParentFile().canRead()) {
                return new ObjectValidationResult(FILE_NAME_TOKEN, absoluteFileName, ValidationStatus.INVALID,
                                "is not readable");
            }

            return null;
        }
    }
}
