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
 * Test the OutputEvent class.
 *
 */
public class OutputEventTest {

    @Test
    public void test() {
        OutputEvent oe = new OutputEvent();
        assertNotNull(oe);

        oe.setTestMatchCaseSelected(32112);
        assertEquals(32112, oe.getTestMatchCaseSelected());

        oe.setTestMatchStateTime(34455778822L);
        assertEquals(34455778822L, oe.getTestMatchStateTime());

        oe.setTestEstablishCaseSelected(1321);
        assertEquals(1321, oe.getTestEstablishCaseSelected());

        oe.setTestEstablishStateTime(13445566778822L);
        assertEquals(13445566778822L, oe.getTestEstablishStateTime());
        
        oe.setTestDecideCaseSelected(321);
        assertEquals(321, oe.getTestDecideCaseSelected());

        oe.setTestDecideStateTime(3445566778822L);
        assertEquals(3445566778822L, oe.getTestDecideStateTime());

        oe.setTestActCaseSelected(332);
        assertEquals(332, oe.getTestActCaseSelected());

        oe.setTestActStateTime(34455667788L);
        assertEquals(34455667788L, oe.getTestActStateTime());

        oe.setTestReceviedTimestamp(134455667788222L);
        assertEquals(134455667788222L, oe.getTestReceviedTimestamp());
        
        oe.setTestSlogan("0-0: Whatever");
        assertEquals(0, oe.findBatchNumber());
        assertEquals(0, oe.findEventNumber());
    }
}
