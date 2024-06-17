/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.executor.mvel;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the MvelTaskExecutor class.
 *
 */
class MvelTaskExecutorTest {
    /**
     * Initiate Parameters.
     */
    @BeforeEach
    void initiateParameters() {
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
    }

    /**
     * Clear Parameters.
     */
    @AfterEach
    void clearParameters() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
    }

    @Test
    void testMvelTaskExecutor() throws StateMachineException, ContextException {
        MvelTaskExecutor mte = new MvelTaskExecutor();
        assertNotNull(mte);

        assertThatThrownBy(mte::prepare)
            .isInstanceOf(java.lang.NullPointerException.class);
        AxTask task = new AxTask();
        ApexInternalContext internalContext = null;
        internalContext = new ApexInternalContext(new AxPolicyModel());
        task.setInputEvent(new AxEvent());
        task.setOutputEvents(new TreeMap<>());
        mte.setContext(null, task, internalContext);

        task.getTaskLogic().setLogic("x > 1 2 ()");
        assertThatThrownBy(mte::prepare)
            .hasMessage("failed to compile MVEL code for task NULL:0.0.0");
        task.getTaskLogic().setLogic("x");

        mte.prepare();

        assertThatThrownBy(() -> mte.execute(-1, new Properties(), null))
            .isInstanceOf(java.lang.NullPointerException.class);
        Map<String, Object> incomingParameters = new HashMap<>();
        assertThatThrownBy(() -> mte.execute(-1, new Properties(), incomingParameters))
            .hasMessage("failed to execute MVEL code for task NULL:0.0.0");
        task.getTaskLogic().setLogic("executionId != -1");
        mte.prepare();
        assertThatThrownBy(() -> mte.execute(-1, new Properties(), incomingParameters))
            .hasMessage("execute-post: task logic execution failure on task \"NULL\" in model NULL:0.0.0");

        mte.prepare();
        Map<String, Map<String, Object>> returnMap = mte.execute(0, new Properties(), incomingParameters);
        assertEquals(0, returnMap.size());
        mte.cleanUp();
    }
}
