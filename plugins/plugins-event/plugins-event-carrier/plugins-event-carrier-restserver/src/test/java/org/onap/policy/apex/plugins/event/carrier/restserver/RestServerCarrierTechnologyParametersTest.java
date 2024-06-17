/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019, 2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ValidationResult;

class RestServerCarrierTechnologyParametersTest {

    RestServerCarrierTechnologyParameters restServerCarrierTechnologyParameters = null;
    ValidationResult result = null;

    /**
     * Set up testing.
     *
     */
    @BeforeEach
    void setUp() {
        restServerCarrierTechnologyParameters = new RestServerCarrierTechnologyParameters();
    }

    @Test
    void testRestServerCarrierTechnologyParameters() {
        assertNotNull(restServerCarrierTechnologyParameters);
        assertFalse(restServerCarrierTechnologyParameters.isStandalone());
    }

    @Test
    void testValidate() {
        result = restServerCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateWithNonDefaultValues() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {

        Field field = RestServerCarrierTechnologyParameters.class.getDeclaredField("standalone");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, true);
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("host");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("port");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, 1023);
        result = restServerCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertFalse(result.isValid());

        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("host");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("port");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, 1023);
        result = restServerCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateWithValidValues() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {

        Field field = RestServerCarrierTechnologyParameters.class.getDeclaredField("standalone");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, true);
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("host");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "localhost");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("port");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, 6969);
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("userName");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "username");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("password");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "password");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("https");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, true);
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("aaf");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, true);
        result = restServerCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateWithInvalidValues() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {

        Field field = RestServerCarrierTechnologyParameters.class.getDeclaredField("standalone");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, false);
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("host");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "localhost");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("port");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, 6969);
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("userName");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "username");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("password");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, "password");
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("https");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, true);
        field = RestServerCarrierTechnologyParameters.class.getDeclaredField("aaf");
        field.setAccessible(true);
        field.set(restServerCarrierTechnologyParameters, true);
        result = restServerCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertFalse(result.isValid());
        assertThat(result.getResult()).contains("host", "port", "should be specified only in standalone mode");
    }
}
