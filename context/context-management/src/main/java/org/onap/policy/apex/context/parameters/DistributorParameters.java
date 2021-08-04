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
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.annotations.ClassName;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * An empty distributor parameter class that may be specialized by context distributor plugins that
 * require plugin specific parameters. The class defines the default distributor plugin as the JVM
 * local distributor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NotNull
@Getter
@Setter
public class DistributorParameters extends ParameterGroupImpl {
    /** The default distributor makes context albums available to all threads in a single JVM. */
    public static final String DEFAULT_DISTRIBUTOR_PLUGIN_CLASS = JvmLocalDistributor.class.getName();

    private @ClassName String pluginClass = DEFAULT_DISTRIBUTOR_PLUGIN_CLASS;

    /**
     * Constructor to create a distributor parameters instance and register the instance with the
     * parameter service.
     */
    public DistributorParameters() {
        super(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
    }

    @Override
    public String toString() {
        return "DistributorParameters [name=" + getName() + ", pluginClass=" + pluginClass + "]";
    }
}
