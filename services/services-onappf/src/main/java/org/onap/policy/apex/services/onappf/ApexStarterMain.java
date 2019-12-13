/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
import org.onap.policy.apex.service.engine.main.ApexPolicyStatisticsManager;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterHandler;
import org.onap.policy.common.utils.services.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class initiates Apex as a service based on instructions from PAP.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexStarterMain {

    private static final String APEX_STARTER_FAIL_MSG = "start of services-onappf failed";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexStarterMain.class);

    private ApexStarterActivator activator;
    private ApexStarterParameterGroup parameterGroup;

    /**
     * Instantiates the ApexStarter.
     *
     * @param args the command line arguments
     */
    public ApexStarterMain(final String[] args) {
        LOGGER.info("In ApexStarter with parameters " + Arrays.toString(args));

        // Check the arguments
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        try {
            // The arguments return a string if there is a message to print and we should exit
            final String argumentMessage = arguments.parse(args);
            if (argumentMessage != null) {
                LOGGER.debug(argumentMessage);
                return;
            }
            // Validate that the arguments are sane
            arguments.validate();
        } catch (final ApexStarterException e) {
            LOGGER.error(APEX_STARTER_FAIL_MSG, e);
            return;
        }

        // Read the parameters
        try {
            parameterGroup = new ApexStarterParameterHandler().getParameters(arguments);
        } catch (final Exception e) {
            LOGGER.error(APEX_STARTER_FAIL_MSG, e);
            return;
        }

        // create the activator
        activator = new ApexStarterActivator(parameterGroup);
        Registry.register(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, activator);
        Registry.register(ApexPolicyStatisticsManager.REG_APEX_PDP_POLICY_COUNTER, new ApexPolicyStatisticsManager());
        // Start the activator
        try {
            activator.initialize();
        } catch (final ApexStarterException e) {
            LOGGER.error("start of ApexStarter failed, used parameters are {}", Arrays.toString(args), e);
            Registry.unregister(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR);
            return;
        }

        // Add a shutdown hook to shut everything down in an orderly manner
        Runtime.getRuntime().addShutdownHook(new ApexStarterShutdownHookClass());

        LOGGER.info("Started ApexStarter service");
    }

    /**
     * Get the parameters specified in JSON.
     *
     * @return parameterGroup the parameters
     */
    public ApexStarterParameterGroup getParameters() {
        return parameterGroup;
    }


    /**
     * Shut down Execution.
     *
     * @throws ApexStarterException on shutdown errors
     */
    public void shutdown() throws ApexStarterException {
        // clear the parameterGroup variable
        parameterGroup = null;

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
                LOGGER.warn("error occured during shut down of the apex starter service", e);
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
