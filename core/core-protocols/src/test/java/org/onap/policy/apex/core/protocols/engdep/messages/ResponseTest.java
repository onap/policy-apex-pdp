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

package org.onap.policy.apex.core.protocols.engdep.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ResponseTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ResponseTest {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(ResponseTest.class);

    /**
     * Test response.
     *
     * @throws UnknownHostException the unknown host exception
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testResponse() throws UnknownHostException {
        final AxArtifactKey responseKey = new AxArtifactKey("ResponseTest", "0.0.1");
        final AxArtifactKey responseToKey = new AxArtifactKey("ResponseTestTo", "0.0.1");
        UpdateModel responseTo = new UpdateModel(responseToKey);
        
        Response message = new Response(responseKey, false, responseTo);
        logger.debug(message.toString());
        assertTrue(message.toString().contains("ResponseTest"));
        assertFalse(message.isSuccessful());

        message = new Response(responseKey, true, responseTo);
        logger.debug(message.toString());
        assertTrue(message.toString().contains("ResponseTest"));
        assertTrue(message.isSuccessful());
        assertEquals(responseTo, message.getResponseTo());

        message = new Response(null, false, null);
        assertNotEquals(0, message.hashCode());
        message = new Response(responseKey, false, null);
        assertNotEquals(0, message.hashCode());
        message = new Response(responseKey, true, null);
        assertNotEquals(0, message.hashCode());
        message = new Response(responseKey, true, new UpdateModel(null));
        assertNotEquals(0, message.hashCode());

        assertEquals(message, message);
        assertNotNull(message);
        assertNotEquals(message, new StartEngine(new AxArtifactKey()));

        message = new Response(null, false, responseTo);
        Response otherMessage = new Response(null, false, null);
        assertNotEquals(message, otherMessage);
        otherMessage = new Response(null, false, responseTo);
        assertEquals(message, otherMessage);
        message = new Response(null, false, null);
        assertNotEquals(message, otherMessage);
        otherMessage = new Response(null, false, null);
        assertEquals(message, (otherMessage));

        message = new Response(null, false, null);
        otherMessage = new Response(null, true, null);
        assertNotEquals(message, otherMessage);
        otherMessage = new Response(null, false, null);
        assertEquals(message, otherMessage);
    }
}
