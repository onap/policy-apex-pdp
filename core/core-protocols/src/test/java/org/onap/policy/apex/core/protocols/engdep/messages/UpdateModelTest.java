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
 * The Class UpdateModelTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class UpdateModelTest {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(UpdateModelTest.class);

    UpdateModel message = null;

    /**
     * Test register entity.
     *
     * @throws UnknownHostException the unknown host exception
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testRegisterEntity() throws UnknownHostException {
        assertNotNull(new UpdateModel(new AxArtifactKey()));
        final AxArtifactKey targetKey = new AxArtifactKey("UpdateModelTest", "0.0.1");
        message = new UpdateModel(targetKey, new String("Placeholder for Apex model XML"), false, false);
        assertNotNull(message);
        logger.debug(message.toString());
        assertTrue((message.toString()).contains("Placeholder for Apex model XML"));
        assertFalse(message.isIgnoreConflicts());
        assertFalse(message.isForceInstall());

        message = new UpdateModel(null, null, false, false);
        assertNotEquals(0, message.hashCode());
        message = new UpdateModel(null, null, true, false);
        assertNotEquals(0, message.hashCode());
        message = new UpdateModel(null, null, true, true);
        assertNotEquals(0, message.hashCode());
        message = new UpdateModel(null, null, false, true);
        assertNotEquals(0, message.hashCode());

        assertNotNull(message);
        assertNotEquals(message, new StartEngine(new AxArtifactKey()));

        message = new UpdateModel(null, null, false, false);
        UpdateModel otherMessage = new UpdateModel(null, null, false, false);
        assertEquals(message, otherMessage);
        message = new UpdateModel(null, null, true, false);
        assertNotEquals(message, otherMessage);
        otherMessage = new UpdateModel(null, null, true, false);
        assertEquals(message, otherMessage);
        message = new UpdateModel(null, null, false, true);
        assertNotEquals(message, otherMessage);
        otherMessage = new UpdateModel(null, null, false, true);
        assertEquals(message, otherMessage);
    }
}
