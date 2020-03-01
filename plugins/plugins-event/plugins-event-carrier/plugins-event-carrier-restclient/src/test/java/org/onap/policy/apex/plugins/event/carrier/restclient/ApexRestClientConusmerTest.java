/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.infrastructure.threading.ThreadMonitor;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * This class tests the ApexRestClientConusmer class.
 *
 */
public class ApexRestClientConusmerTest implements ThreadMonitor<SupportApexEventReceiver> {
    private final PrintStream stdout = System.out;
    private long startTime;

    @Override
    public boolean check(SupportApexEventReceiver supportApexEventReceiver) {
        return supportApexEventReceiver.getEventCount() == 0;
    }

    @Override
    public void waitUntil(long timeOut, SupportApexEventReceiver supportApexEventReceiver) {
        while (check(supportApexEventReceiver)) {
            ThreadUtilities.sleep(1);
            if (System.currentTimeMillis() - startTime > timeOut) {
                break;
            }
        }
    }

    @Mock
    private Client httpClientMock;

    @Mock
    private WebTarget targetMock;

    @Mock
    private Builder builderMock;

    @Mock
    private Response responseMock;

    @Before
    public void initializedTime() {
        startTime = System.currentTimeMillis();
    }

    @After
    public void after() {
        System.setOut(stdout);
    }

    @Test
    public void testApexRestClientConsumerErrors() throws ApexEventException {
        MockitoAnnotations.initMocks(this);

        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();
        try {
            arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException e) {
            assertEquals(
                "specified consumer properties are not applicable to REST client consumer (RestClientConsumer)",
                e.getMessage());
        }

        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.DELETE);
        try {
            arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());
            fail("test should throw an exception here");
        } catch (ApexEventException e) {
            assertEquals("specified HTTP method of \"DELETE\" is invalid, only HTTP method \"GET\" is supported "
                + "for event reception on REST client consumer (RestClientConsumer)", e.getMessage());
        }

        rcctp.setHttpMethod(null);
        rcctp.setHttpCodeFilter("zzz");

        arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());

        assertEquals("RestClientConsumer", arcc.getName());

        arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
        assertEquals(null, arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.BAD_REQUEST.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getHttpCodeFilter());
        arcc.setClient(httpClientMock);

        // We have not set the URL, this test should not receive any events
        arcc.start();
        waitUntil(200, incomingEventReceiver);
        arcc.stop();
        assertEquals(0, incomingEventReceiver.getEventCount());

        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();

        // We have not set the URL, this test should not receive any events
        arcc.start();
        waitUntil(200, incomingEventReceiver);
        arcc.stop();
        assertEquals(0, incomingEventReceiver.getEventCount());
    }

    @Test
    public void testApexRestClientConsumerHttpError() throws ApexEventException {
        MockitoAnnotations.initMocks(this);

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
        assertEquals(null, arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        Mockito.doReturn(Response.Status.BAD_REQUEST.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getHttpCodeFilter());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // We have not set the URL, this test should not receive any events
        arcc.start();
        waitUntil(200, incomingEventReceiver);
        arcc.stop();
        assertEquals(0, incomingEventReceiver.getEventCount());
    }

    @Test
    public void testApexRestClientConsumerJsonError() {
        MockitoAnnotations.initMocks(this);

        ApexRestClientConsumer arcc = new ApexRestClientConsumer();
        assertNotNull(arcc);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        SupportApexEventReceiver incomingEventReceiver = new SupportApexEventReceiver();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpCodeFilter("[1-5][0][0-5]");

        try {
            arcc.init("RestClientConsumer", consumerParameters, incomingEventReceiver);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcc.getName());

            arcc.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);

            assertEquals(null, arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getHttpCodeFilter());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // We have not set the URL, this test should not receive any events
        arcc.start();
        waitUntil(200, incomingEventReceiver);
        arcc.stop();

        assertEquals(0, incomingEventReceiver.getEventCount());

        final String outString = outContent.toString();

        assertTrue(outString.contains("received an empty event from URL \"http://some.place.that.does.not/exist\""));
    }

    @Test
    public void testApexRestClientConsumerJsonEmpty() throws ApexEventException {
        MockitoAnnotations.initMocks(this);

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

        assertEquals(null, arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn("").when(responseMock).readEntity(String.class);
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getHttpCodeFilter());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // We have not set the URL, this test should not receive any events
        arcc.start();
        waitUntil(200, incomingEventReceiver);
        arcc.stop();

        assertEquals(0, incomingEventReceiver.getEventCount());

        final String outString = outContent.toString();

        assertTrue(outString.contains("received an empty event from URL \"http://some.place.that.does.not/exist\""));
    }

    @Test
    public void testApexRestClientConsumerJsonOk() throws ApexEventException {
        MockitoAnnotations.initMocks(this);

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

        assertEquals(null, arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn("This is an event").when(responseMock).readEntity(String.class);
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getHttpCodeFilter());
        arcc.setClient(httpClientMock);

        // We have not set the URL, this test should not receive any events
        arcc.start();
        waitUntil(200, incomingEventReceiver);
        arcc.stop();

        assertEquals("This is an event", incomingEventReceiver.getLastEvent());
    }

    @Test
    public void testApexRestClientConsumerInvalidStatusCode() throws ApexEventException {
        MockitoAnnotations.initMocks(this);

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

        assertEquals(null, arcc.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn("This is an event").when(responseMock).readEntity(String.class);
        Mockito.doReturn(responseMock).when(builderMock).get();
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getHttpCodeFilter());
        arcc.setClient(httpClientMock);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            // We have not set the URL, this test should not receive any events
            arcc.start();
            waitUntil(200, incomingEventReceiver);
            arcc.stop();
        } catch (Exception e) {
            // test invalid status code
            assertEquals("received an invalid status code \"200\"", e.getMessage());
        }
    }
}
