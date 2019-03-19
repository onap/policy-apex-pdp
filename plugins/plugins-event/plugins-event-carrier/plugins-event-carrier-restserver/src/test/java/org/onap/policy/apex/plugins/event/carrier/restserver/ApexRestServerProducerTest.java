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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexRestServerProducerTest {

    ApexRestServerProducer apexRestServerProducer = null;
    EventHandlerParameters producerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexRestServerConsumer apexRestServerConsumer = null;
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
        producerParameters = new EventHandlerParameters();
        apexRestServerProducer = new ApexRestServerProducer();
    }

    @After
    public void tearDown() {
        apexRestServerProducer.stop();
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonWebSocketCarrierTechnologyParameters() throws ApexEventException {
        producerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexRestServerProducer.init("TestApexRestServerProducer", producerParameters);
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithWebSocketCarrierTechnologyParameters() throws ApexEventException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        apexRestServerProducer.init("TestApexRestServerProducer", producerParameters);
    }


    @Test(expected = ApexEventException.class)
    public void testInitWithNonDefaultValue() throws ApexEventException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        Field field = RestServerCarrierTechnologyParameters.class.getDeclaredField("host");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "1ocalhost");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("port");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, 65535);
        producerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        apexRestServerProducer.init("TestApexRestServerProducer", producerParameters);
    }

    @Test
    public void testInitWithSynchronousMode() throws ApexEventException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        producerParameters.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);
        apexRestServerProducer.init("TestApexRestServerProducer", producerParameters);
        assertEquals("TestApexRestServerProducer", apexRestServerProducer.getName());
    }

    @Test
    public void testGetName() {
        assertNull(apexRestServerProducer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexRestServerProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                apexRestServerConsumer, apexRestServerProducer);
        apexRestServerProducer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR,
                peeredReference);
        assertNotNull(apexRestServerProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }
}
