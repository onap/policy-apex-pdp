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

package org.onap.policy.apex.client.monitoring.rest;

import java.io.PrintStream;

/**
 * The main class for Apex Restful Monitoring .
 *
 * @author Michael Watkins (michael.watkins@ericsson.com)
 */
public class ApexMonitoringRestMain {
    // Services state
    public enum ServicesState {
        STOPPED, READY, INITIALIZING, RUNNING
    }

    private ServicesState state = ServicesState.STOPPED;

    // The parameters for the client
    private ApexMonitoringRestParameters parameters = null;

    // Output and error streams for messages
    private final PrintStream outStream;

    // The Apex services client this class is running
    private ApexMonitoringRest apexMonitoringRest = null;

    /**
     * Main method, main entry point for command.
     *
     * @param args The command line arguments for the client
     */
    public static void main(final String[] args) {
        try {
            final ApexMonitoringRestMain restMain = new ApexMonitoringRestMain(args, System.out);
            restMain.init();
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Constructor, kicks off the rest service.
     *
     * @param args The command line arguments for the RESTful service
     * @param outStream The stream for output messages
     */
    public ApexMonitoringRestMain(final String[] args, final PrintStream outStream) {
        // Save the streams for output and error
        this.outStream = outStream;

        // Client parameter parsing
        final ApexMonitoringRestParameterParser parser = new ApexMonitoringRestParameterParser();

        try {
            // Get and check the parameters
            parameters = parser.parse(args);
        } catch (final ApexMonitoringRestParameterException e) {
            throw new ApexMonitoringRestParameterException(
                    "Apex Services REST endpoint (" + this.toString() + ") parameter error, " + e.getMessage() + '\n'
                            + parser.getHelp(ApexMonitoringRestMain.class.getCanonicalName()));
        }

        if (parameters.isHelpSet()) {
            throw new ApexMonitoringRestParameterException(
                    parser.getHelp(ApexMonitoringRestMain.class.getCanonicalName()));
        }

        // Validate the parameters
        final String validationMessage = parameters.validate();
        if (validationMessage.length() > 0) {
            throw new ApexMonitoringRestParameterException(
                    "Apex Services REST endpoint (" + this.toString() + ") parameters invalid, " + validationMessage
                            + '\n' + parser.getHelp(ApexMonitoringRestMain.class.getCanonicalName()));
        }

        state = ServicesState.READY;
    }

    /**
     * Initialize the rest service.
     */
    public void init() {
        outStream.println("Apex Services REST endpoint (" + this.toString() + ") starting at "
                + parameters.getBaseURI().toString() + " . . .");

        try {
            state = ServicesState.INITIALIZING;

            // Start the REST service
            apexMonitoringRest = new ApexMonitoringRest(parameters);

            // Add a shutdown hook to shut down the rest services when the process is exiting
            Runtime.getRuntime().addShutdownHook(new Thread(new ApexServicesShutdownHook()));

            state = ServicesState.RUNNING;

            if (parameters.getTimeToLive() == ApexMonitoringRestParameters.INFINITY_TIME_TO_LIVE) {
                outStream.println("Apex Services REST endpoint (" + this.toString() + ") started at "
                        + parameters.getBaseURI().toString());
            } else {
                outStream.println("Apex Services REST endpoint (" + this.toString() + ") started");
            }

            // Find out how long is left to wait
            long timeRemaining = parameters.getTimeToLive();
            while (timeRemaining == ApexMonitoringRestParameters.INFINITY_TIME_TO_LIVE || timeRemaining > 0) {
                // decrement the time to live in the non-infinity case
                if (timeRemaining > 0) {
                    timeRemaining--;
                }

                // Wait for a second
                Thread.sleep(1000);
            }
        } catch (final Exception e) {
            outStream.println(
                    "Apex Services REST endpoint (" + this.toString() + ") failed at with error: " + e.getMessage());
        } finally {
            if (apexMonitoringRest != null) {
                apexMonitoringRest.shutdown();
                apexMonitoringRest = null;
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
        if (apexMonitoringRest != null) {
            outStream.println("Apex Services REST endpoint (" + this.toString() + ") shutting down");
            apexMonitoringRest.shutdown();
        }
        state = ServicesState.STOPPED;
        outStream.println("Apex Services REST endpoint (" + this.toString() + ") shut down");
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
            if (apexMonitoringRest != null) {
                apexMonitoringRest.shutdown();
            }
        }
    }

}
