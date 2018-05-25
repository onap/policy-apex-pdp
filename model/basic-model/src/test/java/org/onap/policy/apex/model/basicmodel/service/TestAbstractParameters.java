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

package org.onap.policy.apex.model.basicmodel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestAbstractParameters {

    @Test
    public void testAbstractParameters() {
        final LegalParameters parameters = new LegalParameters();
        assertNotNull(parameters);
        assertEquals(
                "AbstractParameters [parameterClassName=org.onap.policy.apex.model.basicmodel.service.LegalParameters]",
                parameters.toString());

        assertEquals(LegalParameters.class, parameters.getParameterClass());
        assertEquals("org.onap.policy.apex.model.basicmodel.service.LegalParameters",
                parameters.getParameterClassName());

        try {
            new IllegalParameters();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "class \"somewhere.over.the.rainbow\" not found or not an instance of \"org.onap.policy.apex.model.basicmodel.service.IllegalParameters\"",
                    e.getMessage());
        }
    }
}
