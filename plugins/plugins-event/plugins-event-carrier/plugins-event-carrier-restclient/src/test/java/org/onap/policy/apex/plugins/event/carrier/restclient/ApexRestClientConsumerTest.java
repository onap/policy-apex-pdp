/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * This class tests the ApexRestClientConsumer class.
 */
@ExtendWith(MockitoExtension.class)
class ApexRestClientConsumerTest {
    private final PrintStream stdout = System.out;

    @Mock
    private Client httpClientMock;

    @Mock
    private WebTarget targetMock;

    @Mock
    private Builder builderMock;

    @Mock
    private Response responseMock;

    @AfterEach
    void after() {
        System.setOut(stdout);
    }

    @Test
    void testApexRestClientConsumerErrors() throws ApexEventException {
        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();
        assertThatThrownBy(() -> arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver))
            .hasMessageContaining(
                "specified consumer properties are not applicable to REST client" + " consumer (RestClientConsumer)");

        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.DELETE);
        assertThatThrownBy(() -> {
            arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        }).hasMessageContaining("specified HTTP method of \"DELETE\" is invalid, only HTTP method \"GET\" is "
            + "supported for event reception on REST client consumer (RestClientConsumer)");

        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.DELETE, rcctp.getHttpMethod());
        rcctp.setHttpMethod(null);
        rcctp.setHttpCodeFilter("zzz");

        arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());

        assertEquals("RestClientConsumer", arcc.getName());

        arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
        assertNull(arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcc.setClient(httpClientMock);

        // We have not set the URL, this test should not receive any events
        arcc.start();
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> incomingEventReceiver.getEventCount() == 0);
        arcc.stop();
        assertEquals(0, incomingEventReceiver.getEventCount());

        // We have not set the URL, this test should not receive any events
        arcc.start();
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> incomingEventReceiver.getEventCount() == 0);
        arcc.stop();
        assertEquals(0, incomingEventReceiver.getEventCount());
    }

    @Test
    void testApexRestClientConsumerHttpError() throws ApexEventException {
        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setUrl("http://some.place.that.does.not/exist");
        rcctp.setHttpCodeFilter("[1-5][0][0-5]");
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();

        arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());

        assertEquals("[1-5][0][0-5]", rcctp.getHttpCodeFilter());

        assertEquals("RestClientConsumer", arcc.getName());

        arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
        assertNull(arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        Mockito.doReturn(Response.Status.BAD_REQUEST.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // We have not set the URL, this test should not receive any events
        arcc.start();
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> incomingEventReceiver.getEventCount() == 0);
        arcc.stop();
        assertEquals(0, incomingEventReceiver.getEventCount());
    }

    @Test
    void testApexRestClientConsumerJsonError() throws ApexEventException {
        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpCodeFilter("[1-5][0][0-5]");

        arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcc.getName());

        arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);

        assertNull(arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // We have not set the URL, this test should not receive any events
        arcc.start();
        await().atMost(400, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
            .contains("received an empty event from URL \"http://some.place.that.does.not/exist\""));
        arcc.stop();
        assertEquals(0, incomingEventReceiver.getEventCount());
    }

    @Test
    void testApexRestClientConsumerJsonEmpty() throws ApexEventException {
        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpCodeFilter("[1-5][0][0-5]");

        arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());

        assertEquals("RestClientConsumer", arcc.getName());

        arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);

        assertNull(arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn("").when(responseMock).readEntity(String.class);
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // We have not set the URL, this test should not receive any events
        arcc.start();
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
            .contains("received an empty event from URL \"http://some.place.that.does.not/exist\""));
        arcc.stop();

        assertEquals(0, incomingEventReceiver.getEventCount());

        final String outString = outContent.toString();

        assertTrue(outString.contains("received an empty event from URL \"http://some.place.that.does.not/exist\""));
    }

    @Test
    void testApexRestClientConsumerJsonOk() throws ApexEventException {
        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpCodeFilter("[1-5][0][0-5]");

        arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());

        assertEquals("RestClientConsumer", arcc.getName());

        arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);

        assertNull(arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn("This is an event").when(responseMock).readEntity(String.class);
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcc.setClient(httpClientMock);

        // We have not set the URL, this test should not receive any events
        arcc.start();
        await().atMost(400, TimeUnit.MILLISECONDS)
            .until(() -> incomingEventReceiver.getLastEvent().equals("This is an event"));
        arcc.stop();
    }

    @Test
    void testApexRestClientConsumerInvalidStatusCode() throws ApexEventException {
        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpCodeFilter("zzz");

        arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());

        assertEquals("RestClientConsumer", arcc.getName());

        arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);

        assertNull(arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn("This is an event").when(responseMock).readEntity(String.class);
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        arcc.start();
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> incomingEventReceiver.getEventCount() == 0);
        arcc.stop();
    }
}
