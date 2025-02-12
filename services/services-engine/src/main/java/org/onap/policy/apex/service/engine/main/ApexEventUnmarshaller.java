/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2022, 2025 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2022 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import com.google.common.base.Strings;
import io.prometheus.metrics.core.metrics.Counter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.EnumUtils;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxToscaPolicyProcessingStatus;
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
import org.onap.policy.common.utils.resources.PrometheusUtils;
import org.onap.policy.models.pdp.enums.PdpResponseStatus;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This event unmarshaler handles events coming into Apex, handles threading, event queuing, transformation and
 * receiving using the configured receiving technology.
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

    private static final String PROMETHEUS_TOTAL_LABEL_VALUE = "TOTAL";

    // prometheus registration for policy execution metrics
    static final Counter POLICY_EXECUTED_COUNTER =
        Counter.builder()
            .name(PrometheusUtils.PdpType.PDPA.getNamespace() + "_" + PrometheusUtils.POLICY_EXECUTION_METRIC)
            .labelNames(PrometheusUtils.STATUS_METRIC_LABEL)
            .help(PrometheusUtils.POLICY_EXECUTION_HELP).register();

    // The name of the unmarshaler
    @Getter
    private final String name;

    // The engine service and consumer parameters
    private final EngineServiceParameters engineServiceParameters;
    private final EventHandlerParameters consumerParameters;

    // The engine service handler to use for forwarding on of unmarshalled events
    private ApexEngineServiceHandler engineServiceHandler;

    // Apex event producer and event converter, all events are sent as string representations
    @Getter
    private ApexEventConsumer consumer;
    @Getter
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
     * @param incomingEngineServiceHandler the Apex engine service handler for passing events to Apex
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void receiveEvent(@NonNull final Properties executionProperties, final Object event)
        throws ApexEventException {
        receiveEvent(0, executionProperties, event, true);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void receiveEvent(final long executionId, @NonNull final Properties executionProperties, final Object event)
        throws ApexEventException {
        receiveEvent(executionId, executionProperties, event, false);
    }

    /**
     * Receive an event from a consumer, convert its protocol and forward it to Apex.
     *
     * @param executionId the execution id the incoming execution ID
     * @param executionProperties properties used during processing of this event
     * @param event the event in its native format
     * @param generateExecutionId if true, let Apex generate the execution ID, if false, use the incoming execution ID
     * @throws ApexEventException on unmarshaling errors on events
     */
    private void receiveEvent(final long executionId, final Properties executionProperties, final Object event,
        final boolean generateExecutionId) throws ApexEventException {
        // Push the event onto the queue
        if (LOGGER.isTraceEnabled()) {
            var eventString = "onMessage(): event received: " + event.toString();
            LOGGER.trace(eventString);
        }

        // Convert the incoming events to Apex events
        List<ApexEvent> apexEventList = convertToApexEvents(event);

        for (final ApexEvent apexEvent : apexEventList) {
            // Check if this event is filtered out by the incoming filter
            if (isEventFilteredOut(apexEvent)) {
                // Ignore this event
                continue;
            }
            if (!generateExecutionId) {
                apexEvent.setExecutionId(executionId);
                apexEvent.setExecutionProperties(executionProperties);
            } else {
                // Clean up executionProperties in case if it is not a response event to a request made from APEX
                apexEvent.setExecutionProperties(new Properties(executionProperties));
            }
            // Cache synchronized events that are sent
            if (consumerParameters.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS)) {
                final var synchronousEventCache =
                    (SynchronousEventCache) consumer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS);
                synchronousEventCache.cacheSynchronizedEventToApex(apexEvent.getExecutionId(), apexEvent);
            }

            // Update policy execution metrics
            updatePolicyExecutedMetrics(apexEvent.getToscaPolicyState());

            // Enqueue the event
            queue.add(apexEvent);
        }
    }

    /**
     * Increment Prometheus counters for TOSCA policy execution metrics.
     *
     * @param toscaPolicyState the TOSCA Policy state flag from the event
     */
    private void updatePolicyExecutedMetrics(String toscaPolicyState) {
        // Skip events that are not flagged as TOSCA processing entry or exit points.
        if (Strings.isNullOrEmpty(toscaPolicyState)
                || !EnumUtils.isValidEnum(AxToscaPolicyProcessingStatus.class, toscaPolicyState)) {
            return;
        }

        // Increment total, successful and failed policy executed counter.
        if (AxToscaPolicyProcessingStatus.ENTRY.name().equals(toscaPolicyState)) {
            POLICY_EXECUTED_COUNTER.labelValues(PROMETHEUS_TOTAL_LABEL_VALUE).inc();
        } else if (AxToscaPolicyProcessingStatus.EXIT_SUCCESS.name().equals(toscaPolicyState)) {
            POLICY_EXECUTED_COUNTER.labelValues(PdpResponseStatus.SUCCESS.name()).inc();
        } else if (AxToscaPolicyProcessingStatus.EXIT_FAILURE.name().equals(toscaPolicyState)) {
            POLICY_EXECUTED_COUNTER.labelValues(PdpResponseStatus.FAIL.name()).inc();
        }
    }

    private List<ApexEvent> convertToApexEvents(final Object event) throws ApexEventException {
        List<ApexEvent> apexEventList = null;
        List<String> eventNamesList = null;
        if (consumerParameters.getEventName() != null) {
            eventNamesList = Arrays.asList(consumerParameters.getEventName().split("\\|"));
        } else {
            eventNamesList = Collections.singletonList(null);
        }
        Iterator<String> iterator = eventNamesList.iterator();
        // Incoming events in an endpoint can have different structure , for e.g., success/failure response events
        // Parse the incoming event into an APEX event defined with any one of the names specified in eventName field
        while (iterator.hasNext()) {
            try {
                String eventName = iterator.next();
                apexEventList = converter.toApexEvent(eventName, event);
                break;
            } catch (ApexException e) {
                if (!iterator.hasNext()) {
                    final String errorMessage = "Error while converting event into an ApexEvent for " + name;
                    if (!LOGGER.isDebugEnabled()) {
                        LOGGER.warn("{}. Detailed logs are available at debug level.", errorMessage);
                    }
                    throw new ApexEventException(errorMessage, e);
                }
            }
        }
        return apexEventList;
    }

    /**
     * Check if an event is filtered out and ignored.
     *
     * @param apexEvent the event to check
     */
    private boolean isEventFilteredOut(final ApexEvent apexEvent) {
        // Check if we are filtering events on this unmarshaler, if so check the event name
        // against the filter
        if (consumerParameters.isSetEventNameFilter()
            && !apexEvent.getName().matches(consumerParameters.getEventNameFilter())) {

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("onMessage(): event {} not processed, filtered  out by filter", apexEvent,
                    consumerParameters.getEventNameFilter());
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Run a thread that runs forever (well until system termination anyway) and listens for incoming events on the
     * queue.
     */
    @Override
    public void run() {
        // Run until interruption
        while (unmarshallerThread.isAlive() && !stopOrderedFlag) {
            try {
                // Take the next event from the queue
                final var apexEvent = queue.poll(EVENT_QUEUE_POLL_INTERVAL, TimeUnit.MILLISECONDS);
                if (apexEvent == null) {
                    continue;
                }

                if (LOGGER.isTraceEnabled()) {
                    var message = apexEvent.toString();
                    LOGGER.trace("event received {}", message);
                }

                // Pass the event to the activator for forwarding to Apex
                engineServiceHandler.forwardEvent(apexEvent);
            } catch (final InterruptedException e) {
                // restore the interrupt status
                Thread.currentThread().interrupt();
                LOGGER.warn("BatchProcessor thread interrupted, Reason {}", e.getMessage());
                stopOrderedFlag = true;
            } catch (final Exception e) {
                LOGGER.warn("Error while forwarding events for " + unmarshallerThread.getName(), e);
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

        // Wait for thread shutdown
        while (unmarshallerThread != null && unmarshallerThread.isAlive()) {
            ThreadUtilities.sleep(UNMARSHALLER_SHUTDOWN_WAIT_INTERVAL);
        }

        // Order a stop on the synchronous cache if one exists
        if (consumerParameters != null && consumerParameters.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS)
            && consumer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS) != null) {
            ((SynchronousEventCache) consumer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS)).stop();
        }
        LOGGER.exit("shut down Apex event unmarshaller");
    }
}
