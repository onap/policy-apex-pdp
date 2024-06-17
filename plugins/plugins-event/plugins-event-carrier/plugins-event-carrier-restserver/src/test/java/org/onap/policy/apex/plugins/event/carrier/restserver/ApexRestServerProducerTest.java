/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019-2021, 2024 Nordix Foundation.
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class ApexRestServerProducerTest {

    ApexRestServerProducer apexRestServerProducer = null;
    EventHandlerParameters producerParameters = null;
    ApexRestServerConsumer apexRestServerConsumer = null;
    RestServerCarrierTechnologyParameters restServerCarrierTechnologyParameters = null;
    Random random = new Random();

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @BeforeEach
    void setUp() throws Exception {
        apexRestServerConsumer = new ApexRestServerConsumer();
        producerParameters = new EventHandlerParameters();
        apexRestServerProducer = new ApexRestServerProducer();
    }

    @AfterEach
    void tearDown() {
        apexRestServerProducer.stop();
    }

    @Test
    void testInitWithNonWebSocketCarrierTechnologyParameters() {
        producerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {
        });
        assertThrows(ApexEventException.class, () ->
            apexRestServerProducer.init("TestApexRestServerProducer", producerParameters));
    }

    @Test
    void testInitWithWebSocketCarrierTechnologyParameters() {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        assertThrows(ApexEventException.class, () ->
            apexRestServerProducer.init("TestApexRestServerProducer", producerParameters));
    }


    @Test
    void testInitWithNonDefaultValue() throws NoSuchFieldException,
        SecurityException, IllegalArgumentException, IllegalAccessException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        Field field = RestServerCarrierTechnologyParameters.class.getDeclaredField("host");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "1ocalhost");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("port");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, 65535);
        producerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        assertThrows(ApexEventException.class, () ->
            apexRestServerProducer.init("TestApexRestServerProducer", producerParameters));
    }

    @Test
    void testInitWithSynchronousMode() throws ApexEventException {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(restServerCarrierTechnologyParameters);
        producerParameters.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);
        apexRestServerProducer.init("TestApexRestServerProducer", producerParameters);
        assertEquals("TestApexRestServerProducer", apexRestServerProducer.getName());
    }

    @Test
    void testGetName() {
        assertNull(apexRestServerProducer.getName());
    }

    @Test
    void testGetPeeredReference() {
        assertNull(apexRestServerProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
            apexRestServerConsumer, apexRestServerProducer);
        apexRestServerProducer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR,
            peeredReference);
        assertNotNull(apexRestServerProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testSendEventNotExistingEventToApex() {
        final long executionId = random.nextLong();
        final String eventName = RandomStringUtils.randomAlphabetic(7);
        final Object event = new Object();
        final ApexRestServerConsumer consumer = new ApexRestServerConsumer();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, apexRestServerProducer,
                random.nextInt(1000));

        this.apexRestServerProducer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, cache);

        // test
        this.apexRestServerProducer.sendEvent(executionId, null, eventName, event);

        assertFalse(cache.existsEventFromApex(executionId));
    }

    @Test
    void testSendEvent() {
        final long executionId = random.nextLong();
        final String eventName = RandomStringUtils.randomAlphabetic(7);
        final Object expected = new Object();

        final ApexRestServerConsumer consumer = new ApexRestServerConsumer();
        final SynchronousEventCache cache = new SynchronousEventCache(
            EventHandlerPeeredMode.SYNCHRONOUS,
            consumer,
            apexRestServerProducer,
            10000);

        // Set EventToApex on cache object
        cache.cacheSynchronizedEventToApex(executionId, new Object());

        this.apexRestServerProducer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, cache);

        this.apexRestServerProducer.sendEvent(executionId, null, eventName, expected);
        final Object actual = cache.removeCachedEventFromApexIfExists(executionId);

        assertSame(expected, actual);
    }
}
