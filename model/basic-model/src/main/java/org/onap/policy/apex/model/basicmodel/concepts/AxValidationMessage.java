/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.concepts;

import lombok.Getter;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * A validation message is created for each validation observation observed during validation of a concept. The message
 * holds the key and the class of the concept on which the observation was made as well as the type of observation and a
 * message describing the observation.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
public class AxValidationMessage {
    private final AxKey observedKey;
    private ValidationResult validationResult = ValidationResult.VALID;
    private final String observedClass;
    private final String message;

    /**
     * Create an validation observation with the given fields.
     *
     * @param observedKey the key of the class on which the validation observation was made
     * @param observedClass the class on which the validation observation was made
     * @param validationResult the type of observation made
     * @param message a message describing the observation
     */
    public AxValidationMessage(final AxKey observedKey, final Class<?> observedClass,
            final ValidationResult validationResult, final String message) {
        Assertions.argumentNotNull(observedKey, "observedKey may not be null");
        Assertions.argumentNotNull(observedClass, "observedClass may not be null");
        Assertions.argumentNotNull(validationResult, "validationResult may not be null");
        Assertions.argumentNotNull(message, "message may not be null");

        this.observedKey = observedKey;
        this.observedClass = observedClass.getName();
        this.validationResult = validationResult;
        this.message = message;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return observedKey.toString() + ':' + observedClass + ':' + validationResult.name() + ':' + message;
    }
}
