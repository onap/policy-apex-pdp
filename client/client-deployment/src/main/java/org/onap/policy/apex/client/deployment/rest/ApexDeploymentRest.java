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

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to launch the services. It creates a Grizzly embedded web server and runs the
 * services.
 */
public class ApexDeploymentRest {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(ApexDeploymentRest.class);

    // The HTTP server exposing JAX-RS resources defined in this application.
    private HttpServer server;

    /**
     * Starts the HTTP server for the Apex services client on the default base URI and with the
     * default REST packages.
     */
    public ApexDeploymentRest() {
        this(new ApexDeploymentRestParameters());
    }

    /**
     * Starts the HTTP server for the Apex services client.
     *
     * @param parameters The Apex parameters to use to start the server
     */
    public ApexDeploymentRest(final ApexDeploymentRestParameters parameters) {
        Assertions.argumentNotNull(parameters, "parameters may not be null");

        logger.debug("Apex services RESTful client starting . . .");

        // Create a resource configuration that scans for JAX-RS resources and providers
        // in org.onap.policy.apex.client.deployment.rest package
        final ResourceConfig rc = new ResourceConfig().packages(parameters.getRestPackages());

        // Add MultiPartFeature class for jersey-media-multipart
        rc.register(MultiPartFeature.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        server = GrizzlyHttpServerFactory.createHttpServer(parameters.getBaseUri(), rc);

        // Add static content
        server.getServerConfiguration().addHttpHandler(new org.glassfish.grizzly.http.server.CLStaticHttpHandler(
                ApexDeploymentRestMain.class.getClassLoader(), "/webapp/"), parameters.getStaticPath());

        logger.debug("Apex services RESTful client started");
    }

    /**
     * Shut down the web server.
     */
    public void shutdown() {
        logger.debug("Apex services RESTful client shutting down . . .");
        server.shutdown();
        logger.debug("Apex services RESTful client shut down");
    }
}
