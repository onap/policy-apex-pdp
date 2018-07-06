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

package org.onap.policy.apex.plugins.event.protocol.jms;

import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JSONEventProtocolParameters;

/**
 * Event protocol parameters for JMS Text messages as an event protocol.
 * <p>
 * Text messages received and sent over JMS in ~Text format are assumed to be in a JSON format that Apex can understand.
 * Therefore this plugin is a subclass of the built in JSON event protocol plugin.
 * <p>
 * On reception of a JMS {@code javax.jms.TextMessage} message, the JMS Text plugin unmarshals the message the JMS text
 * message and passes it to its JSON superclass unmarshaling for processing.
 * <p>
 * When sending an Apex event, the plugin uses its underlying JSON superclass to marshal the event to a JSON string and
 * passes that string to the JSON carrier plugin for sending.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JMSTextEventProtocolParameters extends JSONEventProtocolParameters {
    /** The label of this event protocol. */
    public static final String JMS_TEXT_EVENT_PROTOCOL_LABEL = "JMSTEXT";

    /**
     * Constructor to create a JSON event protocol parameter instance and register the instance with the parameter
     * service.
     */
    public JMSTextEventProtocolParameters() {
        super(JMSTextEventProtocolParameters.class.getCanonicalName(), JMS_TEXT_EVENT_PROTOCOL_LABEL);

        // Set the event protocol properties for the JMS Text event protocol
        this.setLabel(JMS_TEXT_EVENT_PROTOCOL_LABEL);

        // Set the event protocol plugin class
        this.setEventProtocolPluginClass(Apex2JMSTextEventConverter.class.getCanonicalName());
    }
}
