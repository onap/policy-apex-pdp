/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.parameters;

import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.annotations.ClassName;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * An empty lock manager parameter class that may be specialized by context lock manager plugins
 * that require plugin specific parameters. The class defines the default lock manager plugin as the
 * JVM local lock manager.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NotNull
@Getter
@Setter
public class LockManagerParameters extends ParameterGroupImpl {
    /**
     * The default lock manager can lock context album instance across all threads in a single JVM.
     */
    public static final String DEFAULT_LOCK_MANAGER_PLUGIN_CLASS = JvmLocalLockManager.class.getName();

    private @ClassName String pluginClass = DEFAULT_LOCK_MANAGER_PLUGIN_CLASS;

    /**
     * Constructor to create a lock manager parameters instance and register the instance with the
     * parameter service.
     */
    public LockManagerParameters() {
        super(ContextParameterConstants.LOCKING_GROUP_NAME);
    }

    @Override
    public String toString() {
        return "LockManagerParameters [name=" + getName() + ", pluginClass=" + pluginClass + "]";
    }
}
