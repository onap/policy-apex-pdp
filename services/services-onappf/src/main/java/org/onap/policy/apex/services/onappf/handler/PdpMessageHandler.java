/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020 Nordix Foundation.
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
import java.util.Date;
import java.util.List;
import lombok.NonNull;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.service.engine.main.ApexPolicyStatisticsManager;
import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.apex.services.onappf.parameters.PdpStatusParameters;
import org.onap.policy.apex.services.onappf.parameters.ToscaPolicyTypeIdentifierParameters;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.concepts.PdpEngineWorkerStatistics;
import org.onap.policy.models.pdp.concepts.PdpResponseDetails;
import org.onap.policy.models.pdp.concepts.PdpStatistics;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.enums.PdpEngineWorkerState;
import org.onap.policy.models.pdp.enums.PdpHealthStatus;
import org.onap.policy.models.pdp.enums.PdpResponseStatus;
import org.onap.policy.models.pdp.enums.PdpState;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyTypeIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class supports the handling of pdp messages.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpMessageHandler.class);

    /**
     * Method to create PdpStatus message from the parameters which will be saved to the context.
     *
     * @param instanceId instance id of apex pdp
     * @param pdpStatusParameters pdp status parameters read from the configuration file
     *
     * @return pdpStatus the pdp status message
     */
    public PdpStatus createPdpStatusFromParameters(final String instanceId,
            final PdpStatusParameters pdpStatusParameters) {
        final PdpStatus pdpStatus = new PdpStatus();
        pdpStatus.setPdpGroup(pdpStatusParameters.getPdpGroup());
        pdpStatus.setPdpType(pdpStatusParameters.getPdpType());
        pdpStatus.setState(PdpState.PASSIVE);
        pdpStatus.setHealthy(PdpHealthStatus.HEALTHY);
        pdpStatus.setDescription(pdpStatusParameters.getDescription());
        pdpStatus.setName(instanceId);
        return pdpStatus;
    }

    /**
     * Method to get supported policy types from the parameters.
     *
     * @param pdpStatusParameters pdp status parameters
     * @return supportedPolicyTypes list of PolicyTypeIdent
     */
    public List<ToscaPolicyTypeIdentifier> getSupportedPolicyTypesFromParameters(
            final PdpStatusParameters pdpStatusParameters) {
        final List<ToscaPolicyTypeIdentifier> supportedPolicyTypes =
                new ArrayList<>(pdpStatusParameters.getSupportedPolicyTypes().size());
        for (final ToscaPolicyTypeIdentifierParameters policyTypeIdentParameters : pdpStatusParameters
                .getSupportedPolicyTypes()) {
            supportedPolicyTypes.add(new ToscaPolicyTypeIdentifier(policyTypeIdentParameters.getName(),
                    policyTypeIdentParameters.getVersion()));
        }
        return supportedPolicyTypes;
    }

    /**
     * Method to create PdpStatus message from the context, which is to be sent by apex-pdp to pap.
     *
     * @return PdpStatus the pdp status message
     */
    public PdpStatus createPdpStatusFromContext() {
        final PdpStatus pdpStatusContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        final PdpStatus pdpStatus = new PdpStatus();
        pdpStatus.setName(pdpStatusContext.getName());
        pdpStatus.setPdpType(pdpStatusContext.getPdpType());
        pdpStatus.setState(pdpStatusContext.getState());
        pdpStatus.setHealthy(pdpStatusContext.getHealthy());
        pdpStatus.setDescription(pdpStatusContext.getDescription());
        pdpStatus.setPolicies(pdpStatusContext.getPolicies());
        pdpStatus.setPdpGroup(pdpStatusContext.getPdpGroup());
        pdpStatus.setPdpSubgroup(pdpStatusContext.getPdpSubgroup());

        ApexEngineHandler apexEngineHandler = null;
        try {
            apexEngineHandler = Registry.get(ApexStarterConstants.REG_APEX_ENGINE_HANDLER);
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
        }

        pdpStatus.setStatistics(getStatistics(pdpStatus, apexEngineHandler));


        return pdpStatus;
    }

    /**
     * Method to get the statistics.
     *
     * @return PdpStatistics the pdp status message
     */

    private PdpStatistics getStatistics(final PdpStatus pdpStatusContext, final ApexEngineHandler apexEngineHandler) {
        PdpStatistics pdpStatistics = new PdpStatistics();
        pdpStatistics.setPdpInstanceId(pdpStatusContext.getName());
        pdpStatistics.setTimeStamp(new Date());
        pdpStatistics.setPdpGroupName(pdpStatusContext.getPdpGroup());
        pdpStatistics.setPdpSubGroupName(pdpStatusContext.getPdpSubgroup());
        if (apexEngineHandler != null) {
            pdpStatistics.setEngineStats(getEngineWorkerStats(apexEngineHandler));
        }
        final ApexPolicyStatisticsManager apexPolicyCounter = ApexPolicyStatisticsManager.getInstanceFromRegistry();
        if (apexPolicyCounter != null) {
            pdpStatistics.setPolicyDeploySuccessCount(apexPolicyCounter.getPolicyDeploySuccessCount());
            pdpStatistics.setPolicyDeployFailCount(apexPolicyCounter.getPolicyDeployFailCount());
            pdpStatistics.setPolicyDeployCount(apexPolicyCounter.getPolicyDeployCount());
            pdpStatistics.setPolicyExecutedCount(apexPolicyCounter.getPolicyExecutedCount());
            pdpStatistics.setPolicyExecutedSuccessCount(apexPolicyCounter.getPolicyExecutedSuccessCount());
            pdpStatistics.setPolicyExecutedFailCount(apexPolicyCounter.getPolicyExecutedFailCount());
        }
        return pdpStatistics;
    }

    private List<PdpEngineWorkerStatistics> getEngineWorkerStats(@NonNull final ApexEngineHandler apexEngineHandler) {
        List<PdpEngineWorkerStatistics> pdpEngineWorkerStats = new ArrayList<>();
        List<AxEngineModel> engineModels = apexEngineHandler.getEngineStats();
        if (engineModels != null) {
            engineModels.forEach(engineModel -> {
                PdpEngineWorkerStatistics workerStatistics = new PdpEngineWorkerStatistics();
                workerStatistics.setEngineWorkerState(transferEngineState(engineModel.getState()));
                workerStatistics.setEngineId(engineModel.getId());
                workerStatistics.setEventCount(engineModel.getStats().getEventCount());
                workerStatistics.setAverageExecutionTime(engineModel.getStats().getAverageExecutionTime());
                workerStatistics.setEngineTimeStamp(engineModel.getStats().getTimeStamp());
                workerStatistics.setLastEnterTime(engineModel.getStats().getLastEnterTime());
                workerStatistics.setLastExecutionTime(engineModel.getStats().getLastExecutionTime());
                workerStatistics.setLastStart(engineModel.getStats().getLastStart());
                workerStatistics.setUpTime(engineModel.getStats().getUpTime());
                pdpEngineWorkerStats.add(workerStatistics);
            });
        }
        return pdpEngineWorkerStats;
    }

    private PdpEngineWorkerState transferEngineState(@NonNull final AxEngineState state) {
        switch (state) {
            case STOPPING:
                return PdpEngineWorkerState.STOPPING;
            case STOPPED:
                return PdpEngineWorkerState.STOPPED;
            case READY:
                return PdpEngineWorkerState.READY;
            case EXECUTING:
                return PdpEngineWorkerState.EXECUTING;
            default:
                return PdpEngineWorkerState.UNDEFINED;
        }
    }

    /**
     * Method to get a final pdp status when the apex started is shutting down.
     *
     * @return PdpStatus the pdp status message
     */
    public PdpStatus getTerminatedPdpStatus() {
        final PdpStatus pdpStatusInContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        pdpStatusInContext.setState(PdpState.TERMINATED);
        pdpStatusInContext.setDescription("Apex pdp shutting down.");
        return createPdpStatusFromContext();
    }

    /**
     * Method create PdpResponseDetails which will be sent as part of pdp status to PAP.
     *
     * @param requestId request id of the PdpUpdate message from pap
     * @param status response status to be sent back
     * @param responseMessage response message to be sent back
     *
     * @return PdpResponseDetails
     */
    public PdpResponseDetails createPdpResonseDetails(final String requestId, final PdpResponseStatus status,
            final String responseMessage) {
        final PdpResponseDetails pdpResponseDetails = new PdpResponseDetails();
        pdpResponseDetails.setResponseTo(requestId);
        pdpResponseDetails.setResponseStatus(status);
        pdpResponseDetails.setResponseMessage(responseMessage);
        return pdpResponseDetails;
    }

    /**
     * Method to retrieve list of ToscaPolicyIdentifier from the list of ToscaPolicy.
     *
     * @param policies list of ToscaPolicy
     *
     * @return policyTypeIdentifiers
     */
    public List<ToscaPolicyIdentifier> getToscaPolicyIdentifiers(final List<ToscaPolicy> policies) {
        final List<ToscaPolicyIdentifier> policyIdentifiers = new ArrayList<>(policies.size());
        for (final ToscaPolicy policy : policies) {
            if (null != policy.getName() && null != policy.getVersion()) {
                policyIdentifiers.add(new ToscaPolicyIdentifier(policy.getName(), policy.getVersion()));
            }
        }
        return policyIdentifiers;
    }
}
