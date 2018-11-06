/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.websocket;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;

public class TestWs2WsClient {
    private static final long MAX_TEST_LENGTH = 10000;

    private static final int EVENT_COUNT = 100;
    private static final int EVENT_INTERVAL = 20;

    /**
     * Clear relative file root environment variable.
     */
    @Before
    public void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    @Test
    public void testJsonWsEvents() throws MessagingException, ApexException {
        final String[] args = {"-rfr", "target", "-c", "target/examples/config/SampleDomain/Ws2WsClientJsonEvent.json"};
        testWsEvents(args, false);
    }

    @Test
    public void testXmlWsEvents() throws MessagingException, ApexException {
        final String[] args = {"-rfr", "target", "-c", "target/examples/config/SampleDomain/Ws2WsClientXMLEvent.json"};
        testWsEvents(args, true);
    }

    private void testWsEvents(final String[] args, final Boolean xmlEvents) throws MessagingException, ApexException {
        final WebSocketEventSubscriberServer subServer = new WebSocketEventSubscriberServer(42453);
        final WebSocketEventProducerServer prodServer =
                new WebSocketEventProducerServer(42451, EVENT_COUNT, xmlEvents, EVENT_INTERVAL);

        final ApexMain apexMain = new ApexMain(args);

        prodServer.sendEvents();

        final long testStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < testStartTime + MAX_TEST_LENGTH
                && subServer.getEventsReceivedCount() < EVENT_COUNT) {
            ThreadUtilities.sleep(EVENT_INTERVAL);
        }

        assertEquals(prodServer.getEventsSentCount(), subServer.getEventsReceivedCount());

        apexMain.shutdown();
        prodServer.shutdown();
        subServer.shutdown();
    }
}
