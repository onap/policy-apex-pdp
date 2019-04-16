/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.BasicAuthDefinition;
import io.swagger.annotations.Info;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.policy.common.endpoints.report.HealthCheckReport;

/**
 * Class to provide REST endpoints for services-onappf component health check.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
//@formatter:off
@Path("/policy/apex-pdp/v1")
@Api(value = "PDP-A API")
@Produces(MediaType.APPLICATION_JSON)
@SwaggerDefinition(
 info = @Info(description =
                 "PDP-A is responsible for making policy decisions and for managing the administrative"
                         + " state of the PDPs as directed by PolicyAdministration", version = "v1.0",
                 title = "PDP-A"),
 consumes = {MediaType.APPLICATION_JSON},
 produces = {MediaType.APPLICATION_JSON},
 schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
 tags = {@Tag(name = "PDP-A", description = "PDP-A Operations")},
 securityDefinition = @SecurityDefinition(basicAuthDefinitions = {@BasicAuthDefinition(key = "basicAuth")}))
//@formatter:on
public class HealthCheckRestControllerV1 {

    public static final String AUTHORIZATION_TYPE = "basicAuth";

    public static final int AUTHENTICATION_ERROR_CODE = HttpURLConnection.HTTP_UNAUTHORIZED;
    public static final int AUTHORIZATION_ERROR_CODE = HttpURLConnection.HTTP_FORBIDDEN;
    public static final int SERVER_ERROR_CODE = HttpURLConnection.HTTP_INTERNAL_ERROR;

    public static final String AUTHENTICATION_ERROR_MESSAGE = "Authentication Error";
    public static final String AUTHORIZATION_ERROR_MESSAGE = "Authorization Error";
    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";

    @GET
    @Path("healthcheck")
    @ApiOperation(value = "Perform healthcheck", notes = "Returns healthy status of the PDP-A component",
            response = HealthCheckReport.class, authorizations = @Authorization(value = AUTHORIZATION_TYPE))
    @ApiResponses(value = { @ApiResponse(code = AUTHENTICATION_ERROR_CODE, message = AUTHENTICATION_ERROR_MESSAGE),
        @ApiResponse(code = AUTHORIZATION_ERROR_CODE, message = AUTHORIZATION_ERROR_MESSAGE),
        @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_MESSAGE) })
    public Response healthcheck() {
        return Response.status(Response.Status.OK).entity(new HealthCheckProvider().performHealthCheck()).build();
    }
}
