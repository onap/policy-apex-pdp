/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019-2021, 2023 Nordix Foundation.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Field;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.utils.network.NetworkUtil;

public class ApexRestServerConsumerTest {

    ApexRestServerConsumer apexRestServerConsumer = null;
    EventHandlerParameters consumerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexRestServerProducer apexRestServerProducer = null;
    RestServerCarrierTechnologyParameters restServerCarrierTechnologyParameters = null;
    SynchronousEventCache synchronousEventCache = null;

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @Before
    public void setUp() throws Exception {
        apexRestServerConsumer = new ApexRestServerConsumer();
        consumerParameters = new EventHandlerParameters();
        apexRestServerProducer = new ApexRestServerProducer();
        apexRestServerConsumer.start();
    }

    @After
    public void tearDown() {
        apexRestServerConsumer.stop();
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonWebSocketCarrierTechnologyParameters() throws ApexEventException {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters,
                incomingEventReceiver);
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithWebSocketCarrierTechnologyParameters() throws ApexEventException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters,
                incomingEventReceiver);
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithSynchronousMode() throws ApexEventException, SecurityException, IllegalArgumentException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        restServerCarrierTechnologyParameters.setStandalone(true);
        consumerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);
        apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters,
                incomingEventReceiver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitWithSynchronousModeAndProperValues()
            throws ApexEventException, SecurityException, IllegalArgumentException {

        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();

        restServerCarrierTechnologyParameters.setStandalone(true);
        restServerCarrierTechnologyParameters.setHost("1ocalhost");
        restServerCarrierTechnologyParameters.setPort(65535);


        consumerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);
        apexRestServerConsumer.init("TestApexRestServerConsumer", consumerParameters,
                incomingEventReceiver);
    }

    @Test
    public void testInitAndStop() throws ApexEventException, IOException {
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
    public void testGetName() {
        assertNull(apexRestServerConsumer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexRestServerConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                apexRestServerConsumer, apexRestServerProducer);
        apexRestServerConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR,
                peeredReference);
        assertNotNull(apexRestServerConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testReceiveEvent() throws ApexEventException {
        Response response = apexRestServerConsumer.receiveEvent("");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void testReceiveEventWithNonDefaultValues()
            throws ApexEventException, NoSuchFieldException, SecurityException,
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

        apexRestServerConsumer.receiveEvent("TestApexRestServerConsumer");

    }
}
