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

import org.junit.Test;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the executor parameters.
 *
 */
public class ExecutorParametersTest {

    @Test
    public void test() {
        ExecutorParameters pars = new ExecutorParameters();
        pars.setName("Name");
        assertEquals("Name", pars.getName());
        pars.setStateFinalizerExecutorPluginClass("some.state.finalizer.plugin.class");
        assertEquals("some.state.finalizer.plugin.class", pars.getStateFinalizerExecutorPluginClass());
        pars.setTaskExecutorPluginClass("some.task.executor.plugin.class");
        assertEquals("some.task.executor.plugin.class", pars.getTaskExecutorPluginClass());
        pars.setTaskSelectionExecutorPluginClass("some.task.selection.executor.plugin.class");
        assertEquals("some.task.selection.executor.plugin.class", pars.getTaskSelectionExecutorPluginClass());

        assertEquals("ExecutorParameters [name=Name, taskExecutorPluginClass=some.task.executor.plugin.class, "
                        + "taskSelectionExecutorPluginClass=some.task.selection.executor.plugin.class, "
                        + "stateFinalizerExecutorPluginClass=some.state.finalizer.plugin.class]", pars.toString());
        
        assertTrue(pars.validate().isValid());
        
        
        ParameterService.register(pars);
        ParameterService.deregister(pars);
    }
}
