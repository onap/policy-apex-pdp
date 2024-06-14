/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.service.engine.event.impl.eventrequestor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ValidationResult;

class EventRequestorCarrierTechnologyParametersTest {

    @Test
    void getName() {
        final EventRequestorCarrierTechnologyParameters parameters = new EventRequestorCarrierTechnologyParameters();
        final String actual = parameters.getName();
        assertEquals(EventRequestorCarrierTechnologyParameters.EVENT_REQUESTOR_CARRIER_TECHNOLOGY_LABEL, actual);
    }

    @Test
    void validate() {
        final EventRequestorCarrierTechnologyParameters parameters = new EventRequestorCarrierTechnologyParameters();
        final ValidationResult actual = parameters.validate();
        assertNotNull(actual);
    }
}
