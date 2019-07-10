/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.executor.jython;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the JythonStateFinalizerExecutor class.
 *
 */
public class JythonStateFinalizerExecutorTest {
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
    public void testJythonStateFinalizerExecutor() {
        JythonStateFinalizerExecutor jsfe = new JythonStateFinalizerExecutor();
        assertNotNull(jsfe);

        try {
            jsfe.prepare();
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals(java.lang.NullPointerException.class, jtseException.getClass());
        }

        ApexInternalContext internalContext = null;
        try {
            internalContext = new ApexInternalContext(new AxPolicyModel());
        } catch (ContextException e) {
            fail("test should not throw an exception here");
        }

        StateExecutor parentStateExcutor = null;
        try {
            parentStateExcutor = new StateExecutor(new ExecutorFactoryImpl());
        } catch (StateMachineException e) {
            fail("test should not throw an exception here");
        }

        AxState state = new AxState();
        Map<String, AxStateOutput> stateOutputs = new TreeMap<>();
        AxArtifactKey triggerKey = new AxArtifactKey("TriggerName", "0.0.1");
        AxStateOutput isMe = new AxStateOutput(new AxReferenceKey(), triggerKey, new AxReferenceKey());
        stateOutputs.put("SelectedOutputIsMe", isMe);
        state.setStateOutputs(stateOutputs);

        parentStateExcutor.setContext(null, state, internalContext);
        AxStateFinalizerLogic stateFinalizerLogic = new AxStateFinalizerLogic();
        jsfe.setContext(parentStateExcutor, stateFinalizerLogic, internalContext);

        stateFinalizerLogic.setLogic("return false");

        try {
            jsfe.prepare();
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals(
                    "failed to compile Jython code for state finalizer AxReferenceKey:"
                            + "(parentKeyName=NULL,parentKeyVersion=0.0.0,parentLocalName=NULL,localName=NULL)",
                    jtseException.getMessage());
        }

        String scriptSource = "for i in range(0,10): print(i)";
        stateFinalizerLogic.setLogic(scriptSource);
        try {
            jsfe.prepare();
            jsfe.execute(-1, new Properties(), null);
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals(
                    "failed to execute Jython code for state finalizer AxReferenceKey:(parentKeyName=NULL,"
                            + "parentKeyVersion=0.0.0,parentLocalName=NULL,localName=NULL)",
                    jtseException.getMessage());
        }

        scriptSource = "setattr(executor, 'selectedStateOutputName', 'SelectedOutputIsMe')\n"
                + "returnValue=('' if executor == -1 else True)";
        stateFinalizerLogic.setLogic(scriptSource);
        try {
            jsfe.prepare();
        } catch (Exception jteException) {
            fail("test should not throw an exception here");
        }

        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event = new EnEvent(axEvent);
        try {
            jsfe.execute(0, new Properties(), event);
        } catch (Exception jtseException) {
            jtseException.printStackTrace();
            fail("test should not throw an exception here");
        }

        try {
            jsfe.prepare();
            String stateOutput = jsfe.execute(0, new Properties(), event);
            assertEquals("SelectedOutputIsMe", stateOutput);
            jsfe.cleanUp();
        } catch (Exception jtseException) {
            jtseException.printStackTrace();
            fail("test should not throw an exception here");
        }
    }
}
