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

package org.onap.policy.apex.plugins.context.distribution.infinispan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.context.parameters.DistributorParameters;

/**
 * Distributor parameters for the Infinspan Distributor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class InfinispanDistributorParameters extends DistributorParameters {
    // @formatter:off

    /** The default Infinispan configuration file location. */
    public static final String  DEFAULT_INFINISPAN_DISTRIBUTION_CONFIG_FILE = "infinispan/infinispan.xml";

    /** The default Infinispan jgroups configuration file location. */
    public static final String  DEFAULT_INFINISPAN_DISTRIBUTION_JGROUPS_FILE = null;

    /** The default Infinispan IP stack is IPV4. */
    public static final boolean DEFAULT_INFINISPAN_JAVA_NET_PREFER_IPV4_STACK = true;

    /** The default Infinispan bind address is localhost. */
    public static final String  DEFAULT_INFINSPAN_JGROUPS_BIND_ADDRESS = "localhost";

    // Infinspan configuration file names
    private String configFile         = DEFAULT_INFINISPAN_DISTRIBUTION_CONFIG_FILE;
    private String jgroupsFile        = DEFAULT_INFINISPAN_DISTRIBUTION_JGROUPS_FILE;
    private boolean preferIPv4Stack   = DEFAULT_INFINISPAN_JAVA_NET_PREFER_IPV4_STACK;
    private String jgroupsBindAddress = DEFAULT_INFINSPAN_JGROUPS_BIND_ADDRESS;
    // @formatter:on
}
