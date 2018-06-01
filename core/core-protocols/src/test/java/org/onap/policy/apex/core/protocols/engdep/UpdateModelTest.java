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

package org.onap.policy.apex.core.protocols.engdep;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;

import org.junit.Test;
import org.onap.policy.apex.core.protocols.engdep.messages.UpdateModel;
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
    @Test
    public void testRegisterEntity() throws UnknownHostException {
        final AxArtifactKey targetKey = new AxArtifactKey("UpdateModelTest", "0.0.1");
        message = new UpdateModel(targetKey, new String("Placeholder for Apex model XML"), false, false);
        assertNotNull(message);
        logger.debug(message.toString());
        assertTrue((message.toString()).contains("Placeholder for Apex model XML"));
    }
}
