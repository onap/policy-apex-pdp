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

package org.onap.policy.apex.model.policymodel.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxLogic;
import org.onap.policy.apex.model.policymodel.handling.PolicyLogicReader;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestPolicyLogicReader {

    @Test
    public void test() {
        final AxReferenceKey logicKey = new AxReferenceKey("LogicParent", "0.0.1", "LogicInstanceName");

        final PolicyLogicReader plReader = new PolicyLogicReader();

        plReader.setLogicPackage("somewhere.over.the.rainbow");
        assertEquals("somewhere.over.the.rainbow", plReader.getLogicPackage());

        plReader.setDefaultLogic("FunkyDefaultLogic");
        assertEquals("FunkyDefaultLogic", plReader.getDefaultLogic());

        try {
            new AxLogic(logicKey, "FunkyLogic", plReader);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "logic not found for logic \"somewhere/over/the/rainbow/funkylogic/FunkyDefaultLogic.funkylogic\"",
                    e.getMessage());
        }

        plReader.setDefaultLogic(null);
        try {
            new AxLogic(logicKey, "FunkyLogic", plReader);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "logic not found for logic \"somewhere/over/the/rainbow/funkylogic/LogicParent_LogicInstanceName.funkylogic\"",
                    e.getMessage());
        }

        logicKey.setParentLocalName("LogicParentLocalName");
        try {
            new AxLogic(logicKey, "FunkyLogic", plReader);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "logic not found for logic \"somewhere/over/the/rainbow/funkylogic/LogicParent_LogicParentLocalName_LogicInstanceName.funkylogic\"",
                    e.getMessage());
        }

        plReader.setLogicPackage("path.to.apex.logic");
        try {
            final AxLogic logic = new AxLogic(logicKey, "FunkyLogic", plReader);
            assertTrue(logic.getLogic().endsWith("Way out man, this is funky logic!"));
        } catch (final Exception e) {
            fail("test should not throw an exception");
        }

        plReader.setLogicPackage("somewhere.over.the.rainbow");
        plReader.setDefaultLogic("JavaLogic");

        try {
            final AxLogic logic = new AxLogic(logicKey, "JAVA", plReader);
            assertEquals("somewhere.over.the.rainbow.java.JavaLogic", logic.getLogic());
        } catch (final Exception e) {
            fail("test should not throw an exception");
        }

        plReader.setDefaultLogic(null);
        try {
            final AxLogic logic = new AxLogic(logicKey, "JAVA", plReader);
            assertEquals("somewhere.over.the.rainbow.java.LogicParent_LogicParentLocalName_LogicInstanceName",
                    logic.getLogic());
        } catch (final Exception e) {
            fail("test should not throw an exception");
        }

        logicKey.setParentLocalName(AxKey.NULL_KEY_NAME);
        try {
            final AxLogic logic = new AxLogic(logicKey, "JAVA", plReader);
            assertEquals("somewhere.over.the.rainbow.java.LogicParent_LogicInstanceName", logic.getLogic());
        } catch (final Exception e) {
            fail("test should not throw an exception");
        }
    }
}
