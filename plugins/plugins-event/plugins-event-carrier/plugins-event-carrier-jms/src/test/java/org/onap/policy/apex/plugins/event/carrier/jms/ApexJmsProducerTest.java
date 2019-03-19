/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.jms;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.jms.Session;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexJmsProducerTest {

    ApexJmsConsumer apexJmsConsumer = null;
    EventHandlerParameters producerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexEventProducer apexJmsProducer = null;
    Session jmsSession = null;
    JmsCarrierTechnologyParameters jmsCarrierTechnologyParameters = null;
    SynchronousEventCache synchronousEventCache = null;
    private static final long DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT = 1000;

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @Before
    public void setUp() throws Exception {
        apexJmsConsumer = new ApexJmsConsumer();
        producerParameters = new EventHandlerParameters();
        apexJmsProducer = new ApexJmsProducer();
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonJmsCarrierTechnologyParameters() throws ApexEventException {
        producerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexJmsProducer.init("TestApexJmsProducer", producerParameters);
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithJmsCarrierTechnologyParameters() throws ApexEventException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        apexJmsProducer.init("TestApexJmsProducer", producerParameters);
    }

    @Test
    public void testGetName() {
        assertNull(apexJmsProducer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexJmsProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                apexJmsConsumer, apexJmsProducer);
        apexJmsProducer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertNotNull(apexJmsProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test(expected = NullPointerException.class)
    public void testSendEvent() throws ApexEventException {
        producerParameters.setCarrierTechnologyParameters(new JmsCarrierTechnologyParameters() {});
        synchronousEventCache = new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS,
                apexJmsConsumer, apexJmsProducer, DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT);
        apexJmsProducer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS,
                synchronousEventCache);
        ApexEvent apexEvent = new ApexEvent("testEvent", "testVersion", "testNameSpace",
                "testSource", "testTarget");
        apexJmsProducer.sendEvent(1000L, "TestApexJmsProducer", apexEvent);
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void testSendEventWithNonSerializableObject() throws ApexEventException {
        producerParameters.setCarrierTechnologyParameters(new JmsCarrierTechnologyParameters() {});
        synchronousEventCache = new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS,
                apexJmsConsumer, apexJmsProducer, DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT);
        apexJmsProducer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS,
                synchronousEventCache);
        apexJmsProducer.sendEvent(-1L, "TestApexJmsProducer", new ApexJmsProducerTest());
    }

    @Test
    public void testStop() {
        apexJmsProducer.stop();
    }
}
