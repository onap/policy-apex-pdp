/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.services.onappf.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TestToscaPolicyTypeIdentifierParameters {

    ToscaPolicyTypeIdentifierParameters parameters;
    ToscaPolicyTypeIdentifierParameters parametersCopy;

    @Test
    void test() {
        parameters = getParametersInstance();
        parametersCopy = getParametersInstance();

        assertTrue(parameters.equals(parametersCopy));
        assertNotNull(parameters.hashCode());
        assertEquals(parameters.hashCode(), parametersCopy.hashCode());
    }

    protected ToscaPolicyTypeIdentifierParameters getParametersInstance() {
        ToscaPolicyTypeIdentifierParameters parameters = new ToscaPolicyTypeIdentifierParameters();
        parameters.setName("testName");
        parameters.setVersion("1.0.0");
        return parameters;
    }
}