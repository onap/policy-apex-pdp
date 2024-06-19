/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
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

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer.ApexFileEventProducer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class EventRequestorConsumerTest {
    private EventRequestorConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new EventRequestorConsumer();
    }

    @Test
    void initNoCarrierTechnologyParameters() {
        final String consumerName = RandomStringUtils.randomAlphabetic(6);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();

        assertThatThrownBy(() -> consumer.init(consumerName, eventHandlerParameters, null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void initNoPeered() {
        final String consumerName = RandomStringUtils.randomAlphabetic(6);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();
        eventHandlerParameters.setCarrierTechnologyParameters(new EventRequestorCarrierTechnologyParameters());

        assertThatThrownBy(() -> consumer.init(consumerName, eventHandlerParameters, null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void getName() throws ApexEventException {
        final String consumerName = RandomStringUtils.randomAlphabetic(6);
        final EventHandlerParameters eventHandlerParameters = new EventHandlerParameters();
        eventHandlerParameters.setCarrierTechnologyParameters(new EventRequestorCarrierTechnologyParameters());
        eventHandlerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);

        consumer.init(consumerName, eventHandlerParameters, null);
        final String actual = consumer.getName();

        assertEquals(consumerName, actual);
    }

    @Test
    void getSetPeeeredReference() {
        final PeeredReference peeredReference =
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, new ApexFileEventConsumer(),
                new ApexFileEventProducer());
        consumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);

        final PeeredReference actual = consumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR);

        assertEquals(peeredReference, actual);
    }
}