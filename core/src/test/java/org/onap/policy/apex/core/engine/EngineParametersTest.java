/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.core.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the executor parameters.
 */
class EngineParametersTest {

    @Test
    void test() {
        EngineParameters pars = new EngineParameters();
        pars.setName("Name");
        assertEquals("Name", pars.getName());

        ContextParameters contextPars = new ContextParameters();

        pars.setContextParameters(contextPars);
        assertEquals(contextPars, pars.getContextParameters());

        Map<String, ExecutorParameters> executorParameterMap = new LinkedHashMap<>();
        executorParameterMap.put("Executor", new ExecutorParameters());
        pars.setExecutorParameterMap(executorParameterMap);
        assertEquals(executorParameterMap, pars.getExecutorParameterMap());

        List<TaskParameters> taskParameters = new ArrayList<>();
        taskParameters.add(new TaskParameters("param1key", "param1value", "param1taskId"));
        taskParameters.add(new TaskParameters("param1key", "param1value", null));
        pars.setTaskParameters(taskParameters);

        assertThat(pars.validate().getResult()).isNull();
        assertTrue(pars.validate().isValid());

        ParameterService.register(pars);
        ParameterService.deregister(pars);
    }

    @Test
    void test_invalid() {
        EngineParameters pars = new EngineParameters();
        pars.setName("Name");
        assertEquals("Name", pars.getName());

        ContextParameters contextPars = new ContextParameters();

        pars.setContextParameters(contextPars);
        assertEquals(contextPars, pars.getContextParameters());

        Map<String, ExecutorParameters> executorParameterMap = Map.of("Executor", new ExecutorParameters());
        pars.setExecutorParameterMap(executorParameterMap);
        assertEquals(executorParameterMap, pars.getExecutorParameterMap());

        pars.setTaskParameters(List.of(new TaskParameters(null, "param1value", "param1taskId")));
        assertFalse(pars.validate().isValid());
        pars.setTaskParameters(List.of(new TaskParameters(" ", "param1value", "param1taskId")));
        assertFalse(pars.validate().isValid());
        pars.setTaskParameters(List.of(new TaskParameters("param1key", "", "param1taskId")));
        assertFalse(pars.validate().isValid());
    }
}
