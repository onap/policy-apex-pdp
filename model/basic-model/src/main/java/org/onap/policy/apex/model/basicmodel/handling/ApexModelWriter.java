/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2022 Nordix Foundation.
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
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.common.utils.validation.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class writes an Apex concept to a file from a Java Apex Concept.
 *
 * @param <C> the type of Apex concept to write, must be a sub class of {@link AxConcept}
 * @author John Keeney (john.keeney@ericsson.com)
 */
@Getter
@Setter
public class ApexModelWriter<C extends AxConcept> {

    private static final String CONCEPT_MAY_NOT_BE_NULL = "concept may not be null";
    private static final String CONCEPT_WRITER_MAY_NOT_BE_NULL = "concept writer may not be null";
    private static final String CONCEPT_STREAM_MAY_NOT_BE_NULL = "concept stream may not be null";

    // Use GSON to serialize JSON
    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(AxReferenceKey.class, new ApexModelCustomGsonRefereceKeyAdapter())
        .registerTypeAdapter(Map.class, new ApexModelCustomGsonMapAdapter())
        .setPrettyPrinting()
        .create();

    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelWriter.class);

    //  The root class of the concept we are reading
    private final Class<C> rootConceptClass;

    // All written concepts are validated before writing if this flag is set
    private boolean validate = true;

    /**
     * Constructor, initiates the writer with validation on.
     *
     * @param rootConceptClass the root concept class for concept reading
     * @throws ApexModelException the apex concept reader exception
     */
    public ApexModelWriter(final Class<C> rootConceptClass) throws ApexModelException {
        // Save the root concept class
        this.rootConceptClass = rootConceptClass;
    }

    /**
     * This method validates the Apex concept then writes it into a stream.
     *
     * @param concept the concept to write
     * @param apexConceptStream the stream to write to
     * @throws ApexModelException on validation or writing exceptions
     */
    public void write(final C concept, final OutputStream apexConceptStream) throws ApexModelException {
        Assertions.argumentNotNull(concept, CONCEPT_MAY_NOT_BE_NULL);
        Assertions.argumentNotNull(apexConceptStream, CONCEPT_STREAM_MAY_NOT_BE_NULL);

        this.write(concept, new OutputStreamWriter(apexConceptStream));
    }

    /**
     * This method validates the Apex concept then writes it into a writer.
     *
     * @param concept the concept to write
     * @param apexConceptWriter the writer to write to
     * @throws ApexModelException on validation or writing exceptions
     */
    public void write(final C concept, final Writer apexConceptWriter) throws ApexModelException {
        Assertions.argumentNotNull(concept, CONCEPT_MAY_NOT_BE_NULL);
        Assertions.argumentNotNull(apexConceptWriter, CONCEPT_WRITER_MAY_NOT_BE_NULL);

        // Check if we should validate the concept
        if (validate) {
            // Validate the concept first
            final AxValidationResult validationResult = concept.validate(new AxValidationResult());
            if (!validationResult.isValid()) {
                String message =
                    "Apex concept (" + concept.getKey().getId() + ") validation failed: " + validationResult.toString();
                throw new ApexModelException(message);
            }
        }

        writeJson(concept, apexConceptWriter);
    }

    /**
     * This method writes the Apex concept into a writer in JSON format.
     *
     * @param concept the concept to write
     * @param apexConceptWriter the writer to write to
     * @throws ApexModelException on validation or writing exceptions
     */
    private void writeJson(final C concept, final Writer apexConceptWriter) throws ApexModelException {
        Assertions.argumentNotNull(concept, CONCEPT_MAY_NOT_BE_NULL);

        LOGGER.debug("writing Apex concept JSON . . .");

        String modelJsonString = null;
        try {
            modelJsonString = gson.toJson(concept, rootConceptClass);
        } catch (Exception je) {
            throw new ApexModelException("Unable to marshal Apex concept to JSON", je);
        }

        try {
            apexConceptWriter.write(modelJsonString);
            apexConceptWriter.close();
        } catch (IOException ioe) {
            throw new ApexModelException("Unable to write Apex concept as JSON", ioe);
        }

        LOGGER.debug("wrote Apex concept JSON");
    }
}
