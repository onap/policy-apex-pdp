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

import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;

/**
 * Distributor parameters for the Super Dooper Distributor.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version
 */
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
    private String jGroupsBindAddress = DEFAULT_INFINSPAN_JGROUPS_BIND_ADDRESS;

    public SuperDooperDistributorParameters() {
        super(SuperDooperDistributorParameters.class.getCanonicalName());
        ParameterService.registerParameters(SuperDooperDistributorParameters.class, this);
        ParameterService.registerParameters(DistributorParameters.class, this);
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(final String configFile) {
        this.configFile = configFile;
    }

    public String getJgroupsFile() {
        return jgroupsFile;
    }

    public void setJgroupsFile(final String jgroupsFile) {
        this.jgroupsFile = jgroupsFile;
    }

    public boolean preferIPv4Stack() {
        return preferIPv4Stack;
    }

    public void setPreferIPv4Stack(final boolean preferIPv4Stack) {
        this.preferIPv4Stack = preferIPv4Stack;
    }

    public String getjGroupsBindAddress() {
        return jGroupsBindAddress;
    }

    public void setjGroupsBindAddress(final String jGroupsBindAddress) {
        this.jGroupsBindAddress = jGroupsBindAddress;
    }

    @Override
    public String toString() {
        return "SuperDooperDistributorParameters [configFile=" + configFile + ", jgroupsFile=" + jgroupsFile
                + ", preferIPv4Stack=" + preferIPv4Stack + ", jGroupsBindAddress=" + jGroupsBindAddress + "]";
    }
}
