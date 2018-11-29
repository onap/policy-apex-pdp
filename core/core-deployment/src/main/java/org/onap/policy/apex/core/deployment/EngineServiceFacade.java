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

package org.onap.policy.apex.core.deployment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.core.protocols.Message;
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
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelWriter;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class Deployer deploys an Apex model held as an XML file onto an Apex engine. It uses the EngDep protocol to
 * communicate with the engine, with the EngDep protocol being carried on Java web sockets.
 *
 * <p>
 * his deployer is a simple command line deployer that reads the communication parameters and the location of the XML
 * model file as arguments.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EngineServiceFacade {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EngineServiceFacade.class);

    // Repeated string constants
    private static final String RECEIVED_FROM_SERVER = " received from server";
    private static final String FAILED_RESPONSE = "failed response ";

    // The default message timeout and timeout increment (the amount of time between polls) in
    // milliseconds
    private static final int CLIENT_START_WAIT_INTERVAL = 100;
    private static final int REPLY_MESSAGE_TIMEOUT_DEFAULT = 10000;
    private static final int REPLY_MESSAGE_TIMEOUT_INCREMENT = 100;

    // The Apex engine host and EngDep port
    private final String hostName;
    private final int port;

    // The deployment client handles the EngDep communication session towards the Apex server
    private DeploymentClient client = null;
    private Thread clientThread = null;

    // Information about the Engine service we are connected to
    private AxArtifactKey engineServiceKey = null;
    private AxArtifactKey[] engineKeyArray = null;
    private AxArtifactKey apexModelKey = null;

    /**
     * Instantiates a new deployer.
     *
     * @param hostName the host name of the host running the Apex Engine
     * @param port the port to use for EngDep communication with the Apex engine
     */
    public EngineServiceFacade(final String hostName, final int port) {
        this.hostName = hostName;
        this.port = port;

        // Use the deployment client to handle the EngDep communication towards the Apex server.
        client = new DeploymentClient(hostName, port);
    }

    /**
     * Initializes the facade, opens an EngDep communication session with the Apex engine.
     *
     * @throws ApexDeploymentException thrown on deployment and communication errors
     */
    public void init() throws ApexDeploymentException {
        try {
            LOGGER.debug("handshaking with server {}:{} . . .", hostName, port);

            // Use the deployment client to handle the EngDep communication towards the Apex server.
            // The deployment client runs a thread to monitor the session and to send messages
            clientThread = new Thread(client);
            clientThread.start();

            // Wait for the connection to come up
            while (!client.isStarted()) {
                if (clientThread.isAlive()) {
                    ThreadUtilities.sleep(CLIENT_START_WAIT_INTERVAL);
                } else {
                    LOGGER.error("cound not handshake with server {}:{}", hostName, port);
                    throw new ApexDeploymentException("cound not handshake with server " + hostName + ":" + port);
                }
            }

            LOGGER.debug("opened connection to server {}:{} . . .", hostName, port);

            // Get engine service information to see what engines we're dealing with
            final GetEngineServiceInfo engineServiceInfo = new GetEngineServiceInfo(null);
            LOGGER.debug("sending get engine service info message {} to server {}:{} . . .", engineServiceInfo,
                    hostName, port);
            client.sendMessage(engineServiceInfo);
            LOGGER.debug("sent get engine service info message to server {}:{} . . .", hostName, port);

            final EngineServiceInfoResponse engineServiceInfoResponse =
                    (EngineServiceInfoResponse) getResponse(engineServiceInfo);
            if (engineServiceInfoResponse.isSuccessful()) {
                engineServiceKey = engineServiceInfoResponse.getEngineServiceKey();
                engineKeyArray = engineServiceInfoResponse.getEngineKeyArray();
                apexModelKey = engineServiceInfoResponse.getApexModelKey();
            }
        } catch (final Exception e) {
            LOGGER.error("cound not handshake with server {}:{}", hostName, port, e);
            client.stopClient();
            throw new ApexDeploymentException("cound not handshake with server " + hostName + ":" + port, e);
        }

    }

    /**
     * Get the engine service key.
     *
     * @return the engine service key
     */
    public AxArtifactKey getApexModelKey() {
        return apexModelKey;
    }

    /**
     * Get the keys of the engines on this engine service.
     *
     * @return the engine key array
     */
    public AxArtifactKey[] getEngineKeyArray() {
        return engineKeyArray;
    }

    /**
     * Get the engine service key.
     *
     * @return the engine service key
     */
    public AxArtifactKey getKey() {
        return engineServiceKey;
    }

    /**
     * Close the EngDep connection to the Apex server.
     */
    public void close() {
        LOGGER.debug("closing connection to server {}:{} . . .", hostName, port);

        client.stopClient();

        LOGGER.debug("closed connection to server {}:{} . . .", hostName, port);
    }

    /**
     * Deploy an Apex model on the Apex engine service.
     *
     * @param modelFileName the name of the model file containing the model to deploy
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     * @throws ApexException on Apex errors
     */
    public void deployModel(final String modelFileName, final boolean ignoreConflicts, final boolean force)
            throws ApexException {
        if (engineServiceKey == null || engineKeyArray == null || engineKeyArray.length == 0) {
            LOGGER.error("cound not deploy apex model, deployer is not initialized");
            throw new ApexDeploymentException("cound not deploy apex model, deployer is not initialized");
        }

        // Get the model file as a string
        URL apexModelUrl = ResourceUtils.getLocalFile(modelFileName);
        if (apexModelUrl == null) {
            apexModelUrl = ResourceUtils.getUrlResource(modelFileName);
            if (apexModelUrl == null) {
                LOGGER.error("cound not create apex model, could not read from file {}", modelFileName);
                throw new ApexDeploymentException(
                        "cound not create apex model, could not read from file " + modelFileName);
            }
        }

        try {
            deployModel(modelFileName, apexModelUrl.openStream(), ignoreConflicts, force);
        } catch (final Exception deployException) {
            final String errorMessage = "could not deploy apex model from " + modelFileName;
            LOGGER.error(errorMessage, deployException);
            throw new ApexDeploymentException(errorMessage, deployException);
        }
    }

    /**
     * Deploy an Apex model on the Apex engine service.
     *
     * @param modelFileName the name of the model file containing the model to deploy
     * @param modelInputStream the stream that holds the Apex model
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     * @throws ApexException on model deployment errors
     */
    public void deployModel(final String modelFileName, final InputStream modelInputStream,
            final boolean ignoreConflicts, final boolean force) throws ApexException {
        // Read the policy model from the stream
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        modelReader.setValidateFlag(!ignoreConflicts);
        final AxPolicyModel apexPolicyModel = modelReader.read(modelInputStream);

        // Deploy the model
        deployModel(apexPolicyModel, ignoreConflicts, force);
    }

    /**
     * Deploy an Apex model on the Apex engine service.
     *
     * @param apexPolicyModel the name of the model to deploy
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     * @throws ApexException on model deployment errors
     */
    public void deployModel(final AxPolicyModel apexPolicyModel, final boolean ignoreConflicts, final boolean force)
            throws ApexException {
        // Write the model into a byte array
        final ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        final ApexModelWriter<AxPolicyModel> modelWriter = new ApexModelWriter<>(AxPolicyModel.class);
        modelWriter.write(apexPolicyModel, baOutputStream);

        // Create and send Update message
        final UpdateModel umMessage =
                new UpdateModel(engineServiceKey, baOutputStream.toString(), ignoreConflicts, force);

        LOGGER.debug("sending update message {} to server {}:{} . . .", umMessage, hostName, port);
        client.sendMessage(umMessage);
        LOGGER.debug("sent update message to server {}:{} . . .", hostName, port);

        // Check if we got a response
        final Response response = getResponse(umMessage);
        if (!response.isSuccessful()) {
            LOGGER.warn(FAILED_RESPONSE + "{} received from server {}:{}", response.getMessageData(), hostName, port);
            throw new ApexException(
                    FAILED_RESPONSE + response.getMessageData() + RECEIVED_FROM_SERVER + hostName + ':' + port);
        }
    }

    /**
     * Start an Apex engine on the engine service.
     *
     * @param engineKey the key of the engine to start
     * @throws ApexDeploymentException on messaging errors
     */
    public void startEngine(final AxArtifactKey engineKey) throws ApexDeploymentException {
        final StartEngine startEngineMessage = new StartEngine(engineKey);
        LOGGER.debug("sending start engine {} to server {}:{} . . .", startEngineMessage, hostName, port);
        client.sendMessage(startEngineMessage);
        LOGGER.debug("sent start engine message to server {}:{} . . .", hostName, port);

        // Check if we got a response
        final Response response = getResponse(startEngineMessage);
        if (!response.isSuccessful()) {
            final String message =
                    FAILED_RESPONSE + response.getMessageData() + RECEIVED_FROM_SERVER + hostName + ':' + port;
            LOGGER.warn(message);
            throw new ApexDeploymentException(message);
        }
    }

    /**
     * Stop an Apex engine on the engine service.
     *
     * @param engineKey the key of the engine to stop
     * @throws ApexDeploymentException on messaging errors
     */
    public void stopEngine(final AxArtifactKey engineKey) throws ApexDeploymentException {
        final StopEngine stopEngineMessage = new StopEngine(engineKey);
        LOGGER.debug("sending stop engine {} to server {}:{} . . .", stopEngineMessage, hostName, port);
        client.sendMessage(stopEngineMessage);
        LOGGER.debug("sent stop engine message to server {}:{} . . .", hostName, port);

        // Check if we got a response
        final Response response = getResponse(stopEngineMessage);
        if (!response.isSuccessful()) {
            final String message =
                    FAILED_RESPONSE + response.getMessageData() + RECEIVED_FROM_SERVER + hostName + ':' + port;
            LOGGER.warn(message);
            throw new ApexDeploymentException(message);
        }
    }

    /**
     * Start periodic events on an Apex engine on the engine service.
     *
     * @param engineKey the key of the engine to start periodic events on
     * @param period the period in milliseconds between periodic events
     * @throws ApexDeploymentException on messaging errors
     */
    public void startPerioidicEvents(final AxArtifactKey engineKey, final long period) throws ApexDeploymentException {
        final StartPeriodicEvents startPerioidicEventsMessage = new StartPeriodicEvents(engineKey);
        startPerioidicEventsMessage.setMessageData(Long.toString(period));
        LOGGER.debug("sending start perioidic events {} to server {}:{} . . .", startPerioidicEventsMessage, hostName,
                port);
        client.sendMessage(startPerioidicEventsMessage);
        LOGGER.debug("sent start perioidic events message to server {}:{} . . .", hostName, port);

        // Check if we got a response
        final Response response = getResponse(startPerioidicEventsMessage);
        if (!response.isSuccessful()) {
            final String message =
                    FAILED_RESPONSE + response.getMessageData() + RECEIVED_FROM_SERVER + hostName + ':' + port;
            LOGGER.warn(message);
            throw new ApexDeploymentException(message);
        }
    }

    /**
     * Stop periodic events on an Apex engine on the engine service.
     *
     * @param engineKey the key of the engine to stop periodic events on
     * @throws ApexDeploymentException on messaging errors
     */
    public void stopPerioidicEvents(final AxArtifactKey engineKey) throws ApexDeploymentException {
        final StopPeriodicEvents stopPerioidicEventsMessage = new StopPeriodicEvents(engineKey);
        LOGGER.debug("sending stop perioidic events {} to server {}:{} . . .", stopPerioidicEventsMessage, hostName,
                port);
        client.sendMessage(stopPerioidicEventsMessage);
        LOGGER.debug("sent stop perioidic events message to server {}:{} . . .", hostName, port);

        // Check if we got a response
        final Response response = getResponse(stopPerioidicEventsMessage);
        if (!response.isSuccessful()) {
            final String message =
                    FAILED_RESPONSE + response.getMessageData() + RECEIVED_FROM_SERVER + hostName + ':' + port;
            LOGGER.warn(message);
            throw new ApexDeploymentException(message);
        }
    }

    /**
     * Get the status of an Apex engine.
     *
     * @param engineKey the key of the engine to get the status of
     * @return an engine model containing the status of the engine for the given key
     * @throws ApexException the apex exception
     */
    public AxEngineModel getEngineStatus(final AxArtifactKey engineKey) throws ApexException {
        final GetEngineStatus engineStatusMessage = new GetEngineStatus(engineKey);
        LOGGER.debug("sending get engine status message {} to server {}:{} . . .", engineStatusMessage, hostName, port);
        client.sendMessage(engineStatusMessage);
        LOGGER.debug("sent get engine status message to server {}:{} . . .", hostName, port);

        // Check if we got a response
        final Response response = getResponse(engineStatusMessage);
        if (!response.isSuccessful()) {
            final String message =
                    FAILED_RESPONSE + response.getMessageData() + RECEIVED_FROM_SERVER + hostName + ':' + port;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        final ByteArrayInputStream baInputStream = new ByteArrayInputStream(response.getMessageData().getBytes());
        final ApexModelReader<AxEngineModel> modelReader = new ApexModelReader<>(AxEngineModel.class);
        modelReader.setValidateFlag(false);
        return modelReader.read(baInputStream);
    }

    /**
     * Get the runtime information of an Apex engine.
     *
     * @param engineKey the key of the engine to get information for
     * @return an engine model containing information on the engine for the given key
     * @throws ApexException the apex exception
     */
    public String getEngineInfo(final AxArtifactKey engineKey) throws ApexException {
        final GetEngineInfo engineInfoMessage = new GetEngineInfo(engineKey);
        LOGGER.debug("sending get engine information message {} to server {}:{} . . .", engineInfoMessage, hostName,
                port);
        client.sendMessage(engineInfoMessage);
        LOGGER.debug("sent get engine information message to server {}:{} . . .", hostName, port);

        // Check if we got a response
        final Response response = getResponse(engineInfoMessage);
        if (!response.isSuccessful()) {
            final String message =
                    FAILED_RESPONSE + response.getMessageData() + RECEIVED_FROM_SERVER + hostName + ':' + port;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        return response.getMessageData();
    }

    /**
     * Check the response to a model deployment message from the Apex server.
     *
     * @param sentMessage the sent message
     * @return the response message
     * @throws ApexDeploymentException the apex deployment exception
     */
    private Response getResponse(final Message sentMessage) throws ApexDeploymentException {
        // Get the amount of milliseconds we should wait for a timeout
        int timeoutTime = sentMessage.getReplyTimeout();
        if (timeoutTime <= 0) {
            timeoutTime = REPLY_MESSAGE_TIMEOUT_DEFAULT;
        }

        // Wait for the required amount of milliseconds for the response from the Apex server
        Message receivedMessage = null;
        for (int timeWaitedSoFar = 0; receivedMessage == null && timeWaitedSoFar < timeoutTime; timeWaitedSoFar +=
                REPLY_MESSAGE_TIMEOUT_INCREMENT) {
            try {
                receivedMessage = client.getReceiveQueue().poll(REPLY_MESSAGE_TIMEOUT_INCREMENT, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException e) {
                // restore the interrupt status
                Thread.currentThread().interrupt();
                LOGGER.warn("reception of response from server interrupted {}:{}", hostName, port, e);
                throw new ApexDeploymentException(
                        "reception of response from server interrupted " + hostName + ':' + port, e);
            }
        }

        // Check if response to sent message
        if (receivedMessage == null) {
            LOGGER.warn("no response received to sent message " + sentMessage.getAction());
            throw new ApexDeploymentException("no response received to sent message " + sentMessage.getAction());
        }

        // Check instance is a response message
        if (!(receivedMessage instanceof Response)) {
            LOGGER.warn("response received from server is of incorrect type {}, should be of type {}",
                    receivedMessage.getClass().getName(), Response.class.getName());
            throw new ApexDeploymentException("response received from server is of incorrect type "
                    + receivedMessage.getClass().getName() + ", should be of type " + Response.class.getName());
        }

        // Cast the response message
        final Response responseMessage = (Response) receivedMessage;

        // Check if response to sent message
        if (!responseMessage.getResponseTo().equals(sentMessage)) {
            LOGGER.warn("response received is not response to sent message " + sentMessage.getAction());
            throw new ApexDeploymentException(
                    "response received is not correct response to sent message " + sentMessage.getAction());
        }

        // Check if successful
        if (responseMessage.isSuccessful()) {
            LOGGER.debug("response received: {} message was succssful: {}", sentMessage.getAction(),
                    responseMessage.getMessageData());
        } else {
            LOGGER.debug("response received: {} message failed: {}", sentMessage.getAction(),
                    responseMessage.getMessageData());
        }

        return responseMessage;
    }

    /**
     * Set a deployment client for this facade. This method is for testing.
     * 
     * @param deploymentClient the deployment client to set
     */
    protected void setDeploymentClient(final DeploymentClient deploymentClient) {
        this.client = deploymentClient;
    }
}
