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

package org.onap.policy.apex.service.parameters.eventprotocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * An event protocol parameter class for character delimited textual event protocols that may be specialized by event
 * protocol plugins that require plugin specific parameters.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>startChar: starting character delimiter for text blocks containing an event.
 * <li>endChar: ending character delimiter for text blocks containing an event.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class EventProtocolTextCharDelimitedParameters extends EventProtocolParameters {
    // The starting and ending character delimiter
    private char startChar = '\0';
    private char endChar = '\0';

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult result = super.validate();

        if (startChar == '\0') {
            result.addResult("startChar", null, ValidationStatus.INVALID,
                            "text character delimited start character has not been specified");
        }

        if (endChar == '\0') {
            result.addResult("endChar", null, ValidationStatus.INVALID,
                            "text character delimited end character has not been specified\n");
        }

        return result;
    }
}
