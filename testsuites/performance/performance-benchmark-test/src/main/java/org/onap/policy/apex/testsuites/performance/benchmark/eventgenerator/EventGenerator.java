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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.cli.ParseException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is the main class of a REST server that generates sample events.
 */
public class EventGenerator {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventGenerator.class);

    // Parameters for event generation
    private final EventGeneratorParameters parameters;

    // The HTTP server we are running
    private final HttpServer eventGeneratorServer;

    /**
     * Instantiates a new event generator with the given parameters.
     * 
     * @param parameters the parameters for the event generator
     */
    public EventGenerator(final EventGeneratorParameters parameters) {
        this.parameters = parameters;

        // Set the parameters in the event generator endpoint
        EventGeneratorEndpoint.clearEventGenerationStats();
        EventGeneratorEndpoint.setParameters(parameters);

        // Add a shutdown hook to shut down the rest services when the process is exiting
        Runtime.getRuntime().addShutdownHook(new Thread(new EventGeneratorShutdownHook()));

        LOGGER.info("Event generator REST server starting");

        final ResourceConfig rc = new ResourceConfig(EventGeneratorEndpoint.class);
        eventGeneratorServer = GrizzlyHttpServerFactory.createHttpServer(getBaseUri(), rc);

        // Wait for the HTTP server to come up
        while (!eventGeneratorServer.isStarted()) {
            ThreadUtilities.sleep(50);
        }

        LOGGER.info("Event generator REST server started");
    }

    /**
     * Get the current event generation statistics.
     * 
     * @return the statistics as a JSON string
     */
    public String getEventGenerationStats() {
        return EventGeneratorEndpoint.getEventGenerationStats();
    }

    /**
     * Check if event generation is finished.
     * 
     * @return true if event generation is finished
     */
    public boolean isFinished() {
        return EventGeneratorEndpoint.isFinished();
    }

    /**
     * Tear down the event generator.
     */
    public void tearDown() {
        LOGGER.info("Event generator shutting down");

        eventGeneratorServer.shutdown();
        
        if (parameters.getOutFile() != null) {
            try {
                TextFileUtils.putStringAsTextFile(getEventGenerationStats(), parameters.getOutFile());
            }
            catch (IOException ioe) {
                LOGGER.warn("could not output statistics to file \"" + parameters.getOutFile() + "\"", ioe);
            }
        }

        LOGGER.info("Event generator shut down");
    }

    /**
     * Get the base URI for the server.
     * 
     * @return the base URI
     */
    private URI getBaseUri() {
        String baseUri = "http://" + parameters.getHost() + ':' + parameters.getPort() + '/' + "/EventGenerator";
        return URI.create(baseUri);
    }

    /**
     * This class is a shutdown hook for the Apex editor command.
     */
    private class EventGeneratorShutdownHook implements Runnable {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            tearDown();
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting event generator with arguments: " + Arrays.toString(args));
        
        EventGeneratorParameterHandler parameterHandler = new EventGeneratorParameterHandler();
        
        EventGeneratorParameters parameters = null;
        
        try {
            parameters = parameterHandler.parse(args);
        }
        catch (ParseException pe) {
            LOGGER.trace("Event generator start exception", pe);
            LOGGER.info("Start of event generator failed: {}", pe.getMessage());
            return;
        }
        
        // Null parameters means we print help
        if (parameters == null) {
            LOGGER.info(parameterHandler.getHelp(EventGenerator.class.getName()));
            return;
        }
        
        // Start the event generator
        EventGenerator eventGenerator = new EventGenerator(parameters);
        LOGGER.info("Event generator started");
        
        // Wait for event generation to finish
        while (!eventGenerator.isFinished()) {
            ThreadUtilities.sleep(200);
        }

        
        // Shut down the server
        eventGenerator.tearDown();
        
        LOGGER.info("Event generator statistics\n" + eventGenerator.getEventGenerationStats());
        
        LOGGER.info("Event generator finished");
    }
}
