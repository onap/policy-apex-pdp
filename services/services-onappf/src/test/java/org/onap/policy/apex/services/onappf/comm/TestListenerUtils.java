/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.comm;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.onap.policy.models.pdp.concepts.PdpStateChange;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.concepts.PdpUpdate;
import org.onap.policy.models.pdp.enums.PdpState;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

public class TestListenerUtils {

    /**
     * Method to create PdpUpdate message from the arguments passed.
     *
     * @param pdpStatus pdp status
     * @param toscaPolicies list of tosca policies
     *
     * @return PdpUpdate message
     */
    public static PdpUpdate createPdpUpdateMsg(final PdpStatus pdpStatus, List<ToscaPolicy> toscaPolicies ) {
        final PdpUpdate pdpUpdateMsg = new PdpUpdate();
        pdpUpdateMsg.setDescription("dummy pdp status for test");
        pdpUpdateMsg.setPdpGroup("pdpGroup");
        pdpUpdateMsg.setPdpSubgroup("pdpSubgroup");
        pdpUpdateMsg.setName(pdpStatus.getName());
        pdpUpdateMsg.setPdpHeartbeatIntervalMs(Long.valueOf(3000));
        pdpUpdateMsg.setPolicies(toscaPolicies);
        return pdpUpdateMsg;
    }

    /**
     * Method to create ToscaPolicy using the arguments passed.
     *
     * @param policyName the name of the policy
     * @param policyVersion the version of the policy
     * @param policyFilePath the path of the policy content
     *
     * @return PdpUpdate message
     */
    public static ToscaPolicy createToscaPolicy(String policyName, String policyVersion, String policyFilePath) {
        final ToscaPolicy toscaPolicy = new ToscaPolicy();
        toscaPolicy.setType("apexpolicytype");
        toscaPolicy.setVersion(policyVersion);
        toscaPolicy.setName(policyName);
        final Map<String, Object> propertiesMap = new LinkedHashMap<>();
        try {
            Object properties = new ObjectMapper().readValue(Files.readAllBytes(Paths.get(policyFilePath)), Map.class);
            propertiesMap.put("content", properties);
        } catch (final IOException e) {
            propertiesMap.put("content", "");
        }
        toscaPolicy.setProperties(propertiesMap);
        return toscaPolicy;
    }

    /**
     * Method to create PdpStateChange message from the arguments passed.
     *
     * @param state the new pdp state
     * @param pdpGroup name of the pdpGroup
     * @param pdpSubgroup name of the pdpSubGroup
     * @param name the name of the message
     *
     * @return PdpStateChange message
     */
    public static PdpStateChange createPdpStateChangeMsg(PdpState state, String pdpGroup, String pdpSubgroup,
        String name) {
        final PdpStateChange pdpStateChangeMsg = new PdpStateChange();
        pdpStateChangeMsg.setState(state);
        pdpStateChangeMsg.setPdpGroup(pdpGroup);
        pdpStateChangeMsg.setPdpSubgroup(pdpSubgroup);
        pdpStateChangeMsg.setName(name);
        return pdpStateChangeMsg;
    }
}
