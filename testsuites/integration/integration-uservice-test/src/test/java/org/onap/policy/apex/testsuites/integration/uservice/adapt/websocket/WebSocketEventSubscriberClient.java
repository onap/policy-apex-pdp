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
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageClient;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WebSocketEventSubscriberClient.
 */
public class WebSocketEventSubscriberClient implements WsStringMessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventSubscriberClient.class);

    private final int port;
    @Getter
    private long eventsReceivedCount = 0;

    private final WsStringMessageClient client;

    /**
     * Instantiates a new web socket event subscriber client.
     *
     * @param host the host
     * @param port the port
     * @throws MessagingException the messaging exception
     */
    public WebSocketEventSubscriberClient(final String host, final int port) throws MessagingException {
        this.port = port;

        client = new WsStringMessageClient(host, port);
        client.start(this);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void receiveString(final String eventString) {
        LOGGER.debug("{}: port {}, received event {}", WebSocketEventSubscriberClient.class.getName(), port,
                        eventString);
        eventsReceivedCount++;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        client.stop();
        LOGGER.debug("{}: stopped", WebSocketEventSubscriberServer.class.getName());
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws MessagingException the messaging exception
     */
    public static void main(final String[] args) throws MessagingException {
        if (args.length != 2) {
            LOGGER.error("usage WebSocketEventSubscriberClient host port");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            LOGGER.error("usage WebSocketEventSubscriberClient port");
            e.printStackTrace();
            return;
        }

        new WebSocketEventSubscriberClient(args[0], port);
    }
}
