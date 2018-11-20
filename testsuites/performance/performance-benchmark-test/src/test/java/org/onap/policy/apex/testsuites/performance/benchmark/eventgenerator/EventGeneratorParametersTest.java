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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGeneratorParameters;

/**
 * Test event generator parameters.
 */
public class EventGeneratorParametersTest {

    @Test
    public void testEventGeneratorParameters() {
        EventGeneratorParameters parameters = new EventGeneratorParameters();

        parameters.setName("TheName");
        assertEquals("TheName", parameters.getName());

        parameters.setHost("TheHost");
        assertEquals("TheHost", parameters.getHost());

        parameters.setPort(12345);
        assertEquals(12345, parameters.getPort());

        assertTrue(parameters.isValid());

        parameters.setName(null);
        assertFalse(parameters.isValid());
        parameters.setName("    ");
        assertFalse(parameters.isValid());
        parameters.setName("TheName");
        assertTrue(parameters.isValid());

        parameters.setHost(null);
        assertFalse(parameters.isValid());
        parameters.setHost("    ");
        assertFalse(parameters.isValid());
        parameters.setHost("TheHost");
        assertTrue(parameters.isValid());

        parameters.setPort(1023);
        assertFalse(parameters.isValid());
        parameters.setPort(65536);
        assertFalse(parameters.isValid());
        parameters.setPort(12345);
        assertTrue(parameters.isValid());
    }
}
