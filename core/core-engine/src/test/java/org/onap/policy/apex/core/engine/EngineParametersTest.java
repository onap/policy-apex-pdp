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

package org.onap.policy.apex.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the executor parameters.
 *
 */
public class EngineParametersTest {

    @Test
    public void test() {
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
        
        assertTrue(pars.validate().isValid());
        
        ParameterService.register(pars);
        ParameterService.deregister(pars);
    }
}
