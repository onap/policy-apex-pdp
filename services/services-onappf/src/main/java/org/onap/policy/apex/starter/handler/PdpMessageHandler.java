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

package org.onap.policy.apex.starter.handler;

import org.onap.policy.apex.starter.ApexStarterConstants;
import org.onap.policy.apex.starter.parameters.PdpStatusParameters;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.pdp.common.enums.PdpHealthStatus;
import org.onap.policy.pdp.common.enums.PdpState;
import org.onap.policy.pdp.common.models.PdpStatus;

/**
 * This class supports the handling of pdp messages.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpMessageHandler {

    /**
     * Method to create PdpStatus message from the parameters which will be saved to the context
     *
     * @param instanceId instance id of apex pdp
     * @param pdpStatusParameters pdp status parameters read from the configuration file
     *
     * @return PdpStatus the pdp status message
     */
    public PdpStatus createPdpStatusFromParameters(final String instanceId,
            final PdpStatusParameters pdpStatusParameters) {
        final PdpStatus pdpStatus = new PdpStatus();
        pdpStatus.setName(pdpStatusParameters.getPdpName());
        pdpStatus.setVersion(pdpStatusParameters.getVersion());
        pdpStatus.setPdpType(pdpStatusParameters.getPdpType());
        pdpStatus.setState(PdpState.PASSIVE);
        pdpStatus.setHealthy(PdpHealthStatus.HEALTHY);
        pdpStatus.setDescription(pdpStatusParameters.getDescription());
        pdpStatus.setInstance(instanceId);
        pdpStatus.setSupportedPolicyTypes(pdpStatusParameters.getSupportedPolicyTypes());
        return pdpStatus;
    }

    /**
     * Method to create PdpStatus message from the context, which is to be sent by apex-pdp to pap
     *
     * @return PdpStatus the pdp status message
     */
    public PdpStatus createPdpStatusFromContext() {
        final PdpStatus pdpStatusContext =
                Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        final PdpStatus pdpStatus = new PdpStatus();
        pdpStatus.setName(pdpStatusContext.getName());
        pdpStatus.setVersion(pdpStatusContext.getVersion());
        pdpStatus.setPdpType(pdpStatusContext.getPdpType());
        pdpStatus.setState(pdpStatusContext.getState());
        pdpStatus.setHealthy(pdpStatusContext.getHealthy());
        pdpStatus.setDescription(pdpStatusContext.getDescription());
        pdpStatus.setInstance(pdpStatusContext.getInstance());
        pdpStatus.setSupportedPolicyTypes(pdpStatusContext.getSupportedPolicyTypes());
        return pdpStatus;
    }
}
