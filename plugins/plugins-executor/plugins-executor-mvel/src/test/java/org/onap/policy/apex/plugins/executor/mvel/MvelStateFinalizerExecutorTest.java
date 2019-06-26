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

package org.onap.policy.apex.plugins.executor.mvel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Test the MvelStateFinalizerExecutor class.
 *
 */
public class MvelStateFinalizerExecutorTest {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(MvelStateFinalizerExecutorTest.class);

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
    public void testJavaStateFinalizerExecutor() {
        MvelStateFinalizerExecutor msfe = new MvelStateFinalizerExecutor();
        assertNotNull(msfe);

        try {
            msfe.prepare();
            fail("test should throw an exception here");
        } catch (Exception msfeException) {
            assertEquals(java.lang.NullPointerException.class, msfeException.getClass());
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
        parentStateExcutor.setContext(null, state, internalContext);
        AxStateFinalizerLogic stateFinalizerLogic = new AxStateFinalizerLogic();
        msfe.setContext(parentStateExcutor, stateFinalizerLogic, internalContext);

        stateFinalizerLogic.setLogic("x > 1 2 ()");
        try {
            msfe.prepare();
            fail("test should throw an exception here");
        } catch (Exception msfeException) {
            assertEquals("failed to compile MVEL code for state NULL:0.0.0:NULL:NULL", msfeException.getMessage());
        }

        stateFinalizerLogic.setLogic("java.lang.String");

        try {
            msfe.prepare();
        } catch (Exception msfeException) {
            fail("test should not throw an exception here");
        }

        try {
            msfe.execute(-1, null, null);
            fail("test should throw an exception here");
        } catch (Exception msfeException) {
            assertEquals("failed to execute MVEL code for state NULL:0.0.0:NULL:NULL",
                    msfeException.getMessage());
        }

        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event = new EnEvent(axEvent);
        try {
            msfe.execute(-1, null, event);
            fail("test should throw an exception here");
        } catch (Exception msfeException) {
            assertEquals("failed to execute MVEL code for state NULL:0.0.0:NULL:NULL",
                    msfeException.getMessage());
        }

        stateFinalizerLogic.setLogic("executionId !=-1");
        try {
            msfe.prepare();
            msfe.execute(-1, null, event);
            fail("test should throw an exception here");
        } catch (Exception msfeException) {
            assertEquals(
                    "execute-post: state finalizer logic execution failure on state \"NULL:0.0.0:NULL:NULL\" "
                            + "on finalizer logic NULL:0.0.0:NULL:NULL",
                    msfeException.getMessage());
        }

        stateFinalizerLogic.setLogic(
                "if (executionId == -1) {return false;}setSelectedStateOutputName(\"SelectedOutputIsMe\");"
                        + "return true;");
        state.getStateOutputs().put("SelectedOutputIsMe", null);
        try {
            msfe.prepare();
            String stateOutput = msfe.execute(0, null, event);
            assertEquals("SelectedOutputIsMe", stateOutput);
        } catch (Exception msfeException) {
            LOGGER.warn("Unexpected exception happened here.", msfeException);
            fail("test should not throw an exception here");
        } finally {
            try {
                msfe.cleanUp();
            } catch (StateMachineException msfeException) {
                LOGGER.warn("Unexpected exception happened here.", msfeException);
                fail("test should not throw an exception here");
            }
        }
    }
}
