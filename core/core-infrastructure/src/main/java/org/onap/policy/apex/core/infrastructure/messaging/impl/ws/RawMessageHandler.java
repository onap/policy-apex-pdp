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

package org.onap.policy.apex.core.infrastructure.messaging.impl.ws;

import com.google.common.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.core.infrastructure.messaging.MessageHolder;
import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlockHandler;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.RawMessageBlock;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class RawMessageHandler handles raw messages being received on a Java web socket and forwards
 * the messages to the DataHandler instance that has subscribed to the RawMessageHandler instance.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <MESSAGE> the generic type of message being received
 */
public class RawMessageHandler<MESSAGE> implements WebSocketMessageListener<MESSAGE>, Runnable {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(RawMessageHandler.class);

    // The amount of time to sleep during shutdown for the thread of this message handler to stop
    private static final int SHUTDOWN_WAIT_TIME = 10;

    // The timeout to wait between queue poll timeouts in milliseconds
    private static final long QUEUE_POLL_TIMEOUT = 50;

    // A queue that temporarily holds message blocks
    private final BlockingQueue<MessageBlock<MESSAGE>> messageBlockQueue = new LinkedBlockingDeque<>();

    // A queue that temporarily holds message blocks
    private final BlockingQueue<String> stringMessageQueue = new LinkedBlockingDeque<>();

    // Client applications that have subscribed for messages
    private final MessageBlockHandler<MESSAGE> dataHandler = new MessageBlockHandler<MESSAGE>("data-processor");

    // The thread that the raw message handler is receiving messages on
    private Thread thisThread = null;

    /**
     * This method is called by the class with which this message listener has been registered.
     *
     * @param incomingData the data forwarded by the message reception class
     */
    @Override
    @Subscribe
    public void onMessage(final RawMessageBlock incomingData) {
        // Sanity check and get incoming data
        ByteBuffer dataByteBuffer = null;
        if (incomingData != null && incomingData.getMessage() != null) {
            dataByteBuffer = incomingData.getMessage();
        } else {
            return;
        }

        // Read the messages from the web socket and place them on the message queue for handling by
        // the queue
        // processing thread
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(dataByteBuffer.array()));
            @SuppressWarnings("unchecked")
            final MessageHolder<MESSAGE> messageHolder = (MessageHolder<MESSAGE>) ois.readObject();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("message {} recieved from the client {} ", messageHolder,
                        messageHolder == null ? "Apex Engine " : messageHolder.getSenderHostAddress());
            }

            final List<MESSAGE> messages = messageHolder.getMessages();
            if (messages != null) {
                messageBlockQueue.add(new MessageBlock<MESSAGE>(messages, incomingData.getConn()));
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Failed to process message received");
            LOGGER.catching(e);
        } finally {
            closeObjectStream(ois);
        }
    }

    /**
     * This method is called when a string message is received on a web socket and is to be
     * forwarded to a listener.
     *
     * @param messageString the message string
     */
    @Override
    @Subscribe
    public void onMessage(final String messageString) {
        if (messageString == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("message {} recieved from the client {} ", messageString);
        }
        stringMessageQueue.add(messageString);
    }

    /**
     * Close the {@link ObjectInputStream} stream.
     *
     * @param ois is an instance of {@link ObjectInputStream}
     */
    private void closeObjectStream(final ObjectInputStream ois) {
        if (ois != null) {
            try {
                ois.close();
            } catch (final IOException e) {
                LOGGER.catching(e);
            }
        }
    }

    /**
     * This thread monitors the message queue and processes messages as they appear on the queue.
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOGGER.debug("raw message listening started");
        thisThread = Thread.currentThread();

        // Run until termination
        while (thisThread.isAlive() && !thisThread.isInterrupted()) {
            try {
                // Read message block messages from the queue and pass it to the data handler
                MessageBlock<MESSAGE> messageBlock = null;
                while ((messageBlock = messageBlockQueue.poll(1, TimeUnit.MILLISECONDS)) != null) {
                    dataHandler.post(messageBlock);
                }
            } catch (final InterruptedException e) {
                // restore the interrupt status
                Thread.currentThread().interrupt();
                LOGGER.debug("raw message listening has been interrupted");
                break;
            }

            try {
                // Read string messages from the queue and pass it to the data handler
                String stringMessage = null;
                while ((stringMessage = stringMessageQueue.poll(1, TimeUnit.MILLISECONDS)) != null) {
                    dataHandler.post(stringMessage);
                }
            } catch (final InterruptedException e) {
                // restore the interrupt status
                Thread.currentThread().interrupt();
                LOGGER.debug("raw message listening has been interrupted");
                break;
            }

            // Wait for new messages
            try {
                Thread.sleep(QUEUE_POLL_TIMEOUT);
            } catch (final InterruptedException e) {
                // restore the interrupt status
                Thread.currentThread().interrupt();
                LOGGER.debug("raw message listening has been interrupted");
                break;
            }
        }

        LOGGER.debug("raw message listening stopped");
    }

    /**
     * Shutdown the message handler.
     */
    public void shutdown() {
        LOGGER.entry("shutting down raw message listening . . .");

        // Interrupt the message handling thread
        thisThread.interrupt();

        // Wait for thread shutdown
        while (thisThread.isAlive()) {
            ThreadUtilities.sleep(SHUTDOWN_WAIT_TIME);
        }

        LOGGER.exit("shut down raw message listening");
    }

    /**
     * This method is called when a message is received on a web socket and is to be forwarded to a
     * listener.
     *
     * @param data the message data containing a message
     */
    @Override
    public void onMessage(final MessageBlock<MESSAGE> data) {
        throw new UnsupportedOperationException("this operation is not supported");
    }

    /**
     * Register a data forwarder to which messages coming in on the web socket will be forwarded.
     *
     * @param listener The listener to register
     */
    @Override
    public void registerDataForwarder(final MessageListener<MESSAGE> listener) {
        stateCheck(listener);
        dataHandler.registerMessageHandler(listener);
    }

    /**
     * Unregister a data forwarder that was previously registered on the web socket listener.
     *
     * @param listener The listener to unregister
     */
    @Override
    public void unRegisterDataForwarder(final MessageListener<MESSAGE> listener) {
        stateCheck(listener);
        dataHandler.unRegisterMessageHandler(listener);
    }

    /**
     * Sanity check for the listener and data handler.
     *
     * @param listener the listener to check
     */
    private void stateCheck(final MessageListener<MESSAGE> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("The listener object cannot be null");
        }
    }
}
