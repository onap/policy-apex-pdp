/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.service.engine.event.impl.eventrequestor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

@ExtendWith(MockitoExtension.class)
class EventRequestorProducerTest {

    private final Random random = new Random();
    private EventRequestorProducer producer;

    @Mock
    private ApexEventProducer apexProducer;
    @Mock
    private EventRequestorConsumer apexConsumer;

    @BeforeEach
    void setUp() {
        producer = new EventRequestorProducer();
    }

    @Test
    void initWithEmptyParams() {
        final String producerName = RandomStringUtils.random(4);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();

        assertThatThrownBy(() -> producer.init(producerName, eventHandlerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void initNotPeered() {
        final String producerName = RandomStringUtils.random(4);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();
        eventHandlerParameters.setCarrierTechnologyParameters(new EventRequestorCarrierTechnologyParameters());

        assertThatThrownBy(() -> producer.init(producerName, eventHandlerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void getName() throws ApexEventException {
        final String expected = RandomStringUtils.random(4);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();
        eventHandlerParameters.setCarrierTechnologyParameters(new EventRequestorCarrierTechnologyParameters());
        eventHandlerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);

        producer.init(expected, eventHandlerParameters);
        final String actual = producer.getName();

        assertEquals(expected, actual);
    }

    @Test
    void getSetPeeredReference() {
        final PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, apexConsumer,
            apexProducer);
        producer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, peeredReference);

        final PeeredReference actual = this.producer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS);
        assertEquals(peeredReference, actual);
    }

    @Test
    void sendEventNoRequestor() {
        final int id = random.nextInt(1000);

        assertThatThrownBy(() -> producer.sendEvent(id, null, null, null))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void sendEventNoEventRequestorConsumer() {
        final int id = random.nextInt(1000);

        final ApexFileEventConsumer fileEventConsumer = Mockito.mock(ApexFileEventConsumer.class);

        final PeeredReference reference =
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, fileEventConsumer, apexProducer);

        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, reference);

        assertThatThrownBy(() -> producer.sendEvent(id, null, null, null))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void sendEvent() {
        final int id = random.nextInt(1000);

        final PeeredReference reference =
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, apexConsumer, apexProducer);
        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, reference);

        producer.sendEvent(id, null, null, null);
        Mockito.verify(apexConsumer, Mockito.times(1)).processEvent(any());
    }

    @Test
    void sendEventCached() {
        final int id = random.nextInt(1000);

        // Set event cache
        final SynchronousEventCache eventCache = Mockito.mock(SynchronousEventCache.class);
        producer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, eventCache);

        final PeeredReference reference =
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, apexConsumer, apexProducer);
        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, reference);

        producer.sendEvent(id, null, null, null);
        Mockito.verify(apexConsumer, Mockito.times(1)).processEvent(any());
        Mockito.verify(eventCache, Mockito.times(1)).removeCachedEventToApexIfExists(id);
    }
}
