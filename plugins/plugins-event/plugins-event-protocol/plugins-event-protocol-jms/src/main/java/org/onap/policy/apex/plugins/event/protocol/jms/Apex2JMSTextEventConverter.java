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

import java.util.List;

import javax.jms.TextMessage;

import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JSONEventConverter;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class Apex2JMSTextEventConverter converts {@link ApexEvent} instances into string instances of
 * {@link javax.jms.TextMessage} message events for JMS. It is a proxy for the built in
 * {@link org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JSONEventConverter} plugin.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class Apex2JMSTextEventConverter extends Apex2JSONEventConverter {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(Apex2JMSTextEventConverter.class);

    /**
     * Constructor to create the Apex to JMS Object converter.
     *
     * @throws ApexEventException the apex event exception
     */
    public Apex2JMSTextEventConverter() throws ApexEventException {}

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConverter#toApexEvent(java.lang.String, java.lang.Object)
     */
    @Override
    public List<ApexEvent> toApexEvent(final String eventName, final Object eventObject) throws ApexEventException {
        // Check if this is an TextMessage from JMS
        if (!(eventObject instanceof TextMessage)) {
            final String errorMessage = "message \"" + eventObject + "\" received from JMS is not an instance of \""
                    + TextMessage.class.getCanonicalName() + "\"";
            LOGGER.debug(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }

        // Get the string from the object message
        final TextMessage textMessage = (TextMessage) eventObject;
        String jmsString;
        try {
            jmsString = textMessage.getText();
        } catch (final Exception e) {
            final String errorMessage = "object contained in message \"" + eventObject
                    + "\" received from JMS could not be retrieved as a Java String";
            LOGGER.debug(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }

        // Use the generic JSON plugin from here
        return super.toApexEvent(eventName, jmsString);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.event.ApexEventConverter#fromApexEvent(org.onap.policy.apex.service.engine.
     * event. ApexEvent)
     */
    @Override
    public Object fromApexEvent(final ApexEvent apexEvent) throws ApexEventException {
        // Check the Apex event
        if (apexEvent == null) {
            LOGGER.warn("event processing failed, Apex event is null");
            throw new ApexEventException("event processing failed, Apex event is null");
        }

        // Return the Apex event as a string object
        return super.fromApexEvent(apexEvent);
    }
}
