/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.plugins.executor.test.script.engine;

import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.plugins.executor.jython.JythonExecutorParameters;

public class TestApexEngineJython {

    /**
     * Test apex engine.
     *
     * @throws ApexException the apex exception
     * @throws InterruptedException the interrupted exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testApexEngineJython() throws ApexException, InterruptedException, IOException {
        final EngineParameters parameters = new EngineParameters();
        parameters.getExecutorParameterMap().put("JYTHON", new JythonExecutorParameters());

        new TestApexEngine("JYTHON", parameters);
        new TestApexEngine("JYTHON", parameters);
    }
}
