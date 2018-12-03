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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the JavaTaskExecutor class.
 *
 */
public class JavaTaskExecutorTest {
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
    public void testJavaTaskExecutor() {
        JavaTaskExecutor jte = new JavaTaskExecutor();
        assertNotNull(jte);

        try {
            jte.prepare();
            fail("test should throw an exception here");
        } catch (Exception jteException) {
            assertEquals(java.lang.NullPointerException.class, jteException.getClass());
        }

        AxTask task = new AxTask();
        ApexInternalContext internalContext = null;
        try {
            internalContext = new ApexInternalContext(new AxPolicyModel());
        } catch (ContextException e) {
            fail("test should not throw an exception here");
        }
        jte.setContext(null, task, internalContext);

        try {
            jte.prepare();
            fail("test should throw an exception here");
        } catch (Exception jteException) {
            assertEquals("instantiation error on Java class \"\"", jteException.getMessage());
        }

        task.getTaskLogic().setLogic("java.lang.String");

        try {
            jte.prepare();
        } catch (Exception jteException) {
            fail("test should not throw an exception here");
        }

        try {
            jte.execute(-1, null);
            fail("test should throw an exception here");
        } catch (Exception jteException) {
            assertEquals(java.lang.NullPointerException.class, jteException.getClass());
        }

        Map<String, Object> incomingParameters = new HashMap<>();
        try {
            jte.execute(-1, incomingParameters);
            fail("test should throw an exception here");
        } catch (Exception jteException) {
            assertEquals("task logic failed to run for task  \"NULL:0.0.0\"", jteException.getMessage());
        }

        task.getTaskLogic().setLogic("org.onap.policy.apex.plugins.executor.java.DummyJavaTaskLogic");
        try {
            jte.prepare();
            jte.execute(-1, incomingParameters);
            fail("test should throw an exception here");
        } catch (Exception jteException) {
            assertEquals("execute-post: task logic execution failure on task \"NULL\" in model NULL:0.0.0",
                            jteException.getMessage());
        }

        try {
            jte.prepare();
            Map<String, Object> returnMap = jte.execute(0, incomingParameters);
            assertEquals(0, returnMap.size());
            jte.cleanUp();
        } catch (Exception jteException) {
            fail("test should not throw an exception here");
        }
    }
}
