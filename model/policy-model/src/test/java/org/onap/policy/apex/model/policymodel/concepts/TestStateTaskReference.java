/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.model.policymodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;

/**
 * Test state task references.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestStateTaskReference {

    @Test
    public void testStateTaskReference() {
        assertNotNull(new AxStateTaskReference());
        assertNotNull(new AxStateTaskReference(new AxReferenceKey()));
        assertNotNull(new AxStateTaskReference(new AxReferenceKey(), AxStateTaskOutputType.UNDEFINED,
                        new AxReferenceKey()));
        assertNotNull(new AxStateTaskReference(new AxReferenceKey(), new AxArtifactKey(),
                        AxStateTaskOutputType.UNDEFINED, new AxReferenceKey()));

        AxStateTaskReference stRef = new AxStateTaskReference();

        AxReferenceKey stRefKey = new AxReferenceKey("StateParent", "0.0.1", "SOState", "SOName");

        try {
            stRef.setKey(null);
            fail("test should throw an exception here");
        } catch (Exception e) {
            assertEquals("key may not be null", e.getMessage());
        }

        stRef.setKey(stRefKey);
        assertEquals("StateParent:0.0.1:SOState:SOName", stRef.getKey().getId());
        assertEquals("StateParent:0.0.1:SOState:SOName", stRef.getKeys().get(0).getId());

        try {
            stRef.setStateTaskOutputType(null);
            fail("test should throw an exception here");
        } catch (Exception e) {
            assertEquals("outputType may not be null", e.getMessage());
        }

        stRef.setStateTaskOutputType(AxStateTaskOutputType.UNDEFINED);
        assertEquals(AxStateTaskOutputType.UNDEFINED, stRef.getStateTaskOutputType());
        stRef.setStateTaskOutputType(AxStateTaskOutputType.DIRECT);
        assertEquals(AxStateTaskOutputType.DIRECT, stRef.getStateTaskOutputType());
        stRef.setStateTaskOutputType(AxStateTaskOutputType.LOGIC);
        assertEquals(AxStateTaskOutputType.LOGIC, stRef.getStateTaskOutputType());

        try {
            stRef.setOutput(null);
            fail("test should throw an exception here");
        } catch (Exception e) {
            assertEquals("output may not be null", e.getMessage());
        }

        AxReferenceKey soKey = new AxReferenceKey("StateParent", "0.0.1", "SOState", "STRef0");
        stRef.setOutput(soKey);
        assertEquals(soKey, stRef.getOutput());

        AxValidationResult result = new AxValidationResult();
        result = stRef.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        stRef.setKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = stRef.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        stRef.setKey(stRefKey);
        result = new AxValidationResult();
        result = stRef.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        stRef.setStateTaskOutputType(AxStateTaskOutputType.UNDEFINED);
        result = new AxValidationResult();
        result = stRef.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        stRef.setStateTaskOutputType(AxStateTaskOutputType.LOGIC);
        result = new AxValidationResult();
        result = stRef.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        stRef.setOutput(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = stRef.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        stRef.setOutput(soKey);
        result = new AxValidationResult();
        result = stRef.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        stRef.clean();

        AxStateTaskReference clonedStRef = new AxStateTaskReference(stRef);
        assertEquals("AxStateTaskReference:(stateKey=AxReferenceKey:(par", clonedStRef.toString().substring(0, 50));

        assertFalse(stRef.hashCode() == 0);

        assertTrue(stRef.equals(stRef));
        assertTrue(stRef.equals(clonedStRef));
        assertFalse(stRef.equals(null));
        assertFalse(stRef.equals("Hello"));
        assertFalse(stRef.equals(
                        new AxStateTaskReference(AxReferenceKey.getNullKey(), AxStateTaskOutputType.LOGIC, soKey)));
        assertFalse(stRef.equals(new AxStateTaskReference(stRefKey, AxStateTaskOutputType.DIRECT, soKey)));
        assertFalse(stRef
                        .equals(new AxStateTaskReference(stRefKey, AxStateTaskOutputType.LOGIC, new AxReferenceKey())));
        assertTrue(stRef.equals(new AxStateTaskReference(stRefKey, AxStateTaskOutputType.LOGIC, soKey)));

        assertNotNull(new AxStateTaskReference(new AxReferenceKey(), new AxArtifactKey(),
                        AxStateTaskOutputType.UNDEFINED, new AxReferenceKey()));

        assertEquals(0, stRef.compareTo(stRef));
        assertEquals(0, stRef.compareTo(clonedStRef));
        assertNotEquals(0, stRef.compareTo(new AxArtifactKey()));
        assertNotEquals(0, stRef.compareTo(null));
        assertNotEquals(0, stRef.compareTo(
                        new AxStateTaskReference(AxReferenceKey.getNullKey(), AxStateTaskOutputType.LOGIC, soKey)));
        assertNotEquals(0, stRef.compareTo(new AxStateTaskReference(stRefKey, AxStateTaskOutputType.DIRECT, soKey)));
        assertNotEquals(0, stRef.compareTo(
                        new AxStateTaskReference(stRefKey, AxStateTaskOutputType.LOGIC, new AxReferenceKey())));
        assertEquals(0, stRef.compareTo(new AxStateTaskReference(stRefKey, AxStateTaskOutputType.LOGIC, soKey)));

        assertNotNull(stRef.getKeys());
    }
}
