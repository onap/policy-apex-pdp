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

import com.google.gson.JsonObject;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.onap.policy.apex.core.deployment.ApexDeploymentException;
import org.onap.policy.apex.core.deployment.EngineServiceFacade;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The class represents the root resource exposed at the base URL<br>
 *
 * <p>The url to access this resource would be in the form {@code <baseURL>/rest/....} <br>
 * For example: a GET request to the following URL
 * {@code http://localhost:18989/apexservices/rest/?hostName=localhost&port=12345}
 *
 * <p><b>Note:</b> An allocated {@code hostName} and {@code port} query parameter must be included in all requests.
 * Datasets for different {@code hostName} are completely isolated from one another.
 *
 */
@Path("deployment/")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })

public class ApexDeploymentRestResource {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexDeploymentRestResource.class);

    /**
     * Constructor, a new resource director is created for each request.
     */
    public ApexDeploymentRestResource() {}

    /**
     * Query the engine service for data.
     *
     * @param hostName the host name of the engine service to connect to.
     * @param port the port number of the engine service to connect to.
     * @return a Response object containing the engines service, status and context data in JSON
     */
    @GET
    public Response createSession(@QueryParam("hostName") final String hostName, @QueryParam("port") final int port) {
        final String host = hostName + ":" + port;
        final EngineServiceFacade engineServiceFacade = new EngineServiceFacade(hostName, port);

        try {
            engineServiceFacade.init();
        } catch (final ApexDeploymentException e) {
            final String errorMessage = "Error connecting to Apex Engine Service at " + host;
            LOGGER.warn(errorMessage + "<br>", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage + "\n" + e.getMessage())
                    .build();
        }

        final JsonObject responseObject = new JsonObject();

        // Engine Service data
        responseObject.addProperty("engine_id", engineServiceFacade.getKey().getID());
        responseObject.addProperty("model_id",
                engineServiceFacade.getApexModelKey() != null ? engineServiceFacade.getApexModelKey().getID()
                        : "Not Set");
        responseObject.addProperty("server", hostName);
        responseObject.addProperty("port", Integer.toString(port));

        return Response.ok(responseObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Upload a model.
     *
     * @param hostName the host name of the engine service to connect to.
     * @param port the port number of the engine service to connect to.
     * @param uploadedInputStream input stream
     * @param fileDetail details on the file
     * @param ignoreConflicts conflict policy
     * @param forceUpdate update policy
     * @return a response object in plain text confirming the upload was successful
     */
    @POST
    @Path("modelupload/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response modelUpload(@FormDataParam("hostName") final String hostName, @FormDataParam("port") final int port,
            @FormDataParam("file") final InputStream uploadedInputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetail,
            @FormDataParam("ignoreConflicts") final boolean ignoreConflicts,
            @FormDataParam("forceUpdate") final boolean forceUpdate) {
        final EngineServiceFacade engineServiceFacade = new EngineServiceFacade(hostName, port);

        try {
            engineServiceFacade.init();
        } catch (final ApexDeploymentException e) {
            final String errorMessage = "Error connecting to Apex Engine Service at " + hostName + ":" + port;
            LOGGER.warn(errorMessage + "<br>", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage + "\n" + e.getMessage())
                    .build();
        }

        try {
            engineServiceFacade.deployModel(fileDetail.getFileName(), uploadedInputStream, ignoreConflicts,
                    forceUpdate);
        } catch (final Exception e) {
            LOGGER.warn("Error updating model on engine service " + engineServiceFacade.getKey().getID(), e);
            final String errorMessage =
                    "Error updating model on engine service " + engineServiceFacade.getKey().getID();
            LOGGER.warn(errorMessage + "<br>", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage + "\n" + e.getMessage())
                    .build();
        }

        return Response.ok("Model " + fileDetail.getFileName() + " deployed on engine service "
                + engineServiceFacade.getKey().getID()).build();
    }

}
