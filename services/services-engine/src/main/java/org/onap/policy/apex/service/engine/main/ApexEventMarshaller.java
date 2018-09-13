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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.impl.EventProducerFactory;
import org.onap.policy.apex.service.engine.event.impl.EventProtocolFactory;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This event marshaler handles events coming out of Apex and sends them on, handles threading,
 * event queuing, transformations and sending using the configured sending technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEventMarshaller implements ApexEventListener, Runnable {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEventMarshaller.class);

    // Interval to wait between thread shutdown checks
    private static final int MARSHALLER_SHUTDOWN_WAIT_INTERVAL = 10;

    // The amount of time to wait between polls of the event queue in milliseconds
    private static final long EVENT_QUEUE_POLL_INTERVAL = 20;

    // The name of the marshaler
    private final String name;

    // The engine service and producer parameters
    private final EngineServiceParameters engineServiceParameters;
    private final EventHandlerParameters producerParameters;

    // Apex event producer and event converter, all conversions are to and from string
    // representation of events
    private ApexEventProducer producer;
    private ApexEventProtocolConverter converter;

    // Temporary event holder for events coming out of Apex
    private final BlockingQueue<ApexEvent> queue = new LinkedBlockingQueue<>();

    // The marshaler thread and stopping flag
    private Thread marshallerThread;
    private boolean stopOrderedFlag = false;

    /**
     * Create the marshaler.
     *
     * @param name the name of the marshaler
     * @param engineServiceParameters the engine service parameters for this Apex engine
     * @param producerParameters the producer parameters for this specific marshaler
     */
    public ApexEventMarshaller(final String name, final EngineServiceParameters engineServiceParameters,
            final EventHandlerParameters producerParameters) {
        this.name = name;
        this.engineServiceParameters = engineServiceParameters;
        this.producerParameters = producerParameters;
    }

    /**
     * Configure the marshaler by setting up the producer and event converter and initialize the
     * thread for event sending.
     *
     * @throws ApexActivatorException on errors initializing the producer
     * @throws ApexEventException on errors initializing event handling
     */
    public void init() throws ApexActivatorException, ApexEventException {
        // Create the producer for sending events and the converter for transforming events
        producer = new EventProducerFactory().createProducer(name, producerParameters);

        // Initialize the producer
        producer.init(this.name, this.producerParameters);

        // Create the converter for transforming events
        converter = new EventProtocolFactory().createConverter(name, producerParameters.getEventProtocolParameters());

        // Configure and start the event sending thread
        final String threadName =
                engineServiceParameters.getEngineKey().getName() + ':' + this.getClass().getName() + ':' + this.name;
        marshallerThread = new ApplicationThreadFactory(threadName).newThread(this);
        marshallerThread.setDaemon(true);
        marshallerThread.start();
    }

    /**
     * Gets the name of the marshaler.
     *
     * @return the marshaler name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the technology specific producer for this marshaler.
     *
     * @return the producer
     */
    public ApexEventProducer getProducer() {
        return producer;
    }

    /**
     * Gets the event protocol converter for this marshaler.
     *
     * @return the event protocol converter
     */
    public ApexEventProtocolConverter getConverter() {
        return converter;
    }

    /**
     * Callback method called on implementations of this interface when Apex emits an event.
     *
     * @param apexEvent the apex event emitted by Apex
     */
    @Override
    public void onApexEvent(final ApexEvent apexEvent) {
        // Check if we are filtering events on this marshaler, if so check the event name against
        // the filter
        if (producerParameters.isSetEventNameFilter()
                && !apexEvent.getName().matches(producerParameters.getEventNameFilter())) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("onMessage(): event {} not processed, filtered  out by filter", apexEvent,
                        producerParameters.getEventNameFilter());
            }

            // Ignore this event
            return;
        }

        // Push the event onto the queue for handling
        try {
            queue.put(apexEvent);
        } catch (final InterruptedException e) {
            // restore the interrupt status
            Thread.currentThread().interrupt();
            LOGGER.warn("Failed to queue the event: " + apexEvent, e);
        }
    }

    /**
     * Run a thread that runs forever (well until system termination anyway) and listens for
     * outgoing events on the queue.
     */
    @Override
    public void run() {
        // Run until interrupted
        while (marshallerThread.isAlive() && !stopOrderedFlag) {
            try {
                // Take the next event from the queue
                final ApexEvent apexEvent = queue.poll(EVENT_QUEUE_POLL_INTERVAL, TimeUnit.MILLISECONDS);
                if (apexEvent == null) {
                    continue;
                }

                // Process the next Apex event from the queue
                final Object event = converter.fromApexEvent(apexEvent);

                producer.sendEvent(apexEvent.getExecutionId(), apexEvent.getName(), event);

                if (LOGGER.isTraceEnabled()) {
                    String message = "event sent : " + apexEvent.toString();
                    LOGGER.trace(message);
                }
            } catch (final InterruptedException e) {
                // restore the interrupt status
                Thread.currentThread().interrupt();
                LOGGER.debug("Thread interrupted, Reason {}", e.getMessage());
                stopOrderedFlag = true;
            } catch (final Exception e) {
                LOGGER.warn("Error while forwarding events for " + marshallerThread.getName(), e);
            }
        }

        // Stop event production if we are not synchronized,;in the synchronized case, the producer
        // takes care of its own cleanup.
        producer.stop();
    }

    /**
     * Get the marshaler thread.
     *
     * @return the marshaler thread
     */
    public Thread getThread() {
        return marshallerThread;
    }

    /**
     * Stop the Apex event marshaller's event producer using its termination mechanism.
     */
    public void stop() {
        LOGGER.entry("shutting down Apex event marshaller . . .");

        // Order the stop
        stopOrderedFlag = true;

        // Wait for thread shutdown
        while (marshallerThread.isAlive()) {
            ThreadUtilities.sleep(MARSHALLER_SHUTDOWN_WAIT_INTERVAL);
        }

        LOGGER.exit("shut down Apex event marshaller");
    }
}
