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

package org.onap.policy.apex.model.modelapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test policies for API tests.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestApexEditorApiPolicy {
    @Test
    public void myTestPolicyCrud() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = apexModel.validatePolicy(null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.validatePolicy("%%%$£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicy("MyPolicy012", null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicy("MyPolicy012", null, "SomeTemplate", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicy("MyPolicy013", null, null, "AState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicy("MyPolicy012", null, "SomeTemplate", "AState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicy("MyPolicy012", "0.1.2", "SomeTemplate", "AState",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicy("MyTestPolicy", "0.0.1", "SomeTemplate", "TestState",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.deletePolicy("MyPolicy002", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.validatePolicy(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updatePolicy("@£$$", "0.0.2", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicy("MyPolicy002", "0.0.2", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicy("MyPolicy002", "0.0.1", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicy("MyPolicy002", "0.0.2", "SomeOtherTemplate", "BState",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicy("MyPolicy012", null, "SomeOtherTemplate", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicy("MyPolicy012", null, null, "CState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicy("MyPolicy012", null, "SomeOtherTemplate", "BState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicy("MyPolicy015", null, "SomeOtherTemplate", "DState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicy("MyPolicy014", "0.1.5", "SomeOtherTemplate", "EState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.listPolicy("@£$%%", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicy(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(6, result.getMessages().size());
        result = apexModel.listPolicy("MyPolicy012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicy("MyPolicy012", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicy("MyPolicy012", "0.2.5");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicy("MyPolicy014", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deletePolicy("@£$%", "0.1.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));

        result = apexModel.deletePolicy("MyPolicy012", "0.1.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deletePolicy("MyPolicy012oooo", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.listPolicy("MyPolicy012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 2);

        result = apexModel.deletePolicy("MyPolicy002", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 1);

        result = apexModel.listPolicy("MyPolicy012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 2);

        result = apexModel.deletePolicy("MyPolicy012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 2);

        result = apexModel.createPolicyState(null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyState("MyPolicy123", null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyState("MyPolicy123", null, "AState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.2", "AState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "ATrigger", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.2", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "ATask", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "AState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "BState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "CState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "DState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.updatePolicyState(null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyState("MyPolicy123", null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyState("MyPolicy123", null, "AState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.2", "AState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "ZState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "ATrigger", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.2", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.2", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "ATask", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "AState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "BState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "CState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "DState", "inEvent", "0.0.1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.listPolicyState("MyTestPolicy", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(4, result.getMessages().size());
        result = apexModel.listPolicyState(null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyState("SomeOtherPolicy", "0.0.1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyState("SomeOtherPolicy", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.2", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(4, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", "CState");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", null, "CState");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", "AState");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", "EState");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deletePolicyState("@£$$", "0.0.2", "Trigger04");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        assertEquals(4, apexModel.listPolicyState("MyTestPolicy", null, null).getMessages().size());
        result = apexModel.deletePolicyState("MyTestPolicy", "0.0.2", "Trigger04");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        assertEquals(4, apexModel.listPolicyState("MyTestPolicy", null, null).getMessages().size());
        result = apexModel.deletePolicyState("MyTestPolicy", null, "CState");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyState("MyTestPolicy", null, "ZState");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        assertEquals(3, apexModel.listPolicyState("MyTestPolicy", null, null).getMessages().size());
        result = apexModel.deletePolicyState("MyTestPolicy", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.listPolicyState("MyTestPolicy", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyState("MyTestPolicy", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "TestState1", "inEvent", "0.0.1", "task",
                "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "TestState2", "outEvent0", "0.0.1", "task",
                "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "TestState3", "outEvent1", "0.0.1", "task",
                "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createPolicyStateTaskSelectionLogic(null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "1.2.3", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "NewTSL00", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "UNDEFINED",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "MVEL",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));

        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "JAVA",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "JYTHON",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", "JAVASCRIPT",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", "JRUBY",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.updatePolicyStateTaskSelectionLogic(null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState99", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy2", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy1", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "NonExistantState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "",
                "Some Other Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "MVEL",
                "Some Other Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyPolicy012", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null,
                "Some Other Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyPolicy015", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyPolicy014", "0.1.5", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.listPolicyStateTaskSelectionLogic(null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateTaskSelectionLogic("zooby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateTaskSelectionLogic("zooby", null, "looby");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskSelectionLogic("zooby", null, "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "looby");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.2", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());

        result = apexModel.deletePolicyStateTaskSelectionLogic(null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateTaskSelectionLogic("zooby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateTaskSelectionLogic("zooby", null, "looby");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskSelectionLogic("zooby", null, "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", null, "looby");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.2", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null,
                "Some Other Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", "JRUBY",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.createPolicyStateOutput(null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", null, "SomeState", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", null, "SomeState", "SomeOutput", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "1.2.3", "TestState1", "SomeOutput", null, null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", null, null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput",
                "SomeDummyEvent", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
                "1.2.3", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
                "0.0.1", "SomeDummyNextState");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
                "0.0.1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
                "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
                "0.0.1", "TestState2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
                "0.0.1", "TestState2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "AnotherOtherOutput",
                "outEvent0", "0.0.1", "TestState3");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "YetAnotherOtherOutput",
                "outEvent0", "0.0.1", "TestState3");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.listPolicyStateOutput(null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.2", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "SomeState", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "YetAnotherOtherOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "YetAnotherOtherOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState3", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deletePolicyStateOutput(null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.2", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "SomeState", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", "TestState3", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", "TestState3", "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertEquals(2, result.getMessages().size());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "TestState1", "SomeOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState2", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "TestState2", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState2", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
                "0.0.1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
                "0.0.1", "TestState1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
                "0.0.1", "TestState2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "AnotherOtherOutput",
                "outEvent0", "0.0.1", "TestState3");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "YetAnotherOtherOutput",
                "outEvent0", "0.0.1", "TestState3");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createPolicyStateFinalizerLogic(null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", "SFLName01", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "1.2.3", "TestState1", "SFLName01", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName01", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName01",
                "NewTSL00", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02",
                "UNDEFINED", "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName03", "MVEL",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName03", "MVEL",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName04", "JAVA",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName05", "JYTHON",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06",
                "JAVASCRIPT", "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName07", "JRUBY",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.updatePolicyStateFinalizerLogic(null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState99", "SomeSFLName", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy2", null, "TestState1", "SomeSFLName", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy1", "0.0.2", "TestState1", "SomeSFLName", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "NonEistantSFL", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06", "",
                "Some Other Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06", "MVEL",
                "Some Other Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateFinalizerLogic("MyPolicy012", null, "TestState1", "SFLName06", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06", null,
                "Some Other Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updatePolicyStateFinalizerLogic("MyPolicy015", null, "TestState1", "SFLName06", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updatePolicyStateFinalizerLogic("MyPolicy014", "0.1.5", "TestState1", "SFLName06", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.listPolicyStateFinalizerLogic(null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateFinalizerLogic("zooby", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateFinalizerLogic("zooby", null, "looby", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("zooby", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "looby", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(6, result.getMessages().size());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());

        result = apexModel.deletePolicyStateFinalizerLogic(null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateFinalizerLogic("zooby", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateFinalizerLogic("zooby", null, "looby", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateFinalizerLogic("zooby", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", null, "looby", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(5, result.getMessages().size());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName02");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(4, result.getMessages().size());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(4, result.getMessages().size());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02",
                "UNDEFINED", "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName03", "MVEL",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName04", "JAVA",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName05", "JYTHON",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06",
                "JAVASCRIPT", "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName07", "JRUBY",
                "Some Policy Logic");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createTask("TestTask0", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createTask("TestTask1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createTask("TestTask2", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createTask("TestTask3", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createTask("TestTask4", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createPolicyStateTaskRef(null, null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null, null, null,
                "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "1.2.3", "SomeState", null, null, null, null,
                "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("AnyOldPolicy", "1.2.3", "SomeState", null, null, null, null,
                "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", null,
                null, null, "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName",
                "SomeTask", "Zooby|", null, "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName",
                "SomeTask", "0.0.1", null, "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", null, "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", "Some Policy Logic", "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", "DIRECT", "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", "LOGIC", "DummyOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", "DIRECT", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", "LOGIC", "SomeOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", "DIRECT", "SomeOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "NonExistantTask", "0.0.1", "DIRECT", "SomeOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
                "0.0.1", "LOGIC", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "TestTask0", "0.0.1", "LOGIC", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "TestTask1", "0.0.1", "DIRECT", "SomeOtherOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "TestTask2", "0.0.1", "LOGIC", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "TestTask3", "0.0.1", "DIRECT", "SomeOtherOutput");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, "TestTask4", "0.0.1",
                "LOGIC", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, "TestTask4", "0.0.1",
                "LOGIC", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", "TestTask4", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "TestTask4", "0.0.1", "FUNKY", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "TestTask4", "0.0.1", "UNDEFINED", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
                "TestTask4", "0.0.1", "LOGIC", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, "TestTask0", "0.0.1",
                "LOGIC", "SFLName07");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));

        result = apexModel.listPolicyStateTaskRef(null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateTaskRef("zooby", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateTaskRef("zooby", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("zooby", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(12, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeOldTask", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "task", "1.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "task", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());

        result = apexModel.deletePolicyStateTaskRef(null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateTaskRef("zooby", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateTaskRef("zooby", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("zooby", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "ADummyTask", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "task", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(12, result.getMessages().size());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", "TestTask0", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(10, result.getMessages().size());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", "TestTask2", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(8, result.getMessages().size());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(4, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.createPolicyStateContextRef(null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "1.2.3", "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("AnyOldPolicy", "1.2.3", "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTask", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTask", "Zooby|");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTask", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "task", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyPolicy123", null, null, "AContextMap00", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateContextRef("MyPolicy123", null, "TestState1", "AContextMap00", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "4.5.6", "TestState1", "AContextMap00", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.1.4", "TestState1", "AContextMap00", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "AContextMap00", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum1", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum0", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));

        result = apexModel.listPolicyStateContextRef(null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateContextRef("zooby", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateContextRef("zooby", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("zooby", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "AContextMap04", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "1.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deletePolicyStateContextRef(null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateContextRef("zooby", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateContextRef("zooby", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("zooby", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "looby", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "ADummyContextMap", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "AContextMap04", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum0", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum0", "0.1.5");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum1", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(1, result.getMessages().size());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deletePolicy(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(3, result.getMessages().size());

        result = apexModel.listPolicy(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(0, result.getMessages().size());
    }
}
