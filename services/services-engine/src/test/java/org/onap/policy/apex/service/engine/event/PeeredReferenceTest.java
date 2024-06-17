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

package org.onap.policy.apex.service.engine.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer.ApexFileEventProducer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class PeeredReferenceTest {

    @Test
    void getPeeredConsumer() {
        final ApexFileEventConsumer eventConsumer = new ApexFileEventConsumer();
        final ApexFileEventProducer eventProducer = new ApexFileEventProducer();
        final EventHandlerPeeredMode peeredMode = EventHandlerPeeredMode.REQUESTOR;
        final PeeredReference peeredReference =
            new PeeredReference(peeredMode, eventConsumer, eventProducer);

        final ApexEventConsumer actual = peeredReference.getPeeredConsumer();
        assertNotNull(actual);
        assertEquals(peeredReference, actual.getPeeredReference(peeredMode));
    }

    @Test
    void getPeeredProducer() {
        final ApexEventConsumer eventConsumer = new ApexFileEventConsumer();
        final ApexEventProducer eventProducer = new ApexFileEventProducer();
        final EventHandlerPeeredMode peeredMode = EventHandlerPeeredMode.SYNCHRONOUS;
        final PeeredReference reference =
            new PeeredReference(peeredMode, eventConsumer, eventProducer);

        final ApexEventProducer actual = reference.getPeeredProducer();
        assertNotNull(actual);
        assertEquals(reference, actual.getPeeredReference(peeredMode));
    }
}