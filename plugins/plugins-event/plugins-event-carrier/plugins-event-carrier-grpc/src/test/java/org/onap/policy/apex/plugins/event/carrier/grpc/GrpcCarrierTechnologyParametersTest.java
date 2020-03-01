/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.grpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;

public class GrpcCarrierTechnologyParametersTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String HOST = "localhost";

    private GrpcCarrierTechnologyParameters params;

    @Before
    public void setUp() {
        params = new GrpcCarrierTechnologyParameters();
    }

    @Test
    public void testGrpcCarrierTechnologyParameters_invalid() {
        GroupValidationResult result = params.validate();
        assertFalse(result.isValid());
        assertTrue(result.getResult().contains("field \"timeout\" type \"int\" value \"0\" INVALID, must be >= 1"));
        assertTrue(result.getResult().contains("field \"port\" type \"int\" value \"0\" INVALID, must be >= 1024"));
        assertTrue(
            result.getResult().contains("field \"host\" type \"java.lang.String\" value \"null\" INVALID, is null"));
        assertTrue(result.getResult()
            .contains("field \"username\" type \"java.lang.String\" value \"null\" INVALID, is null"));
        assertTrue(result.getResult()
            .contains("field \"password\" type \"java.lang.String\" value \"null\" INVALID, is null"));
        assertTrue(result.getResult().contains(""));
        assertTrue(result.getResult().contains(""));
    }

    @Test
    public void testGrpcCarrierTechnologyParameters_valid() {
        assertEquals("GRPC", params.getName());
        assertEquals(ApexGrpcConsumer.class.getName(), params.getEventConsumerPluginClass());
        assertEquals(ApexGrpcProducer.class.getName(), params.getEventProducerPluginClass());

        params.setHost(HOST);
        params.setPassword(PASSWORD);
        params.setPort(2233);
        params.setTimeout(1000);
        params.setUsername(USERNAME);
        GroupValidationResult result = params.validate();
        assertTrue(result.isValid());
    }

    @Test
    public void testGrpcCarrierTechnologyParameters_invalid_values() {
        params.setHost(HOST);
        params.setPassword(PASSWORD);
        params.setTimeout(1000);
        params.setUsername(USERNAME);

        params.setPort(23); // invalid value
        GroupValidationResult result = params.validate();
        assertFalse(result.isValid());
        assertTrue(result.getResult().contains("field \"port\" type \"int\" value \"23\" INVALID, must be >= 1024"));
    }
}
