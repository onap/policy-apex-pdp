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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.restserver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;


public class TestRestServerVpnContext {
    private static int eventsSent = 0;

    @Ignore
    @Test
    public void testRestServerPut() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventContextJava.json"};
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                .request("application/json").put(Entity.json(setupLinkContext("L09", true)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupLinkContext("L10", true)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("A", "L09 L10", 300, 50)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("B", "L09 L10", 300, 299)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("C", "L09 L10", 300, 300)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("D", "L09 L10", 300, 400)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ThreadUtilities.sleep(100000);

        apexMain.shutdown();
    }

    @Ignore
    @Test
    public void testRestServerPutAvro() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventContextAvro.json"};
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                .request("application/json").put(Entity.json(setupLinkContext("L09", true)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupLinkContext("L10", true)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("A", "L09 L10", 300, 50)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("B", "L09 L10", 300, 299)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("C", "L09 L10", 300, 300)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(setupCustomerContext("D", "L09 L10", 300, 400)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ThreadUtilities.sleep(100000);

        apexMain.shutdown();
    }

    private String setupLinkContext(final String link, final Boolean isUp) {
        final String eventString = "{\n" + "\"nameSpace\": \"org.onap.policy.apex.domains.vpn.events\",\n"
                + "\"name\": \"VPNLinkCtxtTriggerEvent\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_"
                + eventsSent++ + "\",\n" + "\"target\": \"apex\",\n" + "\"Link\": \"" + link + "\",\n" + "\"LinkUp\": "
                + isUp + "\n" + "}";

        return eventString;
    }

    private String setupCustomerContext(final String customerName, final String linkList, final int slaDt,
            final int ytdDt) {
        final String eventString = "{\n" + "\"nameSpace\": \"org.onap.policy.apex.domains.vpn.events\",\n"
                + "\"name\": \"VPNCustomerCtxtTriggerEvent\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_"
                + eventsSent++ + "\",\n" + "\"target\": \"apex\",\n" + "\"CustomerName\": \"" + customerName + "\",\n"
                + "\"LinkList\": \"" + linkList + "\",\n" + "\"SlaDT\": \"" + slaDt + "\",\n" + "\"YtdDT\": " + ytdDt
                + "\n" + "}";

        return eventString;
    }
}
