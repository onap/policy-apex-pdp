/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.core.engine.EngineParameterConstants;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.StateExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.core.engine.executor.impl.ExecutorFactoryImpl;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the MvelStateFinalizerExecutor class.
 *
 */
class MvelStateFinalizerExecutorTest {
    /**
     * Initiate Parameters.
     */
    @BeforeEach
    void initiateParameters() {
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
        ParameterService.register(new EngineParameters());
    }

    /**
     * Clear down Parameters.
     */
    @AfterEach
    void clearParameters() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(EngineParameterConstants.MAIN_GROUP_NAME);
    }

    @Test
    void testJavaStateFinalizerExecutor() throws StateMachineException, ContextException {
        MvelStateFinalizerExecutor msfe = new MvelStateFinalizerExecutor();
        assertNotNull(msfe);

        assertThatThrownBy(msfe::prepare).isInstanceOf(java.lang.NullPointerException.class);
        ApexInternalContext internalContext;
        internalContext = new ApexInternalContext(new AxPolicyModel());

        StateExecutor parentStateExecutor;

        parentStateExecutor = new StateExecutor(new ExecutorFactoryImpl());

        AxState state = new AxState();
        parentStateExecutor.setContext(null, state, internalContext);
        AxStateFinalizerLogic stateFinalizerLogic = new AxStateFinalizerLogic();
        msfe.setContext(parentStateExecutor, stateFinalizerLogic, internalContext);

        stateFinalizerLogic.setLogic("x > 1 2 ()");
        assertThatThrownBy(msfe::prepare).hasMessage("failed to compile MVEL code for state NULL:0.0.0:NULL:NULL");
        stateFinalizerLogic.setLogic("java.lang.String");

        msfe.prepare();

        assertThatThrownBy(() -> msfe.execute(-1, new Properties(), null))
            .hasMessage("failed to execute MVEL code for state NULL:0.0.0:NULL:NULL");
        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event = new EnEvent(axEvent);
        assertThatThrownBy(() -> msfe.execute(-1, new Properties(), event))
            .hasMessage("failed to execute MVEL code for state NULL:0.0.0:NULL:NULL");
        stateFinalizerLogic.setLogic("executionId !=-1");
        msfe.prepare();
        assertThatThrownBy(() -> msfe.execute(-1, new Properties(), event))
            .hasMessage("execute-post: state finalizer logic execution failure on state \"NULL:0.0.0:"
                + "NULL:NULL\" on finalizer logic NULL:0.0.0:NULL:NULL");
        stateFinalizerLogic
            .setLogic("if (executionId == -1) {return false;}setSelectedStateOutputName(\"SelectedOutputIsMe\");"
                + "return true;");
        state.getStateOutputs().put("SelectedOutputIsMe", null);

        msfe.prepare();
        String stateOutput = msfe.execute(0, new Properties(), event);
        assertEquals("SelectedOutputIsMe", stateOutput);
        msfe.cleanUp();
    }
}
