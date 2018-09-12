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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageClient;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageServer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class EndToEndMessagingTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EndToEndStringMessagingTest {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(EndToEndStringMessagingTest.class);

    private WsStringMessageServer server;
    private WsStringMessageClient client;

    private boolean finished = false;

    @Test
    public void testEndToEndMessaging() throws MessagingException {
        logger.debug("end to end messaging test starting . . .");
        server = new WsStringMessageServer(44441);
        assertNotNull(server);
        server.start(new WsStringServerMessageListener());

        try {
            client = new WsStringMessageClient("localhost", 44441);
            assertNotNull(client);
            client.start(new WsStringClientMessageListener());

            client.sendString("Hello, client here");

            while (!finished) {
                ThreadUtilities.sleep(50);
            }
        } finally {
            if (client != null) {
                client.stop();
            }
            if (server != null) {
                server.stop();
            }

        }
        logger.debug("end to end messaging test finished");
    }

    private class WsStringServerMessageListener implements WsStringMessageListener {
        @Override
        public void receiveString(final String stringMessage) {
            logger.debug(stringMessage);
            assertEquals("Hello, client here", stringMessage);
            server.sendString("Hello back from server");
        }
    }

    private class WsStringClientMessageListener implements WsStringMessageListener {
        @Override
        public void receiveString(final String stringMessage) {
            logger.debug(stringMessage);
            assertEquals("Hello back from server", stringMessage);
            finished = true;
        }
    }
}
