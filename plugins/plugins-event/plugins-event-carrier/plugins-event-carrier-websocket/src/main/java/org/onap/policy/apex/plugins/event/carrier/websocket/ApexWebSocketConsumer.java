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

package org.onap.policy.apex.plugins.event.carrier.websocket;

import java.util.EnumMap;
import java.util.Map;

import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageClient;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageServer;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessager;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation an Apex event consumer that receives events using Kafka.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexWebSocketConsumer implements ApexEventConsumer, WSStringMessageListener, Runnable {
    private static final int WEB_SOCKET_WAIT_SLEEP_TIME = 100;

    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexWebSocketConsumer.class);

    // The Web Socket properties
    private WEBSOCKETCarrierTechnologyParameters webSocketConsumerProperties;

    // The web socket messager, may be WS a server or a client
    private WSStringMessager wsStringMessager;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The name for this consumer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    // The consumer thread and stopping flag
    private Thread consumerThread;
    private boolean stopOrderedFlag = false;

    // The number of events read to date
    private int eventsRead = 0;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the Kafka Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof WEBSOCKETCarrierTechnologyParameters)) {
            LOGGER.warn("specified consumer properties are not applicable to a web socket consumer");
            throw new ApexEventException("specified consumer properties are not applicable to a web socket consumer");
        }
        webSocketConsumerProperties =
                (WEBSOCKETCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

        // Check if this is a server or a client Web Socket
        if (webSocketConsumerProperties.isWsClient()) {
            // Create a WS client
            wsStringMessager = new WSStringMessageClient(webSocketConsumerProperties.getHost(),
                    webSocketConsumerProperties.getPort());
        } else {
            wsStringMessager = new WSStringMessageServer(webSocketConsumerProperties.getPort());
        }

        // Start reception of event strings on the web socket
        try {
            wsStringMessager.start(this);
        } catch (final MessagingException e) {
            LOGGER.warn("could not start web socket consumer");
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

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#setPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
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
        while (consumerThread.isAlive() && !stopOrderedFlag) {
            ThreadUtilities.sleep(WEB_SOCKET_WAIT_SLEEP_TIME);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        if (wsStringMessager != null) {
            wsStringMessager.stop();
        }
        stopOrderedFlag = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageListener#receiveString(java.
     * lang. String)
     */
    @Override
    public void receiveString(final String eventString) {
        try {
            eventReceiver.receiveEvent(eventString);
            eventsRead++;
        } catch (final Exception e) {
            final String errorMessage = "Error sending event " + name + '_' + eventsRead + ", " + e.getMessage()
                    + ", event:\n" + eventString;
            LOGGER.warn(errorMessage);
        }
    }
}
