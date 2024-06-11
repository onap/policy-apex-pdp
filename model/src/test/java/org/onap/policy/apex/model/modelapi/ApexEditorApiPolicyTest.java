/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test policies for API.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ApexEditorApiPolicyTest {

    @Test
    void testMyTestPolicyCrud() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        ApexApiResult result = apexModel.validatePolicy(null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.validatePolicy("%%%$£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicy("MyPolicy012", null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicy("MyPolicy012", null, "SomeTemplate", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicy("MyPolicy013", null, null, "AState", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicy("MyPolicy012", null, "SomeTemplate", "AState", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicy("MyPolicy012", "0.1.2", "SomeTemplate", "AState",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicy("MyTestPolicy", "0.0.1", "SomeTemplate", "TestState",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.deletePolicy("MyPolicy002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicy("MyPolicy002", "0.0.2", "SomeTemplate", "AState",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.validatePolicy(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updatePolicy("@£$$", "0.0.2", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicy("MyPolicy002", "0.0.2", null, null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicy("MyPolicy002", "0.0.1", null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicy("MyPolicy002", "0.0.2", "SomeOtherTemplate", "BState",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicy("MyPolicy012", null, "SomeOtherTemplate", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicy("MyPolicy012", null, null, "CState", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicy("MyPolicy012", null, "SomeOtherTemplate", "BState", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicy("MyPolicy015", null, "SomeOtherTemplate", "DState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicy("MyPolicy014", "0.1.5", "SomeOtherTemplate", "EState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listPolicy("@£$%%", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicy(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(6, result.getMessages().size());
        result = apexModel.listPolicy("MyPolicy012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicy("MyPolicy012", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicy("MyPolicy012", "0.2.5");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicy("MyPolicy014", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deletePolicy("@£$%", "0.1.1");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deletePolicy("MyPolicy012", "0.1.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deletePolicy("MyPolicy012oooo", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listPolicy("MyPolicy012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());

        result = apexModel.deletePolicy("MyPolicy002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());

        result = apexModel.listPolicy("MyPolicy012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());

        result = apexModel.deletePolicy("MyPolicy012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.createPolicyState(null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyState("MyPolicy123", null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyState("MyPolicy123", null, "AState", null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.2", "AState", null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "ATrigger", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.2", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "ATask", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "AState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "BState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "CState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "DState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updatePolicyState(null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyState("MyPolicy123", null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyState("MyPolicy123", null, "AState", null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.2", "AState", null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", null, null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "ZState", null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "ATrigger", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.2", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", "0.0.2", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "ATask", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", null, "AState", "inEvent", null, "task", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "AState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "BState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "CState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyState("MyTestPolicy", "0.0.1", "DState", "inEvent", "0.0.1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listPolicyState("MyTestPolicy", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());
        result = apexModel.listPolicyState(null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyState("SomeOtherPolicy", "0.0.1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyState("SomeOtherPolicy", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.2", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", "CState");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", null, "CState");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", "AState");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyState("MyTestPolicy", "0.0.1", "EState");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deletePolicyState("@£$$", "0.0.2", "Trigger04");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        assertEquals(4, apexModel.listPolicyState("MyTestPolicy", null, null).getMessages().size());
        result = apexModel.deletePolicyState("MyTestPolicy", "0.0.2", "Trigger04");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(4, apexModel.listPolicyState("MyTestPolicy", null, null).getMessages().size());
        result = apexModel.deletePolicyState("MyTestPolicy", null, "CState");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyState("MyTestPolicy", null, "ZState");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(3, apexModel.listPolicyState("MyTestPolicy", null, null).getMessages().size());
        result = apexModel.deletePolicyState("MyTestPolicy", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listPolicyState("MyTestPolicy", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyState("MyTestPolicy", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "TestState1", "inEvent", "0.0.1", "task",
            "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "TestState2", "outEvent0", "0.0.1", "task",
            "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createPolicyState("MyTestPolicy", "0.0.1", "TestState3", "outEvent1", "0.0.1", "task",
            "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createPolicyStateTaskSelectionLogic(null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "1.2.3", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "NewTSL00", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "UNDEFINED",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "MVEL",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "JAVA",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "JYTHON",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", "JAVASCRIPT",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", "JRUBY",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updatePolicyStateTaskSelectionLogic(null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState99", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy2", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy1", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "NonExistantState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "",
            "Some Other Policy Logic");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1", "MVEL",
            "Some Other Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyPolicy012", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null,
            "Some Other Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyPolicy015", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyPolicy014", "0.1.5", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listPolicyStateTaskSelectionLogic(null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("zooby", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("zooby", null, "looby");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("zooby", null, "TestState1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "looby");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.2", "TestState1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());

        result = apexModel.deletePolicyStateTaskSelectionLogic(null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("zooby", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("zooby", null, "looby");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("zooby", null, "TestState1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", null, "looby");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.2", "TestState1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", null,
            "Some Other Policy Logic");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.createPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1", "JRUBY",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listPolicyStateTaskSelectionLogic("MyTestPolicy", null, "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateTaskSelectionLogic("MyTestPolicy", "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createPolicyStateOutput(null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", null, "SomeState", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", null, "SomeState", "SomeOutput", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "1.2.3", "TestState1", "SomeOutput", null, null,
            null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", null, null,
            null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput",
            "SomeDummyEvent", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
            "1.2.3", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
            "0.0.1", "SomeDummyNextState");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
            "0.0.1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
            "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
            "0.0.1", "TestState2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
            "0.0.1", "TestState2");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "AnotherOtherOutput",
            "outEvent0", "0.0.1", "TestState3");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "YetAnotherOtherOutput",
            "outEvent0", "0.0.1", "TestState3");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listPolicyStateOutput(null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.2", "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "SomeState", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "YetAnotherOtherOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "YetAnotherOtherOutput");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState3", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deletePolicyStateOutput(null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.2", "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "SomeState", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", "TestState3", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", "0.0.1", "TestState3", "DummyOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertEquals(2, result.getMessages().size());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "TestState1", "SomeOutput");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState2", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.deletePolicyStateOutput("MyTestPolicy", null, "TestState2", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateOutput("MyTestPolicy", null, "TestState2", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOutput", "inEvent",
            "0.0.1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
            "0.0.1", "TestState1");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState1", "SomeOtherOutput", "inEvent",
            "0.0.1", "TestState2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "AnotherOtherOutput",
            "outEvent0", "0.0.1", "TestState3");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateOutput("MyTestPolicy", "0.0.1", "TestState2", "YetAnotherOtherOutput",
            "outEvent0", "0.0.1", "TestState3");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createPolicyStateFinalizerLogic(null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", "SFLName01", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "1.2.3", "TestState1", "SFLName01", null,
            null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName01", null,
            null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName01",
            "NewTSL00", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02",
            "UNDEFINED", "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName03", "MVEL",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName03", "MVEL",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName04", "JAVA",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName05", "JYTHON",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06",
            "JAVASCRIPT", "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName07", "JRUBY",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updatePolicyStateFinalizerLogic(null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState99", "SomeSFLName", null,
            null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy2", null, "TestState1", "SomeSFLName", null,
            null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy1", "0.0.2", "TestState1", "SomeSFLName", null,
            null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "NonEistantSFL", null,
            null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06", null,
            null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06", "",
            "Some Other Policy Logic");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06", "MVEL",
            "Some Other Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyPolicy012", null, "TestState1", "SFLName06", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06", null,
            "Some Other Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyPolicy015", null, "TestState1", "SFLName06", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updatePolicyStateFinalizerLogic("MyPolicy014", "0.1.5", "TestState1", "SFLName06", null,
            null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listPolicyStateFinalizerLogic(null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("zooby", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("zooby", null, "looby", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("zooby", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "looby", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(6, result.getMessages().size());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());

        result = apexModel.deletePolicyStateFinalizerLogic(null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", null, "SomeState", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("zooby", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("zooby", null, "looby", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("zooby", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", null, "looby", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.2", "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName06");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(5, result.getMessages().size());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName02");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());
        result = apexModel.deletePolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName02",
            "UNDEFINED", "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName03", "MVEL",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName04", "JAVA",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", "0.0.1", "TestState1", "SFLName05", "JYTHON",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName06",
            "JAVASCRIPT", "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateFinalizerLogic("MyTestPolicy", null, "TestState1", "SFLName07", "JRUBY",
            "Some Policy Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createTask("TestTask0", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTask("TestTask1", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTask("TestTask2", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTask("TestTask3", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTask("TestTask4", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createPolicyStateTaskRef(null, null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null, null, null,
            "DummyOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "1.2.3", "SomeState", null, null, null, null,
            "DummyOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("AnyOldPolicy", "1.2.3", "SomeState", null, null, null, null,
            "DummyOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", null,
            null, null, "DummyOutput");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName",
            "SomeTask", "Zooby|", null, "DummyOutput");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName",
            "SomeTask", "0.0.1", null, "DummyOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", null, "DummyOutput");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", "Some Policy Logic", "DummyOutput");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", "DIRECT", "DummyOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", "LOGIC", "DummyOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", "DIRECT", "SFLName07");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", "LOGIC", "SomeOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", "DIRECT", "SomeOutput");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "NonExistantTask", "0.0.1", "DIRECT", "SomeOutput");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName", "task",
            "0.0.1", "LOGIC", "SFLName07");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "TestTask0", "0.0.1", "LOGIC", "SFLName07");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "TestTask1", "0.0.1", "DIRECT", "SomeOtherOutput");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "TestTask2", "0.0.1", "LOGIC", "SFLName07");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "TestTask3", "0.0.1", "DIRECT", "SomeOtherOutput");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, "TestTask4", "0.0.1",
            "LOGIC", "SFLName07");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, "TestTask4", "0.0.1",
            "LOGIC", "SFLName07");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", "TestTask4", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "TestTask4", "0.0.1", "FUNKY", "SFLName07");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "TestTask4", "0.0.1", "UNDEFINED", "SFLName07");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeTaskLocalName",
            "TestTask4", "0.0.1", "LOGIC", "SFLName07");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, "TestTask0", "0.0.1",
            "LOGIC", "SFLName07");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        result = apexModel.listPolicyStateTaskRef(null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateTaskRef("zooby", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateTaskRef("zooby", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("zooby", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(12, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "SomeOldTask", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "task", "1.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", "task", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());

        result = apexModel.deletePolicyStateTaskRef(null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("zooby", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("zooby", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("zooby", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "ADummyTask", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", "0.0.1", "TestState1", "task", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(12, result.getMessages().size());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", "TestTask0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(10, result.getMessages().size());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", "TestTask2", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(8, result.getMessages().size());
        result = apexModel.deletePolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());
        result = apexModel.listPolicyStateTaskRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createPolicyStateContextRef(null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "1.2.3", "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("AnyOldPolicy", "1.2.3", "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTask", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTask", "Zooby|");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "SomeTask", "0.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "task", "0.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyPolicy123", null, null, "AContextMap00", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyPolicy123", null, "TestState1", "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "4.5.6", "TestState1", "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.1.4", "TestState1", "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        result = apexModel.listPolicyStateContextRef(null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateContextRef("zooby", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateContextRef("zooby", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("zooby", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "AContextMap04", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "1.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deletePolicyStateContextRef(null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "SomeState", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateContextRef("zooby", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateContextRef("zooby", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("zooby", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "looby", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.2", "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "ADummyContextMap", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "AContextMap04", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum0", "0.1.5");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", "contextAlbum1", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.createPolicyStateContextRef("MyTestPolicy", "0.0.1", "TestState1", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deletePolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listPolicyStateContextRef("MyTestPolicy", null, "TestState1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deletePolicy(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(3, result.getMessages().size());

        result = apexModel.listPolicy(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(0, result.getMessages().size());
    }
}
