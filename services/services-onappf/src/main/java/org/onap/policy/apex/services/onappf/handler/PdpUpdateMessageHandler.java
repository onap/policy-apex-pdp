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

import java.util.Collections;
import java.util.HashSet;
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
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
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
            if (checkIfAlreadyHandled(pdpUpdateMsg, pdpStatusContext)) {
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "Pdp already updated");
            } else {
                pdpResponseDetails = handlePdpUpdate(pdpUpdateMsg, pdpMessageHandler, pdpStatusContext);
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
     * Method to do pdp update.
     *
     * @param pdpUpdateMsg the pdp update message
     * @param pdpMessageHandler the message handler
     * @param pdpStatusContext the pdp status in memory
     * @return pdpResponseDetails the pdp response
     */
    private PdpResponseDetails handlePdpUpdate(final PdpUpdate pdpUpdateMsg, final PdpMessageHandler pdpMessageHandler,
        final PdpStatus pdpStatusContext) {
        PdpResponseDetails pdpResponseDetails = null;
        final PdpStatusPublisher pdpStatusPublisher = Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
        if (null != pdpUpdateMsg.getPdpHeartbeatIntervalMs() && pdpUpdateMsg.getPdpHeartbeatIntervalMs() > 0
                && pdpStatusPublisher.getInterval() != pdpUpdateMsg.getPdpHeartbeatIntervalMs()) {
            updateInterval(pdpUpdateMsg.getPdpHeartbeatIntervalMs());
        }
        pdpStatusContext.setPdpGroup(pdpUpdateMsg.getPdpGroup());
        pdpStatusContext.setPdpSubgroup(pdpUpdateMsg.getPdpSubgroup());
        pdpStatusContext
                .setPolicies(new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()));
        Registry.registerOrReplace(ApexStarterConstants.REG_APEX_TOSCA_POLICY_LIST, pdpUpdateMsg.getPolicies());
        if (pdpStatusContext.getState().equals(PdpState.ACTIVE)) {
            pdpResponseDetails = startOrStopApexEngineBasedOnPolicies(pdpUpdateMsg, pdpMessageHandler);

            ApexEngineHandler apexEngineHandler = Registry.get(ApexStarterConstants.REG_APEX_ENGINE_HANDLER);
            // in hearbeat while in active state, only the policies which are running should be there.
            // if some policy fails, that shouldn't go in the heartbeat.
            // If no policies are running, then the policy list in the heartbeat can be empty
            if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
                pdpStatusContext.setPolicies(apexEngineHandler.getRunningPolicies());
            } else {
                pdpStatusContext.setPolicies(Collections.emptyList());
            }
        }
        if (null == pdpResponseDetails) {
            pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                    PdpResponseStatus.SUCCESS, "Pdp update successful.");
        }
        return pdpResponseDetails;
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
        ApexEngineHandler apexEngineHandler = null;
        try {
            apexEngineHandler = Registry.get(ApexStarterConstants.REG_APEX_ENGINE_HANDLER);
        } catch (final IllegalArgumentException e) {
            LOGGER.debug("ApenEngineHandler not in registry.", e);
        }
        if (pdpUpdateMsg.getPolicies().isEmpty()) {
            pdpResponseDetails = stopApexEngineBasedOnPolicies(pdpUpdateMsg, pdpMessageHandler, apexEngineHandler);
        } else {
            pdpResponseDetails = startApexEngineBasedOnPolicies(pdpUpdateMsg, pdpMessageHandler, apexEngineHandler);
        }
        return pdpResponseDetails;
    }

    private PdpResponseDetails stopApexEngineBasedOnPolicies(final PdpUpdate pdpUpdateMsg,
        final PdpMessageHandler pdpMessageHandler, ApexEngineHandler apexEngineHandler) {
        PdpResponseDetails pdpResponseDetails = null;
        if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
            try {
                apexEngineHandler.shutdown();
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                    PdpResponseStatus.SUCCESS, "Pdp update successful. No policies are running.");
            } catch (final ApexStarterException e) {
                LOGGER.error("Pdp update failed as the policies couldn't be undeployed.", e);
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.FAIL, "Pdp update failed as the policies couldn't be undeployed.");
            }
        }
        return pdpResponseDetails;
    }

    private PdpResponseDetails startApexEngineBasedOnPolicies(final PdpUpdate pdpUpdateMsg,
        final PdpMessageHandler pdpMessageHandler, ApexEngineHandler apexEngineHandler) {
        PdpResponseDetails pdpResponseDetails = null;
        try {
            if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
                apexEngineHandler.updateApexEngine(pdpUpdateMsg.getPolicies());
            } else {
                apexEngineHandler = new ApexEngineHandler(pdpUpdateMsg.getPolicies());
                Registry.registerOrReplace(ApexStarterConstants.REG_APEX_ENGINE_HANDLER, apexEngineHandler);
            }
            if (apexEngineHandler.isApexEngineRunning()) {
                List<ToscaPolicyIdentifier> runningPolicies = apexEngineHandler.getRunningPolicies();
                if (new HashSet<>(runningPolicies)
                    .equals(new HashSet<>(pdpMessageHandler.getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies())))) {
                    pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "Apex engine started and policies are running.");
                } else {
                    StringBuilder message =
                        new StringBuilder("Apex engine started. But, only the following polices are running - ");
                    for (ToscaPolicyIdentifier policy : runningPolicies) {
                        message.append(policy.getName()).append(":").append(policy.getVersion()).append("  ");
                    }
                    message.append(". Other policies failed execution. Please see the logs for more details.");
                    pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, message.toString());
                }
            } else {
                pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                    PdpResponseStatus.FAIL, "Apex engine failed to start.");
            }
        } catch (final ApexStarterException e) {
            LOGGER.error("Apex engine service running failed. ", e);
            pdpResponseDetails = pdpMessageHandler.createPdpResonseDetails(pdpUpdateMsg.getRequestId(),
                    PdpResponseStatus.FAIL, "Apex engine service running failed. " + e.getMessage());
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
        return null != pdpStatusContext.getPdpGroup()
                && pdpStatusContext.getPdpGroup().equals(pdpUpdateMsg.getPdpGroup())
                && null != pdpStatusContext.getPdpSubgroup()
                && pdpStatusContext.getPdpSubgroup().equals(pdpUpdateMsg.getPdpSubgroup())
                && null != pdpStatusContext.getPolicies() && new PdpMessageHandler()
                        .getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()).equals(pdpStatusContext.getPolicies());
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
