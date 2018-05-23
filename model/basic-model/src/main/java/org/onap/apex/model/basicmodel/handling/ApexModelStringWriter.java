/*
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

package org.onap.apex.model.basicmodel.handling;

import java.io.ByteArrayOutputStream;

import org.onap.apex.model.basicmodel.concepts.ApexException;
import org.onap.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class writes an Apex concept to a string.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <C> the type of Apex concept to write to a string, must be a sub class of {@link AxConcept}
 */
public class ApexModelStringWriter<C extends AxConcept> {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelStringWriter.class);

    // Should concepts being written to files be valid
    private boolean validateFlag;

    /**
     * Constructor, set the validation flag.
     *
     * @param validateFlag Should validation be performed prior to output
     */
    public ApexModelStringWriter(final boolean validateFlag) {
        this.validateFlag = validateFlag;
    }

    /**
     * Write a concept to a string.
     *
     * @param concept The concept to write
     * @param rootConceptClass The concept class
     * @param jsonFlag writes JSON if true, and a generic string if false
     * @return The string with the concept
     * @throws ApexException thrown on errors
     */
    public String writeString(final C concept, final Class<C> rootConceptClass, final boolean jsonFlag) throws ApexException {
        Assertions.argumentNotNull(concept, "concept may not be null");
        
        if (jsonFlag) {
            return writeJSONString(concept, rootConceptClass);
        }
        else {
            return concept.toString();
        }
    }

    /**
     * Write a concept to an XML string.
     *
     * @param concept The concept to write
     * @param rootConceptClass The concept class
     * @return The string with the concept
     * @throws ApexException thrown on errors
     */
    public String writeXMLString(final C concept, final Class<C> rootConceptClass) throws ApexException {
        LOGGER.debug("running writeXMLString . . .");

        final ApexModelWriter<C> conceptWriter = new ApexModelWriter<>(rootConceptClass);
        conceptWriter.setValidateFlag(validateFlag);
        conceptWriter.getCDataFieldSet().add("description");
        conceptWriter.getCDataFieldSet().add("logic");
        conceptWriter.getCDataFieldSet().add("uiLogic");

        final ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        try {
            conceptWriter.write(concept, baOutputStream);
            baOutputStream.close();
        }
        catch (final Exception e) {
            LOGGER.warn("error writing XML string", e);
            throw new ApexException("error writing XML string", e);
        }

        LOGGER.debug("ran writeXMLString");
        return baOutputStream.toString();
    }

    /**
     * Write a concept to a JSON string.
     *
     * @param concept The concept to write
     * @param rootConceptClass The concept class
     * @return The string with the concept
     * @throws ApexException thrown on errors
     */
    public String writeJSONString(final C concept, final Class<C> rootConceptClass) throws ApexException {
        LOGGER.debug("running writeJSONString . . .");

        final ApexModelWriter<C> conceptWriter = new ApexModelWriter<>(rootConceptClass);
        conceptWriter.setJsonOutput(true);
        conceptWriter.setValidateFlag(validateFlag);

        final ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        try {
            conceptWriter.write(concept, baOutputStream);
            baOutputStream.close();
        }
        catch (final Exception e) {
            LOGGER.warn("error writing JSON string", e);
            throw new ApexException("error writing JSON string", e);
        }

        LOGGER.debug("ran writeJSONString");
        return baOutputStream.toString();
    }

    /**
     * Checks if is validate flag.
     *
     * @return true, if checks if is validate flag
     */
    public boolean isValidateFlag() {
        return validateFlag;
    }

    /**
     * Sets the validate flag.
     *
     * @param validateFlag the validate flag
     */
    public void setValidateFlag(final boolean validateFlag) {
        this.validateFlag = validateFlag;
    }
}
