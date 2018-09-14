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

package org.onap.policy.apex.client.editor.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to launch the editor. It creates a Grizzly embedded web server and runs the editor.
 */
public class ApexEditor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEditor.class);

    // The HTTP server exposing JAX-RS resources defined in this application.
    private final HttpServer server;

    /**
     * Starts the HTTP server for the Apex editor on the default base URI and with the default REST packages.
     */
    public ApexEditor() {
        this(new ApexEditorParameters());
    }

    /**
     * Starts the HTTP server for the Apex editor.
     *
     * @param parameters the parameters
     */
    public ApexEditor(final ApexEditorParameters parameters) {
        Assertions.argumentNotNull(parameters, "parameters may not be null");

        LOGGER.debug("Apex RESTful editor starting . . .");

        // Create a resource configuration that scans for JAX-RS resources and providers
        // in org.onap.policy.apex.client.editor.rest package
        final ResourceConfig rc = new ResourceConfig().packages(parameters.getRestPackages());

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        server = GrizzlyHttpServerFactory.createHttpServer(parameters.getBaseUri(), rc);

        // Add static content
        server.getServerConfiguration().addHttpHandler(new org.glassfish.grizzly.http.server.CLStaticHttpHandler(
                ApexEditorMain.class.getClassLoader(), "/webapp/"), parameters.getStaticPath());

        LOGGER.debug("Apex RESTful editor started");
    }

    /**
     * Shut down the web server.
     */
    public void shutdown() {
        LOGGER.debug("Apex RESTful editor shutting down . . .");
        server.shutdown();
        LOGGER.debug("Apex RESTful editor shut down");
    }
}
