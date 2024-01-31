/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021,2024 Nordix Foundation.
 *  Modifications Copyright (C) 2019, 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.services.onappf;

import java.util.Arrays;
import lombok.Getter;
import org.onap.policy.apex.service.engine.main.ApexPolicyStatisticsManager;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.exception.ApexStarterRunTimeException;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterHandler;
import org.onap.policy.common.utils.cmd.CommandLineException;
import org.onap.policy.common.utils.resources.MessageConstants;
import org.onap.policy.common.utils.services.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class initiates Apex as a service based on instructions from PAP.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexStarterMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexStarterMain.class);

    private ApexStarterActivator activator;
    @Getter
    private ApexStarterParameterGroup parameters;

    /**
     * Instantiates the ApexStarter.
     *
     * @param args the command line arguments
     */
    public ApexStarterMain(final String[] args) {
        final var params = Arrays.toString(args);
        LOGGER.info("In ApexStarter with parameters {}", params);

        // Check the arguments
        final var arguments = new ApexStarterCommandLineArguments();
        try {
            // The arguments return a string if there is a message to print and we should exit
            final String argumentMessage = arguments.parse(args);
            if (argumentMessage != null) {
                LOGGER.debug(argumentMessage);
                return;
            }
            // Validate that the arguments are sane
            arguments.validate();

            // Read the parameters
            parameters = new ApexStarterParameterHandler().getParameters(arguments);

            // create the activator
            activator = new ApexStarterActivator(parameters);
            Registry.register(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, activator);
            Registry.register(ApexPolicyStatisticsManager.REG_APEX_PDP_POLICY_COUNTER,
                new ApexPolicyStatisticsManager());

            // Start the activator
            activator.initialize();
        } catch (final ApexStarterException | CommandLineException e) {
            if (null != activator) {
                Registry.unregister(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR);
            }
            throw new ApexStarterRunTimeException(
                String.format(MessageConstants.START_FAILURE_MSG, MessageConstants.POLICY_APEX_PDP), e);
        }

        // Add a shutdown hook to shut everything down in an orderly manner
        Runtime.getRuntime().addShutdownHook(new ApexStarterShutdownHookClass());
        var successMsg = String.format(MessageConstants.START_SUCCESS_MSG, MessageConstants.POLICY_APEX_PDP);
        LOGGER.info(successMsg);
    }


    /**
     * Shut down Execution.
     *
     * @throws ApexStarterException on shutdown errors
     */
    public void shutdown() throws ApexStarterException {
        // clear the parameterGroup variable
        parameters = null;

        // clear the apex starter activator
        if (activator != null && activator.isAlive()) {
            activator.terminate();
        }

        Registry.unregister(ApexPolicyStatisticsManager.REG_APEX_PDP_POLICY_COUNTER);
    }

    /**
     * The Class ApexStarterShutdownHookClass terminates the Apex starter for the Apex service when its run method is
     * called.
     */
    private class ApexStarterShutdownHookClass extends Thread {
        /**
         * {@inheritDoc}.
         */
        @Override
        public void run() {
            try {
                // Shutdown the apex starter service and wait for everything to stop
                if (activator != null && activator.isAlive()) {
                    activator.terminate();
                }
            } catch (final ApexStarterException e) {
                LOGGER.warn("error occurred during shut down of the apex starter service", e);
            }
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     *
     */
    public static void main(final String[] args) {
        new ApexStarterMain(args);
    }
}
