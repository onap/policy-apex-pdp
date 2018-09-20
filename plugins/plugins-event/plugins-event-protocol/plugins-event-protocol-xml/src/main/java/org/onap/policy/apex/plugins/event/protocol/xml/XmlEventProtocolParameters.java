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

package org.onap.policy.apex.plugins.event.protocol.xml;

import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextTokenDelimitedParameters;

/**
 * Event protocol parameters for XML as an event protocol.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class XmlEventProtocolParameters extends EventProtocolTextTokenDelimitedParameters {
    /** The label of this carrier technology. */
    public static final String XML_EVENT_PROTOCOL_LABEL = "XML";

    // Constants for the text delimiter token
    private static final String XML_TEXT_DELIMITER_TOKEN = "<?xml";

    /**
     * Constructor to create a JSON event protocol parameter instance and register the instance with the parameter
     * service.
     */
    public XmlEventProtocolParameters() {
        super();

        // Set the event protocol properties for the XML event protocol
        this.setLabel(XML_EVENT_PROTOCOL_LABEL);

        // Set the starting and ending delimiters for text blocks of XML events
        this.setStartDelimiterToken(XML_TEXT_DELIMITER_TOKEN);

        // Set the event protocol plugin class
        this.setEventProtocolPluginClass(Apex2XmlEventConverter.class.getCanonicalName());
    }
}
