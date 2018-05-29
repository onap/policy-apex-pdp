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

import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;

/**
 * An empty distributor parameter class that may be specialized by context distributor plugins that require plugin
 * specific parameters. The class defines the default distributor plugin as the JVM local distributor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class DistributorParameters extends AbstractParameters {
    /** The default distributor makes context albums available to all threads in a single JVM. */
    public static final String DEFAULT_DISTRIBUTOR_PLUGIN_CLASS =
            "org.onap.policy.apex.context.impl.distribution.jvmlocal.JVMLocalDistributor";

    // Plugin class names
    private String pluginClass = DEFAULT_DISTRIBUTOR_PLUGIN_CLASS;

    /**
     * Constructor to create a distributor parameters instance and register the instance with the parameter service.
     */
    public DistributorParameters() {
        super(DistributorParameters.class.getCanonicalName());
        ParameterService.registerParameters(DistributorParameters.class, this);
    }

    /**
     * Constructor to create a distributor parameters instance with the name of a sub class of this class and register
     * the instance with the parameter service.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public DistributorParameters(final String parameterClassName) {
        super(parameterClassName);
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

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.service.AbstractParameters#toString()
     */
    @Override
    public String toString() {
        return "DistributorParameters [pluginClass=" + pluginClass + "]";
    }
}
