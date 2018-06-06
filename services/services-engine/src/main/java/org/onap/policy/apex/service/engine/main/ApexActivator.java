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

package org.onap.policy.apex.service.engine.main;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.engdep.EngDepMessagingService;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class wraps an Apex engine so that it can be activated as a complete service together with
 * all its context, executor, and event plugins.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexActivator {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexActivator.class);

    // The parameters of this Apex activator
    private final ApexParameters apexParameters;

    // Event unmarshalers are used to receive events asynchronously into Apex
    private final Map<String, ApexEventUnmarshaller> unmarshallerMap = new LinkedHashMap<>();

    // Event marshalers are used to send events asynchronously from Apex
    private final Map<String, ApexEventMarshaller> marshallerMap = new LinkedHashMap<>();

    // The engine service handler holds the references to the engine and its EngDep deployment
    // interface. It also acts as a receiver for asynchronous
    // and synchronous events from the engine.
    private ApexEngineServiceHandler engineServiceHandler = null;

    /**
     * Instantiate the activator for the Apex engine as a complete service.
     *
     * @param parameters the apex parameters for the Apex service
     */
    public ApexActivator(final ApexParameters parameters) {
        apexParameters = parameters;
    }

    /**
     * Initialize the Apex engine as a complete service.
     *
     * @throws ApexActivatorException on errors in initializing the engine
     */
    public void initialize() throws ApexActivatorException {
        LOGGER.debug("Apex engine starting as a service . . .");

        try {
            // Create engine with specified thread count
            LOGGER.debug("starting apex engine service . . .");
            final EngineService apexEngineService =
                    EngineServiceImpl.create(apexParameters.getEngineServiceParameters());

            // Instantiate and start the messaging service for Deployment
            LOGGER.debug("starting apex deployment service . . .");
            final EngDepMessagingService engDepService = new EngDepMessagingService(apexEngineService,
                    apexParameters.getEngineServiceParameters().getDeploymentPort());
            engDepService.start();

            // Create the engine holder to hold the engine's references and act as an event receiver
            engineServiceHandler = new ApexEngineServiceHandler(apexEngineService, engDepService);

            // Check if a policy model file has been specified
            if (apexParameters.getEngineServiceParameters().getPolicyModelFileName() != null) {
                LOGGER.debug("deploying policy model in \""
                        + apexParameters.getEngineServiceParameters().getPolicyModelFileName()
                        + "\" to the apex engines . . .");

                // Set the policy model in the engine
                final String policyModelString = TextFileUtils
                        .getTextFileAsString(apexParameters.getEngineServiceParameters().getPolicyModelFileName());
                apexEngineService.updateModel(apexParameters.getEngineServiceParameters().getEngineKey(),
                        policyModelString, true);
                apexEngineService.startAll();
            }

            // Producer parameters specify what event marshalers to handle events leaving Apex are
            // set up and how they are set up
            for (final Entry<String, EventHandlerParameters> outputParameters : apexParameters
                    .getEventOutputParameters().entrySet()) {
                final ApexEventMarshaller marshaller = new ApexEventMarshaller(outputParameters.getKey(),
                        apexParameters.getEngineServiceParameters(), outputParameters.getValue());
                marshaller.init();
                apexEngineService.registerActionListener(outputParameters.getKey(), marshaller);
                marshallerMap.put(outputParameters.getKey(), marshaller);
            }

            // Consumer parameters specify what event unmarshalers to handle events coming into Apex
            // are set up and how they are set up
            for (final Entry<String, EventHandlerParameters> inputParameters : apexParameters.getEventInputParameters()
                    .entrySet()) {
                final ApexEventUnmarshaller unmarshaller = new ApexEventUnmarshaller(inputParameters.getKey(),
                        apexParameters.getEngineServiceParameters(), inputParameters.getValue());
                unmarshallerMap.put(inputParameters.getKey(), unmarshaller);
                unmarshaller.init(engineServiceHandler);
            }

            // Set up unmarshaler/marshaler pairing for synchronized event handling. We only need to
            // traverse the unmarshalers because the
            // unmarshalers and marshalers are paired one to one uniquely so if we find a
            // synchronized unmarshaler we'll also find its
            // paired marshaler
            for (final Entry<String, EventHandlerParameters> inputParameters : apexParameters.getEventInputParameters()
                    .entrySet()) {
                final ApexEventUnmarshaller unmarshaller = unmarshallerMap.get(inputParameters.getKey());

                // Pair up peered unmarshalers and marshalers
                for (final EventHandlerPeeredMode peeredMode : EventHandlerPeeredMode.values()) {
                    // Check if the unmarshaler is synchronized with a marshaler
                    if (inputParameters.getValue().isPeeredMode(peeredMode)) {
                        // Find the unmarshaler and marshaler
                        final ApexEventMarshaller peeredMarshaler =
                                marshallerMap.get(inputParameters.getValue().getPeer(peeredMode));

                        // Connect the unmarshaler and marshaler
                        unmarshaller.connectMarshaler(peeredMode, peeredMarshaler);
                    }
                }
                // Now let's get events flowing
                unmarshaller.start();
            }
        } catch (final Exception e) {
            LOGGER.debug("Apex engine failed to start as a service", e);
            throw new ApexActivatorException("Apex engine failed to start as a service", e);
        }

        LOGGER.debug("Apex engine started as a service");
    }

    /**
     * Terminate the Apex engine.
     *
     * @throws ApexException on termination errors
     */
    public void terminate() throws ApexException {
        // Shut down all marshalers and unmarshalers
        for (final ApexEventMarshaller marshaller : marshallerMap.values()) {
            marshaller.stop();
        }
        marshallerMap.clear();

        for (final ApexEventUnmarshaller unmarshaller : unmarshallerMap.values()) {
            unmarshaller.stop();
        }
        unmarshallerMap.clear();

        // Check if the engine service handler has been shut down already
        if (engineServiceHandler != null) {
            engineServiceHandler.terminate();
            engineServiceHandler = null;
        }
    }

    /**
     * Get the parameters used by the adapter.
     *
     * @return the parameters of the adapter
     */
    public ApexParameters getApexParameters() {
        return apexParameters;
    }
}
