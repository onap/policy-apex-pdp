/*
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

import java.io.ByteArrayOutputStream;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.common.utils.validation.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class writes an Apex concept to a string.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <C> the type of Apex concept to write to a string, must be a sub class of {@link AxConcept}
 */
@Getter
@Setter
public class ApexModelStringWriter<C extends AxConcept> {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelStringWriter.class);

    // Should concepts being written to files be valid
    private boolean validate;

    /**
     * Constructor, set the validation flag.
     *
     * @param validate Should validation be performed prior to output
     */
    public ApexModelStringWriter(final boolean validate) {
        this.validate = validate;
    }

    /**
     * Write a concept to a string.
     *
     * @param concept The concept to write
     * @param rootConceptClass The concept class
     * @return The string with the concept
     * @throws ApexException thrown on errors
     */
    public String writeString(final C concept, final Class<C> rootConceptClass)
                    throws ApexException {
        Assertions.argumentNotNull(concept, "concept may not be null");

        return writeJsonString(concept, rootConceptClass);
    }

    /**
     * Write a concept to a JSON string.
     *
     * @param concept The concept to write
     * @param rootConceptClass The concept class
     * @return The string with the concept
     * @throws ApexException thrown on errors
     */
    public String writeJsonString(final C concept, final Class<C> rootConceptClass) throws ApexException {
        LOGGER.debug("running writeJSONString . . .");

        final ApexModelWriter<C> conceptWriter = new ApexModelWriter<>(rootConceptClass);
        conceptWriter.setValidate(validate);

        try (var baOutputStream = new ByteArrayOutputStream()) {
            conceptWriter.write(concept, baOutputStream);
            return baOutputStream.toString();
        } catch (final Exception e) {
            LOGGER.warn("error writing JSON string", e);
            throw new ApexException("error writing JSON string", e);
        }

    }
}
