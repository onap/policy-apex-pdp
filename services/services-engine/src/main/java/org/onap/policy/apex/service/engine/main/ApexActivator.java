/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.service.engine.main;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelMerger;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class wraps an Apex engine so that it can be activated as a complete
 * service together with all its context, executor, and event plugins.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexActivator {
    private static final String APEX_ENGINE_FAILED_MSG = "Apex engine failed to start as a service";

    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexActivator.class);

    // The parameters of the Apex activator when running with multiple policies
    @Getter
    @Setter
    private Map<ToscaPolicyIdentifier, ApexParameters> apexParametersMap;

    @Getter
    Map<ToscaPolicyIdentifier, AxPolicyModel> policyModelsMap;

    // Event unmarshalers are used to receive events asynchronously into Apex
    private final Map<String, ApexEventUnmarshaller> unmarshallerMap = new LinkedHashMap<>();

    // Event marshalers are used to send events asynchronously from Apex
    private final Map<String, ApexEventMarshaller> marshallerMap = new LinkedHashMap<>();

    // The engine service handler holds the references to the engine and its EngDep
    // deployment
    // interface. It also acts as a receiver for asynchronous
    // and synchronous events from the engine.
    private ApexEngineServiceHandler engineServiceHandler = null;

    // The engine service
    private EngineService apexEngineService;
    private AxArtifactKey engineKey;

    /**
     * Instantiate the activator for the Apex engine as a complete service.
     *
     * @param parametersMap the apex parameters map for the Apex service
     */
    public ApexActivator(Map<ToscaPolicyIdentifier, ApexParameters> parametersMap) {
        apexParametersMap = parametersMap;
    }

    /**
     * Initialize the Apex engine as a complete service.
     *
     * @throws ApexActivatorException on errors in initializing the engine
     */
    public void initialize() throws ApexActivatorException {
        LOGGER.debug("Apex engine starting as a service . . .");

        try {
            ApexParameters apexParameters = apexParametersMap.values().iterator().next();
            // totalInstanceCount is the sum of instance counts required as per each policy
            int totalInstanceCount = apexParametersMap.values().stream()
                .mapToInt(p -> p.getEngineServiceParameters().getInstanceCount()).sum();
            apexParameters.getEngineServiceParameters().setInstanceCount(totalInstanceCount);
            engineKey = apexParameters.getEngineServiceParameters().getEngineKey();
            instantiateEngine(apexParameters);
            setUpModelMarshallerAndUnmarshaller(apexParameters);
        } catch (final Exception e) {
            LOGGER.debug(APEX_ENGINE_FAILED_MSG, e);
            throw new ApexActivatorException(APEX_ENGINE_FAILED_MSG, e);
        }

        LOGGER.debug("Apex engine started as a service");
    }

    private void setUpModelMarshallerAndUnmarshaller(ApexParameters apexParameters) throws ApexException {
        policyModelsMap = new LinkedHashMap<>();
        Map<String, EventHandlerParameters> inputParametersMap = new LinkedHashMap<>();
        Map<String, EventHandlerParameters> outputParametersMap = new LinkedHashMap<>();
        Set<Entry<ToscaPolicyIdentifier, ApexParameters>> apexParamsEntrySet = new LinkedHashSet<>(
            apexParametersMap.entrySet());
        apexParamsEntrySet.stream().forEach(apexParamsEntry -> {
            ApexParameters apexParams = apexParamsEntry.getValue();
            List<String> duplicateInputParameters = new ArrayList<>(apexParams.getEventInputParameters().keySet());
            duplicateInputParameters.retainAll(inputParametersMap.keySet());
            List<String> duplicateOutputParameters = new ArrayList<>(apexParams.getEventOutputParameters().keySet());
            duplicateOutputParameters.retainAll(outputParametersMap.keySet());

            if (!(duplicateInputParameters.isEmpty() && duplicateOutputParameters.isEmpty())) {
                LOGGER.error("I/O Parameters {}/{} for {}:{} are duplicates. So this policy is not executed.",
                    duplicateInputParameters, duplicateOutputParameters, apexParamsEntry.getKey().getName(),
                    apexParamsEntry.getKey().getVersion());
                apexParametersMap.remove(apexParamsEntry.getKey());
                return;
            }

            inputParametersMap.putAll(apexParams.getEventInputParameters());
            outputParametersMap.putAll(apexParams.getEventOutputParameters());
            // Check if a policy model file has been specified
            if (apexParams.getEngineServiceParameters().getPolicyModel() != null) {
                LOGGER.debug("deploying policy model to the apex engines . . .");
                try {
                    final String policyModelString = apexParams.getEngineServiceParameters().getPolicyModel();
                    AxPolicyModel policyModel = EngineServiceImpl.createModel(engineKey, policyModelString);
                    policyModelsMap.put(apexParamsEntry.getKey(), policyModel);
                } catch (ApexException e) {
                    throw new ApexRuntimeException("Failed to create the apex model.", e);
                }
            }
        });
        AxPolicyModel finalPolicyModel = aggregatePolicyModels(policyModelsMap);

        // Set the policy model in the engine
        apexEngineService.updateModel(engineKey, finalPolicyModel, true);

        handleExistingMarshallerAndUnmarshaller(inputParametersMap, outputParametersMap);
        setUpNewMarshallerAndUnmarshaller(apexParameters.getEngineServiceParameters(), inputParametersMap,
            outputParametersMap);

        // Wire up pairings between marhsallers and unmarshallers
        setUpMarshalerPairings(inputParametersMap);

        // Start event processing
        startUnmarshallers(inputParametersMap);
    }

    private AxPolicyModel aggregatePolicyModels(Map<ToscaPolicyIdentifier, AxPolicyModel> policyModelsMap) {
        // Doing a deep copy so that original values in policyModelsMap is retained
        // after reduction operation
        Set<Entry<ToscaPolicyIdentifier, AxPolicyModel>> policyModelsEntries = policyModelsMap.entrySet().stream()
            .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
        Optional<Entry<ToscaPolicyIdentifier, AxPolicyModel>> finalPolicyModelEntry = policyModelsEntries.stream()
            .reduce((entry1, entry2) -> {
                try {
                    entry1.setValue(
                        PolicyModelMerger.getMergedPolicyModel(entry1.getValue(), entry2.getValue(), true, true));
                } catch (ApexModelException exc) {
                    LOGGER.error("Policy model for {} : {} has duplicates. So this policy is not executed.",
                        entry2.getKey().getName(), entry2.getKey().getVersion(), exc);
                    apexParametersMap.remove(entry2.getKey());
                    policyModelsMap.remove(entry2.getKey());
                }
                return entry1;
            });
        AxPolicyModel finalPolicyModel = null;
        if (finalPolicyModelEntry.isPresent()) {
            finalPolicyModel = new AxPolicyModel(finalPolicyModelEntry.get().getValue());
        }
        return finalPolicyModel;
    }

    private void setUpNewMarshallerAndUnmarshaller(EngineServiceParameters engineServiceParameters,
        Map<String, EventHandlerParameters> inputParametersMap, Map<String, EventHandlerParameters> outputParametersMap)
        throws ApexEventException {

        // Producer parameters specify what event marshalers to handle events leaving
        // Apex are
        // set up and how they are set up
        for (Entry<String, EventHandlerParameters> outputParameters : outputParametersMap.entrySet()) {
            final ApexEventMarshaller marshaller = new ApexEventMarshaller(outputParameters.getKey(),
                engineServiceParameters, outputParameters.getValue());
            marshaller.init();
            apexEngineService.registerActionListener(outputParameters.getKey(), marshaller);
            marshallerMap.put(outputParameters.getKey(), marshaller);
        }

        // Consumer parameters specify what event unmarshalers to handle events coming
        // into Apex
        // are set up and how they are set up
        for (final Entry<String, EventHandlerParameters> inputParameters : inputParametersMap.entrySet()) {
            final ApexEventUnmarshaller unmarshaller = new ApexEventUnmarshaller(inputParameters.getKey(),
                engineServiceParameters, inputParameters.getValue());
            unmarshallerMap.put(inputParameters.getKey(), unmarshaller);
            unmarshaller.init(engineServiceHandler);
        }
    }

    private void handleExistingMarshallerAndUnmarshaller(Map<String, EventHandlerParameters> inputParametersMap,
        Map<String, EventHandlerParameters> outputParametersMap) {
        // stop and remove any marshaller/unmarshaller that is part of a policy that is
        // undeployed
        marshallerMap.entrySet().stream()
            .filter(marshallerEntry -> !outputParametersMap.containsKey(marshallerEntry.getKey()))
            .forEach(marshallerEntry -> marshallerEntry.getValue().stop());
        marshallerMap.keySet().removeIf(marshallerKey -> !outputParametersMap.containsKey(marshallerKey));
        unmarshallerMap.entrySet().stream()
            .filter(unmarshallerEntry -> !inputParametersMap.containsKey(unmarshallerEntry.getKey()))
            .forEach(unmarshallerEntry -> unmarshallerEntry.getValue().stop());
        unmarshallerMap.keySet().removeIf(unmarshallerKey -> !inputParametersMap.containsKey(unmarshallerKey));

        // If a marshaller/unmarshaller is already initialized, they don't need to be
        // reinitialized during model update.
        outputParametersMap.keySet().removeIf(marshallerMap::containsKey);
        inputParametersMap.keySet().removeIf(unmarshallerMap::containsKey);
    }

    private void instantiateEngine(ApexParameters apexParameters) throws ApexException {
        if (null != apexEngineService && apexEngineService.getKey().equals(engineKey)) {
            throw new ApexException("Apex Engine already initialized.");
        }
        // Create engine with specified thread count
        LOGGER.debug("starting apex engine service . . .");
        apexEngineService = EngineServiceImpl.create(apexParameters.getEngineServiceParameters());

        // Create the engine holder to hold the engine's references and act as an event
        // receiver
        engineServiceHandler = new ApexEngineServiceHandler(apexEngineService);
    }

    /**
     * Set up unmarshaler/marshaler pairing for synchronized event handling. We only
     * need to traverse the unmarshalers because the unmarshalers and marshalers are
     * paired one to one uniquely so if we find a synchronized unmarshaler we'll
     * also find its paired marshaler
     *
     * @param inputParametersMap the apex parameters
     */
    private void setUpMarshalerPairings(Map<String, EventHandlerParameters> inputParametersMap) {
        for (final Entry<String, EventHandlerParameters> inputParameters : inputParametersMap.entrySet()) {
            final ApexEventUnmarshaller unmarshaller = unmarshallerMap.get(inputParameters.getKey());

            // Pair up peered unmarshalers and marshalers
            for (final EventHandlerPeeredMode peeredMode : EventHandlerPeeredMode.values()) {
                // Check if the unmarshaler is synchronized with a marshaler
                if (inputParameters.getValue().isPeeredMode(peeredMode)) {
                    // Find the unmarshaler and marshaler
                    final ApexEventMarshaller peeredMarshaler = marshallerMap
                        .get(inputParameters.getValue().getPeer(peeredMode));

                    // Connect the unmarshaler and marshaler
                    unmarshaller.connectMarshaler(peeredMode, peeredMarshaler);
                }
            }
        }
    }

    /**
     * Start up event processing, this happens once all marshaller to unmarshaller
     * wiring has been done.
     *
     * @param inputParametersMap the apex parameters
     */
    private void startUnmarshallers(Map<String, EventHandlerParameters> inputParametersMap) {
        for (final Entry<String, EventHandlerParameters> inputParameters : inputParametersMap.entrySet()) {
            unmarshallerMap.get(inputParameters.getKey()).start();
        }
    }

    /**
     * Updates the APEX Engine with the model created from new Policies.
     *
     * @param apexParamsMap the apex parameters map for the Apex service
     * @throws ApexException on errors
     */
    public void updateModel(Map<ToscaPolicyIdentifier, ApexParameters> apexParamsMap) throws ApexException {
        try {
            ApexParameters apexParameters = apexParamsMap.values().iterator().next();
            setUpModelMarshallerAndUnmarshaller(apexParameters);
        } catch (final Exception e) {
            LOGGER.debug(APEX_ENGINE_FAILED_MSG, e);
            throw new ApexActivatorException(APEX_ENGINE_FAILED_MSG, e);
        }
    }

    /**
     * Get the Apex engine worker stats.
     */
    public List<AxEngineModel> getEngineStats() {
        List<AxEngineModel> engineStats = null;
        if (apexEngineService != null) {
            engineStats = apexEngineService.getEngineStats();
        }
        return engineStats;
    }

    /**
     * Terminate the Apex engine.
     *
     * @throws ApexException on termination errors
     */
    public void terminate() throws ApexException {
        // Shut down all marshalers and unmarshalers
        shutdownMarshallerAndUnmarshaller();

        // Check if the engine service handler has been shut down already
        if (engineServiceHandler != null) {
            engineServiceHandler.terminate();
            engineServiceHandler = null;
        }

        // Clear the services
        ModelService.clear();
        ParameterService.clear();
    }

    /**
     * Shuts down all marshallers and unmarshallers.
     */
    private void shutdownMarshallerAndUnmarshaller() {
        marshallerMap.values().forEach(ApexEventMarshaller::stop);
        marshallerMap.clear();
        unmarshallerMap.values().forEach(ApexEventUnmarshaller::stop);
        unmarshallerMap.clear();
    }
}
