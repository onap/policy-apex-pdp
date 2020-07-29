/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.core.deployment;

import com.google.common.eventbus.Subscribe;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.onap.policy.apex.core.infrastructure.messaging.MessageHolder;
import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingService;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingServiceFactory;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.core.protocols.Message;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class DeploymentClient handles the client side of an EngDep communication session with an Apex server. It runs a
 * thread to handle message sending and session monitoring. It uses a sending queue to queue messages for sending by the
 * client thread and a receiving queue to queue messages received from the Apex engine.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class DeploymentClient implements Runnable {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(DeploymentClient.class);

    private static final int CLIENT_STOP_WAIT_INTERVAL = 100;
    private static final int CLIENT_SEND_QUEUE_TIMEOUT = 50;

    // Host and port to use for EngDep messaging
    private String host = null;
    private int port = 0;

    // Messaging service is used to transmit and receive messages over the web socket
    private static MessagingServiceFactory<Message> factory = new MessagingServiceFactory<>();
    private MessagingService<Message> service = null;

    // Send and receive queues for message buffering
    private final BlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Message> receiveQueue = new LinkedBlockingQueue<>();

    // Thread management fields
    private boolean started = false;
    private Thread thisThread = null;

    // Number of messages processed
    private long messagesSent = 0;
    private long messagesReceived = 0;
    protected CountDownLatch countDownLatch;

    /**
     * Instantiates a new deployment client.
     *
     * @param host the host name that the EngDep server is running on
     * @param port the port the port the EngDep server is using
     */
    public DeploymentClient(final String host, final int port) {
        this.host = host;
        this.port = port;
        countDownLatch = new CountDownLatch(1);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        LOGGER.debug("engine<-->deployment to \"ws://{}:{}\" thread starting . . .", host, port);

        // Set up the thread name
        thisThread = Thread.currentThread();
        thisThread.setName(DeploymentClient.class.getName() + "-" + host + ":" + port);

        try {
            // Establish a connection to the Apex server for EngDep message communication over Web
            // Sockets
            service = factory.createClient(new URI("ws://" + host + ":" + port));
            service.addMessageListener(new DeploymentClientListener());

            service.startConnection();
            started = true;
            countDownLatch.countDown();
            LOGGER.debug("engine<-->deployment client thread started");
        } catch (final Exception e) {
            LOGGER.error("engine<-->deployment client thread exception", e);
            return;
        }
        // Loop forever, sending messages as they appear on the queue
        while (started && !thisThread.isInterrupted()) {
            started = sendMessages();
        }

        // Thread has been interrupted
        thisThread = null;
        LOGGER.debug("engine<-->deployment client thread finished");
    }

    /**
     * Send messages off the queue.
     */
    private boolean sendMessages() {
        try {
            final Message messageForSending = sendQueue.poll(CLIENT_SEND_QUEUE_TIMEOUT, TimeUnit.MILLISECONDS);
            if (messageForSending == null) {
                return true;
            }

            // Send the message in its message holder
            InetAddress local = getLocalAddress();
            final MessageHolder<Message> messageHolder = new MessageHolder<>(local);
            messageHolder.addMessage(messageForSending);
            service.send(messageHolder);
            messagesSent++;
        } catch (final InterruptedException e) {
            // Message sending has been interrupted, we are finished
            LOGGER.debug("engine<-->deployment client interrupted");
            // restore the interrupt status
            thisThread.interrupt();
            return false;
        }

        return true;
    }

    /**
     * Get the local address for the WS MessageHolder, or null if there is a problem.
     */
    private InetAddress getLocalAddress() {
        try {
            return MessagingUtils.getLocalHostLanAddress();
        } catch (UnknownHostException e) {
            LOGGER.debug("engine<-->deployment client failed to find the localhost address - continuing ...", e);
            return null;
        }
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Send an EngDep message to the Apex server.
     *
     * @param message the message to send to the Apex server
     */
    public void sendMessage(final Message message) {
        sendQueue.add(message);
    }

    /**
     * Stop the deployment client.
     */
    public void stopClient() {
        LOGGER.debug("engine<-->deployment test client stopping . . .");
        thisThread.interrupt();

        // Wait for the thread to stop
        ThreadUtilities.sleep(CLIENT_STOP_WAIT_INTERVAL);

        // Close the Web Services connection
        if (service != null) {
            service.stopConnection();
        }
        started = false;
        countDownLatch = new CountDownLatch(1);
        LOGGER.debug("engine<-->deployment test client stopped . . .");
    }

    /**
     * Checks if the client thread is started.
     *
     * @return true, if the client thread is started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Allows users of this class to get a reference to the receive queue to receove messages.
     *
     * @return the receive queue
     */
    public BlockingQueue<Message> getReceiveQueue() {
        return receiveQueue;
    }

    /**
     * Get the number of messages received by the client.
     * @return the number of messages received by the client
     */
    public long getMessagesReceived() {
        return messagesReceived;
    }

    /**
     * Get the number of messages sent by the client.
     * @return the number of messages sent by the client
     */
    public long getMessagesSent() {
        return messagesSent;
    }

    /**
     * The listener interface for receiving deploymentClient events. The class that is interested in processing a
     * deploymentClient event implements this interface, and the object created with that class is registered with a
     * component using the component's {@code addDeploymentClientListener} method. When the deploymentClient event
     * occurs, that object's appropriate method is invoked.
     *
     * @see DeploymentClientEvent
     */
    private class DeploymentClientListener implements MessageListener<Message> {
        /**
         * {@inheritDoc}.
         */
        @Subscribe
        @Override
        public void onMessage(final MessageBlock<Message> messageData) {
            messagesReceived++;
            receiveQueue.addAll(messageData.getMessages());
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        public void onMessage(final String messageString) {
            messagesReceived++;
            throw new UnsupportedOperationException("String mesages are not supported on the EngDep protocol");
        }
    }
}
