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

/**
 * The Interface MessagingService specifies the methods that must be implemented by any implementation providing Apex
 * messaging.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <MESSAGE> the type of message being passed by an implementation of Apex messaging
 */
public interface MessagingService<MESSAGE> {

    /**
     * Start the messaging connection.
     */
    void startConnection();

    /**
     * Stop the messaging connection.
     */
    void stopConnection();

    /**
     * Checks if the messaging connection is started.
     *
     * @return true, if is started
     */
    boolean isStarted();

    /**
     * Send a block of messages on the connection, the messages are contained in the the message holder container.
     *
     * @param messageHolder The message holder holding the messages to be sent
     */
    void send(MessageHolder<MESSAGE> messageHolder);

    /**
     * Send a string message on the connection.
     *
     * @param messageString The message string to be sent
     */
    void send(String messageString);

    /**
     * Adds a message listener that will be called when a message is received by this messaging service implementation.
     *
     * @param messageListener the message listener
     */
    void addMessageListener(MessageListener<MESSAGE> messageListener);

    /**
     * Removes the message listener.
     *
     * @param messageListener the message listener
     */
    void removeMessageListener(MessageListener<MESSAGE> messageListener);
}
