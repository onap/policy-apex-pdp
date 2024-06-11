/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
 * Test apex logic.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class LogicTest {

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

        assertThatThrownBy(() -> logic.setLogicFlavour(null))
            .hasMessageContaining("parameter \"logicFlavour\" is null");
        assertThatThrownBy(() -> logic.setLogicFlavour(""))
            .hasMessage("parameter \"logicFlavour\": value \"\", "
                    + "does not match regular expression \"[A-Za-z0-9\\-_]+\"");
        logic.setLogicFlavour(AxLogic.LOGIC_FLAVOUR_UNDEFINED);
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        logic.setLogicFlavour("LogicFlavour");
        result = new AxValidationResult();
        result = logic.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        assertThatThrownBy(() -> logic.setLogic(null)).hasMessage("logic may not be null");
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

        assertNotEquals(0, logic.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(logic, logic); // NOSONAR
        assertEquals(logic, clonedLogic);
        assertNotNull(logic);
        assertNotEquals(logic, (Object) "Hello");
        assertNotEquals(logic, new AxLogic(AxReferenceKey.getNullKey(), "LogicFlavour", "Logic"));
        assertNotEquals(logic, new AxLogic(logicKey, "AnotherLogicFlavour", "Logic"));
        assertNotEquals(logic, new AxLogic(logicKey, "LogicFlavour", "AnotherLogic"));
        assertEquals(logic, new AxLogic(logicKey, "LogicFlavour", "Logic"));

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
