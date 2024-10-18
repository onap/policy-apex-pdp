/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.engine.event;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer.ApexFileEventProducer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class ApexPluginsEventConsumerTest {

    ApexPluginsEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ApexPluginsEventConsumer() {
            @Override
            public void run() {
                // do nothing
            }

            @Override
            public void init(String name, EventHandlerParameters consumerParameters,
                             ApexEventReceiver apexEventReceiver) throws ApexEventException {
                // do nothing
            }

            @Override
            public void stop() {
                // do nothing
            }
        };
    }

    @Test
    void testStart() {
        assertThatCode(() -> consumer.start()).doesNotThrowAnyException();
    }

    @Test
    void testGetPeeredReference() {
        EventHandlerPeeredMode mode = EventHandlerPeeredMode.REQUESTOR;
        assertNull(consumer.getPeeredReference(mode));

        ApexPluginsEventProducer producer = new ApexFileEventProducer();
        PeeredReference reference = new PeeredReference(mode, consumer, producer);
        consumer.setPeeredReference(mode, reference);
        assertEquals(producer, consumer.getPeeredReference(mode).getPeeredProducer());
        assertEquals(consumer, consumer.getPeeredReference(mode).getPeeredConsumer());
    }
}
