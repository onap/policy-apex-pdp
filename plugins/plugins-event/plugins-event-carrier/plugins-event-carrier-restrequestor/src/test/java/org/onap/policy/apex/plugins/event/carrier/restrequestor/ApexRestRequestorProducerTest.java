/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * Test the ApexRestRequestorProducer class.
 */
class ApexRestRequestorProducerTest {
    // String constants
    private static final String PRODUCER_NAME = "ProducerName";

    @Test
    void testApexRestRequestorProducerMethods() throws ApexEventException {
        ApexRestRequestorProducer producer = new ApexRestRequestorProducer();
        assertNotNull(producer);

        EventHandlerParameters producerParameters = new EventHandlerParameters();

        assertThatThrownBy(() -> producer.init(PRODUCER_NAME, producerParameters))
            .hasMessage("specified producer properties are not applicable to REST requestor producer (ProducerName)");

        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rrctp);
        assertThatThrownBy(() -> producer.init(PRODUCER_NAME, producerParameters))
            .hasMessage("REST Requestor producer (ProducerName) must run in peered requestor mode "
                + "with a REST Requestor consumer");

        producerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setUrl("ZZZZ");
        assertThatThrownBy(() -> producer.init(PRODUCER_NAME, producerParameters))
            .hasMessage("URL may not be specified on REST Requestor producer (ProducerName)");

        rrctp.setUrl(null);
        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);

        assertThatThrownBy(() -> {
            producer.init(PRODUCER_NAME, producerParameters);
            fail("test should throw an exception here");
        }).hasMessage("HTTP method may not be specified on REST Requestor producer (ProducerName)");

        rrctp.setHttpMethod(null);
        producer.init(PRODUCER_NAME, producerParameters);
        producer.stop();

        assertEquals(PRODUCER_NAME, producer.getName());
        assertEquals(0, producer.getEventsSent());
        assertNull(producer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testApexRestRequestorProducerRequest() throws ApexEventException {
        EventHandlerParameters producerParameters = new EventHandlerParameters();

        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rrctp);
        producerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setUrl(null);
        rrctp.setHttpMethod(null);

        ApexRestRequestorProducer producer = new ApexRestRequestorProducer();
        producer.init(PRODUCER_NAME, producerParameters);
        producer.stop();

        String eventName = "EventName";
        String event = "This is the event";

        assertThatThrownBy(() -> producer.sendEvent(12345, null, eventName, event))
            .hasMessage("send of event failed, REST response consumer is not defined\n" + "This is the event");

        ApexEventConsumer consumer = new ApexFileEventConsumer();
        SynchronousEventCache eventCache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, 1000);
        producer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, eventCache);

        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR, consumer, producer);
        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertThatThrownBy(() -> producer.sendEvent(12345, null, eventName, event))
            .hasMessage("send of event failed, REST response consumer "
                + "is not an instance of ApexRestRequestorConsumer\n" + "This is the event");
    }
}
