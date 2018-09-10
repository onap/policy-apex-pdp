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

package org.onap.policy.apex.context.parameters;

import org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;

/**
 * An empty lock manager parameter class that may be specialized by context lock manager plugins
 * that require plugin specific parameters. The class defines the default lock manager plugin as the
 * JVM local lock manager.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class LockManagerParameters implements ParameterGroup {
    /**
     * The default lock manager can lock context album instance across all threads in a single JVM.
     */
    public static final String DEFAULT_LOCK_MANAGER_PLUGIN_CLASS = JvmLocalLockManager.class.getCanonicalName();

    private String name;
    private String pluginClass = DEFAULT_LOCK_MANAGER_PLUGIN_CLASS;

    /**
     * Constructor to create a lock manager parameters instance and register the instance with the
     * parameter service.
     */
    public LockManagerParameters() {
        super();

        // Set the name for the parameters
        this.name = ContextParameterConstants.LOCKING_GROUP_NAME;
    }

    /**
     * Gets the plugin class.
     *
     * @return the plugin class
     */
    public String getPluginClass() {
        return pluginClass;
    }

    /**
     * Sets the plugin class.
     *
     * @param pluginClass the plugin class
     */
    public void setPluginClass(final String pluginClass) {
        this.pluginClass = pluginClass;
    }
    
    @Override
    public String toString() {
        return "LockManagerParameters [name=" + name + ", pluginClass=" + pluginClass + "]";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public GroupValidationResult validate() {
        return new GroupValidationResult(this);
    }
}
