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

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.engdep.EngDepMessagingService;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ApexEngineServiceHandler holds the reference to the Apex engine service and the EngDep
 * service for that engine. It also acts as an event receiver for asynchronous and synchronous
 * events.
 */
public class ApexEngineServiceHandler {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEngineServiceHandler.class);

    // The Apex engine service, the Apex engine itself
    private final EngineService apexEngineService;

    // The interface between the Apex engine and Apex policy deployment for the Apex engine
    private final EngDepMessagingService engDepService;

    /**
     * Instantiates a new engine holder with its engine service and EngDep service.
     *
     * @param apexEngineService the apex engine service
     * @param engDepService the EngDep service
     */
    ApexEngineServiceHandler(final EngineService apexEngineService, final EngDepMessagingService engDepService) {
        this.apexEngineService = apexEngineService;
        this.engDepService = engDepService;
    }

    /**
     * This method forwards an event to the Apex service.
     * 
     * @param apexEvent The event to forward to Apex
     */
    public void forwardEvent(final ApexEvent apexEvent) {
        try {
            // Send the event to the engine runtime
            apexEngineService.getEngineServiceEventInterface().sendEvent(apexEvent);
        } catch (final Exception e) {
            final String errorMessage = "error transferring event \"" + apexEvent.getName() + "\" to the Apex engine";
            LOGGER.debug(errorMessage, e);
            throw new ApexActivatorRuntimeException(errorMessage, e);
        }
    }

    /**
     * Terminate the Apex engine.
     *
     * @throws ApexException on termination errors
     */
    public void terminate() throws ApexException {
        // Shut down engine management
        if (engDepService != null) {
            engDepService.stop();
        }

        // Shut down each engine instance
        if (apexEngineService != null) {
            apexEngineService.stop();
            apexEngineService.clear();
        }
    }
}
