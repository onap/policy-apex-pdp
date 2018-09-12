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
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageServer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

public class WebSocketEventProducerServer implements WsStringMessageListener {
    private final int port;
    private final int eventCount;
    private final boolean xmlEvents;
    private final long eventInterval;
    private long eventsSentCount = 0;

    WsStringMessageServer server;

    public WebSocketEventProducerServer(final int port, final int eventCount, final boolean xmlEvents,
            final long eventInterval) throws MessagingException {
        this.port = port;
        this.eventCount = eventCount;
        this.xmlEvents = xmlEvents;
        this.eventInterval = eventInterval;

        server = new WsStringMessageServer(port);
        server.start(this);

        System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ": port " + port + ", event count "
                + eventCount + ", xmlEvents " + xmlEvents);
    }

    public void sendEvents() {
        System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ": sending events on port " + port
                + ", event count " + eventCount + ", xmlEvents " + xmlEvents);

        for (int i = 0; i < eventCount; i++) {
            System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ": waiting " + eventInterval
                    + " milliseconds before sending next event");
            ThreadUtilities.sleep(eventInterval);

            String eventString = null;
            if (xmlEvents) {
                eventString = EventGenerator.xmlEvent();
            } else {
                eventString = EventGenerator.jsonEvent();
            }
            server.sendString(eventString);
            eventsSentCount++;
            System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ": port " + port
                    + ", sent event " + eventString);
        }

        System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ": event sending completed");
    }

    public long getEventsSentCount() {
        return eventsSentCount;
    }

    public void shutdown() {
        server.stop();
        System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ": stopped");
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
        System.out.println(WebSocketEventProducerServer.class.getCanonicalName() + ": port " + port
                + ", received event " + eventString);
    }

    public static void main(final String[] args) throws MessagingException {
        if (args.length != 4) {
            System.err.println("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        int eventCount = 0;
        try {
            eventCount = Integer.parseInt(args[1]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        long eventInterval = 0;
        try {
            eventInterval = Long.parseLong(args[3]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventProducerServer port #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        boolean xmlEvents = false;
        if (args[2].equalsIgnoreCase("XML")) {
            xmlEvents = true;
        } else if (!args[2].equalsIgnoreCase("JSON")) {
            System.err.println("usage WebSocketEventProducerServer port #events XML|JSON startDelay eventInterval");
            return;
        }

        final WebSocketEventProducerServer server =
                new WebSocketEventProducerServer(port, eventCount, xmlEvents, eventInterval);

        server.sendEvents();
        server.shutdown();
    }
}
