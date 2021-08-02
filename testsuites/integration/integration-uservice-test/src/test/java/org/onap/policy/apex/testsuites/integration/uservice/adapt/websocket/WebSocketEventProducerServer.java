/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.websocket;

import lombok.Getter;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageServer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.testsuites.integration.uservice.adapt.events.EventGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WebSocketEventProducerServer.
 */
public class WebSocketEventProducerServer implements WsStringMessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventProducerServer.class);

    private final int port;
    private final int eventCount;
    private final boolean xmlEvents;
    private final long eventInterval;
    @Getter
    private long eventsSentCount = 0;

    WsStringMessageServer server;

    /**
     * Instantiates a new web socket event producer server.
     *
     * @param port the port
     * @param eventCount the event count
     * @param xmlEvents the xml events
     * @param eventInterval the event interval
     * @throws MessagingException the messaging exception
     */
    public WebSocketEventProducerServer(final int port, final int eventCount, final boolean xmlEvents,
                    final long eventInterval) throws MessagingException {
        this.port = port;
        this.eventCount = eventCount;
        this.xmlEvents = xmlEvents;
        this.eventInterval = eventInterval;

        server = new WsStringMessageServer(port);
        server.start(this);

        LOGGER.debug("{}: port {}, event count {}, xmlEvents {}", WebSocketEventProducerServer.class.getName(), port,
                        eventCount, xmlEvents);
    }

    /**
     * Send events.
     */
    public void sendEvents() {
        LOGGER.debug("{}: sending events on port {}, event count {}, xmlEvents {}",
                        WebSocketEventProducerServer.class.getName(), port, eventCount, xmlEvents);

        for (int i = 0; i < eventCount; i++) {
            LOGGER.debug("{}: waiting {} milliseconds before sending next event",
                            WebSocketEventProducerServer.class.getName(), eventInterval);
            ThreadUtilities.sleep(eventInterval);

            String eventString = null;
            if (xmlEvents) {
                eventString = EventGenerator.xmlEvent();
            } else {
                eventString = EventGenerator.jsonEvent();
            }
            server.sendString(eventString);
            eventsSentCount++;
            LOGGER.debug("{}: port {}, sent event {}", WebSocketEventProducerServer.class.getName(), port, eventString);
        }

        LOGGER.debug("{}: event sending completed", WebSocketEventProducerServer.class.getName());
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        server.stop();
        LOGGER.debug("{}: stopped", WebSocketEventProducerServer.class.getName());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void receiveString(final String eventString) {
        LOGGER.debug("{}: port {}, received event {}", WebSocketEventProducerServer.class.getName(), port, eventString);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws MessagingException the messaging exception
     */
    public static void main(final String[] args) throws MessagingException {
        if (args.length != 4) {
            LOGGER.error("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            LOGGER.error("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        int eventCount = 0;
        try {
            eventCount = Integer.parseInt(args[1]);
        } catch (final Exception e) {
            LOGGER.error("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        long eventInterval = 0;
        try {
            eventInterval = Long.parseLong(args[3]);
        } catch (final Exception e) {
            LOGGER.error("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        boolean xmlEvents = false;
        if (args[2].equalsIgnoreCase("XML")) {
            xmlEvents = true;
        } else if (!args[2].equalsIgnoreCase("JSON")) {
            LOGGER.error("usage WebSocketEventProducerServer port #events XML|JSON startDelay eventInterval");
            return;
        }

        final WebSocketEventProducerServer server = new WebSocketEventProducerServer(port, eventCount, xmlEvents,
                        eventInterval);

        server.sendEvents();
        server.shutdown();
    }
}
