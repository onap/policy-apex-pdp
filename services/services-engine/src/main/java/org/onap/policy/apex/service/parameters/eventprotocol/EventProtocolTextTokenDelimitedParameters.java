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
 * An event protocol parameter class for token delimited textual event protocols that may be specialized by event
 * protocol plugins that require plugin specific parameters.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>startDelimiterToken: the token string that delimits the start of text blocks that contain events.
 * <li>endDelimiterToken: the token string that delimits the end of text blocks that contain events, this parameter is
 * optional and defaults to null.
 * <li>delimiterAtStart: indicates if the first text block should have a delimiter at the start (true), or whether
 * processing of the first block should begin at the start of the text (false). The parameter is optional and defaults
 * to true.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class EventProtocolTextTokenDelimitedParameters extends EventProtocolParameters {
    // The delimiter token for text blocks
    private String startDelimiterToken = null;
    private String endDelimiterToken = null;
    private boolean delimiterAtStart = true;

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of this class.
     */
    public EventProtocolTextTokenDelimitedParameters() {
        super();
    }

    /**
     * Gets the start delimiter token that delimits events in the text.
     *
     * @return the start delimiter token
     */
    public String getStartDelimiterToken() {
        return startDelimiterToken;
    }

    /**
     * Sets the start delimiter token that delimits events in the text.
     *
     * @param startDelimiterToken
     *        delimiterToken the delimiter token
     */
    public void setStartDelimiterToken(final String startDelimiterToken) {
        this.startDelimiterToken = startDelimiterToken;
    }

    /**
     * Gets the end delimiter token that delimits events in the text.
     *
     * @return the end delimiter token
     */
    public String getEndDelimiterToken() {
        return endDelimiterToken;
    }

    /**
     * Sets the end delimiter token that delimits events in the text.
     *
     * @param endDelimiterToken
     *        delimiterToken the delimiter token
     */
    public void setEndDelimiterToken(final String endDelimiterToken) {
        this.endDelimiterToken = endDelimiterToken;
    }

    /**
     * Check if there must be a delimiter at the start of the first text block.
     * 
     * @return true if there must be a delimiter at the start of the text block
     */
    public boolean isDelimiterAtStart() {
        return delimiterAtStart;
    }

    /**
     * Sets if there has to be a delimiter at the start of the first text block.
     * 
     * @param delimiterAtStart
     *        true if there must be a delimiter at the start of the text block
     */
    public void setDelimiterAtStart(boolean delimiterAtStart) {
        this.delimiterAtStart = delimiterAtStart;
    }

    @Override
    public String toString() {
        return "EventProtocolTextTokenDelimitedParameters [startDelimiterToken=" + startDelimiterToken
                        + ", endDelimiterToken=" + endDelimiterToken + ", delimiterAtStart=" + delimiterAtStart + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        if (startDelimiterToken == null || startDelimiterToken.length() == 0) {
            result.setResult("startDelimiterToken", ValidationStatus.INVALID,
                            "text start delimiter token not specified or is blank\n");
        }

        return result;
    }
}
