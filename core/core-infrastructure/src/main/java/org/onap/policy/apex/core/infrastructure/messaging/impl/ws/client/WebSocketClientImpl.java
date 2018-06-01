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

package org.onap.policy.apex.core.infrastructure.messaging.impl.ws.client;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class implements {@link WebSocketClient} specific methods in order to act as a Java Web Socket client.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
abstract class WebSocketClientImpl extends WebSocketClient {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(WebSocketClientImpl.class);

    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The channel does not
     * attempt to connect automatically. You must call {@link connect} first to initiate the socket connection.
     *
     * @param serverUri the URI of the web socket server to connect to
     */
    WebSocketClientImpl(final URI serverUri) {
        super(serverUri);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.java_websocket.client.WebSocketClient#onOpen(org.java_websocket.handshake.ServerHandshake)
     */
    @Override
    public void onOpen(final ServerHandshake handshakedata) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connection opened to server {} --> {}", this.getURI(), handshakedata.getHttpStatusMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.java_websocket.client.WebSocketClient#onClose(int, java.lang.String, boolean)
     */
    @Override
    public void onClose(final int code, final String reason, final boolean remote) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connection closed to server {} --> code \"{}\", reason \"{}\"", this.getURI(), code, reason);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.java_websocket.client.WebSocketClient#onError(java.lang.Exception)
     */
    @Override
    public void onError(final Exception ex) {
        LOGGER.info("Failed to make a connection to the server {} ", getURI());
        LOGGER.catching(ex);
    }
}
