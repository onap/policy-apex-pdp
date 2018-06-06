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

package org.onap.policy.apex.service.engine.engdep;

import java.net.InetSocketAddress;

import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import org.onap.policy.apex.core.infrastructure.messaging.MessagingService;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingServiceFactory;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.onap.policy.apex.core.protocols.Message;

/**
 * The Class EngDepMessagingService is used to encapsulate the server side of EngDep communication.
 * This class allows users to create and start an EngDep server.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EngDepMessagingService {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EngDepMessagingService.class);

    // Messaging service is used to transmit and receive messages over a communication protocol
    private static MessagingServiceFactory<Message> messageServiceFactory = new MessagingServiceFactory<>();
    private final MessagingService<Message> messageService;

    // The listener that is listening for messages coming in on the EngDep protocol from clients
    private final EngDepMessageListener messageListener;

    /**
     * Instantiates a new EngDep messaging service. It creates the message service instance, a
     * listener for incoming messages, and starts the message listener thread for handling incoming
     * messages.
     *
     * @param service the Apex engine service that this EngDep service is running for
     * @param port the port The port to use for EngDep communication
     */
    public EngDepMessagingService(final EngineService service, final int port) {
        LOGGER.entry(service);

        // Create the service and listener and add the listener.
        messageService = messageServiceFactory.createServer(new InetSocketAddress(MessagingUtils.checkPort(port)));
        messageListener = new EngDepMessageListener(service);
        messageService.addMessageListener(messageListener);

        // Start incoming message processing on the listener
        messageListener.startProcessorThread();
        LOGGER.exit();
    }

    /**
     * Start the server, open the communication mechanism for connections.
     */
    public void start() {
        LOGGER.info("engine<-->deployment messaging starting . . .");
        messageService.startConnection();
        LOGGER.info("engine<-->deployment messaging started");
    }

    /**
     * Start the server, close the communication mechanism.
     */
    public void stop() {
        LOGGER.info("engine<-->deployment messaging stopping . . .");
        messageService.stopConnection();
        messageListener.stopProcessorThreads();
        LOGGER.info("engine<-->deployment messaging stopped");
    }

    /**
     * Is the server started?.
     *
     * @return true, if checks if is started
     */
    public boolean isStarted() {
        return messageService.isStarted();
    }

    /**
     * Is the server stopped?.
     *
     * @return true, if checks if is stopped
     */
    public boolean isStopped() {
        return !messageService.isStarted();
    }
}
