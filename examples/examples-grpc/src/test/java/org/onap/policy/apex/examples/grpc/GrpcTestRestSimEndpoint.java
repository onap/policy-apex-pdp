/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.grpc;

import static org.awaitility.Awaitility.await;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class GrpcTestRestSimEndpoint creates rest server endpoints for simulating sending/receiving events on DMaaP.
 */
@Path("/sim")
public class GrpcTestRestSimEndpoint {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(GrpcTestRestSimEndpoint.class);
    private static String loggedOutputEvent = null;
    private final Object lock = new Object();

    /**
     * DCAE input of events (simulation of DMaaP). This input event triggers the policy
     *
     * @param timeout the timeout to wait for
     * @return the response
     * @throws IOException on I/O errors
     */
    @Path("events/unauthenticated.DCAE_CL_OUTPUT/APEX/1")
    @GET
    public Response dcaeClOutput(@QueryParam("timeout") final int timeout) throws IOException {
        String createSubscriptionRequest =
            Files.readString(Paths.get("src/main/resources/examples/events/APEXgRPC/CreateSubscriptionEvent.json"));
        LOGGER.info("Create subscription request received (on a timeout of {}): \n {} ",
            timeout, createSubscriptionRequest);

        await().pollDelay(4, TimeUnit.SECONDS)
            .atMost(5, TimeUnit.SECONDS)
            .until(() -> true);
        return Response.status(200).entity(createSubscriptionRequest).build();
    }

    /**
     * Post to Policy management log (Simulation of DMaaP).
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/events/POLICY_CL_MGT")
    @POST
    public Response policyLogRequest(final String jsonString) {
        LOGGER.info("\n*** POLICY LOG ENTRY START ***\n {} \n *** POLICY LOG ENTRY END ***", jsonString);
        synchronized (lock) {
            loggedOutputEvent += jsonString + "\n";
        }
        return Response.status(200).build();
    }

    /**
     * Get the logged event for test verification.
     *
     * @return the response
     */
    @Path("/event/getLoggedEvent")
    @GET
    public Response getDetails() {
        String loggedEvent;
        synchronized (lock) {
            loggedEvent = loggedOutputEvent;
        }
        if (null == loggedEvent) {
            return Response.status(500).entity("Error: Log event not yet generated.").build();
        }
        return Response.status(200).entity(loggedEvent).build();
    }
}
