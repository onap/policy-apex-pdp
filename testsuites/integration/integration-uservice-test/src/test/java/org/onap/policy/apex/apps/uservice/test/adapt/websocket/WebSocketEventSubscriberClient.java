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

import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageClient;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageListener;

public class WebSocketEventSubscriberClient implements WSStringMessageListener {
    private final int port;
    private long eventsReceivedCount = 0;

    private final WSStringMessageClient client;

    public WebSocketEventSubscriberClient(final String host, final int port) throws MessagingException {
        this.port = port;

        client = new WSStringMessageClient(host, port);
        client.start(this);
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
        System.out.println(WebSocketEventSubscriberClient.class.getCanonicalName() + ": port " + port
                + ", received event " + eventString);
        eventsReceivedCount++;
    }

    public long getEventsReceivedCount() {
        return eventsReceivedCount;
    }

    public void shutdown() {
        client.stop();
        System.out.println(WebSocketEventSubscriberServer.class.getCanonicalName() + ": stopped");
    }

    public static void main(final String[] args) throws MessagingException {
        if (args.length != 2) {
            System.err.println("usage WebSocketEventSubscriberClient host port");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventSubscriberClient port");
            e.printStackTrace();
            return;
        }

        new WebSocketEventSubscriberClient(args[0], port);
    }
}
