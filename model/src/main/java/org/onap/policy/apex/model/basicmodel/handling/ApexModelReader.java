/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2022 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.handling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.onap.policy.common.utils.validation.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class reads an Apex concept from a file into a Java Apex Concept {@link AxConcept}.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <C> the type of Apex concept to read, must be a sub class of {@link AxConcept}
 */
@Getter
@Setter
public class ApexModelReader<C extends AxConcept> {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelReader.class);

    // Use GSON to deserialize JSON
    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(AxReferenceKey.class, new ApexModelCustomGsonRefereceKeyAdapter())
        .registerTypeAdapter(Map.class, new ApexModelCustomGsonMapAdapter())
        .setPrettyPrinting()
        .create();

    //  The root class of the concept we are reading
    private final Class<C> rootConceptClass;

    // All read concepts are validated after reading if this flag is set
    private boolean validate = true;

    /**
     * Constructor, initiates the reader with validation on.
     *
     * @param rootConceptClass the root concept class for concept reading
     * @throws ApexModelException the apex concept reader exception
     */
    public ApexModelReader(final Class<C> rootConceptClass) throws ApexModelException {
        // Save the root concept class
        this.rootConceptClass = rootConceptClass;
    }

    /**
     * Constructor, initiates the reader.
     *
     * @param rootConceptClass the root concept class for concept reading
     * @param validate whether to perform validation by default
     * @throws ApexModelException the apex concept reader exception
     */
    public ApexModelReader(final Class<C> rootConceptClass, final boolean validate) throws ApexModelException {
        this(rootConceptClass);
        this.validate = validate;
    }

    /**
     * This method checks the specified Apex concept file and reads it into an Apex concept.
     *
     * @param apexConceptStream the apex concept stream
     * @return the Apex concept
     * @throws ApexModelException on reading exceptions
     */
    public C read(final InputStream apexConceptStream) throws ApexModelException {
        Assertions.argumentNotNull(apexConceptStream, "concept stream may not be null");

        return read(new BufferedReader(new InputStreamReader(apexConceptStream)));
    }

    /**
     * This method reads the specified Apex reader into an Apex concept.
     *
     * @param apexConceptReader the apex concept reader
     * @return the Apex concept
     * @throws ApexModelException on reading exceptions
     */
    public C read(final BufferedReader apexConceptReader) throws ApexModelException {
        Assertions.argumentNotNull(apexConceptReader, "concept reader may not be null");

        LOGGER.entry("reading Apex concept into a String . . .");

        // Get the Apex concept as a string
        String apexConceptString = null;
        try {
            apexConceptString = TextFileUtils.getReaderAsString(apexConceptReader).trim();
        } catch (final IOException e) {
            throw new ApexModelException("Unable to read Apex concept ", e);
        }

        return read(apexConceptString);
    }

    /**
     * This method reads the specified Apex string into an Apex concept.
     *
     * @param apexConceptString the apex concept as a string
     * @return the Apex concept
     * @throws ApexModelException on reading exceptions
     */
    public C read(final String apexConceptString) throws ApexModelException {
        Assertions.argumentNotNull(apexConceptString, "concept string may not be null");

        LOGGER.entry("reading Apex concept from string . . .");

        C apexConcept = null;
        try {
            apexConcept = gson.fromJson(apexConceptString, rootConceptClass);
        } catch (final Exception je) {
            throw new ApexModelException("Unable to unmarshal Apex concept ", je);
        }

        if (apexConcept == null) {
            throw new ApexModelException("Unable to unmarshal Apex concept, unmarshaled model is null ");
        }

        LOGGER.debug("reading of Apex concept {} completed");

        apexConcept.buildReferences();

        // Check if the concept should be validated
        if (validate) {
            // Validate the configuration file
            final AxValidationResult validationResult = apexConcept.validate(new AxValidationResult());
            if (validationResult.isValid()) {
                return apexConcept;
            } else {
                String message = "Apex concept validation failed" + validationResult.toString();
                LOGGER.error(message);
                throw new ApexModelException(message);
            }
        } else {
            // No validation check
            return apexConcept;
        }
    }
}
