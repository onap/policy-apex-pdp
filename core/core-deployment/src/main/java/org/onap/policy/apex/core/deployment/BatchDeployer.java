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

import java.io.IOException;
import java.util.Arrays;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class {@link BatchDeployer} deploys an Apex model held as an XML or Json file onto an Apex engine. It uses the
 * EngDep protocol to communicate with the engine, with the EngDep protocol being carried on Java web sockets.
 *
 * <p>This deployer is a simple command line deployer that reads the communication parameters and the location of the
 * Apex model file as arguments.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class BatchDeployer {
    private static final int NUM_ARGUMENTS = 3;

    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(BatchDeployer.class);

    // The facade that is handling messaging to the engine service
    private EngineServiceFacade engineServiceFacade = null;

    /**
     * Instantiates a new deployer.
     *
     * @param hostName the host name of the host running the Apex Engine
     * @param port the port to use for EngDep communication with the Apex engine
     */
    public BatchDeployer(final String hostName, final int port) {
        engineServiceFacade = new EngineServiceFacade(hostName, port);
    }

    /**
     * Initializes the deployer, opens an EngDep communication session with the Apex engine.
     *
     * @throws ApexDeploymentException thrown on deployment and communication errors
     */
    public void init() throws ApexDeploymentException {
        engineServiceFacade.init();
    }

    /**
     * Close the EngDep connection to the Apex server.
     */
    public void close() {
        engineServiceFacade.close();
    }

    /**
     * Deploy an Apex model on the Apex server.
     *
     * @param modelFileName the name of the model file containing the model to deploy
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     * @throws ApexException on Apex errors
     * @throws IOException on IO exceptions from the operating system
     */
    public void deployModel(final String modelFileName, final boolean ignoreConflicts, final boolean force)
                    throws ApexException, IOException {
        engineServiceFacade.deployModel(modelFileName, ignoreConflicts, force);
    }

    /**
     * Deploy an Apex model on the Apex server.
     *
     * @param policyModel the model to deploy
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     * @throws ApexException on Apex errors
     * @throws IOException on IO exceptions from the operating system
     */
    public void deployModel(final AxPolicyModel policyModel, final boolean ignoreConflicts, final boolean force)
                    throws ApexException {
        engineServiceFacade.deployModel(policyModel, ignoreConflicts, force);
    }

    /**
     * Start the Apex engines on the engine service.
     *
     * @throws ApexDeploymentException on messaging errors
     */
    public void startEngines() throws ApexDeploymentException {
        for (final AxArtifactKey engineKey : engineServiceFacade.getEngineKeyArray()) {
            engineServiceFacade.startEngine(engineKey);
        }
    }

    /**
     * Stop the Apex engines on the engine service.
     *
     * @throws ApexDeploymentException on messaging errors
     */
    public void stopEngines() throws ApexDeploymentException {
        for (final AxArtifactKey engineKey : engineServiceFacade.getEngineKeyArray()) {
            engineServiceFacade.stopEngine(engineKey);
        }
    }

    /**
     * The main method, reads the Apex server host address, port and location of the Apex model file from the command
     * line arguments.
     *
     * @param args the arguments that specify the Apex engine and the Apex model file
     */
    public static void main(final String[] args) {
        if (args.length != NUM_ARGUMENTS) {
            String message = "invalid arguments: " + Arrays.toString(args)
                            + "usage: Deployer <server address> <port address> <Apex Model file location>";
            LOGGER.error(message);
            return;
        }

        BatchDeployer deployer = null;
        try {
            // Use a Deployer object to handle model deployment
            deployer = new BatchDeployer(args[0], Integer.parseInt(args[1]));
            deployer.init();
            deployer.deployModel(args[2], false, false);
            deployer.startEngines();
        } catch (final ApexException | IOException e) {
            LOGGER.error("model deployment failed on parameters {}", args, e);
        } finally {
            if (deployer != null) {
                deployer.close();
            }
        }
    }
}
