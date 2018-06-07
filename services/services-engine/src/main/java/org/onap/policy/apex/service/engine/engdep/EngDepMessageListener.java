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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.onap.policy.apex.core.infrastructure.messaging.MessageHolder;
import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.core.protocols.engdep.EngDepAction;
import org.onap.policy.apex.core.protocols.engdep.messages.EngineServiceInfoResponse;
import org.onap.policy.apex.core.protocols.engdep.messages.GetEngineInfo;
import org.onap.policy.apex.core.protocols.engdep.messages.GetEngineServiceInfo;
import org.onap.policy.apex.core.protocols.engdep.messages.GetEngineStatus;
import org.onap.policy.apex.core.protocols.engdep.messages.Response;
import org.onap.policy.apex.core.protocols.engdep.messages.StartEngine;
import org.onap.policy.apex.core.protocols.engdep.messages.StartPeriodicEvents;
import org.onap.policy.apex.core.protocols.engdep.messages.StopEngine;
import org.onap.policy.apex.core.protocols.engdep.messages.StopPeriodicEvents;
import org.onap.policy.apex.core.protocols.engdep.messages.UpdateModel;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.common.eventbus.Subscribe;

/**
 * The listener interface for receiving engDepMessage events. The class that is interested in
 * processing a engDepMessage event implements this interface, and the object created with that
 * class is registered with a component using the component's <code>addEngDepMessageListener</code>
 * method. When the engDepMessage event occurs, that object's appropriate method is invoked.
 *
 * This class uses a queue to buffer incoming messages. When the listener is called, it places the
 * incoming message on the queue. A thread runs which removes the messages from the queue and
 * forwards them to the Apex engine.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public class EngDepMessageListener implements MessageListener<Message>, Runnable {
    private static final int LISTENER_STOP_WAIT_INTERVAL = 10;

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EngDepMessageListener.class);

    // The timeout to wait between queue poll timeouts in milliseconds
    private static final long QUEUE_POLL_TIMEOUT = 50;

    // The Apex service itself
    private final EngineService apexService;

    // The message listener thread and stopping flag
    private Thread messageListenerThread;
    private boolean stopOrderedFlag = false;

    // The message queue is used to hold messages prior to forwarding to Apex
    private final BlockingQueue<MessageBlock<Message>> messageQueue = new LinkedBlockingDeque<>();

    /**
     * Instantiates a new EngDep message listener for listening for messages coming in from the
     * Deployment client. The <code>apexService</code> is the Apex service to send the messages
     * onto.
     *
     * @param apexService the Apex engine service
     */
    protected EngDepMessageListener(final EngineService apexService) {
        this.apexService = apexService;
    }

    /**
     * This method is an implementation of the message listener. It receives a message and places it
     * on the queue for processing by the message listening thread.
     *
     * @param data the data
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessageListener#onMessage
     *      (org.onap.policy.apex.core.infrastructure.messaging.impl.ws.data.Data)
     */
    @Subscribe
    @Override
    public void onMessage(final MessageBlock<Message> data) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("message received from client application {} port {}",
                    data.getConnection().getRemoteSocketAddress().getAddress(),
                    data.getConnection().getRemoteSocketAddress().getPort());
        }
        messageQueue.add(data);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessageListener#onMessage(java.lang.
     * String)
     */
    @Override
    public void onMessage(final String messageString) {
        throw new UnsupportedOperationException("String messages are not supported on the EngDep protocol");
    }

    /**
     * This method gets a new message listening thread from the thread factory and starts it.
     */
    public void startProcessorThread() {
        LOGGER.entry();
        messageListenerThread = new Thread(this);
        messageListenerThread.setDaemon(true);
        messageListenerThread.start();
        LOGGER.exit();
    }

    /**
     * Stops the message listening threads.
     */
    public void stopProcessorThreads() {
        LOGGER.entry();
        stopOrderedFlag = true;

        while (messageListenerThread.isAlive()) {
            ThreadUtilities.sleep(LISTENER_STOP_WAIT_INTERVAL);
        }
        LOGGER.exit();
    }

    /**
     * Runs the message listening thread. Here, the messages come in on the message queue and are
     * processed one by one
     */
    @Override
    public void run() {
        // Take messages off the queue and forward them to the Apex engine
        while (messageListenerThread.isAlive() && !stopOrderedFlag) {
            try {
                final MessageBlock<Message> data = messageQueue.poll(QUEUE_POLL_TIMEOUT, TimeUnit.MILLISECONDS);
                if (data != null) {
                    final List<Message> messages = data.getMessages();
                    for (final Message message : messages) {
                        handleMessage(message, data.getConnection());
                    }
                }
            } catch (final InterruptedException e) {
                // restore the interrupt status
                Thread.currentThread().interrupt();
                LOGGER.debug("message listener execution has been interrupted");
                break;
            }
        }
    }

    /**
     * This method handles EngDep messages as they come in. It uses the inevitable switch statement
     * to handle the messages.
     *
     * @param message the incoming EngDep message
     * @param webSocket the web socket on which the message came in
     */
    private void handleMessage(final Message message, final WebSocket webSocket) {
        LOGGER.entry(webSocket.getRemoteSocketAddress().toString());
        if (message.getAction() == null) {
            // This is a response message
            return;
        }

        try {
            LOGGER.debug("Manager action {} being applied to engine", message.getAction());

            // Get and check the incoming action for validity
            EngDepAction enDepAction = null;
            if (message.getAction() instanceof EngDepAction) {
                enDepAction = (EngDepAction) message.getAction();
            } else {
                throw new ApexException(message.getAction().getClass().getName()
                        + "action on received message invalid, action must be of type \"EnDepAction\"");
            }

            // Handle each incoming message using the inevitable switch statement for the EngDep
            // protocol
            switch (enDepAction) {
                case GET_ENGINE_SERVICE_INFO:
                    final GetEngineServiceInfo engineServiceInformationMessage = (GetEngineServiceInfo) message;
                    LOGGER.debug("getting engine service information for engine service " + apexService.getKey().getID()
                            + " . . .");
                    // Send a reply with the engine service information
                    sendServiceInfoReply(webSocket, engineServiceInformationMessage, apexService.getKey(),
                            apexService.getEngineKeys(), apexService.getApexModelKey());
                    LOGGER.debug(
                            "returned engine service information for engine service " + apexService.getKey().getID());
                    break;

                case UPDATE_MODEL:
                    final UpdateModel updateModelMessage = (UpdateModel) message;
                    LOGGER.debug("updating model in engine {} . . .", updateModelMessage.getTarget().getID());
                    // Update the model
                    apexService.updateModel(updateModelMessage.getTarget(), updateModelMessage.getMessageData(),
                            updateModelMessage.isForceInstall());
                    // Send a reply indicating the message action worked
                    sendReply(webSocket, updateModelMessage, true,
                            "updated model in engine " + updateModelMessage.getTarget().getID());
                    LOGGER.debug("updated model in engine service {}", updateModelMessage.getTarget().getID());
                    break;

                case START_ENGINE:
                    final StartEngine startEngineMessage = (StartEngine) message;
                    LOGGER.debug("starting engine {} . . .", startEngineMessage.getTarget().getID());
                    // Start the engine
                    apexService.start(startEngineMessage.getTarget());
                    // Send a reply indicating the message action worked
                    sendReply(webSocket, startEngineMessage, true,
                            "started engine " + startEngineMessage.getTarget().getID());
                    LOGGER.debug("started engine {}", startEngineMessage.getTarget().getID());
                    break;

                case STOP_ENGINE:
                    final StopEngine stopEngineMessage = (StopEngine) message;
                    LOGGER.debug("stopping engine {} . . .", stopEngineMessage.getTarget().getID());
                    // Stop the engine
                    apexService.stop(stopEngineMessage.getTarget());
                    // Send a reply indicating the message action worked
                    sendReply(webSocket, stopEngineMessage, true,
                            "stopped engine " + stopEngineMessage.getTarget().getID());
                    LOGGER.debug("stopping engine {}", stopEngineMessage.getTarget().getID());
                    break;

                case START_PERIODIC_EVENTS:
                    final StartPeriodicEvents startPeriodicEventsMessage = (StartPeriodicEvents) message;
                    LOGGER.debug("starting periodic events on engine {} . . .",
                            startPeriodicEventsMessage.getTarget().getID());
                    // Start periodic events with the period specified in the message
                    final Long period = Long.parseLong(startPeriodicEventsMessage.getMessageData());
                    apexService.startPeriodicEvents(period);
                    // Send a reply indicating the message action worked
                    sendReply(webSocket, startPeriodicEventsMessage, true, "started periodic events on engine "
                            + startPeriodicEventsMessage.getTarget().getID() + " with period " + period);
                    LOGGER.debug("started periodic events on engine " + startPeriodicEventsMessage.getTarget().getID()
                            + " with period " + period);
                    break;

                case STOP_PERIODIC_EVENTS:
                    final StopPeriodicEvents stopPeriodicEventsMessage = (StopPeriodicEvents) message;
                    LOGGER.debug("stopping periodic events on engine {} . . .",
                            stopPeriodicEventsMessage.getTarget().getID());
                    // Stop periodic events
                    apexService.stopPeriodicEvents();
                    // Send a reply indicating the message action worked
                    sendReply(webSocket, stopPeriodicEventsMessage, true,
                            "stopped periodic events on engine " + stopPeriodicEventsMessage.getTarget().getID());
                    LOGGER.debug("stopped periodic events on engine " + stopPeriodicEventsMessage.getTarget().getID());
                    break;

                case GET_ENGINE_STATUS:
                    final GetEngineStatus getEngineStatusMessage = (GetEngineStatus) message;
                    LOGGER.debug("getting status for engine{} . . .", getEngineStatusMessage.getTarget().getID());
                    // Send a reply with the engine status
                    sendReply(webSocket, getEngineStatusMessage, true,
                            apexService.getStatus(getEngineStatusMessage.getTarget()));
                    LOGGER.debug("returned status for engine {}", getEngineStatusMessage.getTarget().getID());
                    break;

                case GET_ENGINE_INFO:
                    final GetEngineInfo getEngineInfo = (GetEngineInfo) message;
                    LOGGER.debug("getting runtime information for engine {} . . .", getEngineInfo.getTarget().getID());
                    // Send a reply with the engine runtime information
                    sendReply(webSocket, getEngineInfo, true, apexService.getRuntimeInfo(getEngineInfo.getTarget()));
                    LOGGER.debug("returned runtime information for engine {}", getEngineInfo.getTarget().getID());
                    break;
                case RESPONSE:
                    throw new ApexException("RESPONSE action on received message not handled by engine");

                default:
                    break;
            }
        } catch (final ApexException e) {
            LOGGER.warn("apex failed to execute message", e);
            sendReply(webSocket, message, false, e.getCascadedMessage());
        } catch (final Exception e) {
            LOGGER.warn("system failure executing message", e);
            sendReply(webSocket, message, false, e.getMessage());
        }
        LOGGER.exit();
    }

    /**
     * Send the Response message to the client.
     *
     * @param client the client to which to send the response message
     * @param requestMessage the message to which we are responding
     * @param result the result indicating success or failure
     * @param messageData the message data
     */
    private void sendReply(final WebSocket client, final Message requestMessage, final boolean result,
            final String messageData) {
        LOGGER.entry(result, messageData);

        if (client == null || !client.isOpen()) {
            LOGGER.debug("error sending reply {}, client has disconnected", requestMessage.getAction());
            return;
        }

        LOGGER.debug("sending {} to web socket {}", requestMessage.getAction(),
                client.getRemoteSocketAddress().toString());

        final Response responseMessage = new Response(requestMessage.getTarget(), result, requestMessage);
        responseMessage.setMessageData(messageData);

        final MessageHolder<Message> messageHolder = new MessageHolder<>(MessagingUtils.getHost());
        messageHolder.addMessage(responseMessage);
        client.send(MessagingUtils.serializeObject(messageHolder));

        LOGGER.exit();
    }

    /**
     * Send the EngineServiceInfoResponse message to the client.
     *
     * @param client the client to which to send the response message
     * @param requestMessage the message to which we are responding
     * @param engineServiceKey The key of this engine service
     * @param engineKeyCollection The keys of the engines in this engine service
     * @param apexModelKey the apex model key
     */
    private void sendServiceInfoReply(final WebSocket client, final Message requestMessage,
            final AxArtifactKey engineServiceKey, final Collection<AxArtifactKey> engineKeyCollection,
            final AxArtifactKey apexModelKey) {
        LOGGER.entry();
        LOGGER.debug("sending {} to web socket {}", requestMessage.getAction(),
                client.getRemoteSocketAddress().toString());

        final EngineServiceInfoResponse responseMessage =
                new EngineServiceInfoResponse(requestMessage.getTarget(), true, requestMessage);
        responseMessage.setMessageData("engine service information");
        responseMessage.setEngineServiceKey(engineServiceKey);
        responseMessage.setEngineKeyArray(engineKeyCollection);
        responseMessage.setApexModelKey(apexModelKey);

        final MessageHolder<Message> messageHolder = new MessageHolder<>(MessagingUtils.getHost());
        messageHolder.addMessage(responseMessage);
        client.send(MessagingUtils.serializeObject(messageHolder));

        LOGGER.exit();
    }
}
