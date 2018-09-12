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

package org.onap.policy.apex.core.infrastructure.messaging;

import static org.junit.Assert.assertNotNull;

import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageServer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

// TODO: Auto-generated Javadoc
/**
 * The Class StringTestServer.
 */
public class StringTestServer {
    private WsStringMessageServer server;

    /**
     * Create a string test server.
     *
     * @param port port to use
     * @param timeToLive time to live
     * @throws MessagingException exceptions on messages
     */
    public StringTestServer(final int port, long timeToLive) throws MessagingException {
        System.out.println("StringTestServer starting on port " + port + " for " + timeToLive + " seconds . . .");
        server = new WsStringMessageServer(port);
        assertNotNull(server);
        server.start(new WsStringServerMessageListener());

        System.out.println("StringTestServer started on port " + port + " for " + timeToLive + " seconds");

        for (; timeToLive > 0; timeToLive--) {
            ThreadUtilities.sleep(1000);
        }

        server.stop();
        System.out.println("StringTestServer completed");
    }

    /**
     * The listener interface for receiving WSStringServerMessage events. The class that is interested in processing a
     * WSStringServerMessage event implements this interface, and the object created with that class is registered with
     * a component using the component's <code>addWSStringServerMessageListener</code> method. When the
     * WSStringServerMessage event occurs, that object's appropriate method is invoked.
     *
     * @see WSStringServerMessageEvent
     */
    private class WsStringServerMessageListener implements WsStringMessageListener {

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener#receiveString(java
         * .lang.String)
         */
        @Override
        public void receiveString(final String stringMessage) {
            System.out.println("Server received string \"" + stringMessage + "\"");
            server.sendString("Server echoing back the message: \"" + stringMessage + "\"");
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws MessagingException the messaging exception
     */
    public static void main(final String[] args) throws MessagingException {
        if (args.length != 2) {
            System.err.println("Usage: StringTestServer port timeToLive");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            System.err.println("Usage: StringTestServer port timeToLive");
            e.printStackTrace();
            return;
        }

        long timeToLive = 0;
        try {
            timeToLive = Long.parseLong(args[1]);
        } catch (final Exception e) {
            System.err.println("Usage: StringTestServer port timeToLive");
            e.printStackTrace();
            return;
        }

        new StringTestServer(port, timeToLive);

    }
}
