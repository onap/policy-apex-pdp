/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2020 Nordix Foundation.
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
package org.onap.policy.apex.core.infrastructure.messaging;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;

public class MessagingUtilsTest {

    @Test
    public void testCheckPort() throws UnknownHostException, IOException {
        assertEquals(1,MessagingUtils.checkPort(1));
        assertEquals(1,MessagingUtils.findPort(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentException() {
        assertEquals(1,MessagingUtils.findPort(65536));
    }

    @Test
    public void testGetHost() throws UnknownHostException {
        InetAddress host = InetAddress.getLocalHost();
        assertEquals(host,MessagingUtils.getHost());
    }

    @Test
    public void testValidAllocateAddress() throws UnknownHostException {
        assertNotNull(MessagingUtils.getLocalHostLanAddress());
        assertEquals(3306,MessagingUtils.allocateAddress(3306));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAllocateAddress() {
        assertEquals(1,MessagingUtils.allocateAddress(1));
    }

    @Test
    public void testSerializeObject() {
        String testString = "Test";
        MessagingUtils.serializeObject(new Object());
        assertNotNull(MessagingUtils.serializeObject(testString));
    }
}
