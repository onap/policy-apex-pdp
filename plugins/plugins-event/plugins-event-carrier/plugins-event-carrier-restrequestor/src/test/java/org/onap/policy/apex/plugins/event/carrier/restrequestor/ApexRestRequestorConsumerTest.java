/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * Test the ApexRestRequestorConsumer class.
 *
 */
public class ApexRestRequestorConsumerTest {
    // String constants
    private static final String CONSUMER_NAME = "ConsumerName";
    private static final String EVENT_NAME = "EventName";
    private static final String EVENT_BODY = "Event body";

    @Test
    public void testApexRestRequestorConsumerSetup() throws ApexEventException {
        ApexRestRequestorConsumer consumer = new ApexRestRequestorConsumer();
        assertNotNull(consumer);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        ApexEventReceiver incomingEventReceiver = null;

        try {
            consumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("specified consumer properties are not applicable to REST Requestor consumer (ConsumerName)",
                    aee.getMessage());
        }

        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rrctp);
        try {
            consumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("REST Requestor consumer (ConsumerName) must run in peered requestor mode "
                    + "with a REST Requestor producer", aee.getMessage());
        }

        consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setHttpMethod(null);
        try {
            consumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("no URL has been specified on REST Requestor consumer (ConsumerName)", aee.getMessage());
        }

        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        rrctp.setUrl("ZZZZ");
        try {
            consumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);
            fail("test should throw an exception here");
        } catch (ApexEventException aee) {
            assertEquals("invalid URL has been specified on REST Requestor consumer (ConsumerName)", aee.getMessage());
        }

        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        rrctp.setUrl("http://www.onap.org");
        rrctp.setHttpCodeFilter("[1-5][0][0-5]");
        consumerParameters.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 0);

        consumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);

        try {
            consumer.processRestRequest(null);
            fail("test should throw an exception here");
        } catch (Exception ex) {
            assertEquals("could not queue request \"null\" on REST Requestor consumer (ConsumerName)", ex.getMessage());
        }

        assertEquals(CONSUMER_NAME, consumer.getName());
        assertEquals(0, consumer.getEventsReceived());
        assertEquals(null, consumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testApexRestRequestorConsumerRequest() throws ApexEventException {
        ApexRestRequestorConsumer consumer = new ApexRestRequestorConsumer();
        assertNotNull(consumer);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rrctp);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);
        rrctp.setUrl("http://www.onap.org");
        rrctp.setHttpCodeFilter("[1-5][0][0-5]");
        consumerParameters.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 0);

        // Test should time out requests
        consumer.init(CONSUMER_NAME, consumerParameters, null);
        consumer.start();
        ApexRestRequest request = new ApexRestRequest(123, null, EVENT_NAME, EVENT_BODY);
        consumer.processRestRequest(request);
        await().atMost(200, TimeUnit.MILLISECONDS);
        consumer.stop();
        assertEquals(0, consumer.getEventsReceived());
    }

    @Test
    public void testApexRestRequestorConsumerUrlUpdate() throws ApexEventException {
        ApexRestRequestorConsumer consumer = new ApexRestRequestorConsumer();
        assertNotNull(consumer);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rrctp);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);

        rrctp.setUrl("http://www.{site}.{site}.{net}");
        consumerParameters.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 2000);
        Properties properties = new Properties();
        properties.put("site", "onap");
        properties.put("net", "org");

        consumer.init(CONSUMER_NAME, consumerParameters, null);
        consumer.start();
        ApexRestRequest request = new ApexRestRequest(123, properties, EVENT_NAME, EVENT_BODY);
        consumer.processRestRequest(request);
        await().atMost(2000, TimeUnit.MILLISECONDS);
        consumer.stop();
        assertEquals(0, consumer.getEventsReceived());
    }

    @Test
    public void testApexRestRequestorConsumerUrlUpdateError() throws ApexEventException {
        ApexRestRequestorConsumer consumer = new ApexRestRequestorConsumer();
        assertNotNull(consumer);

        EventHandlerParameters consumerParameters = new EventHandlerParameters();
        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(rrctp);
        consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.GET);

        rrctp.setUrl("http://www.{site}.{net}");
        consumerParameters.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 2000);
        Properties properties = new Properties();
        properties.put("site", "onap");

        consumer.init(CONSUMER_NAME, consumerParameters, null);
        consumer.start();
        ApexRestRequest request = new ApexRestRequest(123, properties, EVENT_NAME, EVENT_BODY);
        consumer.processRestRequest(request);
        await().atMost(2000, TimeUnit.MILLISECONDS);
        consumer.stop();
        assertEquals(0, consumer.getEventsReceived());
    }
}
