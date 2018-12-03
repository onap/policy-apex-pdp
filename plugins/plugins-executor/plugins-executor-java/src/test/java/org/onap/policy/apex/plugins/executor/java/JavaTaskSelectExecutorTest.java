/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the JavaTaskSelectExecutor class.
 *
 */
public class JavaTaskSelectExecutorTest {
    @Before
    public void initiateParameters() {
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
    }

    @After
    public void clearParameters() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
    }

    @Test
    public void testJavaTaskSelectExecutor() {
        JavaTaskSelectExecutor jtse = new JavaTaskSelectExecutor();
        assertNotNull(jtse);

        try {
            jtse.prepare();
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals(java.lang.NullPointerException.class, jtseException.getClass());
        }

        AxState state = new AxState();
        ApexInternalContext internalContext = null;
        try {
            internalContext = new ApexInternalContext(new AxPolicyModel());
        } catch (ContextException e) {
            fail("test should not throw an exception here");
        }
        jtse.setContext(null, state, internalContext);

        try {
            jtse.prepare();
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals("instantiation error on Java class \"\"", jtseException.getMessage());
        }

        state.getTaskSelectionLogic().setLogic("java.lang.String");

        try {
            jtse.prepare();
        } catch (Exception jtseException) {
            fail("test should not throw an exception here");
        }

        try {
            jtse.execute(-1, null);
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals(java.lang.NullPointerException.class, jtseException.getClass());
        }

        AxEvent axEvent = new AxEvent(new AxArtifactKey("Event", "0.0.1"));
        EnEvent event = new EnEvent(axEvent);
        try {
            jtse.execute(-1, event);
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals("task selection logic failed to run for state  \"NULL:0.0.0:NULL:NULL\"",
                            jtseException.getMessage());
        }

        state.getTaskSelectionLogic()
                        .setLogic("org.onap.policy.apex.plugins.executor.java.DummyJavaTaskSelectionLogic");
        try {
            jtse.prepare();
            jtse.execute(-1, event);
            fail("test should throw an exception here");
        } catch (Exception jtseException) {
            assertEquals("execute-post: task selection logic failed on state \"NULL:0.0.0:NULL:NULL\"",
                            jtseException.getMessage());
        }

        try {
            jtse.prepare();
            AxArtifactKey taskKey = jtse.execute(0, event);
            assertEquals("NULL:0.0.0", taskKey.getId());
            jtse.cleanUp();
        } catch (Exception jtseException) {
            fail("test should not throw an exception here");
        }
    }
}
