/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.core.protocols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test of the abstract Message class.
 */
public class SupportMessageTester {

    @Test
    public void testMessage() {
        assertNotNull(new DummyMessage(new DummyAction(null), new AxArtifactKey()));
        assertNotNull(new DummyMessage(new DummyAction(null), new AxArtifactKey(), "Message Data"));

        DummyMessage dummyMessage = new DummyMessage(new DummyAction(null), new AxArtifactKey("Target:0.0.1"));
        assertEquals(new DummyAction(null), dummyMessage.getAction());
        assertEquals("Message(action=org.onap.policy.apex.core.protocols.DummyAction@1f, "
            + "targetKey=AxArtifactKey:(name=Target,version=0.0.1), messageData=null)", dummyMessage.toString());

        dummyMessage.setMessageData("Message Data");
        assertEquals("Message Data", dummyMessage.getMessageData());
        dummyMessage.appendMessageData("\nMore Message Data");
        assertEquals("Message Data\nMore Message Data", dummyMessage.getMessageData());
        dummyMessage.setMessageData(null);
        dummyMessage.appendMessageData("\nMore Message Data");
        assertEquals("\nMore Message Data", dummyMessage.getMessageData());

        dummyMessage.setReplyTimeout(123);
        assertEquals(123, dummyMessage.getReplyTimeout());
        assertEquals(new AxArtifactKey("Target:0.0.1"), dummyMessage.getTarget());
        assertEquals("Target", dummyMessage.getTargetName());

        assertNotEquals(0, dummyMessage.hashCode());
        dummyMessage.setMessageData(null);
        assertNotEquals(0, dummyMessage.hashCode());
        dummyMessage = new DummyMessage(null, null, null);
        assertNotEquals(0, dummyMessage.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(dummyMessage, dummyMessage); // NOSONAR
        assertNotNull(dummyMessage);

        dummyMessage = new DummyMessage(new DummyAction(null), null, null);
        DummyMessage otherDummyMessage = new DummyMessage(null, null, null);
        assertNotEquals(dummyMessage, otherDummyMessage);
        otherDummyMessage = new DummyMessage(new DummyAction(null), null, null);
        assertEquals(dummyMessage, otherDummyMessage);
        dummyMessage = new DummyMessage(null, null, null);
        assertNotEquals(dummyMessage, otherDummyMessage);
        otherDummyMessage = new DummyMessage(null, null, null);
        assertEquals(dummyMessage, otherDummyMessage);

        dummyMessage = new DummyMessage(null, new AxArtifactKey(), null);
        otherDummyMessage = new DummyMessage(null, null, null);
        assertNotEquals(dummyMessage, otherDummyMessage);
        otherDummyMessage = new DummyMessage(null, new AxArtifactKey(), null);
        assertEquals(dummyMessage, otherDummyMessage);
        dummyMessage = new DummyMessage(null, null, null);
        assertNotEquals(dummyMessage, otherDummyMessage);
        otherDummyMessage = new DummyMessage(null, null, null);
        assertEquals(dummyMessage, otherDummyMessage);

        dummyMessage = new DummyMessage(null, null, "Message");
        otherDummyMessage = new DummyMessage(null, null, null);
        assertNotEquals(dummyMessage, otherDummyMessage);
        otherDummyMessage = new DummyMessage(null, null, "Message");
        assertEquals(dummyMessage, otherDummyMessage);
        dummyMessage = new DummyMessage(null, null, null);
        assertNotEquals(dummyMessage, otherDummyMessage);
        otherDummyMessage = new DummyMessage(null, null, null);
        assertEquals(dummyMessage, otherDummyMessage);
    }
}
