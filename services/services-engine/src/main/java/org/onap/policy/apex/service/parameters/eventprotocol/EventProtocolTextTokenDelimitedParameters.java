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

import org.onap.policy.apex.service.parameters.ApexParameterValidator;

/**
 * An event protocol parameter class for token delimited textual event protocols that may be
 * specialized by event protocol plugins that require plugin specific parameters.
 *
 * <p>
 * The following parameters are defined:
 * <ol>
 * <li>delimiterToken: the token string that delimits text blocks that contain events.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class EventProtocolTextTokenDelimitedParameters extends EventProtocolParameters
        implements ApexParameterValidator {
    // The delimiter token for text blocks
    private String delimiterToken = null;

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of
     * this class.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public EventProtocolTextTokenDelimitedParameters(final String parameterClassName) {
        super(parameterClassName);
    }

    /**
     * Gets the delimiter token that delimits events in the text.
     *
     * @return the delimiter token
     */
    public String getDelimiterToken() {
        return delimiterToken;
    }


    /**
     * Sets the delimiter token that delimits events in the text.
     *
     * @param delimiterToken the delimiter token
     */
    public void setDelimiterToken(final String delimiterToken) {
        this.delimiterToken = delimiterToken;
    }


    @Override
    public String toString() {
        return "EventProtocolTextCharDelimitedParameters {" + super.toString() + "} [delimiterToken=" + delimiterToken
                + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.parameters.ApexParameterValidator#validate()
     */
    @Override
    public String validate() {
        final StringBuilder errorMessageBuilder = new StringBuilder();

        errorMessageBuilder.append(super.validate());

        if (delimiterToken == null || delimiterToken.length() == 0) {
            errorMessageBuilder.append("  text delimiter token not specified or is blank\n");
        }

        return errorMessageBuilder.toString();
    }
}
