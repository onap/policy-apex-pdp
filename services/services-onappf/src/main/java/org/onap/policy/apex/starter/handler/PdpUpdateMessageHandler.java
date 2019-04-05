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

import java.util.List;

import org.onap.policy.apex.starter.ApexStarterConstants;
import org.onap.policy.apex.starter.comm.PdpStatusPublisher;
import org.onap.policy.apex.starter.engine.ApexEngineHandler;
import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.onap.policy.common.endpoints.event.comm.TopicSink;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.concepts.PdpResponseDetails;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.concepts.PdpUpdate;
import org.onap.policy.models.pdp.enums.PdpResponseStatus;

/**
 * This class supports the handling of pdp update messages.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpUpdateMessageHandler {


    /**
     * Method which handles a pdp update event from PAP.
     *
     * @param pdpUpdateMsg pdp update message
     */
    public void handlePdpUpdateEvent(final PdpUpdate pdpUpdateMsg) {
        final PdpMessageHandler pdpMessageHandler = new PdpMessageHandler();
        final PdpStatus pdpStatusContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        PdpResponseDetails pdpResponseDetails;
        if (pdpStatusContext.getName().equals(pdpUpdateMsg.getName())) {
            final PdpStatusPublisher pdpStatusPublisher = Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
            if (checkIfAlreadyHandled(pdpUpdateMsg, pdpStatusContext, pdpStatusPublisher.getInterval())) {
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
                if (pdpUpdateMsg.getPolicies().isEmpty()) {
                    final ApexEngineHandler apexEngineHandler =
                            Registry.get(ApexStarterConstants.REG_APEX_ENGINE_HANDLER);
                    if (apexEngineHandler.isApexEngineRunning()) {
                        try {
                            apexEngineHandler.shutdown();
                        } catch (final ApexStarterException e) {
                            pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                                    PdpResponseStatus.FAIL,
                                    "Pdp update failed as the policies couldn't be undeployed.");
                        }
                    }
                }
                Registry.registerOrReplace(ApexStarterConstants.REG_APEX_TOSCA_POLICY_LIST, pdpUpdateMsg.getPolicies());
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "Pdp update successful.");
            }
            final PdpStatusPublisher pdpStatusPublisherTemp =
                    Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
            final PdpStatus pdpStatus = pdpMessageHandler.createPdpStatusFromContext();
            pdpStatus.setResponse(pdpResponseDetails);
            pdpStatusPublisherTemp.send(pdpStatus);
        }
    }

    /**
     * Method checks of the Pdp update message is already handled by checking the values in the context.
     *
     * @param pdpUpdateMsg pdp update message received from pap
     * @param pdpStatusContext values saved in context memory
     * @param interval the current interval in which the pdp status publisher is sending the heartbeats
     * @return boolean flag which tells if the information is same or not
     */
    private boolean checkIfAlreadyHandled(final PdpUpdate pdpUpdateMsg, final PdpStatus pdpStatusContext,
            final long interval) {
        return null != pdpStatusContext.getPdpGroup()
                && pdpStatusContext.getPdpGroup().equals(pdpUpdateMsg.getPdpGroup())
                && null != pdpStatusContext.getPdpSubgroup()
                && pdpStatusContext.getPdpSubgroup().equals(pdpUpdateMsg.getPdpSubgroup())
                && null != pdpStatusContext.getPolicies()
                && pdpStatusContext.getPolicies()
                        .containsAll(new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()))
                && null != pdpUpdateMsg.getPdpHeartbeatIntervalMs() && pdpUpdateMsg.getPdpHeartbeatIntervalMs() > 0
                && interval == pdpUpdateMsg.getPdpHeartbeatIntervalMs();
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
