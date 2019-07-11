/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

import org.junit.Test;

import java.util.Properties;

/**
 * Test the ApexRestRequest class.
 */
public class ApexRestRequestTest {

    @Test
    public void testApexRestRequest() {
        final String eventName = "EventName";
        final String eventString = "The Event String";

        Properties properties = new Properties();
        properties.put("key", "value");
        ApexRestRequest rr = new ApexRestRequest(1, properties, eventName, eventString);

        assertEquals(1, rr.getExecutionId());
        assertEquals(eventName, rr.getEventName());
        assertEquals(eventString, rr.getEvent());
        assertEquals(properties, rr.getExecutionProperties());

        rr.setTimestamp(1234567);
        assertEquals(1234567, rr.getTimestamp());

        assertEquals("ApexRestRequest [executionId=1, eventName=EventName, event=The Event String, timestamp=1234567]",
                        rr.toString());
    }
}
