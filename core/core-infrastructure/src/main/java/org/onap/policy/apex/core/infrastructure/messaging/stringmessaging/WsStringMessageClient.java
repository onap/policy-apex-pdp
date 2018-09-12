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

import java.net.URI;

import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingService;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingServiceFactory;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class uses a web socket client to send and receive strings over a web socket.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class WsStringMessageClient implements WsStringMessager {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(WsStringMessageClient.class);

    // Repeated string constants
    private static final String MESSAGE_PREAMBLE = "web socket event consumer client to \"";

    // Message service factory and the message service itself
    private final MessagingServiceFactory<String> factory = new MessagingServiceFactory<>();
    private MessagingService<String> service = null;

    // The listener to use for reception of strings
    private WsStringMessageListener wsStringMessageListener;

    // Address of the server
    private final String host;
    private final int port;
    private String uriString;

    /**
     * Constructor, define the host and port of the server to connect to.
     *
     * @param host the host of the server
     * @param port the port of the server
     */
    public WsStringMessageClient(final String host, final int port) {
        this.host = host;
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

        uriString = "ws://" + host + ":" + port;
        String messagePreamble = MESSAGE_PREAMBLE + uriString + "\" ";
        LOGGER.entry(messagePreamble + "starting . . .");

        try {
            service = factory.createClient(new URI(uriString));
            service.addMessageListener(new WsStringMessageClientListener());
            service.startConnection();
        } catch (final Exception e) {
            String message = messagePreamble + "start failed";
            LOGGER.warn(message, e);
            throw new MessagingException(message, e);
        }

        LOGGER.exit(messagePreamble + "started");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageSender#stop()
     */
    @Override
    public void stop() {
        LOGGER.entry(MESSAGE_PREAMBLE + uriString + "\" stopping . . .");
        service.stopConnection();
        LOGGER.exit(MESSAGE_PREAMBLE + uriString + "\" stopped");
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
            String message = "message sent to server: " + stringMessage;
            LOGGER.debug(message);
        }
    }

    /**
     * The Class WSStringMessageClientListener.
     */
    private class WsStringMessageClientListener implements MessageListener<String> {
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
