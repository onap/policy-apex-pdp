/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.parameters.carriertechnology;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarrierTechnologyParametersTest {

    CarrierTechnologyParameters parameters;

    @BeforeEach
    void setUp() {
        parameters = new CarrierTechnologyParameters() {
            @Override
            public String getLabel() {
                return super.getLabel();
            }
        };
    }

    @Test
    void testSetLabel() {
        parameters.setLabel("testLabel");
        assertEquals("testLabel", parameters.getLabel());
        assertEquals("testLabel", parameters.getName());

        parameters.setLabel(null);
        assertNull(parameters.getLabel());
    }

    @Test
    void testSetEventProducerPluginClass() {
        parameters.setEventProducerPluginClass("testClassName");
        assertEquals("testClassName", parameters.getEventProducerPluginClass());

        parameters.setEventProducerPluginClass(null);
        assertNull(parameters.getEventProducerPluginClass());
    }

    @Test
    void testSetEventConsumerPluginClass() {
        parameters.setEventConsumerPluginClass("testClassName");
        assertEquals("testClassName", parameters.getEventConsumerPluginClass());

        parameters.setEventConsumerPluginClass(null);
        assertNull(parameters.getEventConsumerPluginClass());
    }

    @Test
    void testToString() {
        assertThat(parameters.toString()).contains("CarrierTechnologyParameters");
    }
}
