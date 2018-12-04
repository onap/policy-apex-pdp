/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
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
public class ApexRestRequestorProducerTest {

    @Test
    public void testApexRestRequestorProducerMethods() {
        ApexRestRequestorProducer producer = new ApexRestRequestorProducer();
        assertNotNull(producer);

        String producerName = "ProducerName";
        EventHandlerParameters producerParameters = new EventHandlerParameters();

        try {
            producer.init(producerName, producerParameters);
        } catch (ApexEventException aee) {
            assertEquals("specified producer properties are not applicable to REST requestor producer (ProducerName)",
                            aee.getMessage());
        }

        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rrctp);
        try {
            producer.init(producerName, producerParameters);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("REST Requestor producer (ProducerName) must run in peered requestor mode "
                            + "with a REST Requestor consumer", aee.getMessage());
        }

        producerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setUrl("ZZZZ");
        try {
            producer.init(producerName, producerParameters);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("URL may not be specified on REST Requestor producer (ProducerName)", aee.getMessage());
        }

        rrctp.setUrl(null);
        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        try {
            producer.init(producerName, producerParameters);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("HTTP method may not be specified on REST Requestor producer (ProducerName)",
                            aee.getMessage());
        }

        rrctp.setHttpMethod(null);
        try {
            producer.init(producerName, producerParameters);
            producer.stop();
        } catch (ApexEventException aee) {
            fail("test should not throw an exception here");
        }

        assertEquals("ProducerName", producer.getName());
        assertEquals(0, producer.getEventsSent());
        assertEquals(null, producer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testApexRestRequestorProducerRequest() {
        ApexRestRequestorProducer producer = new ApexRestRequestorProducer();

        String producerName = "ProducerName";
        EventHandlerParameters producerParameters = new EventHandlerParameters();

        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(rrctp);
        producerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setUrl(null);
        rrctp.setHttpMethod(null);

        try {
            producer.init(producerName, producerParameters);
            producer.stop();
        } catch (ApexEventException aee) {
            fail("test should not throw an exception here");
        }

        String eventName = "EventName";
        String event = "This is the event";

        try {
            producer.sendEvent(12345, eventName, event);
            fail("test should throw an exception here");
        } catch (Exception aee) {
            assertEquals("send of event to URL \"null\" failed, REST response consumer is not defined\n"
                            + "This is the event", aee.getMessage());
        }

        ApexEventConsumer consumer = new ApexFileEventConsumer();
        SynchronousEventCache eventCache = new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer,
                        producer, 1000);
        producer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS, eventCache);

        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR, consumer, producer);
        producer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        try {
            producer.sendEvent(12345, eventName, event);
            fail("test should throw an exception here");
        } catch (Exception aee) {
            assertEquals("send of event to URL \"null\" failed, REST response consumer "
                            + "is not an instance of ApexRestRequestorConsumer\n" + "This is the event",
                            aee.getMessage());
        }
    }
}
