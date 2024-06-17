/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class ApexKafkaProducerTest {
    ApexKafkaProducer apexKafkaProducer = null;
    ApexKafkaConsumer apexKafkaConsumer = null;
    EventHandlerParameters producerParameters = null;
    PeeredReference peeredReference = null;
    SynchronousEventCache synchronousEventCache = null;
    private static final long DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT = 1000;

    /**
     * Set up testing.
     */
    @BeforeEach
    void setUp() {
        apexKafkaProducer = new ApexKafkaProducer();
        apexKafkaConsumer = new ApexKafkaConsumer();
        producerParameters = new EventHandlerParameters();

    }

    @Test
    void testInit() {
        assertThrows(ApexEventException.class,
            () -> apexKafkaProducer.init("TestApexKafkaProducer", producerParameters));
    }

    @Test
    void testGetName() {
        assertNull(apexKafkaProducer.getName());
    }

    @Test
    void testGetPeeredReference() {
        assertNull(apexKafkaProducer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
    }

    @Test
    void testWithProperValues() throws ApexEventException {
        producerParameters
            .setCarrierTechnologyParameters(new KafkaCarrierTechnologyParameters() { });
        synchronousEventCache = new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS,
            apexKafkaConsumer, apexKafkaProducer, DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT);
        apexKafkaProducer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS,
            synchronousEventCache);
        apexKafkaProducer.init("TestApexKafkaProducer", producerParameters);
        assertEquals("TestApexKafkaProducer", apexKafkaProducer.getName());
        assertNotNull(apexKafkaProducer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
        apexKafkaProducer.stop();
    }

}
