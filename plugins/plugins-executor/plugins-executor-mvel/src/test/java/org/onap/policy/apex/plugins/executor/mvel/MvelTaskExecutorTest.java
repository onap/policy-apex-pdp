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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
 * Test the MvelTaskExecutor class.
 *
 */
public class MvelTaskExecutorTest {
    /**
     * Initiate Parameters.
     */
    @Before
    public void initiateParameters() {
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
    }

    /**
     * Clear Parameters.
     */
    @After
    public void clearParameters() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
    }

    @Test
    public void testMvelTaskExecutor() {
        MvelTaskExecutor mte = new MvelTaskExecutor();
        assertNotNull(mte);

        try {
            mte.prepare();
            fail("test should throw an exception here");
        } catch (Exception mteException) {
            assertEquals(java.lang.NullPointerException.class, mteException.getClass());
        }

        AxTask task = new AxTask();
        ApexInternalContext internalContext = null;
        try {
            internalContext = new ApexInternalContext(new AxPolicyModel());
        } catch (ContextException e) {
            fail("test should not throw an exception here");
        }
        mte.setContext(null, task, internalContext);

        task.getTaskLogic().setLogic("x > 1 2 ()");
        try {
            mte.prepare();
            fail("test should throw an exception here");
        } catch (Exception mteException) {
            assertEquals("failed to compile MVEL code for task NULL:0.0.0", mteException.getMessage());
        }

        task.getTaskLogic().setLogic("x");

        try {
            mte.prepare();
        } catch (Exception mteException) {
            fail("test should not throw an exception here");
        }

        try {
            mte.execute(-1, new Properties(), null);
            fail("test should throw an exception here");
        } catch (Exception mteException) {
            assertEquals(java.lang.NullPointerException.class, mteException.getClass());
        }

        Map<String, Object> incomingParameters = new HashMap<>();
        try {
            mte.execute(-1, new Properties(), incomingParameters);
            fail("test should throw an exception here");
        } catch (Exception mteException) {
            assertEquals("failed to execute MVEL code for task NULL:0.0.0", mteException.getMessage());
        }

        task.getTaskLogic().setLogic("executionId != -1");
        try {
            mte.prepare();
            mte.execute(-1, new Properties(), incomingParameters);
            fail("test should throw an exception here");
        } catch (Exception mteException) {
            assertEquals("execute-post: task logic execution failure on task \"NULL\" in model NULL:0.0.0",
                    mteException.getMessage());
        }

        try {
            mte.prepare();
            Map<String, Object> returnMap = mte.execute(0, new Properties(), incomingParameters);
            assertEquals(0, returnMap.size());
            mte.cleanUp();
        } catch (Exception mteException) {
            fail("test should not throw an exception here");
        }
    }
}
