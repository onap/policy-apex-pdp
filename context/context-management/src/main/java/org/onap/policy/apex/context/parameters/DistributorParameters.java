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

import org.onap.policy.apex.context.impl.distribution.jvmlocal.JVMLocalDistributor;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;

/**
 * An empty distributor parameter class that may be specialized by context distributor plugins that
 * require plugin specific parameters. The class defines the default distributor plugin as the JVM
 * local distributor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class DistributorParameters implements ParameterGroup {
    /** The default distributor makes context albums available to all threads in a single JVM. */
    public static final String DEFAULT_DISTRIBUTOR_PLUGIN_CLASS = JVMLocalDistributor.class.getCanonicalName();

    private String name;
    private String pluginClass = DEFAULT_DISTRIBUTOR_PLUGIN_CLASS;

    /**
     * Constructor to create a distributor parameters instance and register the instance with the
     * parameter service.
     */
    public DistributorParameters() {
        super();
        
        // Set the name for the parameters
        this.name = ContextParameterConstants.DISTRIBUTOR_GROUP_NAME;
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
        return "DistributorParameters [name=" + name + ", pluginClass=" + pluginClass + "]";
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
