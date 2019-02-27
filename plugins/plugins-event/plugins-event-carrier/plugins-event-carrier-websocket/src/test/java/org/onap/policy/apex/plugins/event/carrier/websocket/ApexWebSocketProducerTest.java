/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
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
package org.onap.policy.apex.plugins.event.carrier.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexWebSocketProducerTest {

    ApexWebSocketProducer apexWebSocketProducer = null;
    EventHandlerParameters producerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexEventConsumer apexWebSocketConsumer = null;
    WebSocketCarrierTechnologyParameters webSocketCarrierTechnologyParameters = null;
    SynchronousEventCache synchronousEventCache = null;
    private static final long DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT = 1000;

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @Before
    public void setUp() throws Exception {
        apexWebSocketConsumer = new ApexWebSocketConsumer();
        producerParameters = new EventHandlerParameters();
        apexWebSocketProducer = new ApexWebSocketProducer();
        apexWebSocketProducer.receiveString("testEventString");
    }

    @After
    public void tearDown() {
        apexWebSocketProducer.stop();
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonWebSocketCarrierTechnologyParameters() throws ApexEventException {
        producerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexWebSocketProducer.init("TestApexWebSocketProducer", producerParameters);
    }

    @Test
    public void testInitWithWebSocketCarrierTechnologyParameters() throws ApexEventException {
        webSocketCarrierTechnologyParameters = new WebSocketCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(webSocketCarrierTechnologyParameters);
        apexWebSocketProducer.init("TestApexWebSocketProducer", producerParameters);
        assertEquals("TestApexWebSocketProducer",apexWebSocketProducer.getName());
    }

    @Test
    public void testGetName() {
        assertNull(apexWebSocketProducer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexWebSocketProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                apexWebSocketConsumer, apexWebSocketProducer);
        apexWebSocketProducer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertNotNull(apexWebSocketProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test(expected = NullPointerException.class)
    public void testSendEvent() throws ApexEventException {
        producerParameters
                .setCarrierTechnologyParameters(new WebSocketCarrierTechnologyParameters() {});
        synchronousEventCache = new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS,
                apexWebSocketConsumer, apexWebSocketProducer, DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT);
        apexWebSocketProducer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS,
                synchronousEventCache);
        apexWebSocketProducer.sendEvent(1000L, "TestApexWebSocketProducer", "apexEvent");
    }

}
