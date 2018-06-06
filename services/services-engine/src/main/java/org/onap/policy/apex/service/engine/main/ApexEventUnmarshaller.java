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

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.engine.event.impl.EventConsumerFactory;
import org.onap.policy.apex.service.engine.event.impl.EventProtocolFactory;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This event unmarshaler handles events coming into Apex, handles threading, event queuing,
 * transformation and receiving using the configured receiving technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEventUnmarshaller implements ApexEventReceiver, Runnable {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEventUnmarshaller.class);

    // Interval to wait between thread shutdown checks
    private static final int UNMARSHALLER_SHUTDOWN_WAIT_INTERVAL = 10;

    // The amount of time to wait between polls of the event queue in milliseconds
    private static final long EVENT_QUEUE_POLL_INTERVAL = 20;

    // The name of the unmarshaler
    private final String name;

    // The engine service and consumer parameters
    private final EngineServiceParameters engineServiceParameters;
    private final EventHandlerParameters consumerParameters;

    // The engine service handler to use for forwarding on of unmarshalled events
    private ApexEngineServiceHandler engineServiceHandler;

    // Apex event producer and event converter, all events are sent as string representations
    private ApexEventConsumer consumer;
    private ApexEventProtocolConverter converter;

    // Temporary event holder for events going into Apex
    private final BlockingQueue<ApexEvent> queue = new LinkedBlockingQueue<>();

    // The unmarshaler thread and stopping flag
    private Thread unmarshallerThread = null;
    private boolean stopOrderedFlag = false;

    /**
     * Create the unmarshaler.
     *
     * @param name the name of the unmarshaler
     * @param engineServiceParameters the engine service parameters for this Apex engine
     * @param consumerParameters the consumer parameters for this specific unmarshaler
     */
    public ApexEventUnmarshaller(final String name, final EngineServiceParameters engineServiceParameters,
            final EventHandlerParameters consumerParameters) {
        this.name = name;
        this.engineServiceParameters = engineServiceParameters;
        this.consumerParameters = consumerParameters;
    }

    /**
     * Configure the consumer and initialize the thread for event sending.
     *
     * @param incomingEngineServiceHandler the Apex engine service handler for passing events to
     *        Apex
     * @throws ApexEventException on errors initializing event handling
     */
    public void init(final ApexEngineServiceHandler incomingEngineServiceHandler) throws ApexEventException {
        this.engineServiceHandler = incomingEngineServiceHandler;

        // Create the consumer for sending events and the converter for transforming events
        consumer = new EventConsumerFactory().createConsumer(name, consumerParameters);
        consumer.init(this.name, this.consumerParameters, this);

        converter = new EventProtocolFactory().createConverter(name, consumerParameters.getEventProtocolParameters());
    }

    /**
     * Start the unmarshaler and consumer threads.
     */
    public void start() {
        // Start the consumer
        consumer.start();

        // Configure and start the event reception thread
        final String threadName =
                engineServiceParameters.getEngineKey().getName() + ":" + this.getClass().getName() + ":" + name;
        unmarshallerThread = new ApplicationThreadFactory(threadName).newThread(this);
        unmarshallerThread.setDaemon(true);
        unmarshallerThread.start();
    }

    /**
     * Gets the name of the unmarshaler.
     *
     * @return the unmarshaler name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the technology specific consumer for this unmarshaler.
     *
     * @return the consumer
     */
    public ApexEventConsumer getConsumer() {
        return consumer;
    }

    /**
     * Gets the event protocol converter for this unmarshaler.
     *
     * @return the event protocol converter
     */
    public ApexEventProtocolConverter getConverter() {
        return converter;
    }

    /**
     * Connect a synchronous unmarshaler with a synchronous marshaler.
     * 
     * @param peeredMode the peered mode under which the unmarshaler and marshaler are connected
     * @param peeredMarshaller the synchronous marshaler to connect with
     */
    public void connectMarshaler(final EventHandlerPeeredMode peeredMode, final ApexEventMarshaller peeredMarshaller) {
        switch (peeredMode) {
            case SYNCHRONOUS:
                // To connect a synchronous unmarshaler and marshaler, we create a synchronous event
                // cache on the consumer/producer pair
                new SynchronousEventCache(peeredMode, consumer, peeredMarshaller.getProducer(),
                        consumerParameters.getPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS));
                return;

            case REQUESTOR:
                new PeeredReference(peeredMode, consumer, peeredMarshaller.getProducer());
                return;

            default:
                return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.event.ApexEventReceiver#receiveEvent(java.lang.Object)
     */
    @Override
    public void receiveEvent(final Object event) throws ApexEventException {
        receiveEvent(0, event, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventReceiver#receiveEvent(long,
     * java.lang.Object)
     */
    @Override
    public void receiveEvent(final long executionId, final Object event) throws ApexEventException {
        receiveEvent(executionId, event, false);
    }

    /**
     * Receive an event from a consumer, convert its protocol and forward it to Apex.
     *
     * @param executionId the execution id the incoming execution ID
     * @param event the event in its native format
     * @param generateExecutionId if true, let Apex generate the execution ID, if false, use the
     *        incoming execution ID
     * @throws ApexEventException on unmarshaling errors on events
     */
    private void receiveEvent(final long executionId, final Object event, final boolean generateExecutionId)
            throws ApexEventException {
        // Push the event onto the queue
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("onMessage(): event received: {}", event.toString());
        }

        // Convert the incoming events to Apex events
        try {
            final List<ApexEvent> apexEventList = converter.toApexEvent(consumerParameters.getEventName(), event);
            for (final ApexEvent apexEvent : apexEventList) {
                // Check if we are filtering events on this unmarshaler, if so check the event name
                // against the filter
                if (consumerParameters.isSetEventNameFilter()
                        && !apexEvent.getName().matches(consumerParameters.getEventNameFilter())) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("onMessage(): event {} not processed, filtered  out by filter", apexEvent,
                                consumerParameters.getEventNameFilter());
                    }

                    // Ignore this event
                    continue;
                }

                if (!generateExecutionId) {
                    apexEvent.setExecutionID(executionId);
                }

                // Enqueue the event
                queue.add(apexEvent);

                // Cache synchronized events that are sent
                if (consumerParameters.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS)) {
                    final SynchronousEventCache synchronousEventCache =
                            (SynchronousEventCache) consumer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS);
                    synchronousEventCache.cacheSynchronizedEventToApex(apexEvent.getExecutionID(), apexEvent);
                }
            }
        } catch (final ApexException e) {
            final String errorMessage = "Error while converting event into an ApexEvent for " + name + ": "
                    + e.getMessage() + ", Event=" + event;
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }
    }

    /**
     * Run a thread that runs forever (well until system termination anyway) and listens for
     * incoming events on the queue.
     */
    @Override
    public void run() {
        // Run until interruption
        while (unmarshallerThread.isAlive() && !stopOrderedFlag) {
            try {
                // Take the next event from the queue
                final ApexEvent apexEvent = queue.poll(EVENT_QUEUE_POLL_INTERVAL, TimeUnit.MILLISECONDS);
                if (apexEvent == null) {
                    continue;
                }

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("event received {}", apexEvent.toString());
                }

                // Pass the event to the activator for forwarding to Apex
                engineServiceHandler.forwardEvent(apexEvent);
            } catch (final InterruptedException e) {
                LOGGER.warn("BatchProcessor thread interrupted, Reason {}", e.getMessage());
                break;
            } catch (final Exception e) {
                LOGGER.warn("Error while forwarding events for " + unmarshallerThread.getName(), e);
                continue;
            }
        }

        // Stop event production
        consumer.stop();
    }

    /**
     * Get the unmarshaler thread.
     *
     * @return the unmarshaler thread
     */
    public Thread getThread() {
        return unmarshallerThread;
    }

    /**
     * Stop the Apex event unmarshaller's event producer using its termination mechanism.
     */
    public void stop() {
        LOGGER.entry("shutting down Apex event unmarshaller . . .");

        // Order the stop
        stopOrderedFlag = true;

        // Order a stop on the synchronous cache if one exists
        if (consumerParameters != null && consumerParameters.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS)) {
            if (consumer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS) != null) {
                ((SynchronousEventCache) consumer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS)).stop();
            }
        }

        // Wait for thread shutdown
        while (unmarshallerThread != null && unmarshallerThread.isAlive()) {
            ThreadUtilities.sleep(UNMARSHALLER_SHUTDOWN_WAIT_INTERVAL);
        }

        LOGGER.exit("shut down Apex event unmarshaller");
    }
}
