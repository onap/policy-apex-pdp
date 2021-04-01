/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021  Nordix Foundation
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

import static org.junit.Assert.assertEquals;

import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class EventRequestorProducerTest {
    private final Random random = new Random();
    private EventRequestorProducer producer;

    @Mock
    private ApexEventProducer apexProducer;
    @Mock
    private EventRequestorConsumer apexConsumer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        producer = new EventRequestorProducer();
    }

    @Test(expected = ApexEventException.class)
    public void initWithEmptyParams() throws ApexEventException {
        final String producerName = RandomStringUtils.random(4);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();
        producer.init(producerName, eventHandlerParameters);
    }

    @Test(expected = ApexEventException.class)
    public void initNotPeered() throws ApexEventException {
        final String producerName = RandomStringUtils.random(4);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();
        eventHandlerParameters.setCarrierTechnologyParameters(new EventRequestorCarrierTechnologyParameters());
        producer.init(producerName, eventHandlerParameters);
    }

    @Test
    public void getName() throws ApexEventException {
        final String expected = RandomStringUtils.random(4);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();
        eventHandlerParameters.setCarrierTechnologyParameters(new EventRequestorCarrierTechnologyParameters());
        eventHandlerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);

        producer.init(expected, eventHandlerParameters);
        final String actual = producer.getName();

        assertEquals(expected, actual);
    }

    @Test
    public void getSetPeeredReference() {
        final PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, apexConsumer,
            apexProducer);
        producer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, peeredReference);

        final PeeredReference actual = this.producer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS);
        assertEquals(peeredReference, actual);
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void sendEventNoRequestor() {
        final int id = random.nextInt(1000);

        producer.sendEvent(id, null, null, null);
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void sendEventNoEventRequestorConsumer() {
        final int id = random.nextInt(1000);

        final ApexFileEventConsumer fileEventConsumer = Mockito.mock(ApexFileEventConsumer.class);

        final PeeredReference reference =
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, fileEventConsumer, apexProducer);

        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, reference);

        producer.sendEvent(id, null, null, null);
    }

    @Test
    public void sendEvent() {
        final int id = random.nextInt(1000);
        // Prepare mocks
        final PeeredReference peeredReference = Mockito.mock(PeeredReference.class);

        Mockito.when(apexConsumer.getPeeredReference(Matchers.any())).thenReturn(peeredReference);
        Mockito.when(peeredReference.getPeeredConsumer()).thenReturn(apexConsumer);

        final PeeredReference reference =
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, apexConsumer, apexProducer);
        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, reference);

        producer.sendEvent(id, null, null, null);
        Mockito.verify(apexConsumer, Mockito.times(1)).processEvent(Matchers.any());
    }

    @Test
    public void sendEventCached() {
        final int id = random.nextInt(1000);

        // Set event cache
        final SynchronousEventCache eventCache = Mockito.mock(SynchronousEventCache.class);
        producer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, eventCache);

        // Prepare other mocks
        final PeeredReference peeredReference = Mockito.mock(PeeredReference.class);

        Mockito.when(peeredReference.getPeeredConsumer()).thenReturn(apexConsumer);
        Mockito.when(apexConsumer.getPeeredReference(Matchers.any())).thenReturn(peeredReference);

        final PeeredReference reference =
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, apexConsumer, apexProducer);
        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, reference);

        producer.sendEvent(id, null, null, null);
        Mockito.verify(apexConsumer, Mockito.times(1)).processEvent(Matchers.any());
        Mockito.verify(eventCache, Mockito.times(1)).removeCachedEventToApexIfExists(id);
    }
}