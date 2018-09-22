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

package org.onap.policy.apex.client.deployment.rest;

import java.io.PrintStream;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The main class for ApexDeploymentRest.
 *
 * @author Michael Watkins (michael.watkins@ericsson.com)
 */
public class ApexDeploymentRestMain {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexDeploymentRestMain.class);

    // Recurring string constants
    private static final String REST_ENDPOINT_PREFIX = "Apex Services REST endpoint (";

    // Services state
    public enum ServicesState {
        STOPPED, READY, INITIALIZING, RUNNING
    }

    private ServicesState state = ServicesState.STOPPED;

    // The parameters for the client
    private ApexDeploymentRestParameters parameters = null;

    // Output and error streams for messages
    private final PrintStream outStream;

    // The Apex services client this class is running
    private ApexDeploymentRest apexDeploymentRest = null;

    /**
     * Constructor, kicks off the rest service.
     *
     * @param args The command line arguments for the RESTful service
     * @param outStream The stream for output messages
     */
    public ApexDeploymentRestMain(final String[] args, final PrintStream outStream) {
        // Save the streams for output and error
        this.outStream = outStream;

        // Client parameter parsing
        final ApexDeploymentRestParameterParser parser = new ApexDeploymentRestParameterParser();

        try {
            // Get and check the parameters
            parameters = parser.parse(args);
        } catch (final ApexDeploymentRestParameterException e) {
            throw new ApexDeploymentRestParameterException(
                            REST_ENDPOINT_PREFIX + this.toString() + ") parameter error, " + e.getMessage() + '\n'
                                            + parser.getHelp(ApexDeploymentRestMain.class.getCanonicalName()), e);
        }

        if (parameters.isHelpSet()) {
            throw new ApexDeploymentRestParameterException(
                            parser.getHelp(ApexDeploymentRestMain.class.getCanonicalName()));
        }

        // Validate the parameters
        final String validationMessage = parameters.validate();
        if (validationMessage.length() > 0) {
            throw new ApexDeploymentRestParameterException(
                            REST_ENDPOINT_PREFIX + this.toString() + ") parameters invalid, " + validationMessage + '\n'
                                            + parser.getHelp(ApexDeploymentRestMain.class.getCanonicalName()));
        }

        state = ServicesState.READY;
    }

    /**
     * Initialize the rest service.
     */
    public void init() {
        outStream.println(REST_ENDPOINT_PREFIX + this.toString() + ") starting at " + parameters.getBaseUri().toString()
                        + " . . .");

        try {
            state = ServicesState.INITIALIZING;

            // Start the REST service
            apexDeploymentRest = new ApexDeploymentRest(parameters);

            // Add a shutdown hook to shut down the rest services when the process is exiting
            Runtime.getRuntime().addShutdownHook(new Thread(new ApexServicesShutdownHook()));

            state = ServicesState.RUNNING;

            if (parameters.getTimeToLive() == ApexDeploymentRestParameters.INFINITY_TIME_TO_LIVE) {
                outStream.println(REST_ENDPOINT_PREFIX + this.toString() + ") started at "
                                + parameters.getBaseUri().toString());
            } else {
                outStream.println(REST_ENDPOINT_PREFIX + this.toString() + ") started");
            }

            // Find out how long is left to wait
            long timeRemaining = parameters.getTimeToLive();
            while (timeRemaining == ApexDeploymentRestParameters.INFINITY_TIME_TO_LIVE || timeRemaining > 0) {
                // decrement the time to live in the non-infinity case
                if (timeRemaining > 0) {
                    timeRemaining--;
                }

                // Wait for a second
                Thread.sleep(1000);
            }
        } catch (final Exception e) {
            String message = REST_ENDPOINT_PREFIX + this.toString() + ") failed at with error: " + e.getMessage();
            outStream.println(message);
            LOGGER.warn(message, e);
        } finally {
            if (apexDeploymentRest != null) {
                apexDeploymentRest.shutdown();
                apexDeploymentRest = null;
            }
            state = ServicesState.STOPPED;
        }

    }

    /**
     * Get services state.
     *
     * @return the service state
     */
    public ServicesState getState() {
        return state;
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append(this.getClass().getSimpleName()).append(": Config=[").append(this.parameters).append("], State=")
                        .append(this.getState());
        return ret.toString();
    }

    /**
     * Explicitly shut down the services.
     */
    public void shutdown() {
        if (apexDeploymentRest != null) {
            outStream.println(REST_ENDPOINT_PREFIX + this.toString() + ") shutting down");
            apexDeploymentRest.shutdown();
        }
        state = ServicesState.STOPPED;
        outStream.println(REST_ENDPOINT_PREFIX + this.toString() + ") shut down");
    }

    /**
     * This class is a shutdown hook for the Apex services command.
     */
    private class ApexServicesShutdownHook implements Runnable {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            if (apexDeploymentRest != null) {
                apexDeploymentRest.shutdown();
            }
        }
    }

    /**
     * Main method, main entry point for command.
     *
     * @param args The command line arguments for the client
     */
    public static void main(final String[] args) {
        try {
            final ApexDeploymentRestMain restMain = new ApexDeploymentRestMain(args, System.out);
            restMain.init();
        } catch (final Exception e) {
            LOGGER.error("start failed", e);
        }
    }
}
