/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021-2022 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.services.onappf.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.onap.policy.apex.service.engine.main.ApexPolicyStatisticsManager;
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
import org.onap.policy.models.tosca.authorative.concepts.ToscaConceptIdentifier;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaWithTypeAndObjectProperties;
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
        final var pdpMessageHandler = new PdpMessageHandler();
        final var pdpStatusContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        PdpResponseDetails pdpResponseDetails = null;
        if (pdpUpdateMsg.appliesTo(pdpStatusContext.getName(), pdpStatusContext.getPdpGroup(),
                pdpStatusContext.getPdpSubgroup())) {
            if (checkIfAlreadyHandled(pdpUpdateMsg, pdpStatusContext)) {
                pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.SUCCESS, "Pdp already updated");
            } else {
                pdpResponseDetails = handlePdpUpdate(pdpUpdateMsg, pdpMessageHandler, pdpStatusContext);
            }
            final var pdpStatusPublisherTemp =
                    Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER, PdpStatusPublisher.class);
            final var pdpStatus = pdpMessageHandler.createPdpStatusFromContext();
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
        final var pdpStatusPublisher =
                        Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER, PdpStatusPublisher.class);
        if (null != pdpUpdateMsg.getPdpHeartbeatIntervalMs() && pdpUpdateMsg.getPdpHeartbeatIntervalMs() > 0
                && pdpStatusPublisher.getInterval() != pdpUpdateMsg.getPdpHeartbeatIntervalMs()) {
            updateInterval(pdpUpdateMsg.getPdpHeartbeatIntervalMs());
        }
        pdpStatusContext.setPdpGroup(pdpUpdateMsg.getPdpGroup());
        pdpStatusContext.setPdpSubgroup(pdpUpdateMsg.getPdpSubgroup());
        @SuppressWarnings("unchecked")
        List<ToscaPolicy> policies = Registry.getOrDefault(ApexStarterConstants.REG_APEX_TOSCA_POLICY_LIST,
                List.class, new ArrayList<>());
        policies.addAll(pdpUpdateMsg.getPoliciesToBeDeployed());
        policies.removeIf(policy -> pdpUpdateMsg.getPoliciesToBeUndeployed().contains(policy.getIdentifier()));
        Set<ToscaConceptIdentifier> policiesInDeployment = policies.stream().map(ToscaPolicy::getIdentifier)
                .collect(Collectors.toSet());
        policiesInDeployment.removeAll(pdpUpdateMsg.getPoliciesToBeUndeployed());
        pdpStatusContext.setPolicies(new ArrayList<>(policiesInDeployment));
        Registry.registerOrReplace(ApexStarterConstants.REG_APEX_TOSCA_POLICY_LIST,
                policies);
        if (pdpStatusContext.getState().equals(PdpState.ACTIVE)) {
            pdpResponseDetails = startOrStopApexEngineBasedOnPolicies(pdpUpdateMsg, pdpMessageHandler);

            var apexEngineHandler =
                Registry.getOrDefault(ApexStarterConstants.REG_APEX_ENGINE_HANDLER, ApexEngineHandler.class, null);
            // in hearbeat while in active state, only the policies which are running should be there.
            // if some policy fails, that shouldn't go in the heartbeat.
            // If no policies are running, then the policy list in the heartbeat can be empty
            if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
                var runningPolicies = apexEngineHandler.getRunningPolicies();
                pdpStatusContext.setPolicies(runningPolicies);
                policies.removeIf(policy -> !runningPolicies.contains(policy.getIdentifier()));
            } else {
                pdpStatusContext.setPolicies(Collections.emptyList());
                policies.clear();
            }
        }
        if (null == pdpResponseDetails) {
            pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
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
        if (null != apexEngineHandler
            && pdpUpdateMsg.getPoliciesToBeUndeployed().containsAll(apexEngineHandler.getRunningPolicies())
            && pdpUpdateMsg.getPoliciesToBeDeployed().isEmpty()) {
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
            List<ToscaConceptIdentifier> runningPolicies = apexEngineHandler.getRunningPolicies();
            try {
                apexEngineHandler.shutdown();
                runningPolicies = apexEngineHandler.getRunningPolicies();
                pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
                    PdpResponseStatus.SUCCESS, "Pdp update successful. No policies are running.");
            } catch (final ApexStarterException e) {
                LOGGER.error("Pdp update failed as the policies couldn't be undeployed.", e);
                pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
                        PdpResponseStatus.FAIL, "Pdp update failed as the policies couldn't be undeployed.");
            }
            updateDeploymentCounts(runningPolicies, pdpUpdateMsg);
        }
        return pdpResponseDetails;
    }

    private PdpResponseDetails startApexEngineBasedOnPolicies(final PdpUpdate pdpUpdateMsg,
        final PdpMessageHandler pdpMessageHandler, ApexEngineHandler apexEngineHandler) {
        PdpResponseDetails pdpResponseDetails = null;
        List<ToscaConceptIdentifier> runningPolicies = new ArrayList<>();
        try {
            if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
                apexEngineHandler.updateApexEngine(pdpUpdateMsg.getPoliciesToBeDeployed(),
                    pdpUpdateMsg.getPoliciesToBeUndeployed());
                runningPolicies = apexEngineHandler.getRunningPolicies();
            } else {
                apexEngineHandler = new ApexEngineHandler(pdpUpdateMsg.getPoliciesToBeDeployed());
                Registry.registerOrReplace(ApexStarterConstants.REG_APEX_ENGINE_HANDLER, apexEngineHandler);
            }
            if (apexEngineHandler.isApexEngineRunning()) {
                pdpResponseDetails =
                    populateResponseForEngineInitiation(pdpUpdateMsg, pdpMessageHandler, apexEngineHandler);
                runningPolicies = apexEngineHandler.getRunningPolicies();
            } else {
                pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
                    PdpResponseStatus.FAIL, "Apex engine failed to start.");
            }
        } catch (final ApexStarterException e) {
            LOGGER.error("Apex engine service running failed. ", e);
            pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
                    PdpResponseStatus.FAIL, "Apex engine service running failed. " + e.getMessage());
        }
        updateDeploymentCounts(runningPolicies, pdpUpdateMsg);
        return pdpResponseDetails;
    }

    private PdpResponseDetails populateResponseForEngineInitiation(final PdpUpdate pdpUpdateMsg,
        final PdpMessageHandler pdpMessageHandler, ApexEngineHandler apexEngineHandler) {
        PdpResponseDetails pdpResponseDetails;
        Set<ToscaConceptIdentifier> runningPolicies = new HashSet<>(apexEngineHandler.getRunningPolicies());
        List<ToscaConceptIdentifier> policiesToBeDeployed =
            pdpMessageHandler.getToscaPolicyIdentifiers(pdpUpdateMsg.getPoliciesToBeDeployed());
        List<ToscaConceptIdentifier> policiesToBeUndeployed = pdpUpdateMsg.getPoliciesToBeUndeployed();
        if (runningPolicies.containsAll(policiesToBeDeployed)
            && !containsAny(runningPolicies, policiesToBeUndeployed)) {
            var message = new StringBuilder("Apex engine started. ");
            if (!policiesToBeDeployed.isEmpty()) {
                message.append("Deployed policies are: ");
                for (ToscaConceptIdentifier policy : policiesToBeDeployed) {
                    message.append(policy.getName()).append(":").append(policy.getVersion()).append("  ");
                }
            }
            if (!policiesToBeUndeployed.isEmpty()) {
                message.append("Undeployed policies are: ");
                for (ToscaConceptIdentifier policy : policiesToBeUndeployed) {
                    message.append(policy.getName()).append(":").append(policy.getVersion()).append("  ");
                }
            }
            pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
                PdpResponseStatus.SUCCESS, message.toString());
        } else {
            var message =
                new StringBuilder("Apex engine started. But, only the following polices are running - ");
            for (ToscaConceptIdentifier policy : runningPolicies) {
                message.append(policy.getName()).append(":").append(policy.getVersion()).append("  ");
            }
            message.append(". Other policies failed execution. Please see the logs for more details.");
            pdpResponseDetails = pdpMessageHandler.createPdpResponseDetails(pdpUpdateMsg.getRequestId(),
                PdpResponseStatus.SUCCESS, message.toString());
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
            && null != pdpStatusContext.getPolicies()
            && pdpStatusContext.getPolicies()
                .containsAll(new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPoliciesToBeDeployed()))
            && !containsAny(new HashSet<>(pdpStatusContext.getPolicies()), pdpUpdateMsg.getPoliciesToBeUndeployed());
    }

    /**
     * Method to update the time interval used by the timer task.
     *
     * @param interval time interval received in the pdp update message from pap
     */
    public void updateInterval(final long interval) {
        final var pdpStatusPublisher =
                        Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER, PdpStatusPublisher.class);
        pdpStatusPublisher.terminate();
        final List<TopicSink> topicSinks = Registry.get(ApexStarterConstants.REG_APEX_PDP_TOPIC_SINKS);
        Registry.registerOrReplace(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER,
                new PdpStatusPublisher(topicSinks, interval));
    }

    /**
     * Checks if one list contains any element of another.
     *
     * @param listToCheckWith list to check contents of
     * @param listToCheckAgainst list to check against other list for similarities
     * @return boolean flag which tells if lists share same elements or not
     */
    private boolean containsAny(Set<ToscaConceptIdentifier> listToCheckWith,
            List<ToscaConceptIdentifier> listToCheckAgainst) {
        return listToCheckAgainst.stream().anyMatch(listToCheckWith::contains);
    }

    /**
     * Update count values for deployment actions (deploy and undeploy) when applicable.
     * @param runningPolicies the policies running in apex engine
     * @param pdpUpdateMsg the pdp update message from pap
     */
    private void updateDeploymentCounts(final List<ToscaConceptIdentifier> runningPolicies,
        final PdpUpdate pdpUpdateMsg) {
        final var statisticsManager = ApexPolicyStatisticsManager.getInstanceFromRegistry();
        if (statisticsManager != null && runningPolicies != null) {
            if (pdpUpdateMsg.getPoliciesToBeDeployed() != null && !pdpUpdateMsg.getPoliciesToBeDeployed().isEmpty()) {
                var policiesToDeploy = pdpUpdateMsg.getPoliciesToBeDeployed().stream()
                    .map(ToscaWithTypeAndObjectProperties::getIdentifier).collect(Collectors.toList());

                var policiesSuccessfullyDeployed = new ArrayList<>(policiesToDeploy);
                policiesSuccessfullyDeployed.retainAll(runningPolicies);
                policiesSuccessfullyDeployed.forEach(policy -> statisticsManager.updatePolicyDeployCounter(true));

                var policiesFailedToDeploy =  new ArrayList<>(policiesToDeploy);
                policiesFailedToDeploy.removeIf(runningPolicies::contains);
                policiesFailedToDeploy.forEach(policy -> statisticsManager.updatePolicyDeployCounter(false));
            }

            var policiesToUndeploy = pdpUpdateMsg.getPoliciesToBeUndeployed();
            if (policiesToUndeploy != null && !policiesToUndeploy.isEmpty()) {
                var policiesFailedToUndeploy = new ArrayList<>(policiesToUndeploy);
                policiesFailedToUndeploy.retainAll(runningPolicies);
                policiesFailedToUndeploy.forEach(policy -> statisticsManager.updatePolicyUndeployCounter(false));

                var policiesSuccessfullyUndeployed =  new ArrayList<>(policiesToUndeploy);
                policiesSuccessfullyUndeployed.removeIf(runningPolicies::contains);
                policiesSuccessfullyUndeployed.forEach(policy -> statisticsManager.updatePolicyUndeployCounter(true));
            }
        }
    }
}
