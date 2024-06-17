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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * Test the ApexRestClientProducer class.
 *
 */
@ExtendWith(MockitoExtension.class)
class ApexRestClientProducerTest {
    @Mock
    private Client httpClientMock;

    @Mock
    private WebTarget targetMock;

    @Mock
    private Builder builderMock;

    @Mock
    private Response responseMock;

    AutoCloseable closeable;

    @Test
    void testApexRestClientProducerErrors() throws ApexEventException {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        assertThatThrownBy(() -> arcp.init("RestClientProducer", producerParameters))
            .hasMessage("specified producer properties are not applicable to REST client producer"
                + " (RestClientProducer)");

        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);
        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.DELETE);
        assertThatThrownBy(() -> arcp.init("RestClientConsumer", producerParameters))
            .hasMessage("specified HTTP method of \"DELETE\" is invalid, only HTTP methods \"POST\""
                + " and \"PUT\" are supported for event sending on REST client producer (RestClientConsumer)");

        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.DELETE, rcctp.getHttpMethod());
        rcctp.setHttpMethod(null);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());
        arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
        assertNull(arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        arcp.stop();

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());
        arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
        assertNull(arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        arcp.stop();

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.PUT);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.PUT, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());
        arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, null);
        assertNull(arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        arcp.stop();
    }

    @Test
    void testApexRestClientProducerPutEvent() throws ApexEventException {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.PUT);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.PUT, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        closeable = Mockito.doReturn(responseMock).when(builderMock).put(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        arcp.sendEvent(123, null, "EventName", "This is an Event");
        arcp.stop();
    }

    @Test
    void testApexRestClientProducerPostEventFail() throws ApexEventException {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());

        rcctp.setUrl("http://some.place.that.does.not/exist");
        arcp.setClient(httpClientMock);

        // test property not found
        rcctp.setUrl("http://some.place2.that.{key}.not/{tag}and.again.{tag}");
        Properties properties = new Properties();
        properties.put("tag", "exist");
        assertThatThrownBy(() -> {
            arcp.sendEvent(123, properties, "EventName", "This is an Event");
            arcp.stop();
        }).hasMessageContaining("key \"key\" specified on url "
                + "\"http://some.place2.that.{key}.not/{tag}and.again.{tag}\" not found "
                + "in execution properties passed by the current policy");
    }

    @Test
    void testApexRestClientProducerPostEventOK() throws ApexEventException {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.PUT);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.PUT, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());

        System.out.println("fail test");
        rcctp.setUrl("http://some.place.{key}.does.not/{tag}");
        Properties properties = new Properties();
        properties.put("tag", "exist");
        properties.put("key", "that");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        closeable = Mockito.doReturn(responseMock).when(builderMock).put(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target("http://some.place.that.does.not/exist");
        arcp.setClient(httpClientMock);

        arcp.sendEvent(123, properties, "EventName", "This is an Event");
        arcp.stop();
    }

    @Test
    void testApexRestClientProducerPostEventAccepted() throws ApexEventException {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.PUT);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.PUT, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());

        rcctp.setUrl("http://some.place.{key}.does.not/{tag}");
        Properties properties = new Properties();
        properties.put("tag", "exist");
        properties.put("key", "that");
        Mockito.doReturn(Response.Status.ACCEPTED.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).put(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target("http://some.place.that.does.not/exist");
        arcp.setClient(httpClientMock);

        arcp.sendEvent(123, properties, "EventName", "This is an ACCEPTED Event");
        arcp.stop();
    }

    @Test
    void testApexRestClientProducerPostEventCache() throws ApexEventException {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);

        ApexEventConsumer consumer = new ApexFileEventConsumer();
        SynchronousEventCache cache =
                new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, arcp, 1000);
        arcp.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, cache);
        assertEquals(cache, arcp.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.OK.getStatusCode()).when(responseMock).getStatus();
        Mockito.doReturn(responseMock).when(builderMock).post(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        arcp.sendEvent(123, null, "EventName", "This is an Event");
        arcp.stop();
    }

    @Test
    void testApexRestClientProducerHttpError() throws ApexEventException {
        ApexRestClientProducer arcp = new ApexRestClientProducer();
        assertNotNull(arcp);

        EventHandlerParameters producerParameters = new EventHandlerParameters();
        RestClientCarrierTechnologyParameters rcctp = new RestClientCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rcctp);

        rcctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);
        arcp.init("RestClientConsumer", producerParameters);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.POST, rcctp.getHttpMethod());
        assertEquals("RestClientConsumer", arcp.getName());

        rcctp.setUrl("http://some.place.that.does.not/exist");
        Mockito.doReturn(Response.Status.BAD_REQUEST.getStatusCode()).when(responseMock).getStatus();
        closeable = Mockito.doReturn(responseMock).when(builderMock).post(Mockito.any());
        Mockito.doReturn(builderMock).when(targetMock).request("application/json");
        Mockito.doReturn(builderMock).when(builderMock).headers(Mockito.any());
        Mockito.doReturn(targetMock).when(httpClientMock).target(rcctp.getUrl());
        arcp.setClient(httpClientMock);

        assertThatThrownBy(() -> arcp.sendEvent(123, null, "EventName", "This is an Event"))
            .hasMessageContaining("send of event to URL \"http://some.place.that.does.not/exist\" using HTTP \"POST\" "
                + "failed with status code 400");
    }

    @AfterEach
    void after() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }
}
