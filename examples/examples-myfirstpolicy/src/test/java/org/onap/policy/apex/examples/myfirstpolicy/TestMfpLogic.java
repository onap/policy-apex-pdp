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

package org.onap.policy.apex.examples.myfirstpolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.examples.myfirstpolicy.model.MFPDomainModelFactory;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * The Class TestMfpLogic.
 */
public class TestMfpLogic {

    private static final Map<String, String> LOGICEXTENSIONS = new LinkedHashMap<>();

    /**
     * Test setup.
     */
    @BeforeClass
    public static void testMfpUseCaseSetup() {
        LOGICEXTENSIONS.put("MVEL", "mvel");
        LOGICEXTENSIONS.put("JAVASCRIPT", "js");
    }

    /**
     * Check logic for MyFirstPolicy#1.
     */
    @Test
    public void testMfp1TaskLogic() {
        final AxPolicyModel apexPolicyModel = new MFPDomainModelFactory().getMFP1PolicyModel();
        assertNotNull(apexPolicyModel);

        final Map<String, String> logics = new LinkedHashMap<>();
        logics.putAll(getTslLogics(apexPolicyModel));
        logics.putAll(getTaskLogics(apexPolicyModel));

        for (final Entry<String, String> logicvalue : logics.entrySet()) {
            final String filename = "examples/models/MyFirstPolicy/1/" + logicvalue.getKey();
            final String logic = logicvalue.getValue();
            final String expectedlogic = ResourceUtils.getResourceAsString(filename);
            assertNotNull("File " + filename + " was not found. It should contain logic for PolicyModel "
                    + apexPolicyModel.getKey(), expectedlogic);
            assertEquals(
                    "The task in " + filename + " is not the same as the relevant logic in PolicyModel "
                            + apexPolicyModel.getKey(),
                    expectedlogic.replaceAll("\\s", ""), logic.replaceAll("\\s", ""));
        }
    }


    /**
     * Check logic for MyFirstPolicyAlt#1.
     */
    @Test
    public void testMfp1AltTaskLogic() {
        final AxPolicyModel apexPolicyModel = new MFPDomainModelFactory().getMFP1AltPolicyModel();
        assertNotNull(apexPolicyModel);

        final Map<String, String> logics = new LinkedHashMap<>();
        logics.putAll(getTslLogics(apexPolicyModel));
        logics.putAll(getTaskLogics(apexPolicyModel));

        for (final Entry<String, String> logicvalue : logics.entrySet()) {
            final String filename = "examples/models/MyFirstPolicy/1/" + logicvalue.getKey();
            final String logic = logicvalue.getValue();
            final String expectedlogic = ResourceUtils.getResourceAsString(filename);
            assertNotNull("File " + filename + " was not found. It should contain logic for PolicyModel "
                    + apexPolicyModel.getKey(), expectedlogic);
            assertEquals(
                    "The task in " + filename + " is not the same as the relevant logic in PolicyModel "
                            + apexPolicyModel.getKey(),
                    expectedlogic.replaceAll("\\s", ""), logic.replaceAll("\\s", ""));
        }
    }

    /**
     * Check logic for MyFirstPolicy2.
     */
    @Test
    public void testMfp2TaskLogic() {
        final AxPolicyModel apexPolicyModel = new MFPDomainModelFactory().getMFP2PolicyModel();
        assertNotNull(apexPolicyModel);

        final Map<String, String> logics = new LinkedHashMap<>();
        logics.putAll(getTslLogics(apexPolicyModel));
        logics.putAll(getTaskLogics(apexPolicyModel));

        for (final Entry<String, String> logicvalue : logics.entrySet()) {
            final String filename = "examples/models/MyFirstPolicy/2/" + logicvalue.getKey();
            final String logic = logicvalue.getValue();
            final String expectedlogic = ResourceUtils.getResourceAsString(filename);
            assertNotNull("File " + filename + " was not found. It should contain logic for PolicyModel "
                    + apexPolicyModel.getKey(), expectedlogic);
            assertEquals(
                    "The task in " + filename + " is not the same as the relevant logic in PolicyModel "
                            + apexPolicyModel.getKey(),
                    expectedlogic.replaceAll("\\s", ""), logic.replaceAll("\\s", ""));
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
        for (final Entry<AxArtifactKey, AxPolicy> policyentry : apexPolicyModel.getPolicies().getPolicyMap()
                .entrySet()) {
            for (final Entry<String, AxState> statesentry : policyentry.getValue().getStateMap().entrySet()) {
                final AxState state = statesentry.getValue();
                final String tsllogic = state.getTaskSelectionLogic().getLogic();
                final String tsllogicflavour = state.getTaskSelectionLogic().getLogicFlavour();
                if (tsllogic != null && tsllogic.trim().length() > 0) {
                    assertNotNull(
                            "Logic Type \"" + tsllogicflavour + "\" in state " + statesentry.getKey() + " in policy "
                                    + policyentry.getKey() + " is not supported in this test",
                            LOGICEXTENSIONS.get(tsllogicflavour.toUpperCase()));
                    final String filename = policyentry.getKey().getName() + "_" + statesentry.getKey() + "TSL."
                            + LOGICEXTENSIONS.get(tsllogicflavour.toUpperCase());
                    ret.put(filename, tsllogic);
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
            final String tasklogic = task.getTaskLogic().getLogic();
            final String tasklogicflavour = task.getTaskLogic().getLogicFlavour();
            assertTrue("No/Blank logic found in task " + taskentry.getKey(),
                    (tasklogic != null && tasklogic.trim().length() > 0));
            assertNotNull("Logic Type \"" + tasklogicflavour + "\" in task " + taskentry.getKey()
                    + " is not supported in this test", LOGICEXTENSIONS.get(tasklogicflavour.toUpperCase()));
            final String filename =
                    taskentry.getKey().getName() + "." + LOGICEXTENSIONS.get(tasklogicflavour.toUpperCase());
            ret.put(filename, tasklogic);
        }
        return ret;
    }
}
