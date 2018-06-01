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
public class WSStringMessageClient implements WSStringMessager {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(WSStringMessageClient.class);

    // Message service factory and the message service itself
    private final MessagingServiceFactory<String> factory = new MessagingServiceFactory<>();
    private MessagingService<String> service = null;

    // The listener to use for reception of strings
    private WSStringMessageListener wsStringMessageListener;

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
    public WSStringMessageClient(final String host, final int port) {
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
    public void start(final WSStringMessageListener newWsStringMessageListener) throws MessagingException {
        this.wsStringMessageListener = newWsStringMessageListener;

        uriString = "ws://" + host + ":" + port;
        LOGGER.entry("web socket event consumer client to \"" + uriString + "\" starting . . .");

        try {
            service = factory.createClient(new URI(uriString));
            service.addMessageListener(new WSStringMessageClientListener());
            service.startConnection();
        } catch (final Exception e) {
            LOGGER.warn("web socket event consumer client to \"" + uriString + "\" start failed", e);
            throw new MessagingException("web socket event consumer client to \"" + uriString + "\" start failed", e);
        }

        LOGGER.exit("web socket event consumer client to \"" + uriString + "\" started");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageSender#stop()
     */
    @Override
    public void stop() {
        LOGGER.entry("web socket event consumer client to \"" + uriString + "\" stopping . . .");
        service.stopConnection();
        LOGGER.exit("web socket event consumer client to \"" + uriString + "\" stopped");
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
            LOGGER.debug("message sent to server: " + stringMessage);
        }
    }

    /**
     * The Class WSStringMessageClientListener.
     */
    private class WSStringMessageClientListener implements MessageListener<String> {
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
