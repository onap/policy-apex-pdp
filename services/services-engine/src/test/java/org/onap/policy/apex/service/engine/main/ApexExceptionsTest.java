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

package org.onap.policy.apex.service.engine.main;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;

class ApexExceptionsTest {

    @Test
    void testExceptions() {
        assertThatCode(() -> {
            throw new ApexActivatorException("Test Apex Activator Exception");
        }).hasMessageContaining("Test Apex Activator Exception")
            .isInstanceOf(ApexException.class);

        Exception e = new Exception();
        assertThatCode(() -> {
            throw new ApexActivatorException("Test Apex Activator Exception", e);
        }).hasMessageContaining("Test Apex Activator Exception")
            .isInstanceOf(ApexException.class);

        assertThatCode(() -> {
            throw new ApexActivatorRuntimeException("Test runtime exception");
        }).hasMessageContaining("Test runtime exception")
            .isInstanceOf(ApexRuntimeException.class);

        assertThatCode(() -> {
            throw new ApexActivatorRuntimeException("Test runtime exception", e);
        }).hasMessageContaining("Test runtime exception")
            .isInstanceOf(ApexRuntimeException.class);
    }
}
