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
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;

/**
 * Test state outputs.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class StateOutputTest {

    @Test
    public void testStateOutput() {
        assertNotNull(new AxStateOutput());
        assertNotNull(new AxStateOutput(new AxReferenceKey()));
        assertNotNull(new AxStateOutput(new AxReferenceKey(), new AxReferenceKey(), new AxArtifactKey()));
        assertNotNull(new AxStateOutput(new AxReferenceKey(), new AxArtifactKey(), new AxReferenceKey()));

        final AxStateOutput so = new AxStateOutput();

        final AxReferenceKey soKey = new AxReferenceKey("SOStateParent", "0.0.1", "SOState", "SOName");
        final AxReferenceKey nsKey = new AxReferenceKey("SOStateParent", "0.0.1", "NotUsed", "NextStateName");
        final AxArtifactKey eKey = new AxArtifactKey("EventName", "0.0.1");

        try {
            so.setKey(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("key may not be null", e.getMessage());
        }

        so.setKey(soKey);
        assertEquals("SOStateParent:0.0.1:SOState:SOName", so.getKey().getId());
        assertEquals("SOStateParent:0.0.1:SOState:SOName", so.getKeys().get(0).getId());

        try {
            so.setNextState(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("nextState may not be null", e.getMessage());
        }

        so.setNextState(nsKey);
        assertEquals(nsKey, so.getNextState());

        try {
            so.setOutgoingEvent(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("outgoingEvent may not be null", e.getMessage());
        }

        so.setOutgoingEvent(eKey);
        assertEquals(eKey, so.getOutgingEvent());

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

        assertFalse(so.hashCode() == 0);

        assertTrue(so.equals(so));
        assertTrue(so.equals(clonedPar));
        assertFalse(so.equals(null));
        assertFalse(so.equals("Hello"));
        assertFalse(so.equals(new AxStateOutput(AxReferenceKey.getNullKey(), eKey, nsKey)));
        assertFalse(so.equals(new AxStateOutput(soKey, new AxArtifactKey(), nsKey)));
        assertFalse(so.equals(new AxStateOutput(soKey, eKey, new AxReferenceKey())));
        assertTrue(so.equals(new AxStateOutput(soKey, eKey, nsKey)));

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
