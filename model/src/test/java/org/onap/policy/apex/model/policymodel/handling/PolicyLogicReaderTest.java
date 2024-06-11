/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation
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

package org.onap.policy.apex.model.policymodel.handling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxLogic;

/**
 * Logic reader for policy tests.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class PolicyLogicReaderTest {

    @Test
    void test() {
        final AxReferenceKey logicKey = new AxReferenceKey("LogicParent", "0.0.1", "LogicInstanceName");

        final PolicyLogicReader plReader = new PolicyLogicReader();

        plReader.setLogicPackage("somewhere.over.the.rainbow");
        assertEquals("somewhere.over.the.rainbow", plReader.getLogicPackage());

        plReader.setDefaultLogic("FunkyDefaultLogic");
        assertEquals("FunkyDefaultLogic", plReader.getDefaultLogic());

        assertThatThrownBy(() -> new AxLogic(logicKey, "FunkyLogic", plReader))
            .hasMessage("logic not found for logic "
                            + "\"somewhere/over/the/rainbow/funkylogic/FunkyDefaultLogic.funkylogic\"");
        plReader.setDefaultLogic(null);
        assertThatThrownBy(() -> new AxLogic(logicKey, "FunkyLogic", plReader))
            .hasMessage("logic not found for logic "
                            + "\"somewhere/over/the/rainbow/funkylogic/LogicParentLogicInstanceName.funkylogic\"");
        logicKey.setParentLocalName("LogicParentLocalName");
        assertThatThrownBy(() -> new AxLogic(logicKey, "FunkyLogic", plReader))
            .hasMessage("logic not found for logic " + "\"somewhere/over/the/rainbow/funkylogic/"
                            + "LogicParentLogicParentLocalNameLogicInstanceName.funkylogic\"");
        plReader.setLogicPackage("path.to.apex.logic");

        AxLogic logic = new AxLogic(logicKey, "FunkyLogic", plReader);
        assertThat(logic.getLogic()).endsWith("Way out man, this is funky logic!");

        plReader.setLogicPackage("somewhere.over.the.rainbow");
        plReader.setDefaultLogic("JavaLogic");

        logic = new AxLogic(logicKey, "JAVA", plReader);
        assertEquals("somewhere.over.the.rainbow.java.JavaLogic", logic.getLogic());

        plReader.setDefaultLogic(null);

        logic = new AxLogic(logicKey, "JAVA", plReader);
        assertEquals("somewhere.over.the.rainbow.java.LogicParentLogicParentLocalNameLogicInstanceName",
                        logic.getLogic());

        logicKey.setParentLocalName(AxKey.NULL_KEY_NAME);
        logic = new AxLogic(logicKey, "JAVA", plReader);
        assertEquals("somewhere.over.the.rainbow.java.LogicParentLogicInstanceName", logic.getLogic());
    }
}
