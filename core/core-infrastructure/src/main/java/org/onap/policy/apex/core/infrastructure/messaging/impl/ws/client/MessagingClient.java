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

import org.java_websocket.WebSocket;
import org.onap.policy.apex.core.infrastructure.messaging.MessageHolder;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingService;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

/**
 * The Class MessagingClient is the class that wraps web socket handling, message sending, and message reception on the
 * client side of a web socket in Apex.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <MESSAGE> the generic type
 */
public class MessagingClient<MESSAGE> extends InternalMessageBusClient<MESSAGE> implements MessagingService<MESSAGE> {
    // The length of time to wait for a connection to a web socket server before aborting
    private static final int CONNECTION_TIMEOUT_TIME_MS = 3000;

    // The length of time to wait before checking if a connection to a web socket server has worked or not
    private static final int CONNECTION_TRY_INTERVAL_MS = 100;

    /**
     * Constructor of this class, uses its {@link InternalMessageBusClient} superclass to set up the web socket and
     * handle incoming message forwarding.
     *
     * @param serverUri The URI of the service
     */
    public MessagingClient(final URI serverUri) {
        // Call the super class to create the web socket and set up received message forwarding
        super(serverUri);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessagingService#stopConnection()
     */
    @Override
    public void stopConnection() {
        // Stop message reception in the super class
        super.stopListener();

        // Close the web socket
        final WebSocket connection = super.getConnection();
        if (connection != null && connection.isOpen()) {
            connection.closeConnection(0, "");
        }
        this.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessagingService#startConnection()
     */
    @Override
    public void startConnection() {
        // Open the web socket
        final WebSocket connection = super.getConnection();
        if (connection != null && !connection.isOpen()) {
            connect();
        }

        if (!waitforConnection(connection)) {
            throw new IllegalStateException("Could not connect to the server");
        }
    }

    /**
     * This method waits for the timeout value for the client to connect to the web socket server.
     *
     * @param connection the connection to wait on
     * @return true, if successful
     */
    private boolean waitforConnection(final WebSocket connection) {
        // The total time we have before timeout
        int timeoutMSCounter = CONNECTION_TIMEOUT_TIME_MS;

        // Check the connection state
        do {
            switch (connection.getReadyState()) {
                case NOT_YET_CONNECTED:
                case CONNECTING:
                case CLOSING:
                    // Not connected yet so wait for the try interval
                    ThreadUtilities.sleep(CONNECTION_TRY_INTERVAL_MS);
                    timeoutMSCounter -= CONNECTION_TRY_INTERVAL_MS;
                    break;
                case OPEN:
                    // Connection is open, happy days
                    return true;
                case CLOSED:
                    // Connection is closed, bah
                    return false;
                default:
                    break;
            }
        }
        // While the timeout value has not expired
        while (timeoutMSCounter > 0);

        // We have timed out
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessagingService#send(org.onap.policy.apex.core.
     * infrastructure. messaging.MessageHolder)
     */
    @Override
    public void send(final MessageHolder<MESSAGE> commands) {
        // Get the connection and send the message
        final WebSocket connection = super.getConnection();
        connection.send(MessagingUtils.serializeObject(commands));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessagingService#send(java.lang.String)
     */
    @Override
    public void send(final String messageString) {
        final WebSocket connection = super.getConnection();
        connection.send(messageString);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessagingService#isStarted()
     */
    @Override
    public boolean isStarted() {
        return getConnection().isOpen();
    }
}
