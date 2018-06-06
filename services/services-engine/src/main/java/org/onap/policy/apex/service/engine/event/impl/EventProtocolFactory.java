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

package org.onap.policy.apex.service.engine.event.impl;

import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This factory class uses the Apex event protocol parameters to create and return an instance of
 * the correct Apex event protocol converter plugin for the specified event protocol.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventProtocolFactory {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventProtocolFactory.class);

    /**
     * Create an event converter that converts between an
     * {@link org.onap.policy.apex.service.engine.event.ApexEvent} and the specified event protocol.
     *
     * @param name the name of the event protocol
     * @param eventProtocolParameters the event protocol parameters defining what to convert from
     *        and to
     * @return The event converter for converting events to and from Apex format
     */
    public ApexEventProtocolConverter createConverter(final String name,
            final EventProtocolParameters eventProtocolParameters) {
        // Get the class for the event protocol plugin using reflection
        final String eventProtocolPluginClass = eventProtocolParameters.getEventProtocolPluginClass();
        Object eventProtocolPluginObject = null;
        try {
            eventProtocolPluginObject = Class.forName(eventProtocolPluginClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            final String errorMessage = "could not create an Apex event protocol converter for \"" + name
                    + "\" for the protocol \"" + eventProtocolParameters.getLabel()
                    + "\", specified event protocol converter plugin class \"" + eventProtocolPluginClass
                    + "\" not found";
            LOGGER.error(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }

        // Check the class is an event consumer
        if (!(eventProtocolPluginObject instanceof ApexEventProtocolConverter)) {
            final String errorMessage = "could not create an Apex event protocol converter for \"" + name
                    + "\" for the protocol \"" + eventProtocolParameters.getLabel()
                    + "\", specified event protocol converter plugin class \"" + eventProtocolPluginClass
                    + "\" is not an instance of \"" + ApexEventProtocolConverter.class.getCanonicalName() + "\"";
            LOGGER.error(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }
        ((ApexEventProtocolConverter) eventProtocolPluginObject).init(eventProtocolParameters);
        return (ApexEventProtocolConverter) eventProtocolPluginObject;
    }
}
