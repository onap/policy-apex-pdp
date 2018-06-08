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

package org.onap.policy.apex.plugins.executor.mvel;

import org.onap.policy.apex.core.engine.ExecutorParameters;

/**
 * This class provides executor parameters for the MVEL Executor plugin. It specifies the classes that provide the MVEL
 * implementations of the abstract classes {@link org.onap.policy.apex.core.engine.executor.TaskExecutor},
 * {@link org.onap.policy.apex.core.engine.executor.TaskSelectExecutor}, and
 * {@link org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor}.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class MVELExecutorParameters extends ExecutorParameters {
    /**
     * Constructor that sets the abstract implementation classes.
     */
    public MVELExecutorParameters() {
        this.setTaskExecutorPluginClass(MvelTaskExecutor.class.getCanonicalName());
        this.setTaskSelectionExecutorPluginClass(MvelTaskSelectExecutor.class.getCanonicalName());
        this.setStateFinalizerExecutorPluginClass(MvelStateFinalizerExecutor.class.getCanonicalName());
    }
}
