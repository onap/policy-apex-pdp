/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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
 * Test state outputs.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class StateOutputTest {

    @Test
    void testStateOutput() {
        assertNotNull(new AxStateOutput());
        assertNotNull(new AxStateOutput(new AxReferenceKey()));
        assertNotNull(new AxStateOutput(new AxReferenceKey(), new AxReferenceKey(), new AxArtifactKey()));
        assertNotNull(new AxStateOutput(new AxReferenceKey(), new AxArtifactKey(), new AxReferenceKey()));

        final AxStateOutput so = new AxStateOutput();

        final AxReferenceKey soKey = new AxReferenceKey("SOStateParent", "0.0.1", "SOState", "SOName");
        final AxReferenceKey nsKey = new AxReferenceKey("SOStateParent", "0.0.1", "NotUsed", "NextStateName");
        final AxArtifactKey eKey = new AxArtifactKey("EventName", "0.0.1");

        assertThatThrownBy(() -> so.setKey(null))
            .hasMessage("key is marked non-null but is null");
        so.setKey(soKey);
        assertEquals("SOStateParent:0.0.1:SOState:SOName", so.getKey().getId());
        assertEquals("SOStateParent:0.0.1:SOState:SOName", so.getKeys().get(0).getId());

        assertThatThrownBy(() -> so.setNextState(null))
            .hasMessage("nextState is marked non-null but is null");
        so.setNextState(nsKey);
        assertEquals(nsKey, so.getNextState());

        assertThatThrownBy(() -> so.setOutgoingEvent(null))
            .hasMessage("outgoingEvent is marked non-null but is null");
        so.setOutgoingEvent(eKey);
        assertEquals(eKey, so.getOutgoingEvent());

        AxValidationResult result = new AxValidationResult();
        result = so.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        so.setKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = so.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        so.setKey(soKey);
        result = new AxValidationResult();
        result = so.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        so.setOutgoingEvent(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = so.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        so.setOutgoingEvent(eKey);
        result = new AxValidationResult();
        result = so.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        so.clean();

        final AxStateOutput clonedPar = new AxStateOutput(so);
        assertEquals("AxStateOutput:(stateKey=AxReferenceKey:(parentKeyN", clonedPar.toString().substring(0, 50));

        assertNotEquals(0, so.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(so, so); // NOSONAR
        assertEquals(so, clonedPar);
        assertNotNull(so);
        assertNotEquals(so, (Object) "Hello");
        assertNotEquals(so, new AxStateOutput(AxReferenceKey.getNullKey(), eKey, nsKey));
        assertNotEquals(so, new AxStateOutput(soKey, new AxArtifactKey(), nsKey));
        assertNotEquals(so, new AxStateOutput(soKey, eKey, new AxReferenceKey()));
        assertEquals(so, new AxStateOutput(soKey, eKey, nsKey));

        assertEquals(0, so.compareTo(so));
        assertEquals(0, so.compareTo(clonedPar));
        assertNotEquals(0, so.compareTo(new AxArtifactKey()));
        assertNotEquals(0, so.compareTo(null));
        assertNotEquals(0, so.compareTo(new AxStateOutput(AxReferenceKey.getNullKey(), eKey, nsKey)));
        assertNotEquals(0, so.compareTo(new AxStateOutput(soKey, new AxArtifactKey(), nsKey)));
        assertNotEquals(0, so.compareTo(new AxStateOutput(soKey, eKey, new AxReferenceKey())));
        assertEquals(0, so.compareTo(new AxStateOutput(soKey, eKey, nsKey)));

        assertNotNull(so.getKeys());
    }
}
