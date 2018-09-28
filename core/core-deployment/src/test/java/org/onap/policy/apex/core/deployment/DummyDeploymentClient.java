/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * Dummy deployment client.
 */
public class DummyDeploymentClient extends DeploymentClient implements Runnable {
    private static final AxArtifactKey MODEL_KEY = new AxArtifactKey("Model", "0.0.1");
    private static final AxArtifactKey ENGINE_KEY = new AxArtifactKey("Engine", "0.0.1");
    private static final AxArtifactKey ENGINE_SERVICE_KEY = new AxArtifactKey("EngineService", "0.0.1");

    private Thread thisThread;

    private final BlockingQueue<Message> receiveQueue = new LinkedBlockingQueue<>();

    private boolean started = false;

    private boolean initSuccessful = false;
    private boolean deployModelSuccessful = false;
    private boolean startEngineSuccessful = false;
    private boolean stopEngineSuccessful = false;
    private boolean startPeriodicSuccessful = false;
    private boolean stopPeriodicSuccessful = false;
    private boolean statusSuccessful = false;
    private boolean infoSuccessful = false;

    public DummyDeploymentClient(String host, int port) {
        super(host, port);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // Set up the thread name
        thisThread = Thread.currentThread();
        thisThread.setName(DeploymentClient.class.getName() + "-" + getHost() + ":" + getPort());

        started = true;

        // Loop forever, sending messages as they appear on the queue
        while (started && !thisThread.isInterrupted()) {
            ThreadUtilities.sleep(50);
        }

        // Thread has been interrupted
        thisThread = null;
        started = false;
    }

    /**
     * Send an EngDep message to the Apex server.
     *
     * @param message the message to send to the Apex server
     */
    public void sendMessage(final Message message) {
        if (message instanceof GetEngineServiceInfo) {
            handleEngineServiceInfo(message);
        } else if (message instanceof UpdateModel) {
            deployModelSuccessful = handleAndReturnMessage(message, deployModelSuccessful);
        } else if (message instanceof StartEngine) {
            startEngineSuccessful = handleAndReturnMessage(message, startEngineSuccessful);
        } else if (message instanceof StopEngine) {
            stopEngineSuccessful = handleAndReturnMessage(message, stopEngineSuccessful);
        } else if (message instanceof StartPeriodicEvents) {
            startPeriodicSuccessful = handleAndReturnMessage(message, startPeriodicSuccessful);
        } else if (message instanceof StopPeriodicEvents) {
            stopPeriodicSuccessful = handleAndReturnMessage(message, stopPeriodicSuccessful);
        } else if (message instanceof GetEngineStatus) {
            statusSuccessful = handleAndReturnEngineStatus(message, statusSuccessful);
        } else if (message instanceof GetEngineInfo) {
            infoSuccessful = handleAndReturnMessage(message, infoSuccessful);
        }
    }

    /**
     * Handle the EngineServiceInfo message.
     * 
     * @param message the EngineServiceInfo message
     */
    private void handleEngineServiceInfo(final Message message) {
        EngineServiceInfoResponse infoResponse = new EngineServiceInfoResponse(ENGINE_KEY, initSuccessful, message);
        infoResponse.setApexModelKey(MODEL_KEY);

        List<AxArtifactKey> engineKeyList = new ArrayList<>();
        engineKeyList.add(ENGINE_KEY);
        infoResponse.setEngineKeyArray(engineKeyList);

        infoResponse.setEngineServiceKey(ENGINE_SERVICE_KEY);

        receiveQueue.add(infoResponse);

        initSuccessful = !initSuccessful;
    }

    /**
     * Handle and return the response to the engine status message.
     * 
     * @param message the incoming status message
     * @param successFlag true if the result should be successful
     * @return
     */
    private boolean handleAndReturnEngineStatus(Message message, boolean successFlag) {
        if ("DoNotRespond".equals(message.getTarget().getName())) {
            return !successFlag;
        }

        if ("ReturnBadMessage".equals(message.getTarget().getName())) {
            receiveQueue.add(message);
            return !successFlag;
        }

        if ("ReturnBadResponse".equals(message.getTarget().getName())) {
            Response badResponse = new Response(ENGINE_KEY, successFlag,new StartEngine(message.getTarget()));
            receiveQueue.add(badResponse);
            return !successFlag;
        }
        
        Response response = new Response(ENGINE_KEY, successFlag, message);

        if (successFlag) {
            try {
                response.setMessageData(TextFileUtils
                                .getTextFileAsString("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            response.setMessageData("Operation failed");
        }

        receiveQueue.add(response);
        return !successFlag;
    }

    /**
     * Handle and return a message.
     * 
     * @param message the message
     */
    private boolean handleAndReturnMessage(final Message message, final boolean successFlag) {
        Response response = new Response(ENGINE_KEY, successFlag, message);

        if (successFlag) {
            response.setMessageData("Operation was successful");
        } else {
            response.setMessageData("Operation failed");
        }

        receiveQueue.add(response);
        return !successFlag;
    }

    /**
     * Stop the deployment client.
     */
    public void stopClient() {
        if (thisThread != null) {
            thisThread.interrupt();
        }
        started = false;
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
}
