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

package org.onap.policy.apex.service.engine.parameters.dummyclasses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.context.parameters.DistributorParameters;

/**
 * Distributor parameters for the Super Dooper Distributor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version
 */
@Getter
@Setter
@ToString
public class SuperDooperDistributorParameters extends DistributorParameters {
    // Constants for SuperDooper configuration file locations
    public static final String DEFAULT_SUPER_DOOPER_DISTRIBUTION_CONFIG_FILE = "superDooper/superDooper.xml";
    public static final String DEFAULT_SUPER_DOOPER_DISTRIBUTION_JGROUPS_FILE =
            "superDooper/jgroups-superDooper-apex.xml";
    public static final boolean DEFAULT_SUPER_DOOPER_JAVA_NET_PREFER_IPV4_STACK = true;
    public static final String DEFAULT_INFINSPAN_JGROUPS_BIND_ADDRESS = "localhost";

    // SuperDooper configuration file names
    private String configFile = DEFAULT_SUPER_DOOPER_DISTRIBUTION_CONFIG_FILE;
    private String jgroupsFile = DEFAULT_SUPER_DOOPER_DISTRIBUTION_JGROUPS_FILE;
    private boolean preferIPv4Stack = DEFAULT_SUPER_DOOPER_JAVA_NET_PREFER_IPV4_STACK;
    private String jgroupsBindAddress = DEFAULT_INFINSPAN_JGROUPS_BIND_ADDRESS;
}
