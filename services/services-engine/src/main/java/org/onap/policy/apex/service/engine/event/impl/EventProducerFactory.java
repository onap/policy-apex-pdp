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

import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This factory class creates event producers for the defined technology type for Apex engines.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventProducerFactory {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventProducerFactory.class);

    /**
     * Create an event producer of the required type for the specified producer technology.
     *
     * @param name the name of the producer
     * @param producerParameters The Apex parameters containing the configuration for the producer
     * @return the event producer
     * @throws ApexEventException on errors creating the Apex event producer
     */
    public ApexEventProducer createProducer(final String name, final EventHandlerParameters producerParameters)
            throws ApexEventException {
        // Get the carrier technology parameters
        final CarrierTechnologyParameters technologyParameters = producerParameters.getCarrierTechnologyParameters();

        // Get the class for the event producer using reflection
        final String producerPluginClass = technologyParameters.getEventProducerPluginClass();
        Object producerPluginObject = null;
        try {
            producerPluginObject = Class.forName(producerPluginClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            final String errorMessage = "could not create an Apex event producer for Producer \"" + name
                    + "\" for the carrier technology \"" + technologyParameters.getLabel()
                    + "\", specified event producer plugin class \"" + producerPluginClass + "\" not found";
            LOGGER.error(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Check the class is an event producer
        if (!(producerPluginObject instanceof ApexEventProducer)) {
            final String errorMessage = "could not create an Apex event producer for Producer \"" + name
                    + "\" for the carrier technology \"" + technologyParameters.getLabel()
                    + "\", specified event producer plugin class \"" + producerPluginClass
                    + "\" is not an instance of \"" + ApexEventProducer.class.getCanonicalName() + "\"";
            LOGGER.error(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        return (ApexEventProducer) producerPluginObject;
    }
}
