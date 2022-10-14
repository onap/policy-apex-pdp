/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexKafkaConsumerTest {
    ApexKafkaConsumer apexKafkaConsumer = null;
    ApexKafkaConsumer apexKafkaConsumer2 = null;
    EventHandlerParameters consumerParameters = null;
    EventHandlerParameters consumerParameters2 = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexEventProducer apexKafkaProducer = null;
    KafkaCarrierTechnologyParameters kafkaParameters = null;

    /**
     * Set up testing.
     *
     * @throws ApexEventException on test set up errors.
     */
    @Before
    public void setUp() throws ApexEventException {
        apexKafkaConsumer = new ApexKafkaConsumer();
        consumerParameters = new EventHandlerParameters();
        apexKafkaProducer = new ApexKafkaProducer();
        consumerParameters
                .setCarrierTechnologyParameters(new KafkaCarrierTechnologyParameters() {});
        apexKafkaConsumer.init("TestApexKafkaConsumer", consumerParameters, incomingEventReceiver);

        apexKafkaConsumer2 = new ApexKafkaConsumer();
        consumerParameters2 = new EventHandlerParameters();
        kafkaParameters = new KafkaCarrierTechnologyParameters();
        String[][] kafkaProperties = {
            {"value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer"},
            {"schema.registry.url", "[http://test-registory:8080]"}
        };
        kafkaParameters.setKafkaProperties(kafkaProperties);

        consumerParameters2
                .setCarrierTechnologyParameters(kafkaParameters);
        apexKafkaConsumer2.init("TestApexKafkaConsumer2", consumerParameters2, incomingEventReceiver);
    }

    @Test
    public void testStart() {
        assertThatCode(apexKafkaConsumer::start).doesNotThrowAnyException();
        assertThatCode(apexKafkaConsumer2::start).doesNotThrowAnyException();
    }

    @Test
    public void testGetName() {
        assertEquals("TestApexKafkaConsumer", apexKafkaConsumer.getName());
        assertEquals("TestApexKafkaConsumer2", apexKafkaConsumer2.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexKafkaConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
        assertNull(apexKafkaConsumer2.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                apexKafkaConsumer, apexKafkaProducer);
        apexKafkaConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertNotNull(apexKafkaConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));

        PeeredReference peeredReference2 = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                        apexKafkaConsumer2, apexKafkaProducer);
        apexKafkaConsumer2.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference2);
        assertNotNull(apexKafkaConsumer2.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void testRun() {
        apexKafkaConsumer.run();
        apexKafkaConsumer2.run();
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void testStop() {
        apexKafkaConsumer.stop();
        apexKafkaConsumer2.stop();
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonKafkaCarrierTechnologyParameters() throws ApexEventException {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexKafkaConsumer.init("TestApexKafkaConsumer", consumerParameters, incomingEventReceiver);
        apexKafkaConsumer2.init("TestApexKafkaConsumer2", consumerParameters, incomingEventReceiver);
    }

}
