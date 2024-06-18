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

package org.onap.policy.apex.testsuites.integration.common.testclasses;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Test the ping test class.
 */
class TestPingClassTest {
    @Test
    void testPingClass() throws ApexException {
        PingTestClass ptc = new PingTestClass();

        ptc.setName("Hello");
        assertEquals("Hello", ptc.getName());

        ptc.setDescription("Good Day");
        assertEquals("Good Day", ptc.getDescription());

        ptc.setPingTime(0);
        assertEquals(0, ptc.getPingTime());

        ptc.setPongTime(-1);
        assertEquals(-1, ptc.getPongTime());
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, name does not start with \"Rose\"");

        ptc.setName(null);
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, name length null or less than 4");

        ptc.setName("Ros");
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, name length null or less than 4");

        ptc.setName("Rose");
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, description length null or less than 44");

        ptc.setDescription(null);
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, description length null or less than 44");

        ptc.setDescription("A rose by any other name would smell as swee");
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, description length null or less than 44");

        ptc.setDescription("A rose by any other name would smell as swell");
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, description is incorrect");

        ptc.setDescription("A rose by any other name would smell as sweet");
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, pong time -1 is less than ping time 0");

        ptc.setPongTime(-2);
        assertThatThrownBy(ptc::verify).isInstanceOf(ApexException.class)
            .hasMessageContaining("TestPing is not valid, pong time -2 is less than ping time 0");

        ptc.setPongTime(1);
        ptc.verify();

        assertEquals("PingTestClass(id=0, name=Rose, description=A rose by any other name would smell as sweet, "
            + "pingTime=0, pongTime=1)", ptc.toString());
    }
}
