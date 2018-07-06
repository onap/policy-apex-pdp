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

import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

/**
 * Event protocol parameters for JMS Object messages as an event protocol.
 *
 * <p>
 * On reception of an a JMS {@code javax.jms.ObjectMessage}, the JMS Object plugin unmarshals the message as follows:
 * <ol>
 * <li>It extracts the Java object from the {@code javax.jms.ObjectMessage} instance.</li>
 * <li>It creates an {@link org.onap.policy.apex.service.engine.event.ApexEvent} instance to hold the java object.</li>
 * <li>It sets the name of the Apex event to be the simple class name of the incoming Java object and appends the value
 * of the {@code incomingEventSuffix} parameter to it.</li>
 * <li>It sets the version of the incoming event to the value of the {@code incomingEventVersion} parameter.</li>
 * <li>It sets the name space of the incoming event to be the value of the package of the class of the incoming Java
 * object.</li>
 * <li>It sets the source of the incoming event to the value of the {@code incomingEventSource} parameter.</li>
 * <li>It sets the target of the incoming event to the value of the {@code incomingEventTarget} parameter.</li>
 * <li>It puts a single entry into the Apex event map with the the simple class name of the incoming Java object being
 * the key of the entry and the actual incoming object as the value of the entry.</li>
 * </ol>
 * <p>
 * When sending an object to JMS, the plugin expects to receive an Apex event with a single entry. The plugin marshals
 * the value of that entry to an object that can be sent by JMS as a {@code javax.jms.ObjectMessage} instance.
 * <p>
 * The parameters for this plugin are:
 * <ol>
 * <li>incomingEventSuffix: The suffix to append to the simple name of incoming Java class instances when they are
 * encapsulated in Apex events. The parameter defaults to the string value {@code IncomingEvent}.</li>
 * <li>incomingEventVersion: The event version to use for incoming Java class instances when they are encapsulated in
 * Apex events. The parameter defaults to the string value {@code 1.0.0}.</li>
 * <li>incomingEventSource: The event source to use for incoming Java class instances when they are encapsulated in Apex
 * events. The parameter defaults to the string value {@code JMS}.</li>
 * <li>incomingEventTarget: The event target to use for incoming Java class instances when they are encapsulated in Apex
 * events. The parameter defaults to the string value {@code Apex}.</li>
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JMSObjectEventProtocolParameters extends EventProtocolParameters {
    /** The label of this event protocol. */
    public static final String JMS_OBJECT_EVENT_PROTOCOL_LABEL = "JMSOBJECT";

    //@formatter:off
    // Default parameter values
    private static final String   DEFAULT_INCOMING_EVENT_SUFFIX  = "IncomingEvent";
    private static final String   DEFAULT_INCOMING_EVENT_VERSION = "1.0.0";
    private static final String   DEFAULT_INCOMING_EVENT_SOURCE  = "JMS";
    private static final String   DEFAULT_INCOMING_EVENT_TARGET  = "Apex";

    // JMS carrier parameters
    private String incomingEventSuffix   = DEFAULT_INCOMING_EVENT_SUFFIX;
    private String incomingEventVersion  = DEFAULT_INCOMING_EVENT_VERSION;
    private String incomingEventSource   = DEFAULT_INCOMING_EVENT_SOURCE;
    private String incomingEventTarget   = DEFAULT_INCOMING_EVENT_TARGET;
    //@formatter:off

    /**
     * Constructor to create a JSON event protocol parameter instance and register the instance with the parameter service.
     */
    public JMSObjectEventProtocolParameters() {
        super(JMSObjectEventProtocolParameters.class.getCanonicalName());

        // Set the event protocol properties for the JMS Text event protocol
        this.setLabel(JMS_OBJECT_EVENT_PROTOCOL_LABEL);

        // Set the event protocol plugin class
        this.setEventProtocolPluginClass(Apex2JMSObjectEventConverter.class.getCanonicalName());
    }

    /**
     * Gets the incoming event version.
     *
     * @return the incoming event version
     */
    public String getIncomingEventVersion() {
        return incomingEventVersion;
    }

    /**
     * Gets the incoming event source.
     *
     * @return the incoming event source
     */
    public String getIncomingEventSource() {
        return incomingEventSource;
    }

    /**
     * Gets the incoming event target.
     *
     * @return the incoming event target
     */
    public String getIncomingEventTarget() {
        return incomingEventTarget;
    }

    /**
     * Gets the incoming event suffix.
     *
     * @return the incoming event suffix
     */
    public String getIncomingEventSuffix() {
        return incomingEventSuffix;
    }
}
