/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import java.time.Instant;
import java.util.ArrayList;
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
import org.onap.policy.models.tosca.authorative.concepts.ToscaConceptIdentifier;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class supports the handling of pdp messages.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpMessageHandler.class);

    static final Gauge ENGINE_EVENTS_EXECUTED_COUNT = Gauge.build().name("apex_engine_events_executed_count")
            .labelNames("engine_instance_id")
            .help("Total number of APEX events processed by the engine.").register();
    static final Gauge ENGINE_STATE = Gauge.build().name("apex_engine_state").labelNames("engine_instance_id")
            .help("State of the APEX engine as integers mapped as - 0:UNDEFINED, 1:STOPPED, 2:READY,"
                    + " 3:EXECUTING, 4:STOPPING").register();
    static final Gauge ENGINE_UPTIME = Gauge.build().name("apex_engine_uptime")
            .labelNames("engine_instance_id")
            .help("Time elapsed since the engine was started.").register();
    static final Histogram ENGINE_LAST_EXECUTION_TIME = Histogram.build()
            .name("apex_engine_last_execution_time_seconds").labelNames("engine_instance_id")
            .help("Time taken to execute the last APEX policy in seconds.").register();
    static final Gauge ENGINE_AVG_EXECUTION_TIME = Gauge.build().name("apex_engine_average_execution_time_seconds")
            .labelNames("engine_instance_id")
            .help("Average time taken to execute an APEX policy in seconds.").register();

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
        final var pdpStatus = new PdpStatus();
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
    public List<ToscaConceptIdentifier> getSupportedPolicyTypesFromParameters(
            final PdpStatusParameters pdpStatusParameters) {
        final List<ToscaConceptIdentifier> supportedPolicyTypes =
                new ArrayList<>(pdpStatusParameters.getSupportedPolicyTypes().size());
        for (final ToscaPolicyTypeIdentifierParameters policyTypeIdentParameters : pdpStatusParameters
                .getSupportedPolicyTypes()) {
            supportedPolicyTypes.add(new ToscaConceptIdentifier(policyTypeIdentParameters.getName(),
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
        final var pdpStatusContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
        final var pdpStatus = new PdpStatus();
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
        var pdpStatistics = new PdpStatistics();
        pdpStatistics.setPdpInstanceId(pdpStatusContext.getName());
        pdpStatistics.setTimeStamp(Instant.now());
        pdpStatistics.setPdpGroupName(pdpStatusContext.getPdpGroup());
        pdpStatistics.setPdpSubGroupName(pdpStatusContext.getPdpSubgroup());
        if (apexEngineHandler != null) {
            pdpStatistics.setEngineStats(getEngineWorkerStats(apexEngineHandler));
        }
        final var apexPolicyCounter = ApexPolicyStatisticsManager.getInstanceFromRegistry();
        if (apexPolicyCounter != null) {
            pdpStatistics.setPolicyDeploySuccessCount(apexPolicyCounter.getPolicyDeploySuccessCount());
            pdpStatistics.setPolicyDeployFailCount(apexPolicyCounter.getPolicyDeployFailCount());
            pdpStatistics.setPolicyDeployCount(apexPolicyCounter.getPolicyDeployCount());

            pdpStatistics.setPolicyUndeploySuccessCount(apexPolicyCounter.getPolicyUndeploySuccessCount());
            pdpStatistics.setPolicyUndeployFailCount(apexPolicyCounter.getPolicyUndeployFailCount());
            pdpStatistics.setPolicyUndeployCount(apexPolicyCounter.getPolicyUndeployCount());

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
                var workerStatistics = new PdpEngineWorkerStatistics();
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
                registerEngineMetricsWithPrometheus(engineModel);
            });
        }
        return pdpEngineWorkerStats;
    }

    private void registerEngineMetricsWithPrometheus(AxEngineModel engineModel) {
        var engineStats = engineModel.getStats();
        var engineId = engineModel.getId();

        LOGGER.info("Registering metrics with prometheus for ApexEngine instance->{}", engineId);

        ENGINE_UPTIME.labels(engineId).set(engineStats.getUpTime() / 1000d);
        ENGINE_STATE.labels(engineId).set(engineModel.getState().getStateId());
        ENGINE_EVENTS_EXECUTED_COUNT.labels(engineId).set(engineStats.getEventCount());
        ENGINE_AVG_EXECUTION_TIME.labels(engineId).set(engineStats.getAverageExecutionTime() / 1000d);
        ENGINE_LAST_EXECUTION_TIME.labels(engineId).observe(engineStats.getLastExecutionTime() / 1000d);
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
        final var pdpStatusInContext = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT, PdpStatus.class);
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
        final var pdpResponseDetails = new PdpResponseDetails();
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
    public List<ToscaConceptIdentifier> getToscaPolicyIdentifiers(final List<ToscaPolicy> policies) {
        final List<ToscaConceptIdentifier> policyIdentifiers = new ArrayList<>(policies.size());
        for (final ToscaPolicy policy : policies) {
            if (null != policy.getName() && null != policy.getVersion()) {
                policyIdentifiers.add(new ToscaConceptIdentifier(policy.getName(), policy.getVersion()));
            }
        }
        return policyIdentifiers;
    }
}