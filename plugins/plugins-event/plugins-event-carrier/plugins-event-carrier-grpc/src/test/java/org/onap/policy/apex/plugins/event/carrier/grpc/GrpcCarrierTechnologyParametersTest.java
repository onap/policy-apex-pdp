/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.grpc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ValidationResult;

class GrpcCarrierTechnologyParametersTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String HOST = "localhost";

    private GrpcCarrierTechnologyParameters params;

    @BeforeEach
    void setUp() {
        params = new GrpcCarrierTechnologyParameters();
    }

    @Test
    void testGrpcCarrierTechnologyParameters_invalid_producer_params() {
        ValidationResult result = params.validate();
        assertTrue(result.isValid());
        assertThatThrownBy(() -> params.validateGrpcParameters(true))
            .hasMessageContaining("Issues in specifying gRPC Producer parameters")
            .hasMessageContaining("timeout should have a positive value")
            .hasMessageContaining("port range should be between 1024 and 65535")
            .hasMessageContaining("host should be specified")
            .hasMessageContaining("username should be specified")
            .hasMessageContaining("password should be specified");
    }

    @Test
    void testGrpcCarrierTechnologyParameters_valid() {
        assertEquals("GRPC", params.getName());
        assertEquals(ApexGrpcConsumer.class.getName(), params.getEventConsumerPluginClass());
        assertEquals(ApexGrpcProducer.class.getName(), params.getEventProducerPluginClass());

        params.setHost(HOST);
        params.setPassword(PASSWORD);
        params.setPort(2233);
        params.setTimeout(1000);
        params.setUsername(USERNAME);
        ValidationResult result = params.validate();
        assertTrue(result.isValid());
        Assertions.assertThatCode(() -> params.validateGrpcParameters(true)).doesNotThrowAnyException();
    }

    @Test
    void testGrpcCarrierTechnologyParameters_invalid_values() {
        params.setHost(HOST);
        params.setPassword(PASSWORD);
        params.setTimeout(1000);
        params.setUsername(USERNAME);

        params.setPort(23); // invalid value
        ValidationResult result = params.validate();
        assertTrue(result.isValid());
        assertThatThrownBy(() -> params.validateGrpcParameters(true))
            .hasMessageContaining("port range should be between 1024 and 65535");
    }
}
