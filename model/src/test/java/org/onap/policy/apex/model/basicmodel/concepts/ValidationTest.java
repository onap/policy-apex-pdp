/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

class ValidationTest {

    @Test
    void testValid() {
        AxValidationResult result = new AxValidationResult();
        AxReferenceKey refKey = new AxReferenceKey("PK", "0.0.1", "PLN", "LN");
        result = refKey.validate(result);
        assertResultIsValid(result, ValidationResult.VALID);

        AxValidationMessage vmess0 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
            ValidationResult.VALID, "Some message");
        result.addValidationMessage(vmess0);
        assertResultIsValid(result, ValidationResult.VALID);

        AxValidationMessage vmess1 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
            ValidationResult.OBSERVATION, "Some message");
        result.addValidationMessage(vmess1);
        assertResultIsValid(result, ValidationResult.OBSERVATION);
    }

    @Test
    void testWarning() {
        AxValidationResult result = new AxValidationResult();
        AxValidationMessage vmess2 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
            ValidationResult.WARNING, "Some message");
        result.addValidationMessage(vmess2);

        assertFalse(result.isOk());
        assertTrue(result.isValid());
        assertEquals(AxValidationResult.ValidationResult.WARNING, result.getValidationResult());
        assertNotNull(result.getMessageList());
        assertNotNull(result.toString());
    }

    @Test
    void testInvalid() {
        AxValidationResult result = new AxValidationResult();
        AxValidationMessage vmess3 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
            ValidationResult.INVALID, "Some message");
        result.addValidationMessage(vmess3);

        assertFalse(result.isOk());
        assertFalse(result.isValid());
        assertEquals(AxValidationResult.ValidationResult.INVALID, result.getValidationResult());
        assertNotNull(result.getMessageList());
        assertNotNull(result.toString());

        var otherResult = result.getMessageList().get(0);
        assertEquals(AxValidationResult.ValidationResult.INVALID, otherResult.getValidationResult());
        assertEquals("Some message", otherResult.getMessage());
        assertEquals(AxArtifactKey.class.getName(), otherResult.getObservedClass());
        assertEquals(AxArtifactKey.getNullKey(), otherResult.getObservedKey());
    }

    private static void assertResultIsValid(AxValidationResult result, ValidationResult valid) {
        assertNotNull(result);
        assertTrue(result.isOk());
        assertTrue(result.isValid());
        assertEquals(valid, result.getValidationResult());
        assertNotNull(result.getMessageList());
    }
}
