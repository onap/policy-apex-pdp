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

import java.util.List;

import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.apex.services.onappf.comm.PdpStatusPublisher;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.common.endpoints.event.comm.TopicSink;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.concepts.PdpResponseDetails;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.concepts.PdpUpdate;
import org.onap.policy.models.pdp.enums.PdpResponseStatus;
import org.onap.policy.models.pdp.enums.PdpState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class supports the handling of pdp update messages.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpUpdateMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdpUpdateMessageHandler.class);

    /**
     * Method which handles a pdp update event from PAP.
     *
     * @param pdpUpdateMsg pdp update message
     */
    public void handlePdpUpdateEvent(final PdpUpdate pdpUpdateMsg) {
        final PdpMessageHandler pdpMessageHandler = new PdpMessageHandler();
        final PdpStatus pdpStatusContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        PdpResponseDetails pdpResponseDetails = null;
        if (pdpUpdateMsg.appliesTo(pdpStatusContext.getName(), pdpStatusContext.getPdpGroup(),
                pdpStatusContext.getPdpSubgroup())) {
            final PdpStatusPublisher pdpStatusPublisher = Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
            if (checkIfAlreadyHandled(pdpUpdateMsg, pdpStatusContext)) {
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "Pdp already updated");
            } else {
                if (null != pdpUpdateMsg.getPdpHeartbeatIntervalMs() && pdpUpdateMsg.getPdpHeartbeatIntervalMs() > 0
                        && pdpStatusPublisher.getInterval() != pdpUpdateMsg.getPdpHeartbeatIntervalMs()) {
                    updateInterval(pdpUpdateMsg.getPdpHeartbeatIntervalMs());
                }
                pdpStatusContext.setPdpGroup(pdpUpdateMsg.getPdpGroup());
                pdpStatusContext.setPdpSubgroup(pdpUpdateMsg.getPdpSubgroup());
                pdpStatusContext
                        .setPolicies(new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()));
                if (pdpStatusContext.getState().equals(PdpState.ACTIVE)) {
                    pdpResponseDetails = startOrStopApexEngineBasedOnPolicies(pdpUpdateMsg, pdpMessageHandler);
                }
                Registry.registerOrReplace(ApexStarterConstants.REG_APEX_TOSCA_POLICY_LIST, pdpUpdateMsg.getPolicies());
                if (null == pdpResponseDetails) {
                    pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                            PdpResponseStatus.SUCCESS, "Pdp update successful.");
                }
            }
            final PdpStatusPublisher pdpStatusPublisherTemp =
                    Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
            final PdpStatus pdpStatus = pdpMessageHandler.createPdpStatusFromContext();
            pdpStatus.setResponse(pdpResponseDetails);
            pdpStatus.setDescription("Pdp status response message for PdpUpdate");
            pdpStatusPublisherTemp.send(pdpStatus);
        }
    }

    /**
     * Method to start or stop apex engine based on the list of policies received from pap. When current state is
     * active, if PAP sends PdpUpdate with empty policies list, stop apex engine, or, if there is a change in policies,
     * stop the current running policies and the deploy the new ones.
     *
     * @param pdpUpdateMsg the pdp update message from pap
     * @param pdpMessageHandler pdp message handler
     * @param pdpStatusContext the pdp status object in memory
     * @return pdpResponseDetails the pdp response
     */
    private PdpResponseDetails startOrStopApexEngineBasedOnPolicies(final PdpUpdate pdpUpdateMsg,
            final PdpMessageHandler pdpMessageHandler) {
        PdpResponseDetails pdpResponseDetails = null;
        if (pdpUpdateMsg.getPolicies().isEmpty()) {
            final ApexEngineHandler apexEngineHandler = Registry.get(ApexStarterConstants.REG_APEX_ENGINE_HANDLER);
            if (apexEngineHandler.isApexEngineRunning()) {
                try {
                    apexEngineHandler.shutdown();
                } catch (final ApexStarterException e) {
                    LOGGER.error("Pdp update failed as the policies couldn't be undeployed.", e);
                    pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                            PdpResponseStatus.FAIL, "Pdp update failed as the policies couldn't be undeployed.");
                }
            }
        } else {
            try {
                ApexEngineHandler apexEngineHandler = Registry.get(ApexStarterConstants.REG_APEX_ENGINE_HANDLER);
                if (apexEngineHandler.isApexEngineRunning()) {
                    apexEngineHandler.shutdown();
                }
                apexEngineHandler = new ApexEngineHandler(
                        (String) pdpUpdateMsg.getPolicies().get(0).getProperties().get("content"));
                Registry.registerOrReplace(ApexStarterConstants.REG_APEX_ENGINE_HANDLER, apexEngineHandler);
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "Apex engine started and policies are running.");
            } catch (final ApexStarterException e) {
                LOGGER.error("Apex engine service running failed. ", e);
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.FAIL, "Apex engine service running failed. " + e.getMessage());
            }
        }
        return pdpResponseDetails;
    }

    /**
     * Method checks if the Pdp update message is already handled by checking the values in the context.
     *
     * @param pdpUpdateMsg pdp update message received from pap
     * @param pdpStatusContext values saved in context memory
     * @return boolean flag which tells if the information is same or not
     */
    private boolean checkIfAlreadyHandled(final PdpUpdate pdpUpdateMsg, final PdpStatus pdpStatusContext) {

        if (null != pdpStatusContext.getPdpGroup()
                && pdpStatusContext.getPdpGroup().equals(pdpUpdateMsg.getPdpGroup())) {
            return true;
        } else if (null != pdpStatusContext.getPdpSubgroup()
                && pdpStatusContext.getPdpSubgroup().equals(pdpUpdateMsg.getPdpSubgroup())) {
            return true;
        } else {
            return null != pdpStatusContext.getPolicies() && new PdpMessageHandler()
                    .getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()).equals(pdpStatusContext.getPolicies());
        }
    }

    /**
     * Method to update the time interval used by the timer task.
     *
     * @param interval time interval received in the pdp update message from pap
     */
    public void updateInterval(final long interval) {
        final PdpStatusPublisher pdpStatusPublisher = Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
        pdpStatusPublisher.terminate();
        final List<TopicSink> topicSinks = Registry.get(ApexStarterConstants.REG_APEX_PDP_TOPIC_SINKS);
        Registry.registerOrReplace(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER,
                new PdpStatusPublisher(topicSinks, interval));
    }
}
