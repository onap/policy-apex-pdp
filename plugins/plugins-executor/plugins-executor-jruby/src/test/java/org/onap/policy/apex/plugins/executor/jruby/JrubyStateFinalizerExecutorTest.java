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

package org.onap.policy.apex.plugins.executor.jruby;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
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
 * Test the JrubyStateFinalizerExecutor class.
 *
 */
class JrubyStateFinalizerExecutorTest {
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
    void testJrubyStateFinalizerExecutor() throws StateMachineException, ContextException,
        NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        JrubyStateFinalizerExecutor jsfe = new JrubyStateFinalizerExecutor();
        assertNotNull(jsfe);

        Field fieldContainer = JrubyStateFinalizerExecutor.class.getDeclaredField("container");
        fieldContainer.setAccessible(true);
        fieldContainer.set(jsfe, null);
        assertThatThrownBy(jsfe::prepare).isInstanceOf(java.lang.NullPointerException.class);
        ApexInternalContext internalContext = null;

        internalContext = new ApexInternalContext(new AxPolicyModel());

        StateExecutor parentStateExcutor = null;
        parentStateExcutor = new StateExecutor(new ExecutorFactoryImpl());

        AxState state = new AxState();
        parentStateExcutor.setContext(null, state, internalContext);
        AxStateFinalizerLogic stateFinalizerLogic = new AxStateFinalizerLogic();
        jsfe.setContext(parentStateExcutor, stateFinalizerLogic, internalContext);

        jsfe.prepare();

        assertThatThrownBy(() -> jsfe.execute(-1, new Properties(), null))
            .hasMessage("execute-post: state finalizer logic execution failure on state \""
                + "NULL:0.0.0:NULL:NULL\" on finalizer logic NULL:0.0.0:NULL:NULL");
        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));

        final String jrubyLogic = """
            if executor.executionId == -1
             return false
            else
             executor.setSelectedStateOutputName("SelectedOutputIsMe")
             return true
             end""";
        stateFinalizerLogic.setLogic(jrubyLogic);

        EnEvent event = new EnEvent(axEvent);
        state.getStateOutputs().put("SelectedOutputIsMe", null);
        jsfe.prepare();
        String stateOutput = jsfe.execute(0, new Properties(), event);
        assertEquals("SelectedOutputIsMe", stateOutput);
        jsfe.cleanUp();

    }
}
