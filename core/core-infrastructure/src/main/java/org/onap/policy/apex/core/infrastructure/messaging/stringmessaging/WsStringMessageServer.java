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

package org.onap.policy.apex.core.infrastructure.messaging.stringmessaging;

import com.google.common.eventbus.Subscribe;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingService;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingServiceFactory;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class runs a web socket server for sending and receiving of strings over a web socket.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class WsStringMessageServer implements WsStringMessager {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(WsStringMessageServer.class);

    // Message service factory and the message service itself
    private final MessagingServiceFactory<String> factory = new MessagingServiceFactory<>();
    private MessagingService<String> service = null;

    // The listener to use for reception of strings
    private WsStringMessageListener wsStringMessageListener;

    // Address of the server
    private final int port;

    /**
     * Constructor, define the port of the server.
     *
     * @param port the port of the server
     */
    public WsStringMessageServer(final int port) {
        this.port = port;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageSender#start(org.onap.policy.
     * apex. core.infrastructure.messaging. stringmessaging.WSStringMessageListener)
     */
    @Override
    public void start(final WsStringMessageListener newWsStringMessageListener) throws MessagingException {
        this.wsStringMessageListener = newWsStringMessageListener;

        LOGGER.entry("web socket event consumer server starting . . .");

        try {
            final InetAddress addrLan = MessagingUtils.getLocalHostLanAddress();
            LOGGER.debug("web socket string message server LAN address=" + addrLan.getHostAddress());
            final InetAddress addr = InetAddress.getLocalHost();
            LOGGER.debug("web socket string message server host address=" + addr.getHostAddress());

            service = factory.createServer(new InetSocketAddress(port));
            service.addMessageListener(new WsStringMessageServerListener());

            service.startConnection();
        } catch (final Exception e) {
            LOGGER.warn("web socket string message server start failed", e);
            throw new MessagingException("web socket string message start failed", e);
        }

        LOGGER.exit("web socket string message server started");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageSender#stop()
     */
    @Override
    public void stop() {
        LOGGER.entry("web socket string message server stopping . . .");
        service.stopConnection();
        LOGGER.exit("web socket string message server stopped");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageSender#sendString(java.lang.
     * String)
     */
    @Override
    public void sendString(final String stringMessage) {
        service.send(stringMessage);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("server sent message: {}", stringMessage);
        }
    }

    /**
     * The listener for strings coming into the server.
     */
    private class WsStringMessageServerListener implements MessageListener<String> {

        /*
         * (non-Javadoc)
         *
         * @see org.onap.policy.apex.core.infrastructure.messaging.MessageListener#onMessage(org.onap.policy.apex.core.
         * infrastructure.messaging.impl.ws.messageblock. MessageBlock)
         */
        @Subscribe
        @Override
        public void onMessage(final MessageBlock<String> messageBlock) {
            throw new UnsupportedOperationException("raw messages are not supported on string message clients");
        }

        /*
         * (non-Javadoc)
         *
         * @see org.onap.policy.apex.core.infrastructure.messaging.MessageListener#onMessage(java.lang.String)
         */
        @Subscribe
        @Override
        public void onMessage(final String messageString) {
            wsStringMessageListener.receiveString(messageString);
        }
    }
}
