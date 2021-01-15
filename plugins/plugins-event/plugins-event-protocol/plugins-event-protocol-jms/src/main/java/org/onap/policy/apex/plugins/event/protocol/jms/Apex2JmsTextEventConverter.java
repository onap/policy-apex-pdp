/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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
import java.util.List;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JsonEventConverter;

/**
 * The Class Apex2JMSTextEventConverter converts {@link ApexEvent} instances into string instances of
 * text message events for JMS. It is a proxy for the built in
 * {@link org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JsonEventConverter} plugin.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class Apex2JmsTextEventConverter extends Apex2JsonEventConverter {

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<ApexEvent> toApexEvent(final String eventName, final Object eventObject) throws ApexEventException {
        // Look for a "getText()" method on the incoming object, if there is no such method, then we cannot fetch the
        // text from JMS
        Method getTextMethod;
        try {
            getTextMethod = eventObject.getClass().getMethod("getText", (Class<?>[]) null);
        } catch (Exception exp) {
            final String errorMessage = "message \"" + eventObject
                            + "\" received from JMS does not have a \"getText()\" method";
            throw new ApexEventRuntimeException(errorMessage, exp);
        }


        String jmsString;
        try {
            jmsString = (String) getTextMethod.invoke(eventObject, (Object[]) null);
        } catch (final Exception exp) {
            final String errorMessage = "object contained in message \"" + eventObject
                    + "\" received from JMS could not be retrieved as a Java String";
            throw new ApexEventRuntimeException(errorMessage, exp);
        }

        // Use the generic JSON plugin from here
        return super.toApexEvent(eventName, jmsString);
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

        // Return the Apex event as a string object
        return super.fromApexEvent(apexEvent);
    }
}
