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
import java.nio.ByteBuffer;

import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.RawMessageHandler;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlockHandler;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.RawMessageBlock;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class InternalMessageBusClient handles the client side of a web socket and handles the callback mechanism used to
 * receive messages on the web socket.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <MESSAGE> the generic type of message being handled
 */
abstract class InternalMessageBusClient<MESSAGE> extends WebSocketClientImpl {
    private static final int THREAD_FACTORY_STACK_SIZE = 256;

    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(InternalMessageBusClient.class);

    // Name of the event bus.
    private static final String RAW_EVENT_BUS = "Raw-Event-Bus";

    // This instance handles the raw data received from the web socket
    private final RawMessageHandler<MESSAGE> rawMessageHandler = new RawMessageHandler<>();

    // The message block handler to which to pass messages coming in on this client
    private MessageBlockHandler<MESSAGE> messageBlockHandler = null;

    // The raw message handler uses a thread to process incoming events off a queue, this class owns and controls that
    // thread. These fields hold the thread and
    // the thread factory for creating threads.
    private ApplicationThreadFactory tFactory =
            new ApplicationThreadFactory("ws-client-thread", THREAD_FACTORY_STACK_SIZE);
    private Thread forwarderThread = null;

    /**
     * Construct the class and start the forwarding thread for received messages.
     *
     * @param serverUri the server URI to connect to
     */
    InternalMessageBusClient(final URI serverUri) {
        // Call the super class to create the web socket
        super(serverUri);
        LOGGER.entry(serverUri.toString());

        // Create the data handler for forwarding messages
        messageBlockHandler = new MessageBlockHandler<>(RAW_EVENT_BUS);
        messageBlockHandler.registerMessageHandler(rawMessageHandler);

        // Create the thread that manages the queue in the data handler
        forwarderThread = tFactory.newThread(rawMessageHandler);
        forwarderThread.start();

        LOGGER.exit();
    }

    /**
     * Callback for binary messages received from the remote host.
     *
     * @param rawMessage the received raw message
     * @see org.java_websocket.client.WebSocketClient#onMessage(java.nio.ByteBuffer)
     */
    @Override
    public void onMessage(final ByteBuffer rawMessage) {
        // Post the message to the data handler for forwarding to its listeners
        messageBlockHandler.post(new RawMessageBlock(rawMessage, null));
    }

    /**
     * Callback for binary messages received from the remote host.
     *
     * @param stringMessage the string message
     * @see org.java_websocket.client.WebSocketClient#onMessage(java.lang.String)
     */
    @Override
    public final void onMessage(final String stringMessage) {
        messageBlockHandler.post(stringMessage);
    }

    /**
     * Register a subscriber class to the raw message handler.
     *
     * @param listener a simple class, that listens for the events from Event
     */
    public void addMessageListener(final MessageListener<MESSAGE> listener) {
        rawMessageHandler.registerDataForwarder(listener);
    }

    /**
     * Removes the message listener.
     *
     * @param listener the listener
     */
    public void removeMessageListener(final MessageListener<MESSAGE> listener) {
        rawMessageHandler.unRegisterDataForwarder(listener);
    }

    /**
     * Stop the thread handling message forwarding.
     */
    protected void stopListener() {
        rawMessageHandler.shutdown();
    }
}
