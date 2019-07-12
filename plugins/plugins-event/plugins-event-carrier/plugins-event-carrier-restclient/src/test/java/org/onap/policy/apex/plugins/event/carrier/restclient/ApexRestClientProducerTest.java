/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
import static org.junit.Assert.fail;

import ch.qos.logback.classic.Level;

import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test the ApexRestClientProducer class.
 *
 */
public class ApexRestClientProducerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestClientProducer.class);

    @Mock
    private Client httpClientMock;

    @Mock
    private WebTarget targetMock;

    @Mock
    private Builder builderMock;

    @Mock
    private Response responseMock;

    @Test
    public void testApexRestClientProducerErrors() {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        try {
            arcp.init("RestClientProducer", producerParameters);
            fail("test should throw an exception here");
        } catch (ApexEventException e) {
            assertEquals(
                "specified producer properties are not applicable to REST client producer (RestClientProducer)",
                e.getMessage());
        }

        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.DELETE);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.GET, rcctp.getHttpMethod());
            fail("test should throw an exception here");
        } catch (ApexEventException e) {
            assertEquals("specified HTTP method of \"DELETE\" is invalid, only HTTP methods \"POST\" and \"PUT\" "
                + "are supproted for event sending on REST client producer (RestClientConsumer)", e.getMessage());
        }

        rcctp.setHttpMethod(null);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());

            arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
            assertEquals(null, arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

            arcp.stop();
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());

            arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
            assertEquals(null, arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

            arcp.stop();
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.PUT);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.PUT, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());

            arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
            assertEquals(null, arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));

            arcp.stop();
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testApexRestClientProducerPutEvent() {
        MockitoAnnotations.initMocks(this);

        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.PUT);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.PUT, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).put(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        try {
            arcp.sendEvent(123, null, "EventName", "This is an Event");
            arcp.stop();
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testApexRestClientProducerPostEventFail() {
        MockitoAnnotations.initMocks(this);

        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).post(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        //test property not found
        rcctp.setUrl("http://some.place2.that.{key}.not/{tag}and.again.{tag}");
        Properties properties = new Properties();
        properties.put("tag", "exist");
        try {
            arcp.sendEvent(123, properties, "EventName", "This is an Event");
            arcp.stop();
            fail("test should throw an exception");
        } catch (Exception e) {
            assertEquals(
                    "key\"key\"specified on url "
                            + "\"http://some.place2.that.{key}.not/{tag}and.again.{tag}\"not found "
                            + "in execution properties passed by the current policy",
                    e.getMessage());
        }
    }

    @Test
    public void testApexRestClientProducerPostEventOK() {
        MockitoAnnotations.initMocks(this);

        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.PUT);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.PUT, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        System.out.println("fail test");
        rcctp.setUrl("http://some.place.{key}.does.not/{tag}");
        Properties properties = new Properties();
        properties.put("tag", "exist");
        properties.put("key", "that");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).put(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target("http://some.place.that.does.not/exist");
        arcp.setClient(httpClientMock);

        try {
            arcp.sendEvent(123, properties, "EventName", "This is an Event");
            arcp.stop();
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testApexRestClientProducerPostEventCache() {
        MockitoAnnotations.initMocks(this);

        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);

        ApexEventConsumer consumer = new ApexFileEventConsumer();
        SynchronousEventCache cache = new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, arcp,
            1000);
        arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, cache);
        assertEquals(cache, arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).post(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        try {
            arcp.sendEvent(123, null, "EventName", "This is an Event");
            arcp.stop();
        } catch (Exception e) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testApexRestClientProducerPostEventCacheTrace() {
        MockitoAnnotations.initMocks(this);

        ch.qos.logback.classic.Logger classicLogger = (ch.qos.logback.classic.Logger) LOGGER;
        classicLogger.setLevel(Level.TRACE);

        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);

        ApexEventConsumer consumer = new ApexFileEventConsumer();
        SynchronousEventCache cache = new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, arcp,
            1000);
        arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, cache);
        assertEquals(cache, arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).post(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        try {
            arcp.sendEvent(123, null, "EventName", "This is an Event");
            arcp.stop();
        } catch (Exception e) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testApexRestClientProducerHttpError() {
        MockitoAnnotations.initMocks(this);

        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);
        try {
            arcp.init("RestClientConsumer", producerParameters);
            assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());

            assertEquals("RestClientConsumer", arcp.getName());
        } catch (ApexEventException e) {
            fail("test should not throw an exception");
        }

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.BAD_REQUEST.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).post(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        try {
            arcp.sendEvent(123, null, "EventName", "This is an Event");
            fail("test should throw an exception here");
        } catch (Exception e) {
            assertEquals(
                "send of event to URL \"http://some.place.that.does.not/exist\" using HTTP \"POST\" "
                    + "failed with status code 400 and message \"null\", event:\n" + "This is an Event",
                e.getMessage());
        }
    }
}
