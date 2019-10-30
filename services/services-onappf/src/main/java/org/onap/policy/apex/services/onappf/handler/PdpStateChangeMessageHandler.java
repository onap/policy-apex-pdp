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

package org.onap.policy.apex.services.onappf.handler;

import java.util.HashSet;
import java.util.List;
import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.apex.services.onappf.comm.PdpStatusPublisher;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.concepts.PdpResponseDetails;
import org.onap.policy.models.pdp.concepts.PdpStateChange;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.enums.PdpResponseStatus;
import org.onap.policy.models.pdp.enums.PdpState;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class supports the handling of pdp state change messages.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpStateChangeMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdpStateChangeMessageHandler.class);

    /**
     * Method which handles a pdp state change event from PAP.
     *
     * @param pdpStateChangeMsg pdp state change message
     */
    public void handlePdpStateChangeEvent(final PdpStateChange pdpStateChangeMsg) {
        final PdpStatus pdpStatusContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        final PdpStatusPublisher pdpStatusPublisher = Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
        final PdpMessageHandler pdpMessageHandler = new PdpMessageHandler();
        PdpResponseDetails pdpResponseDetails = null;
        if (pdpStateChangeMsg.appliesTo(pdpStatusContext.getName(), pdpStatusContext.getPdpGroup(),
                pdpStatusContext.getPdpSubgroup())) {
            switch (pdpStateChangeMsg.getState()) {
                case PASSIVE:
                    pdpResponseDetails = handlePassiveState(pdpStateChangeMsg, pdpStatusContext, pdpMessageHandler);
                    break;
                case ACTIVE:
                    pdpResponseDetails = handleActiveState(pdpStateChangeMsg, pdpStatusContext, pdpMessageHandler);
                    break;
                default:
                    break;
            }
            final PdpStatus pdpStatus = pdpMessageHandler.createPdpStatusFromContext();
            pdpStatus.setResponse(pdpResponseDetails);
            pdpStatus.setDescription("Pdp status response message for PdpStateChange");
            pdpStatusPublisher.send(pdpStatus);
        }
    }

    /**
     * Method to handle when the new state from pap is active.
     *
     * @param pdpStateChangeMsg pdp state change message
     * @param pdpStatusContext pdp status object in memory
     * @param pdpMessageHandler the pdp message handler
     * @return pdpResponseDetails pdp response
     */
    private PdpResponseDetails handleActiveState(final PdpStateChange pdpStateChangeMsg,
            final PdpStatus pdpStatusContext, final PdpMessageHandler pdpMessageHandler) {
        PdpResponseDetails pdpResponseDetails = null;
        if (pdpStatusContext.getState().equals(PdpState.ACTIVE)) {
            pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                    PdpResponseStatus.SUCCESS, "Pdp already in active state");
        } else {
            final List<ToscaPolicy> policies = Registry.get(ApexStarterConstants.REG_APEX_TOSCA_POLICY_LIST);
            if (policies.isEmpty()) {
                pdpStatusContext.setState(PdpState.ACTIVE);
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "State changed to active. No policies found.");
            } else {
                pdpResponseDetails = startApexEngine(pdpStateChangeMsg, pdpStatusContext, pdpMessageHandler, policies);
            }
        }
        return pdpResponseDetails;
    }

    /**
     * Method to start apex engine.
     *
     * @param pdpStateChangeMsg pdp state change message
     * @param pdpStatusContext pdp status in memory
     * @param pdpMessageHandler the pdp message handler
     * @param policies list of policies
     * @return pdp response details
     */
    private PdpResponseDetails startApexEngine(final PdpStateChange pdpStateChangeMsg, final PdpStatus pdpStatusContext,
        final PdpMessageHandler pdpMessageHandler, final List<ToscaPolicy> policies) {
        PdpResponseDetails pdpResponseDetails;
        try {
            final ApexEngineHandler apexEngineHandler = new ApexEngineHandler(policies);
            Registry.registerOrReplace(ApexStarterConstants.REG_APEX_ENGINE_HANDLER, apexEngineHandler);
            if (apexEngineHandler.isApexEngineRunning()) {
                List<ToscaPolicyIdentifier> runningPolicies = apexEngineHandler.getRunningPolicies();
                // only the policies which are succesfully executed should be there in the heartbeat
                pdpStatusContext.setPolicies(runningPolicies);
                if (new HashSet<>(runningPolicies)
                    .equals(new HashSet<>(pdpMessageHandler.getToscaPolicyIdentifiers(policies)))) {
                    pdpResponseDetails =
                        pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                            PdpResponseStatus.SUCCESS, "Apex engine started. State changed to active.");
                } else {
                    StringBuilder message = new StringBuilder(
                        "Apex engine started. But, only the following polices are running - ");
                    for (ToscaPolicyIdentifier policy : runningPolicies) {
                        message.append(policy.getName()).append(":").append(policy.getVersion()).append("  ");
                    }
                    message.append(". Other policies failed execution. Please see the logs for more details.");
                    pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(
                        pdpStateChangeMsg.getRequestId(), PdpResponseStatus.SUCCESS, message.toString());
                }
                pdpStatusContext.setState(PdpState.ACTIVE);
            } else {
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                    PdpResponseStatus.FAIL, "Apex engine failed to start. State cannot be changed to active.");
            }
        } catch (final ApexStarterException e) {
            LOGGER.error("Pdp State Change failed.", e);
            pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                    PdpResponseStatus.FAIL, "Apex engine service running failed. " + e.getMessage());
        }
        return pdpResponseDetails;
    }

    /**
     * Method to handle when the new state from pap is passive.
     *
     * @param pdpStateChangeMsg pdp state change message
     * @param pdpStatusContext pdp status object in memory
     * @param pdpMessageHandler the pdp message handler
     * @return pdpResponseDetails pdp response
     */
    private PdpResponseDetails handlePassiveState(final PdpStateChange pdpStateChangeMsg,
            final PdpStatus pdpStatusContext, final PdpMessageHandler pdpMessageHandler) {
        PdpResponseDetails pdpResponseDetails = null;
        if (pdpStatusContext.getState().equals(PdpState.PASSIVE)) {
            pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                    PdpResponseStatus.SUCCESS, "Pdp already in passive state");
        } else {
            ApexEngineHandler apexEngineHandler = null;
            try {
                apexEngineHandler = Registry.get(ApexStarterConstants.REG_APEX_ENGINE_HANDLER);
            } catch (final IllegalArgumentException e) {
                LOGGER.debug("ApenEngineHandler not in registry.", e);
            }
            try {
                if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
                    apexEngineHandler.shutdown();
                }
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "Apex pdp state changed from Active to Passive.");
                pdpStatusContext.setState(PdpState.PASSIVE);
            } catch (final Exception e) {
                LOGGER.error("Stopping apex engine failed. State cannot be changed to Passive.", e);
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpStateChangeMsg.getRequestId(),
                        PdpResponseStatus.FAIL,
                        "Stopping apex engine failed. State cannot be changed to Passive." + e.getMessage());
            }
        }
        return pdpResponseDetails;
    }
}
