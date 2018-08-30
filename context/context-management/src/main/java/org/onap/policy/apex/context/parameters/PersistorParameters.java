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

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;

/**
 * A persistor parameter class that may be specialized by context persistor plugins that require
 * plugin specific parameters.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>pluginClass: the persistor plugin as the JVM local dummy ephemeral persistor
 * <li>flushPeriod: Context is flushed to any persistor plugin that is defined periodically, and the
 * period for flushing is the flush period.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PersistorParameters implements ParameterGroup {
    /** The default persistor is a dummy persistor that stubs the Persistor interface. */
    public static final String DEFAULT_PERSISTOR_PLUGIN_CLASS =
            "org.onap.policy.apex.context.impl.persistence.ephemeral.EphemeralPersistor";

    /** Default periodic flushing interval, 5 minutes in milliseconds. */
    public static final long DEFAULT_FLUSH_PERIOD = 300000;

    private String name;
    private String pluginClass = DEFAULT_PERSISTOR_PLUGIN_CLASS;

    // Parameters for flushing
    private long flushPeriod = DEFAULT_FLUSH_PERIOD;

    /**
     * Constructor to create a persistor parameters instance and register the instance with the
     * parameter service.
     */
    public PersistorParameters() {
        super();

        // Set the name for the parameters
        this.name = ContextParameterConstants.PERSISTENCE_GROUP_NAME;
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

    /**
     * Gets the flush period in milliseconds.
     *
     * @return the flush period
     */
    public long getFlushPeriod() {
        return flushPeriod;
    }

    /**
     * Sets the flush period in milliseconds.
     *
     * @param flushPeriod the flush period
     */
    public void setFlushPeriod(final long flushPeriod) {
        if (flushPeriod <= 0) {
            this.flushPeriod = DEFAULT_FLUSH_PERIOD;
        } else {
            this.flushPeriod = flushPeriod;
        }
    }

    @Override
    public String toString() {
        return "PersistorParameters [name=" + name + ", pluginClass=" + pluginClass + ", flushPeriod=" + flushPeriod
                        + "]";
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
