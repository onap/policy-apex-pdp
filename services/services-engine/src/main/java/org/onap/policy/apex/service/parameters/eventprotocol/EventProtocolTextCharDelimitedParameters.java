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

package org.onap.policy.apex.service.parameters.eventprotocol;

import org.onap.policy.common.parameters.GroupValidationResult;
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
public abstract class EventProtocolTextCharDelimitedParameters extends EventProtocolParameters {
    // The starting and ending character delimiter
    private char startChar = '\0';
    private char endChar = '\0';

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of this class.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public EventProtocolTextCharDelimitedParameters(final String parameterClassName) {
        super(parameterClassName);
    }

    /**
     * Gets the start character that delimits the start of text blocks.
     *
     * @return the start char
     */
    public char getStartChar() {
        return startChar;
    }

    /**
     * Sets the start character that delimits the start of text blocks.
     *
     * @param startChar the start character
     */
    public void setStartChar(final char startChar) {
        this.startChar = startChar;
    }

    /**
     * Gets the end character that delimits the end of text blocks.
     *
     * @return the end character
     */
    public char getEndChar() {
        return endChar;
    }

    /**
     * Sets the end character that delimits the end of text blocks.
     *
     * @param endChar the end character
     */
    public void setEndChar(final char endChar) {
        this.endChar = endChar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters#toString()
     */
    @Override
    public String toString() {
        return "EventProtocolTextCharDelimitedParameters {" + super.toString() + "} [startChar=" + startChar
                        + ", endChar=" + endChar + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        if (startChar == '\0') {
            result.setResult("startChar", ValidationStatus.INVALID,
                            "text character delimited start character has not been specified");
        }

        if (endChar == '\0') {
            result.setResult("endChar", ValidationStatus.INVALID,
                            "text character delimited end character has not been specified\n");
        }

        return result;
    }
}
