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

import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This factory class creates event consumers of various technology types for Apex engines.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventConsumerFactory {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventConsumerFactory.class);

    /**
     * Create an event consumer of the required type for the specified consumer technology.
     *
     * @param name the name of the consumer
     * @param consumerParameters The parameters for the Apex engine, we use the technology type of
     *        the required consumer
     * @return the event consumer
     * @throws ApexEventException on errors creating the Apex event consumer
     */
    public ApexEventConsumer createConsumer(final String name, final EventHandlerParameters consumerParameters)
            throws ApexEventException {
        // Get the carrier technology parameters
        final CarrierTechnologyParameters technologyParameters = consumerParameters.getCarrierTechnologyParameters();

        // Get the class for the event consumer using reflection
        final String consumerPluginClass = technologyParameters.getEventConsumerPluginClass();
        Object consumerPluginObject = null;
        try {
            consumerPluginObject = Class.forName(consumerPluginClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            final String errorMessage = "could not create an Apex event consumer for \"" + name
                    + "\" for the carrier technology \"" + technologyParameters.getLabel()
                    + "\", specified event consumer plugin class \"" + consumerPluginClass + "\" not found";
            LOGGER.error(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Check the class is an event consumer
        if (!(consumerPluginObject instanceof ApexEventConsumer)) {
            final String errorMessage = "could not create an Apex event consumer \"" + name
                    + "\" for the carrier technology \"" + technologyParameters.getLabel()
                    + "\", specified event consumer plugin class \"" + consumerPluginClass
                    + "\" is not an instance of \"" + ApexEventConsumer.class.getCanonicalName() + "\"";
            LOGGER.error(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        return (ApexEventConsumer) consumerPluginObject;
    }
}
