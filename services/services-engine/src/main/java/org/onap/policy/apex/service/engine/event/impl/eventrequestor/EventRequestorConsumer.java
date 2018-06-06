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

package org.onap.policy.apex.service.engine.event.impl.eventrequestor;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events from its peered event requestor
 * producer.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventRequestorConsumer implements ApexEventConsumer, Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(EventRequestorConsumer.class);

    // The amount of time to wait in milliseconds between checks that the consumer thread has
    // stopped
    private static final long EVENT_REQUESTOR_WAIT_SLEEP_TIME = 50;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The name for this consumer
    private String name = null;

    // The peer references for this event handler
    private final Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap =
            new EnumMap<>(EventHandlerPeeredMode.class);

    // Temporary request holder for incoming event send requests
    private final BlockingQueue<Object> incomingEventRequestQueue = new LinkedBlockingQueue<>();

    // The consumer thread and stopping flag
    private Thread consumerThread;
    private boolean stopOrderedFlag = false;

    // The number of events received to date
    private int eventsReceived = 0;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the event requestor consumer properties
        if (!(consumerParameters
                .getCarrierTechnologyParameters() instanceof EventRequestorCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified consumer properties are not applicable to event Requestor consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check if we are in peered mode
        if (!consumerParameters.isPeeredMode(EventHandlerPeeredMode.REQUESTOR)) {
            final String errorMessage = "event Requestor consumer (" + this.name
                    + ") must run in peered requestor mode with a event Requestor producer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

    }

    /**
     * Receive an incoming event send request from the peered event Requestor producer and queue it
     * 
     * @param eventObject the incoming event to process
     * @throws ApexEventRuntimeException on queueing errors
     */
    public void processEvent(final Object eventObject) {
        // Push the event onto the queue for handling
        try {
            incomingEventRequestQueue.add(eventObject);
        } catch (final Exception e) {
            final String errorMessage =
                    "could not queue request \"" + eventObject + "\" on event Requestor consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#start()
     */
    @Override
    public void start() {
        // Configure and start the event reception thread
        final String threadName = this.getClass().getName() + ":" + this.name;
        consumerThread = new ApplicationThreadFactory(threadName).newThread(this);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the number of events received to date
     * 
     * @return the number of events received
     */
    public int getEventsReceived() {
        return eventsReceived;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getPeeredReference(org.onap.
     * policy.apex .service.parameters.eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#setPeeredReference(org.onap.
     * policy.apex .service.parameters.eventhandler.EventHandlerPeeredMode,
     * org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // The endless loop that receives events using REST calls
        while (consumerThread.isAlive() && !stopOrderedFlag) {
            try {
                // Take the next event from the queue
                final Object eventObject =
                        incomingEventRequestQueue.poll(EVENT_REQUESTOR_WAIT_SLEEP_TIME, TimeUnit.MILLISECONDS);
                if (eventObject == null) {
                    // Poll timed out, wait again
                    continue;
                }

                // Send the event into Apex
                eventReceiver.receiveEvent(eventObject);

                eventsReceived++;
            } catch (final InterruptedException e) {
                LOGGER.debug("Thread interrupted, Reason {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                LOGGER.warn("error receiving events on thread {}", consumerThread.getName(), e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventConsumer#stop()
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(EVENT_REQUESTOR_WAIT_SLEEP_TIME);
        }
    }
}
