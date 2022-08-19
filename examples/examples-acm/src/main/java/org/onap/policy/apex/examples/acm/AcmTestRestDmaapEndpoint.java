/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2021-2022 Nordix Foundation.
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

package org.onap.policy.apex.examples.acm;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class AcmTestRestDmaapEndpoint creates rest server endpoints for simulating sending/receiving events on DMaaP.
 */
@Path("/")
@Produces("application/json")
public class AcmTestRestDmaapEndpoint {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AcmTestRestDmaapEndpoint.class);
    private final Object lock = new Object();
    private static String loggedOutputEvent = "";
    private static final AtomicInteger counter = new AtomicInteger(1);

    /**
     * DMaaP input of events. This input event triggers the policy
     *
     * @param timeout the timeout to wait for
     * @return the response
     */
    @Path("events/POLICY_UPDATE_MSG/APEX/1")
    @GET
    public Response getMessages(@QueryParam("timeout") final int timeout) {
        String createRequest = "{\"messageType\":\"STATUS\","
                + "\"elementId\":{\"name\":\"onap.policy.clamp.ac.startertobridge\",\"version\":\"1.0.0\"},"
                + "\"message\":\"starter: onap.policy.clamp.ac.starter 1.0.0\",\"messageId\":\""
                + counter.incrementAndGet() + "\",\"timestamp\":\"2022-08-19T07:37:01.198592Z\"}";
        LOGGER.info("Create request received: \n {}", createRequest);

        return Response.status(200).entity(List.of(createRequest)).build();
    }

    /**
     * Post new message.
     *
     * @param jsonString the message
     * @return the response
     */
    @Path("events/POLICY_UPDATE_MSG")
    @POST
    public Response policyMessage(final String jsonString) {
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
    @Path("events/getLoggedEvent")
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
