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

package org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin;

import java.util.ArrayList;
import java.util.List;

import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventList;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class Apex2ApexEventConverter passes through {@link ApexEvent} instances. It is used for
 * transferring Apex events directly as POJOs between APEX producers and consumers.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class Apex2ApexEventConverter implements ApexEventProtocolConverter {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(Apex2ApexEventConverter.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter#init(org.onap.policy.
     * apex. service.parameters.eventprotocol.EventProtocolParameters)
     */
    @Override
    public void init(final EventProtocolParameters parameters) {
        // Check and get the APEX parameters
        if (!(parameters instanceof ApexEventProtocolParameters)) {
            final String errorMessage = "specified consumer properties are not applicable to the APEX event protocol";
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.event.ApexEventConverter#toApexEvent(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public List<ApexEvent> toApexEvent(final String eventName, final Object eventObject) throws ApexEventException {
        // Check the event eventObject
        if (eventObject == null) {
            LOGGER.warn("event processing failed, event is null");
            throw new ApexEventException("event processing failed, event is null");
        }

        // The list of events we will return
        final List<ApexEvent> eventList = new ArrayList<>();

        try {
            // Check if its a single APEX event
            if (!(eventObject instanceof ApexEvent)) {
                throw new ApexEventException("incoming event (" + eventObject + ") is not an ApexEvent");
            }

            final ApexEvent event = (ApexEvent) eventObject;

            // Check whether we have any ApexEventList fields, if so this is an event of events and
            // all fields should be of type ApexEventList
            boolean foundEventListFields = false;
            boolean foundOtherFields = false;
            for (final Object fieldObject : event.values()) {
                if (fieldObject instanceof ApexEventList) {
                    foundEventListFields = true;

                    // Add the events to the event list
                    eventList.addAll((ApexEventList) fieldObject);
                } else {
                    foundOtherFields = true;
                }
            }

            // If we found both event list fields and other fields we're in trouble
            if (foundEventListFields && foundOtherFields) {
                throw new ApexEventException("incoming event (" + eventObject
                        + ") has both event list fields and other fields, it cannot be processed");
            }

            // Check if the incoming event just has other fields, if so it's just a regular event
            // and we add it to the event list as the only event there
            if (foundOtherFields) {
                eventList.add(event);
            }
        } catch (final Exception e) {
            final String errorString = "Failed to unmarshal APEX event: " + e.getMessage() + ", event=" + eventObject;
            LOGGER.warn(errorString, e);
            throw new ApexEventException(errorString, e);
        }

        // Return the list of events we have unmarshalled
        return eventList;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.event.ApexEventConverter#fromApexEvent(org.onap.policy.
     * apex.service.engine.event.ApexEvent)
     */
    @Override
    public Object fromApexEvent(final ApexEvent apexEvent) throws ApexEventException {
        // Check the Apex event
        if (apexEvent == null) {
            LOGGER.warn("event processing failed, Apex event is null");
            throw new ApexEventException("event processing failed, Apex event is null");
        }

        return apexEvent;
    }
}
