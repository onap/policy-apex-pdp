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
package org.onap.policy.apex.plugins.event.carrier.jms;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.jms.Message;
import javax.jms.Session;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexJmsConsumerTest {

    ApexJmsConsumer apexJmsConsumer = null;
    EventHandlerParameters consumerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexEventProducer apexJmsProducer = null;
    Session jmsSession = null;
    JmsCarrierTechnologyParameters jmsCarrierTechnologyParameters = null;

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @Before
    public void setUp() throws Exception {
        apexJmsConsumer = new ApexJmsConsumer();
        consumerParameters = new EventHandlerParameters();
        apexJmsProducer = new ApexJmsProducer();
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonJmsCarrierTechnologyParameters() throws ApexEventException {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver);
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithJmsCarrierTechnologyParameters() throws ApexEventException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver);
    }

    @Test
    public void testStart() {
        apexJmsConsumer.start();
    }

    @Test
    public void testGetName() {
        assertNull(apexJmsConsumer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexJmsConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                apexJmsConsumer, apexJmsProducer);
        apexJmsConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertNotNull(apexJmsConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void testRun() {
        apexJmsConsumer.run();

    }

    @Test(expected = ApexEventRuntimeException.class)
    public void testOnMessage() {
        Message jmsMessage = null;
        apexJmsConsumer.onMessage(jmsMessage);
    }

    @Test(expected = NullPointerException.class)
    public void testStop() {
        apexJmsConsumer.stop();
    }
}
