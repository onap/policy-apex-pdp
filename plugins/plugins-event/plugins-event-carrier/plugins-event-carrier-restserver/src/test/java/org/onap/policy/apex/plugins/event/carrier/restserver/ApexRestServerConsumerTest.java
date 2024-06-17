/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019-2021, 2023-2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.utils.network.NetworkUtil;

class ApexRestServerConsumerTest {

    ApexRestServerConsumer apexRestServerConsumer = null;
    EventHandlerParameters consumerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexRestServerProducer apexRestServerProducer = null;
    RestServerCarrierTechnologyParameters restServerCarrierTechnologyParameters = null;

    AutoCloseable closeable;

    /**
     * Set up testing.
     */
    @BeforeEach
    void setUp() {
        apexRestServerConsumer = new ApexRestServerConsumer();
        consumerParameters = new EventHandlerParameters();
        apexRestServerProducer = new ApexRestServerProducer();
        apexRestServerConsumer.start();
    }

    @AfterEach
    void tearDown() {
        apexRestServerConsumer.stop();
    }

    @Test
    void testInitWithNonWebSocketCarrierTechnologyParameters() {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {
        });
        assertThrows(ApexEventException.class, () ->
            apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters, incomingEventReceiver));
    }

    @Test
    void testInitWithWebSocketCarrierTechnologyParameters() {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        assertThrows(ApexEventException.class, () ->
            apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters, incomingEventReceiver));
    }

    @Test
    void testInitWithSynchronousMode() throws SecurityException, IllegalArgumentException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        restServerCarrierTechnologyParameters.setStandalone(true);
        consumerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);

        assertThrows(ApexEventException.class, () -> apexRestServerConsumer.init("TestApexRestServerConsumer",
            consumerParameters, incomingEventReceiver));
    }

    @Test
    void testInitWithSynchronousModeAndProperValues() throws SecurityException, IllegalArgumentException {

        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();

        restServerCarrierTechnologyParameters.setStandalone(true);
        restServerCarrierTechnologyParameters.setHost("1ocalhost");
        restServerCarrierTechnologyParameters.setPort(65535);


        consumerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);

        assertThrows(IllegalArgumentException.class,
            () -> apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters,
                incomingEventReceiver));
    }

    @Test
    void testInitAndStop() throws ApexEventException, IOException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();

        restServerCarrierTechnologyParameters.setStandalone(true);
        restServerCarrierTechnologyParameters.setHost("localhost");
        // get any available port
        final int availableTcpPort = NetworkUtil.allocPort();
        restServerCarrierTechnologyParameters.setPort(availableTcpPort);

        consumerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);
        apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters,
            incomingEventReceiver);
        HttpServletServer server = apexRestServerConsumer.getServer();

        // check if server is alive
        assertTrue(server.isAlive());

        apexRestServerConsumer.stop();
        // check if server is stopped
        assertFalse(server.isAlive());
    }

    @Test
    void testGetName() {
        assertNull(apexRestServerConsumer.getName());
    }

    @Test
    void testGetPeeredReference() {
        assertNull(apexRestServerConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
            apexRestServerConsumer, apexRestServerProducer);
        apexRestServerConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR,
            peeredReference);
        assertNotNull(apexRestServerConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testReceiveEvent() {
        Response response = apexRestServerConsumer.receiveEvent("");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        response.close();
    }

    @Test
    void testReceiveEventWithNonDefaultValues() throws NoSuchFieldException, SecurityException,
        IllegalArgumentException, IllegalAccessException {

        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
            apexRestServerConsumer, apexRestServerProducer);
        apexRestServerConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR,
            peeredReference);

        ApexEventReceiver apexEventReceiver = new SupportApexEventReceiver();

        apexRestServerConsumer.setEventReceiver(apexEventReceiver);
        Field field = ApexPluginsEventConsumer.class.getDeclaredField("name");
        field.setAccessible(true);
        field.set(apexRestServerConsumer, "TestApexRestServerConsumer");
        assertThrows(NullPointerException.class, () ->
            closeable = apexRestServerConsumer.receiveEvent("TestApexRestServerConsumer"));
    }

    @AfterEach
    void after() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }
}
