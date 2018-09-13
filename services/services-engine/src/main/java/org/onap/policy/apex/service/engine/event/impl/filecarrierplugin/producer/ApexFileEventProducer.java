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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.Map;

import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.FileCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex event producer that sends events to a file.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexFileEventProducer implements ApexEventProducer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexFileEventProducer.class);

    // The name for this producer
    private String producerName = null;

    // The output stream to write events to
    private PrintStream eventOutputStream;

    // The peer references for this event handler
    private final Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap =
            new EnumMap<>(EventHandlerPeeredMode.class);

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#init()
     */
    @Override
    public void init(final String name, final EventHandlerParameters producerParameters) throws ApexEventException {
        producerName = name;

        // Get and check the Apex parameters from the parameter service
        if (producerParameters == null) {
            final String errorMessage = "Producer parameters for ApexFileProducer \"" + producerName + "\" is null";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check and get the file Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof FileCarrierTechnologyParameters)) {
            final String errorMessage = "specified producer properties for ApexFileProducer \"" + producerName
                    + "\" are not applicable to a FILE producer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        final FileCarrierTechnologyParameters fileCarrierTechnologyParameters =
                (FileCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        // Now we create a writer for events
        try {
            if (fileCarrierTechnologyParameters.isStandardError()) {
                eventOutputStream = System.err;
            } else if (fileCarrierTechnologyParameters.isStandardIo()) {
                eventOutputStream = System.out;
            } else {
                eventOutputStream =
                        new PrintStream(new FileOutputStream(fileCarrierTechnologyParameters.getFileName()), true);
            }
        } catch (final IOException e) {
            final String errorMessage = "ApexFileProducer \"" + producerName + "\" failed to open file for writing: \""
                    + fileCarrierTechnologyParameters.getFileName() + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        if (fileCarrierTechnologyParameters.getStartDelay() > 0) {
            ThreadUtilities.sleep(fileCarrierTechnologyParameters.getStartDelay());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getName()
     */
    @Override
    public String getName() {
        return producerName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getPeeredReference(org.onap.
     * policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#setPeeredReference(org.onap.
     * policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode,
     * org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#sendEvent(long,
     * java.lang.String, java.lang.Object)
     */
    @Override
    public void sendEvent(final long executionId, final String eventName, final Object event) {
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache =
                (SynchronousEventCache) peerReferenceMap.get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        // Cast the event to a string, if our conversion is correctly configured, this cast should
        // always work
        String stringEvent = null;
        try {
            stringEvent = (String) event;
        } catch (final Exception e) {
            final String errorMessage = "error in ApexFileProducer \"" + producerName + "\" while transferring event \""
                    + event + "\" to the output stream";
            LOGGER.debug(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }

        eventOutputStream.println(stringEvent);
        eventOutputStream.flush();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        eventOutputStream.close();
    }
}
