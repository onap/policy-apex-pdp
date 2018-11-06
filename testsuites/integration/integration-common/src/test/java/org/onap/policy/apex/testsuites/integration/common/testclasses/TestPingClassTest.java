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

package org.onap.policy.apex.testsuites.integration.common.testclasses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Test the ping test class.
 */
public class TestPingClassTest {
    @Test
    public void testPingClass() {
        PingTestClass ptc = new PingTestClass();

        ptc.setName("Hello");
        assertEquals("Hello", ptc.getName());

        ptc.setDescription("Good Day");
        assertEquals("Good Day", ptc.getDescription());

        ptc.setPingTime(0);
        assertEquals(0, ptc.getPingTime());

        ptc.setPongTime(-1);
        assertEquals(-1, ptc.getPongTime());

        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, name does not start with \"Rose\"", ae.getMessage());
        }

        ptc.setName(null);
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, name length null or less than 4", ae.getMessage());
        }

        ptc.setName("Ros");
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, name length null or less than 4", ae.getMessage());
        }

        ptc.setName("Rose");
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, description length null or less than 44", ae.getMessage());
        }

        ptc.setDescription(null);
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, description length null or less than 44", ae.getMessage());
        }

        ptc.setDescription("A rose by any other name would smell as swee");
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, description length null or less than 44", ae.getMessage());
        }

        ptc.setDescription("A rose by any other name would smell as swell");
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, description is incorrect", ae.getMessage());
        }

        ptc.setDescription("A rose by any other name would smell as sweet");
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, pong time is not greater than ping time", ae.getMessage());
        }

        ptc.setPongTime(0);
        try {
            ptc.verify();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("TestPing is not valid, pong time is not greater than ping time", ae.getMessage());
        }

        ptc.setPongTime(1);
        try {
            ptc.verify();
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }

        assertEquals("TestPing [name=Rose, description=A rose by any other name would smell as sweet, "
                        + "pingTime=0, pongTime=1]", ptc.toString());
    }
}
