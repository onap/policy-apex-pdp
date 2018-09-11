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
import org.onap.policy.apex.model.policymodel.concepts.AxLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;

/**
 * Test apex logic.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestLogic {

    @Test
    public void testLogic() {
        final DummyLogicReader logicReader = new DummyLogicReader();

        assertNotNull(new AxLogic());
        assertNotNull(new AxLogic(new AxReferenceKey()));
        assertNotNull(new AxLogic(new AxReferenceKey(), "LogicFlavour", "Logic"));
        assertNotNull(new AxLogic(new AxReferenceKey(), "LogicName", "LogicFlavour", "Logic"));
        assertNotNull(new AxLogic(new AxReferenceKey(), "LogicFlavour", logicReader));

        assertNotNull(new AxTaskLogic());
        assertNotNull(new AxTaskLogic(new AxReferenceKey()));
        assertNotNull(new AxTaskLogic(new AxReferenceKey(), "LogicFlavour", "Logic"));
        assertNotNull(new AxTaskLogic(new AxReferenceKey(), "LogicFlavour", logicReader));
        assertNotNull(new AxTaskLogic(new AxLogic()));
        assertNotNull(new AxTaskLogic(new AxArtifactKey(), "LogicName", "LogicFlavour", logicReader));
        assertNotNull(new AxTaskLogic(new AxArtifactKey(), "LogicName", "LogicFlavour", "Logic"));
        assertNotNull(new AxTaskLogic(new AxReferenceKey(), "LogicFlavour", logicReader));

        assertNotNull(new AxTaskSelectionLogic());
        assertNotNull(new AxTaskSelectionLogic(new AxReferenceKey()));
        assertNotNull(new AxTaskSelectionLogic(new AxReferenceKey(), "LogicFlavour", "Logic"));
        assertNotNull(new AxTaskSelectionLogic(new AxReferenceKey(), "LogicName", "LogicFlavour", "Logic"));
        assertNotNull(new AxTaskSelectionLogic(new AxReferenceKey(), "LogicFlavour", logicReader));
        assertNotNull(new AxTaskSelectionLogic(new AxLogic()));
        assertNotNull(new AxTaskSelectionLogic(new AxReferenceKey(), "LogicFlavour", logicReader));
        assertNotNull(new AxTaskSelectionLogic(new AxReferenceKey(), "LogicName", "LogicFlavour", logicReader));

        assertNotNull(new AxStateFinalizerLogic());
        assertNotNull(new AxStateFinalizerLogic(new AxReferenceKey()));
        assertNotNull(new AxStateFinalizerLogic(new AxReferenceKey(), "LogicFlavour", "Logic"));
        assertNotNull(new AxStateFinalizerLogic(new AxReferenceKey(), "LogicName", "LogicFlavour", "Logic"));
        assertNotNull(new AxStateFinalizerLogic(new AxReferenceKey(), "LogicFlavour", logicReader));
        assertNotNull(new AxStateFinalizerLogic(new AxLogic()));
        assertNotNull(new AxStateFinalizerLogic(new AxReferenceKey(), "LogicFlavour", logicReader));
        assertNotNull(new AxStateFinalizerLogic(new AxReferenceKey(), "LogicName", "LogicFlavour", logicReader));

        final AxLogic logic = new AxLogic();

        final AxReferenceKey logicKey = new AxReferenceKey("LogicParentName", "0.0.1", "PLN", "LN");
        logic.setKey(logicKey);
        assertEquals("LogicParentName:0.0.1:PLN:LN", logic.getKey().getId());
        assertEquals("LogicParentName:0.0.1:PLN:LN", logic.getKeys().get(0).getId());

        logic.setLogicFlavour("LogicFlavour");
        assertEquals("LogicFlavour", logic.getLogicFlavour());

        logic.setLogic("Logic");
        assertEquals("Logic", logic.getLogic());

        AxValidationResult result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        logic.setKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        logic.setKey(logicKey);
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        try {
            logic.setLogicFlavour(null);
            fail("test shold throw an exception here");
        } catch (final Exception e) {
            assertEquals("parameter \"logicFlavour\" is null", e.getMessage());
        }

        try {
            logic.setLogicFlavour("");
            fail("test shold throw an exception here");
        } catch (final Exception e) {
            assertEquals("parameter \"logicFlavour\": value \"\", "
                            + "does not match regular expression \"[A-Za-z0-9\\-_]+\"", e.getMessage());
        }

        logic.setLogicFlavour(AxLogic.LOGIC_FLAVOUR_UNDEFINED);
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        logic.setLogicFlavour("LogicFlavour");
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        try {
            logic.setLogic(null);
            fail("test shold throw an exception here");
        } catch (final Exception e) {
            assertEquals("logic may not be null", e.getMessage());
        }

        logic.setLogic("");
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        logic.setLogic("Logic");
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        logic.clean();

        final AxLogic clonedLogic = new AxLogic(logic);
        assertEquals("AxLogic:(key=AxReferenceKey:(parentKeyName=LogicParentName,parentKeyVersion=0.0.1,"
                        + "parentLocalName=PLN,localName=LN),logicFlavour=LogicFlavour,logic=Logic)",
                        clonedLogic.toString());

        assertFalse(logic.hashCode() == 0);

        assertTrue(logic.equals(logic));
        assertTrue(logic.equals(clonedLogic));
        assertFalse(logic.equals(null));
        assertFalse(logic.equals("Hello"));
        assertFalse(logic.equals(new AxLogic(AxReferenceKey.getNullKey(), "LogicFlavour", "Logic")));
        assertFalse(logic.equals(new AxLogic(logicKey, "AnotherLogicFlavour", "Logic")));
        assertFalse(logic.equals(new AxLogic(logicKey, "LogicFlavour", "AnotherLogic")));
        assertTrue(logic.equals(new AxLogic(logicKey, "LogicFlavour", "Logic")));

        assertEquals(0, logic.compareTo(logic));
        assertEquals(0, logic.compareTo(clonedLogic));
        assertNotEquals(0, logic.compareTo(new AxArtifactKey()));
        assertNotEquals(0, logic.compareTo(null));
        assertNotEquals(0, logic.compareTo(new AxLogic(AxReferenceKey.getNullKey(), "LogicFlavour", "Logic")));
        assertNotEquals(0, logic.compareTo(new AxLogic(logicKey, "AnotherLogicFlavour", "Logic")));
        assertNotEquals(0, logic.compareTo(new AxLogic(logicKey, "LogicFlavour", "AnotherLogic")));
        assertEquals(0, logic.compareTo(new AxLogic(logicKey, "LogicFlavour", "Logic")));

        assertNotNull(logic.getKeys());
    }
}
