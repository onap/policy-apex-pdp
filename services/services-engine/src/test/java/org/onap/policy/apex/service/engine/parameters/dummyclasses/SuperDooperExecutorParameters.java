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

package org.onap.policy.apex.service.engine.parameters.dummyclasses;

import org.onap.policy.apex.core.engine.ExecutorParameters;

/**
 * Default Executor parameters for MVEL.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version
 */
public class SuperDooperExecutorParameters extends ExecutorParameters {
    public SuperDooperExecutorParameters() {
        this.setTaskExecutorPluginClass(
                "org.onap.policy.apex.service.engine.parameters.dummyclasses.DummyTaskExecutor");
        this.setTaskSelectionExecutorPluginClass(
                "org.onap.policy.apex.service.engine.parameters.dummyclasses.DummyTaskSelectExecutor");
        this.setStateFinalizerExecutorPluginClass(
                "org.onap.policy.apex.service.engine.parameters.dummyclasses.DummyStateFinalizerExecutor");
    }
}
