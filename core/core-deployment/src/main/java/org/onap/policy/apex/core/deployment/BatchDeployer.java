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
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class {@link BatchDeployer} deploys an Apex model held as an XML or Json file onto an Apex engine. It uses the
 * EngDep protocol to communicate with the engine, with the EngDep protocol being carried on Java web sockets.
 *
 * <p>
 * This deployer is a simple command line deployer that reads the communication parameters and the location of the Apex
 * model file as arguments.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class BatchDeployer {
    private static final int NUM_ARGUMENTS = 3;

    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(BatchDeployer.class);

    // The facade that is handling messaging to the engine service
    private EngineServiceFacade engineServiceFacade = null;

    private String hostName;
    private int port;

    /**
     * Instantiates a new deployer.
     *
     * @param hostName the apex host name
     * @param port the apex EngDep port
     * @param outputStream the output stream
     */
    public BatchDeployer(final String hostName, final int port, final PrintStream outputStream) {
        this.hostName = hostName;
        this.port = port;

        engineServiceFacade = new EngineServiceFacade(hostName, port);
    }

    /**
     * Initializes the deployer, opens an EngDep communication session with the Apex engine.
     *
     * @throws ApexDeploymentException thrown on deployment and communication errors
     */
    public void init() throws ApexDeploymentException {
        try {
            engineServiceFacade.init();
        } catch (final ApexException e) {
            final String errorMessage = "model deployment failed on parameters " + hostName + " " + port;
            LOGGER.error(errorMessage, e);
            throw new ApexDeploymentException(errorMessage);
        }
    }

    /**
     * Close the EngDep connection to the Apex server.
     */
    public void close() {
        if (engineServiceFacade != null) {
            engineServiceFacade.close();
        }
    }

    /**
     * Deploy an Apex model on the Apex server.
     *
     * @param modelFileName the name of the model file containing the model to deploy
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     * @throws ApexException on Apex errors
     */
    public void deployModel(final String modelFileName, final boolean ignoreConflicts, final boolean force)
            throws ApexException {
        engineServiceFacade.deployModel(modelFileName, ignoreConflicts, force);
    }

    /**
     * Deploy an Apex model on the Apex server.
     *
     * @param policyModel the model to deploy
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     * @throws ApexException on Apex errors
     */
    public void deployModel(final AxPolicyModel policyModel, final boolean ignoreConflicts, final boolean force)
            throws ApexException {
        engineServiceFacade.deployModel(policyModel, ignoreConflicts, force);
    }

    /**
     * Get the engine service facade of the event manager. This method is used for testing only.
     *
     * @return the engine service facade
     */
    protected EngineServiceFacade getEngineServiceFacade() {
        return engineServiceFacade;
    }

    /**
     * The main method, reads the Apex server host address, port and location of the Apex model file from the command
     * line arguments.
     *
     * @param args the arguments that specify the Apex engine and the Apex model file
     * @throws ApexException on deployment errors
     */
    public static void main(final String[] args) throws ApexException {
        if (args.length != NUM_ARGUMENTS) {
            final String message = "invalid arguments: " + Arrays.toString(args)
                    + "\nusage: BatchDeployer <server address> <port address> <model file path";
            LOGGER.error(message);
            throw new ApexDeploymentException(message);
        }

        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (final NumberFormatException nfe) {
            throw new ApexDeploymentException("argument port is invalid", nfe);
        }

        final BatchDeployer deployer = new BatchDeployer(args[0], port, System.out);
        deployer.init();
        deployer.deployModel(args[2], false, false);
        deployer.close();
    }
}
