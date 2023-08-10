/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020, 2023 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.taskparameters;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * The Class RestClientEndpointForTaskParameters.
 */
@Path("/apex")
public class RestClientEndpointForTaskParameters {

    private static String closedLoopId;
    private static String serviceId;

    /**
     * Get event that triggers policy for testing TaskParameters.
     *
     * @return the response
     */
    @Path("/event/GetEvent")
    @GET
    public Response getEvent() {
        return Response.status(200).entity("{\"event\": \"CLTriggerEvent\"}").build();
    }

    /**
     * Fetch information of service using serviceId.
     *
     * @param servicId the service id
     *
     * @return the response
     */
    @Path("/service/getInfoForServiceId/{servicId}")
    @POST
    public Response getInfoForServiceId(@PathParam("servicId") String servicId) {
        serviceId = servicId;
        return Response.status(200)
            .entity("{\"name\": \"ServiceInfoEvent\", \"serviceDetails\": \"serviceDetailsFullBody\"}").build();
    }

    /**
     * Closed loop action using closedLoopId.
     *
     * @param closedLpId the closedLoopId
     *
     * @return the response
     */
    @Path("/action/doActionForCL/{closedLpId}")
    @POST
    public Response doActionForCL(@PathParam("closedLpId") String closedLpId) {
        closedLoopId = closedLpId;
        return Response.status(200).entity("{\"name\": \"CLOutputEvent\", \"status\": \"ClosedLoop Success\"}").build();
    }

    /**
     * Get details that are set as part of the policy execution.
     *
     * @return the response
     */
    @Path("/event/getDetails")
    @GET
    public Response getDetails() {
        if (null == serviceId || null == closedLoopId) {
            return Response.status(500).entity("Error: Flow incomplete").build();
        }
        return Response.status(200).entity("{\"closedLoopId\": " + closedLoopId + ",\"serviceId\": " + serviceId + "}")
            .build();
    }

    /**
     * Clear details that are set as part of the policy execution.
     *
     * @return the response
     */
    @Path("/event/clearDetails")
    @GET
    public Response clearDetails() {
        closedLoopId = null;
        serviceId = null;
        return Response.status(200).entity("Details cleared.").build();
    }
}
