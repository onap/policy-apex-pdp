/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.executor.javascript;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.core.engine.EngineParameterConstants;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.StateExecutor;
import org.onap.policy.apex.core.engine.executor.impl.ExecutorFactoryImpl;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the JavascriptStateFinalizerExecutor class.
 *
 */
public class JavascriptStateFinalizerExecutorTest {
    /**
     * Initiate Parameters.
     */
    @Before
    public void initiateParameters() {
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
        ParameterService.register(new EngineParameters());
    }

    /**
     * Clear down Parameters.
     */
    @After
    public void clearParameters() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(EngineParameterConstants.MAIN_GROUP_NAME);
    }

    @Test
    public void testJavaStateFinalizerExecutor() throws Exception {
        JavascriptStateFinalizerExecutor jsfe = new JavascriptStateFinalizerExecutor();
        assertNotNull(jsfe);

        assertThatThrownBy(() -> {
            jsfe.prepare();
        }).isInstanceOf(java.lang.NullPointerException.class);

        ApexInternalContext internalContext = new ApexInternalContext(new AxPolicyModel());
        StateExecutor parentStateExcutor = new StateExecutor(new ExecutorFactoryImpl());

        AxState state = new AxState();
        parentStateExcutor.setContext(null, state, internalContext);
        AxStateFinalizerLogic stateFinalizerLogic = new AxStateFinalizerLogic();
        jsfe.setContext(parentStateExcutor, stateFinalizerLogic, internalContext);

        stateFinalizerLogic.setLogic("return false");
        assertThatThrownBy(() -> {
            jsfe.prepare();
        }).hasMessage("logic failed to compile for NULL:0.0.0:NULL:NULL "
            + "with message: invalid return (NULL:0.0.0:NULL:NULL#1)");

        Map<String, Object> incomingParameters1 = new HashMap<>();
        assertThatThrownBy(() -> {
            jsfe.execute(-1, new Properties(), incomingParameters1);
        }).hasMessage("execution failed, executor NULL:0.0.0:NULL:NULL is not running");

        assertThatThrownBy(() -> {
            jsfe.prepare();
        }).hasMessage("initiation failed, executor NULL:0.0.0:NULL:NULL failed to start");

        assertThatThrownBy(() -> {
            jsfe.cleanUp();
        }).hasMessage("cleanup failed, executor NULL:0.0.0:NULL:NULL is not running");

        JavascriptStateFinalizerExecutor jsfe1 = new JavascriptStateFinalizerExecutor();
        stateFinalizerLogic.setLogic("java.lang.String");
        jsfe1.setContext(parentStateExcutor, stateFinalizerLogic, internalContext);
        jsfe1.prepare();

        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event = new EnEvent(axEvent);

        assertThatThrownBy(() -> {
            jsfe1.execute(-1, new Properties(), event);
        }).hasMessage(
            "execute: logic for NULL:0.0.0:NULL:NULL returned a non-boolean value [JavaClass java.lang.String]");

        assertThatThrownBy(() -> {
            jsfe1.execute(-1, new Properties(), event);
        }).hasMessage(
            "execute: logic for NULL:0.0.0:NULL:NULL returned a non-boolean value [JavaClass java.lang.String]");

        assertThatCode(() -> {
            jsfe1.cleanUp();
        }).doesNotThrowAnyException();

        JavascriptStateFinalizerExecutor jsfe2 = new JavascriptStateFinalizerExecutor();

        stateFinalizerLogic.setLogic("executor.setSelectedStateOutputName(\"SelectedOutputIsMe\");\n true;");

        jsfe2.setContext(parentStateExcutor, stateFinalizerLogic, internalContext);
        assertThatCode(() -> {
            jsfe2.prepare();
        }).doesNotThrowAnyException();

        state.getStateOutputs().put("SelectedOutputIsMe", null);

        String stateOutput = jsfe2.execute(0, new Properties(), event);
        assertEquals("SelectedOutputIsMe", stateOutput);

        assertThatCode(() -> {
            jsfe2.cleanUp();
        }).doesNotThrowAnyException();
    }
}
