/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.kafka;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;

public class KafkaCarrierTechnologyParametersTest {

    KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = null;
    Properties kafkaProducerProperties = null;
    Properties kafkaConsumerProperties = null;
    GroupValidationResult result = null;

    /**
     * Set up testing.
     *
     * @throws Exception on setup errors
     */
    @Before
    public void setUp() throws Exception {
        kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();
        kafkaProducerProperties = kafkaCarrierTechnologyParameters.getKafkaProducerProperties();
        kafkaConsumerProperties = kafkaCarrierTechnologyParameters.getKafkaConsumerProperties();
    }

    @Test
    public void testKafkaCarrierTechnologyParameters() {
        assertNotNull(kafkaCarrierTechnologyParameters);
    }

    @Test
    public void testGetKafkaProducerProperties() {
        assertNotNull(kafkaProducerProperties);
    }

    @Test
    public void testGetKafkaConsumerProperties() {
        assertNotNull(kafkaConsumerProperties);
    }

    @Test
    public void testValidate() {
        result = kafkaCarrierTechnologyParameters.validate();
        assertNotNull(result);
    }
}
