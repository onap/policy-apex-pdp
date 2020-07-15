/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
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
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the JrubyTaskExecutor class.
 *
 */
public class JrubyTaskExecutorTest {
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
    public void testJrubyTaskExecutor() throws StateMachineException, ContextException,
        IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        // Run test twice to check for incorrect shutdown activity
        jrubyExecutorTest();
        jrubyExecutorTest();
    }

    /**
     * Test the JRuby executor.
     */
    private void jrubyExecutorTest() throws StateMachineException, ContextException,
        IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        JrubyTaskExecutor jte = new JrubyTaskExecutor();
        assertNotNull(jte);

        Field fieldContainer = JrubyTaskExecutor.class.getDeclaredField("container");
        fieldContainer.setAccessible(true);
        fieldContainer.set(jte, null);
        assertThatThrownBy(jte::prepare).isInstanceOf(java.lang.NullPointerException.class);
        AxTask task = new AxTask();
        ApexInternalContext internalContext = null;
        internalContext = new ApexInternalContext(new AxPolicyModel());

        jte.setContext(null, task, internalContext);

        jte.prepare();

        Map<String, Object> incomingParameters = new HashMap<>();
        assertThatThrownBy(() -> jte.execute(-1, new Properties(), incomingParameters))
            .hasMessage("execute-post: task logic execution failure on task \"NULL\" in model NULL:0.0.0");
        final String jrubyLogic = "if executor.executionId == -1" + "\n return false" + "\n else " + "\n return true"
                        + "\n end";
        task.getTaskLogic().setLogic(jrubyLogic);

        jte.prepare();
        Map<String, Object> returnMap = jte.execute(0, new Properties(), incomingParameters);
        assertEquals(0, returnMap.size());
        jte.cleanUp();

        jte.prepare();
        Map<String, Object> returnMap1 = jte.execute(0, new Properties(), incomingParameters);
        assertEquals(0, returnMap1.size());
        jte.cleanUp();
    }
}
