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

import java.io.PrintStream;
import java.util.Arrays;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This utility class is used to start and stop periodic events on Apex engines over the EngDep protocol.
 */
public class PeriodicEventManager {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(BatchDeployer.class);

    private static final int NUM_ARGUMENTS = 4;

    // The facade that is handling messaging to the engine service
    private EngineServiceFacade engineServiceFacade = null;

    // Host name and port of the Apex service
    private String hostName;
    private int port;

    // Should we start or stop periodic events
    private boolean startFlag;

    // The period for periodic events
    private long period;

    /**
     * Instantiates a new periodic event manager.
     * 
     * @param args the command parameters
     * @param outputStream the output stream
     */
    public PeriodicEventManager(final String[] args, final PrintStream outputStream)  {
        if (args.length != NUM_ARGUMENTS) {
            String message = "invalid arguments: " + Arrays.toString(args)
                            + "\nusage: Deployer <server address> <port address> <start/stop> <periods in ms>";
            LOGGER.error(message);
            outputStream.println(message);
            return;
        }

        this.hostName = args[0];
        this.port = Integer.parseInt(args[1]);
        this.startFlag = "start".equalsIgnoreCase(args[2]);
        this.period = Long.parseLong(args[3]);
    }

    /**
     * Initializes the deployer, opens an EngDep communication session with the Apex engine.
     *
     * @throws ApexDeploymentException thrown on deployment and communication errors
     */
    public void init() throws ApexDeploymentException {
        try {
            // Use an engine service facade to handle model deployment
            engineServiceFacade = new EngineServiceFacade(hostName, port);
            engineServiceFacade.init();

            if (startFlag) {
                startPerioidicEvents(period);
            } else {
                stopPerioidicEvents();
            }
        } catch (final ApexException e) {
            LOGGER.error("model deployment failed on parameters {} {} {}", hostName, port, startFlag, e);
        } finally {
            close();
        }
    }

    /**
     * Close the EngDep connection to the Apex server.
     */
    private void close() {
        engineServiceFacade.close();
    }

    /**
     * Start the Apex engines on the engine service.
     *
     * @param period the interval in milliseconds between periodic events
     * @throws ApexDeploymentException on messaging errors
     */
    private void startPerioidicEvents(final long period) throws ApexDeploymentException {
        for (final AxArtifactKey engineKey : engineServiceFacade.getEngineKeyArray()) {
            engineServiceFacade.startPerioidicEvents(engineKey, period);
        }
    }

    /**
     * Stop the Apex engines on the engine service.
     *
     * @throws ApexDeploymentException on messaging errors
     */
    private void stopPerioidicEvents() throws ApexDeploymentException {
        for (final AxArtifactKey engineKey : engineServiceFacade.getEngineKeyArray()) {
            engineServiceFacade.stopPerioidicEvents(engineKey);
        }
    }

    /**
     * The main method, reads the Apex server host address, port and location of the Apex model XML file from the
     * command line arguments.
     *
     * @param args the arguments that specify the Apex engine and the Apex model file
     * @throws ApexDeploymentException on deployment errors
     */
    public static void main(final String[] args) throws ApexDeploymentException {
        PeriodicEventManager peManager = new PeriodicEventManager(args, System.out);
        peManager.init();
    }
}
