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

package org.onap.policy.apex.service.engine.main;

import java.util.Arrays;
import java.util.Map.Entry;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class initiates Apex as a complete service from the command line.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexMain {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexMain.class);

    // The Apex Activator that activates the Apex engine
    private ApexActivator activator;

    // The parameters read in from JSON
    private ApexParameters parameters;

    /**
     * Instantiates the Apex service.
     *
     * @param args the commaind line arguments
     */
    public ApexMain(final String[] args) {
        LOGGER.entry("Starting Apex service with parameters " + Arrays.toString(args) + " . . .");

        // Check the arguments
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        try {
            // The arguments return a string if there is a message to print and we should exit
            final String argumentMessage = arguments.parse(args);
            if (argumentMessage != null) {
                LOGGER.info(argumentMessage);
                return;
            }

            // Validate that the arguments are sane
            arguments.validate();
        } catch (final ApexException e) {
            LOGGER.error("start of Apex service failed", e);
            return;
        }

        // Read the parameters
        try {
            parameters = new ApexParameterHandler().getParameters(arguments);
        } catch (final Exception e) {
            LOGGER.error("start of Apex service failed", e);
            return;
        }

        // Set the name of the event handler parameters for producers and consumers
        for (final Entry<String, EventHandlerParameters> ehParameterEntry : parameters.getEventOutputParameters()
                .entrySet()) {
            if (!ehParameterEntry.getValue().checkSetName()) {
                ehParameterEntry.getValue().setName(ehParameterEntry.getKey());
            }
        }
        for (final Entry<String, EventHandlerParameters> ehParameterEntry : parameters.getEventInputParameters()
                .entrySet()) {
            if (!ehParameterEntry.getValue().checkSetName()) {
                ehParameterEntry.getValue().setName(ehParameterEntry.getKey());
            }
        }

        // Now, create the activator for the Apex service
        activator = new ApexActivator(parameters);

        // Start the activator
        try {
            activator.initialize();
        } catch (final ApexActivatorException e) {
            LOGGER.error("start of Apex service failed, used parameters are " + Arrays.toString(args), e);
            return;
        }

        // Add a shutdown hook to shut everything down in an orderly manner
        Runtime.getRuntime().addShutdownHook(new ApexMainShutdownHookClass());
        LOGGER.exit("Started Apex");
    }

    /**
     * Get the parameters specified in JSON.
     *
     * @return the parameters
     */
    public ApexParameters getParameters() {
        return parameters;
    }

    /**
     * Shut down Execution.
     *
     * @throws ApexException on shutdown errors
     */
    public void shutdown() throws ApexException {
        if (activator != null) {
            activator.terminate();
        }
    }

    /**
     * The Class ApexMainShutdownHookClass terminates the Apex engine for the Apex service when its
     * run method is called.
     */
    private class ApexMainShutdownHookClass extends Thread {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                // Shutdown the Apex engine and wait for everything to stop
                activator.terminate();
            } catch (final ApexException e) {
                LOGGER.warn("error occured during shut down of the Apex service", e);
            }
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        new ApexMain(args);
    }
}
