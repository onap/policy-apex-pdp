/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.myfirstpolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * The Class TestMfpLogic.
 */
class MfpLogicTest {

    private static final Map<String, String> LOGICEXTENSIONS = new LinkedHashMap<>();

    /**
     * Test setup.
     */
    @BeforeAll
    static void testMfpUseCaseSetup() {
        LOGICEXTENSIONS.put("MVEL", "mvel");
        LOGICEXTENSIONS.put("JAVASCRIPT", "js");
    }

    /**
     * Check logic for MyFirstPolicy#1.
     */
    @Test
    void testMfp1TaskLogic() {
        final AxPolicyModel apexPolicyModel = new MfpDomainModelFactory().getMfp1PolicyModel();
        assertNotNull(apexPolicyModel);

        final Map<String, String> logics = new LinkedHashMap<>();
        logics.putAll(getTslLogics(apexPolicyModel));
        logics.putAll(getTaskLogics(apexPolicyModel));

        for (final Entry<String, String> logicValue : logics.entrySet()) {
            final String filename = "examples/models/MyFirstPolicy/1/" + logicValue.getKey();
            final String logic = logicValue.getValue();
            final String expectedLogic = ResourceUtils.getResourceAsString(filename);
            assertNotNull(expectedLogic, "File " + filename + " was not found. It should contain logic for PolicyModel "
                + apexPolicyModel.getKey());
            assertEquals(expectedLogic.replaceAll("\\s", ""), logic.replaceAll("\\s", ""),
                "The task in " + filename + " is not the same as the relevant logic in PolicyModel "
                    + apexPolicyModel.getKey());
        }
    }

    /**
     * Check logic for MyFirstPolicyAlt#1.
     */
    @Test
    void testMfp1AltTaskLogic() {
        final AxPolicyModel apexPolicyModel = new MfpDomainModelFactory().getMfp1AltPolicyModel();
        assertNotNull(apexPolicyModel);

        final Map<String, String> logics = new LinkedHashMap<>();
        logics.putAll(getTslLogics(apexPolicyModel));
        logics.putAll(getTaskLogics(apexPolicyModel));

        for (final Entry<String, String> logicValue : logics.entrySet()) {
            final String filename = "examples/models/MyFirstPolicy/1/" + logicValue.getKey();
            final String logic = logicValue.getValue();
            final String expectedLogic = ResourceUtils.getResourceAsString(filename);
            assertNotNull(expectedLogic, "File " + filename + " was not found. It should contain logic for PolicyModel "
                + apexPolicyModel.getKey());
            assertEquals(expectedLogic.replaceAll("\\s", ""), logic.replaceAll("\\s", ""),
                "The task in " + filename + " is not the same as the relevant logic in PolicyModel "
                    + apexPolicyModel.getKey());
        }
    }

    /**
     * Check logic for MyFirstPolicy2.
     */
    @Test
    void testMfp2TaskLogic() {
        final AxPolicyModel apexPolicyModel = new MfpDomainModelFactory().getMfp2PolicyModel();
        assertNotNull(apexPolicyModel);

        final Map<String, String> logics = new LinkedHashMap<>();
        logics.putAll(getTslLogics(apexPolicyModel));
        logics.putAll(getTaskLogics(apexPolicyModel));

        for (final Entry<String, String> logicValue : logics.entrySet()) {
            final String logic = logicValue.getValue();
            final String filename = "examples/models/MyFirstPolicy/2/" + logicValue.getKey();
            final String expectedLogic = ResourceUtils.getResourceAsString(filename);
            assertNotNull(expectedLogic, "File " + filename + " was not found. It should contain logic for PolicyModel "
                + apexPolicyModel.getKey());
            assertEquals(expectedLogic.replaceAll("\\s", ""), logic.replaceAll("\\s", ""),
                "The task in " + filename + " is not the same as the relevant logic in PolicyModel "
                    + apexPolicyModel.getKey());
        }
    }

    /**
     * Gets the TSL logics.
     *
     * @param apexPolicyModel the apex policy model
     * @return the TSL logics
     */
    private Map<String, String> getTslLogics(final AxPolicyModel apexPolicyModel) {
        final Map<String, String> ret = new LinkedHashMap<>();
        for (final Entry<AxArtifactKey, AxPolicy> policyentry :
            apexPolicyModel.getPolicies().getPolicyMap().entrySet()) {
            for (final Entry<String, AxState> statesentry : policyentry.getValue().getStateMap().entrySet()) {
                final AxState state = statesentry.getValue();
                final String tslLogic = state.getTaskSelectionLogic().getLogic();
                final String tslLogicFlavour = state.getTaskSelectionLogic().getLogicFlavour();
                if (tslLogic != null && !tslLogic.trim().isEmpty()) {
                    assertNotNull(LOGICEXTENSIONS.get(tslLogicFlavour.toUpperCase()),
                        "Logic Type \"" + tslLogicFlavour + "\" in state " + statesentry.getKey() + " in policy "
                            + policyentry.getKey() + " is not supported in this test");
                    final String filename = policyentry.getKey().getName() + "_" + statesentry.getKey() + "TSL."
                        + LOGICEXTENSIONS.get(tslLogicFlavour.toUpperCase());
                    ret.put(filename, tslLogic);
                }
            }
        }
        return ret;
    }

    /**
     * Gets the task logics.
     *
     * @param apexPolicyModel the apex policy model
     * @return the task logics
     */
    private Map<String, String> getTaskLogics(final AxPolicyModel apexPolicyModel) {
        final Map<String, String> ret = new LinkedHashMap<>();
        for (final Entry<AxArtifactKey, AxTask> taskentry : apexPolicyModel.getTasks().getTaskMap().entrySet()) {
            final AxTask task = taskentry.getValue();
            final String taskLogic = task.getTaskLogic().getLogic();
            final String taskLogicFlavour = task.getTaskLogic().getLogicFlavour();
            assertTrue((taskLogic != null && !taskLogic.trim().isEmpty()),
                "No/Blank logic found in task " + taskentry.getKey());
            assertNotNull(LOGICEXTENSIONS.get(taskLogicFlavour.toUpperCase()),
                "Logic Type \"" + taskLogicFlavour + "\" in task " + taskentry.getKey()
                    + " is not supported in this test");
            final String filename =
                taskentry.getKey().getName() + "." + LOGICEXTENSIONS.get(taskLogicFlavour.toUpperCase());
            ret.put(filename, taskLogic);
        }
        return ret;
    }
}
