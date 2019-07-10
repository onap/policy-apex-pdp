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
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * Test the ApexRestRequestorConsumer class.
 *
 */
public class ApexRestRequestorConsumerTest {
    @Test
    public void testApexRestRequestorConsumerSetup() {
        ApexRestRequestorConsumer consumer = new ApexRestRequestorConsumer();
        assertNotNull(consumer);

        String consumerName = "ConsumerName";

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        ApexEventReceiver incomingEventReceiver = null;

        try {
            consumer.init(consumerName, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("specified consumer properties are not applicable to REST Requestor consumer (ConsumerName)",
                            aee.getMessage());
        }

        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rrctp);
        try {
            consumer.init(consumerName, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("REST Requestor consumer (ConsumerName) must run in peered requestor mode "
                            + "with a REST Requestor producer", aee.getMessage());
        }

        consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setHttpMethod(null);
        try {
            consumer.init(consumerName, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("no URL has been specified on REST Requestor consumer (ConsumerName)", aee.getMessage());
        }

        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        rrctp.setUrl("ZZZZ");
        try {
            consumer.init(consumerName, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("invalid URL has been specified on REST Requestor consumer (ConsumerName)", aee.getMessage());
        }

        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        rrctp.setUrl("http://www.onap.org");
        rrctp.setHttpCodeFilter("[1-5][0][0-5]");
        consumerParameters.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 0);

        try {
            consumer.init(consumerName, consumerParameters, incomingEventReceiver);
        } catch (ApexEventException aee) {
            fail("test should not throw an exception");
        }

        try {
            consumer.processRestRequest(null);
            fail("test should throw an exception here");
        } catch (Exception ex) {
            assertEquals("could not queue request \"null\" on REST Requestor consumer (ConsumerName)",
                            ex.getMessage());
        }

        assertEquals("ConsumerName", consumer.getName());
        assertEquals(0, consumer.getEventsReceived());
        assertEquals(null, consumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testApexRestRequestorConsumerRequest() {
        ApexRestRequestorConsumer consumer = new ApexRestRequestorConsumer();
        assertNotNull(consumer);

        String consumerName = "ConsumerName";

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        ApexEventReceiver incomingEventReceiver = null;
        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rrctp);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        rrctp.setUrl("http://www.onap.org");
        rrctp.setHttpCodeFilter("[1-5][0][0-5]");
        consumerParameters.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 0);

        // Test should time out requests
        try {
            consumer.init(consumerName, consumerParameters, incomingEventReceiver);
            consumer.start();
            ApexRestRequest request = new ApexRestRequest(123, "EventName", "Event body");
            consumer.processRestRequest(request);
            ThreadUtilities.sleep(2000);
            consumer.stop();
            assertEquals(0, consumer.getEventsReceived());
        } catch (ApexEventException aee) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testApexRestRequestorConsumerBadReturnCode() {
        ApexRestRequestorConsumer consumer = new ApexRestRequestorConsumer();
        assertNotNull(consumer);

        String consumerName = "ConsumerName";

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        ApexEventReceiver incomingEventReceiver = null;
        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rrctp);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        rrctp.setUrl("http://www.onap.org");
        rrctp.setHttpCodeFilter("zzz");
        consumerParameters.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 0);

        // test invalid status code
        try {
            consumer.init(consumerName, consumerParameters, incomingEventReceiver);
            consumer.start();
            ApexRestRequest request = new ApexRestRequest(123, "EventName", "Event body");
            consumer.processRestRequest(request);
            ThreadUtilities.sleep(2000);
            consumer.stop();
        } catch (ApexEventException aee) {
            assertEquals("received an invalid status code \"200\"",aee.getMessage());
        }
    }
}