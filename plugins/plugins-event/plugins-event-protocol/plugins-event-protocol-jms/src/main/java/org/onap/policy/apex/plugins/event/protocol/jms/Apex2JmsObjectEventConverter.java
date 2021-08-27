/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.plugins.event.protocol.jms;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class Apex2JMSObjectEventConverter converts {@link ApexEvent} instances into string instances of object message
 * events for JMS.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@NoArgsConstructor
public final class Apex2JmsObjectEventConverter implements ApexEventProtocolConverter {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(Apex2JmsObjectEventConverter.class);

    // JMS event protocol parameters on the consumer (JMS->Apex) sides
    private JmsObjectEventProtocolParameters eventProtocolParameters = null;

    @Override
    public void init(final EventProtocolParameters parameters) {
        // Check if properties have been set for JMS object event conversion as a consumer. They may not be set because
        // JMS may not be in use
        // on both sides of Apex
        if (!(parameters instanceof JmsObjectEventProtocolParameters)) {
            final String errormessage = "specified Event Protocol Parameters properties of type \""
                            + parameters.getClass().getName() + "\" are not applicable to a "
                            + Apex2JmsObjectEventConverter.class.getName() + " converter";
            LOGGER.error(errormessage);
        } else {
            this.eventProtocolParameters = (JmsObjectEventProtocolParameters) parameters;
        }

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<ApexEvent> toApexEvent(final String eventName, final Object eventObject) throws ApexEventException {
        // Look for a "getObject()" method on the incoming object, if there is no such method, then we cannot fetch the
        // object from JMS
        Method getObjectMethod;
        try {
            getObjectMethod = eventObject.getClass().getMethod("getObject", (Class<?>[]) null);
        } catch (Exception exp) {
            final String errorMessage = "message \"" + eventObject
                            + "\" received from JMS does not have a \"getObject()\" method";
            throw new ApexEventRuntimeException(errorMessage, exp);
        }

        Object jmsIncomingObject;
        try {
            jmsIncomingObject = getObjectMethod.invoke(eventObject, (Object[]) null);
        } catch (final Exception exp) {
            final String errorMessage = "object contained in message \"" + eventObject
                            + "\" received from JMS could not be retrieved as a Java object";
            throw new ApexEventRuntimeException(errorMessage, exp);
        }

        // Check that the consumer parameters for JMS->Apex messaging have been set
        if (eventProtocolParameters == null) {
            final String errorMessage = "consumer parameters for JMS events consumed by "
                            + "Apex are not set in the Apex configuration for this engine";
            throw new ApexEventRuntimeException(errorMessage);
        }

        // Create the Apex event
        // @formatter:off
        final var apexEvent = new ApexEvent(
                        jmsIncomingObject.getClass().getSimpleName() + eventProtocolParameters.getIncomingEventSuffix(),
                        eventProtocolParameters.getIncomingEventVersion(),
                        jmsIncomingObject.toString().getClass().getPackage().getName(),
                        eventProtocolParameters.getIncomingEventSource(),
                        eventProtocolParameters.getIncomingEventTarget());
        // @formatter:on

        // Set the data on the apex event as the incoming object
        apexEvent.put(jmsIncomingObject.getClass().getSimpleName(), jmsIncomingObject);

        // Return the event in a single element
        final ArrayList<ApexEvent> eventList = new ArrayList<>();
        eventList.add(apexEvent);
        return eventList;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object fromApexEvent(final ApexEvent apexEvent) throws ApexEventException {
        // Check the Apex event
        if (apexEvent == null) {
            throw new ApexEventException("event processing failed, Apex event is null");
        }

        // Check that the Apex event has a single parameter
        if (apexEvent.size() != 1) {
            final String errorMessage = "event processing failed, "
                            + "Apex event must have one and only one parameter for JMS Object handling";
            throw new ApexEventException(errorMessage);
        }

        // Return the single object from the Apex event message
        return apexEvent.values().iterator().next();
    }
}
