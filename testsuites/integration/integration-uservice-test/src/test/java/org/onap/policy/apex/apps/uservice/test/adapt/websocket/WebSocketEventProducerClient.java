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

package org.onap.policy.apex.apps.uservice.test.adapt.websocket;

import org.onap.policy.apex.apps.uservice.test.adapt.events.EventGenerator;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageClient;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

/**
 * The Class WebSocketEventProducerClient.
 */
public class WebSocketEventProducerClient implements WsStringMessageListener {
    private final String host;
    private final int port;
    private final int eventCount;
    private final boolean xmlEvents;
    private final long eventInterval;
    private long eventsSentCount = 0;

    WsStringMessageClient client;

    /**
     * Instantiates a new web socket event producer client.
     *
     * @param host the host
     * @param port the port
     * @param eventCount the event count
     * @param xmlEvents the xml events
     * @param eventInterval the event interval
     * @throws MessagingException the messaging exception
     */
    public WebSocketEventProducerClient(final String host, final int port, final int eventCount,
            final boolean xmlEvents, final long eventInterval) throws MessagingException {
        this.host = host;
        this.port = port;
        this.eventCount = eventCount;
        this.xmlEvents = xmlEvents;
        this.eventInterval = eventInterval;

        client = new WsStringMessageClient(host, port);
        client.start(this);

        System.out.println(WebSocketEventProducerClient.class.getCanonicalName() + ": host " + host + ", port " + port
                + ", event count " + eventCount + ", xmlEvents " + xmlEvents);
    }

    /**
     * Send events.
     */
    public void sendEvents() {
        System.out.println(WebSocketEventProducerClient.class.getCanonicalName() + ": sending events on host " + host
                + ", port " + port + ", event count " + eventCount + ", xmlEvents " + xmlEvents);

        for (int i = 0; i < eventCount; i++) {
            System.out.println(WebSocketEventProducerClient.class.getCanonicalName() + ": waiting " + eventInterval
                    + " milliseconds before sending next event");
            ThreadUtilities.sleep(eventInterval);

            String eventString = null;
            if (xmlEvents) {
                eventString = EventGenerator.xmlEvent();
            } else {
                eventString = EventGenerator.jsonEvent();
            }
            client.sendString(eventString);
            eventsSentCount++;
            System.out.println(WebSocketEventProducerClient.class.getCanonicalName() + ":  host " + host + ", port "
                    + port + ", sent event " + eventString);
        }
        System.out.println(WebSocketEventProducerClient.class.getCanonicalName() + ": completed");
    }

    /**
     * Gets the events sent count.
     *
     * @return the events sent count
     */
    public long getEventsSentCount() {
        return eventsSentCount;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        client.stop();
        System.out.println(WebSocketEventProducerClient.class.getCanonicalName() + ": stopped");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageListener#
     * receiveString(java.lang.String)
     */
    @Override
    public void receiveString(final String eventString) {
        System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ":  host " + host + ", port " + port
                + ", received event " + eventString);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws MessagingException the messaging exception
     */
    public static void main(final String[] args) throws MessagingException {
        if (args.length != 5) {
            System.err.println("usage WebSocketEventProducerClient host port #events XML|JSON eventInterval");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventProducerClient host port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        int eventCount = 0;
        try {
            eventCount = Integer.parseInt(args[2]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventProducerClient host port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        long eventInterval = 0;
        try {
            eventInterval = Long.parseLong(args[4]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventProducerClient host port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        boolean xmlEvents = false;
        if (args[3].equalsIgnoreCase("XML")) {
            xmlEvents = true;
        } else if (!args[3].equalsIgnoreCase("JSON")) {
            System.err.println("usage WebSocketEventProducerClient host port #events XML|JSON eventInterval");
            return;
        }

        final WebSocketEventProducerClient client =
                new WebSocketEventProducerClient(args[0], port, eventCount, xmlEvents, eventInterval);

        client.sendEvents();
        client.shutdown();
    }
}
