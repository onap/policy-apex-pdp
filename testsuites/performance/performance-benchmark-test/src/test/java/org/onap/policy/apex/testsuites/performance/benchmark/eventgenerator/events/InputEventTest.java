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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test the InputEvent class.
 *
 */
public class InputEventTest {

    @Test
    public void testInputEvent() {
        InputEvent ie = new InputEvent();
        assertNotNull(ie);

        ie.setName("EventName");
        assertEquals("EventName", ie.getName());

        ie.setNameSpace("a.b.c.d");
        assertEquals("a.b.c.d", ie.getNameSpace());

        ie.setSource("Source");
        assertEquals("Source", ie.getSource());

        ie.setTarget("Target");
        assertEquals("Target", ie.getTarget());

        ie.setTestMatchCase(123);
        assertEquals(123, ie.getTestMatchCase());

        ie.setTestSlogan("A Slogan");
        assertEquals("A Slogan", ie.getTestSlogan());

        ie.setTestTemperature(123.45);
        assertEquals((Double)123.45, (Double)ie.getTestTemperature());

        ie.setTestTimestamp(1234567879);
        assertEquals(1234567879, ie.getTestTimestamp());

        ie.setVersion("1.2.3");
        assertEquals("1.2.3", ie.getVersion());

        assertEquals("\"nameSpace\": \"a.b.c.d\",", ie.asJson().substring(4, 27));
    }
}
