/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test state task references.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class StateTaskReferenceTest {

    @Test
    void testStateTaskReference() {
        assertNotNull(new AxStateTaskReference());
        assertNotNull(new AxStateTaskReference(new AxReferenceKey()));
        assertNotNull(new AxStateTaskReference(new AxReferenceKey(), AxStateTaskOutputType.UNDEFINED,
                        new AxReferenceKey()));
        assertNotNull(new AxStateTaskReference(new AxReferenceKey(), new AxArtifactKey(),
                        AxStateTaskOutputType.UNDEFINED, new AxReferenceKey()));

        AxStateTaskReference stRef = new AxStateTaskReference();

        AxReferenceKey stRefKey = new AxReferenceKey("StateParent", "0.0.1", "SOState", "SOName");

        assertThatThrownBy(() -> stRef.setKey(null))
            .hasMessage("key may not be null");
        stRef.setKey(stRefKey);
        assertEquals("StateParent:0.0.1:SOState:SOName", stRef.getKey().getId());
        assertEquals("StateParent:0.0.1:SOState:SOName", stRef.getKeys().get(0).getId());

        assertThatThrownBy(() -> stRef.setStateTaskOutputType(null))
            .hasMessage("outputType may not be null");
        stRef.setStateTaskOutputType(AxStateTaskOutputType.UNDEFINED);
        assertEquals(AxStateTaskOutputType.UNDEFINED, stRef.getStateTaskOutputType());
        stRef.setStateTaskOutputType(AxStateTaskOutputType.DIRECT);
        assertEquals(AxStateTaskOutputType.DIRECT, stRef.getStateTaskOutputType());
        stRef.setStateTaskOutputType(AxStateTaskOutputType.LOGIC);
        assertEquals(AxStateTaskOutputType.LOGIC, stRef.getStateTaskOutputType());

        assertThatThrownBy(() -> stRef.setOutput(null))
            .hasMessage("output may not be null");
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

        assertNotEquals(0, stRef.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(stRef, stRef); // NOSONAR
        assertEquals(stRef, clonedStRef);
        assertNotNull(stRef);
        assertNotEquals(stRef, (Object) "Hello");
        assertNotEquals(stRef, new AxStateTaskReference(AxReferenceKey.getNullKey(), AxStateTaskOutputType.LOGIC,
                        soKey));
        assertNotEquals(stRef, new AxStateTaskReference(stRefKey, AxStateTaskOutputType.DIRECT, soKey));
        assertNotEquals(stRef, new AxStateTaskReference(stRefKey, AxStateTaskOutputType.LOGIC, new AxReferenceKey()));
        assertEquals(stRef, new AxStateTaskReference(stRefKey, AxStateTaskOutputType.LOGIC, soKey));

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
