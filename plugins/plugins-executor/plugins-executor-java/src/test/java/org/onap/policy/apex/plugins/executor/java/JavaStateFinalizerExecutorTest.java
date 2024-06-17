/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.plugins.executor.java;

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
 * Test the JavaStateFinalizerExecutor class.
 */
class JavaStateFinalizerExecutorTest {
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
        JavaStateFinalizerExecutor jsfe = new JavaStateFinalizerExecutor();
        assertNotNull(jsfe);

        assertThatThrownBy(jsfe::prepare).isInstanceOf(java.lang.NullPointerException.class);
        ApexInternalContext internalContext = null;

        internalContext = new ApexInternalContext(new AxPolicyModel());

        StateExecutor parentStateExcutor = null;

        parentStateExcutor = new StateExecutor(new ExecutorFactoryImpl());

        AxState state = new AxState();
        parentStateExcutor.setContext(null, state, internalContext);
        AxStateFinalizerLogic stateFinalizerLogic = new AxStateFinalizerLogic();
        jsfe.setContext(parentStateExcutor, stateFinalizerLogic, internalContext);

        assertThatThrownBy(jsfe::prepare)
            .hasMessage("instantiation error on Java class \"\"");
        stateFinalizerLogic.setLogic("java.lang.String");

        jsfe.prepare();

        assertThatThrownBy(() -> jsfe.execute(-1, new Properties(), null))
            .hasMessage("state finalizer logic failed to run for state finalizer  \"NULL:0.0.0:NULL:NULL\"");
        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event = new EnEvent(axEvent);
        assertThatThrownBy(() -> jsfe.execute(-1, new Properties(), event))
            .hasMessage("state finalizer logic failed to run for state finalizer  \"NULL:0.0.0:NULL:NULL\"");
        stateFinalizerLogic.setLogic("org.onap.policy.apex.plugins.executor.java.DummyJavaStateFinalizerLogic");
        jsfe.prepare();
        assertThatThrownBy(() -> {
            jsfe.execute(-1, new Properties(), event);
        }).hasMessage("execute-post: state finalizer logic execution failure on state "
            + "\"NULL:0.0.0:NULL:NULL\" on finalizer logic NULL:0.0.0:NULL:NULL");
        state.getStateOutputs().put("SelectedOutputIsMe", null);

        jsfe.prepare();
        String stateOutput = jsfe.execute(0, new Properties(), event);
        assertEquals("SelectedOutputIsMe", stateOutput);
        jsfe.cleanUp();

    }
}
