/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelMerger;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.parameters.ApexParameterConstants;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.parameters.ParameterService;
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

    @Getter
    @Setter
    private ApexParameters apexParameters;

    @Getter
    private AxPolicyModel policyModel;

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
     * @param parameters the apex parameters for the Apex service
     */
    public ApexActivator(ApexParameters parameters) {
        apexParameters = parameters;
    }

    /**
     * Initialize the Apex engine as a complete service.
     *
     * @throws ApexActivatorException on errors in initializing the engine
     */
    public void initialize() throws ApexActivatorException {
        LOGGER.debug("Apex engine starting as a service . . .");

        try {
            instantiateEngine(apexParameters);
            setUpModelMarshallerAndUnmarshaller(apexParameters);
        } catch (final Exception e) {
            try {
                terminate();
            } catch (ApexException e1) {
                LOGGER.error("Terminating the ApexActivator encountered error", e1);
            }
            throw new ApexActivatorException(APEX_ENGINE_FAILED_MSG, e);
        }

        LOGGER.debug("Apex engine started as a service");
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

    private void setUpModelMarshallerAndUnmarshaller(ApexParameters apexParameters) throws ApexException {
        AxPolicyModel model;
        try {
            final var policyModelString = apexParameters.getEngineServiceParameters().getPolicyModel();
            model = EngineServiceImpl.createModel(apexParameters.getEngineServiceParameters().getEngineKey(),
                policyModelString);
        } catch (ApexException e) {
            throw new ApexRuntimeException("Failed to create the apex model.", e);
        }

        AxKeyInformation existingKeyInformation = null;
        AxContextSchemas existingSchemas = null;
        AxEvents existingEvents = null;
        AxContextAlbums existingAlbums = null;
        AxTasks existingTasks = null;
        AxPolicies existingPolicies = null;
        if (ModelService.existsModel(AxPolicyModel.class)) {
            existingKeyInformation = new AxKeyInformation(ModelService.getModel(AxKeyInformation.class));
            existingSchemas = new AxContextSchemas(ModelService.getModel(AxContextSchemas.class));
            existingEvents = new AxEvents(ModelService.getModel(AxEvents.class));
            existingAlbums = new AxContextAlbums(ModelService.getModel(AxContextAlbums.class));
            existingTasks = new AxTasks(ModelService.getModel(AxTasks.class));
            existingPolicies = new AxPolicies(ModelService.getModel(AxPolicies.class));
        }
        // Set the policy model in the engine
        try {
            apexEngineService.updateModel(apexParameters.getEngineServiceParameters().getEngineKey(), model, true);
            policyModel = new AxPolicyModel(model);
        } catch (ApexException exp) {
            // Discard concepts from the current policy
            if (null != existingKeyInformation) {
                // Update model service with other policies' concepts.
                ModelService.registerModel(AxKeyInformation.class, existingKeyInformation);
                ModelService.registerModel(AxContextSchemas.class, existingSchemas);
                ModelService.registerModel(AxEvents.class, existingEvents);
                ModelService.registerModel(AxContextAlbums.class, existingAlbums);
                ModelService.registerModel(AxTasks.class, existingTasks);
                ModelService.registerModel(AxPolicies.class, existingPolicies);
            } else {
                ModelService.clear();
            }
            throw exp;
        }
        if (null != existingKeyInformation) {
            // Make sure all concepts in previously deployed policies are retained in ModelService
            // during multi policy deployment.
            updateModelService(existingKeyInformation, existingSchemas, existingEvents, existingAlbums, existingTasks,
                existingPolicies);
        }

        setUpNewMarshallerAndUnmarshaller(apexParameters.getEngineServiceParameters(),
            apexParameters.getEventInputParameters(), apexParameters.getEventOutputParameters());

        // Wire up pairings between marhsallers and unmarshallers
        setUpMarshalerPairings(apexParameters.getEventInputParameters());

        // Start event processing
        startUnmarshallers(apexParameters.getEventInputParameters());
    }

    private void updateModelService(AxKeyInformation existingKeyInformation,
        AxContextSchemas existingSchemas, AxEvents existingEvents,
        AxContextAlbums existingAlbums, AxTasks existingTasks,
        AxPolicies existingPolicies) throws ApexModelException {

        var axContextSchemas = ModelService.getModel(AxContextSchemas.class);
        var axEvents = ModelService.getModel(AxEvents.class);
        var axContextAlbums = ModelService.getModel(AxContextAlbums.class);
        var axTasks = ModelService.getModel(AxTasks.class);
        var axPolicies = ModelService.getModel(AxPolicies.class);

        Map<AxArtifactKey, AxContextSchema> newSchemasMap = axContextSchemas.getSchemasMap();
        Map<AxArtifactKey, AxEvent> newEventsMap = axEvents.getEventMap();
        Map<AxArtifactKey, AxContextAlbum> newAlbumsMap = axContextAlbums.getAlbumsMap();
        Map<AxArtifactKey, AxTask> newTasksMap = axTasks.getTaskMap();
        Map<AxArtifactKey, AxPolicy> newPoliciesMap = axPolicies.getPolicyMap();

        var errorMessage = new StringBuilder();
        PolicyModelMerger.checkForDuplicateItem(existingSchemas.getSchemasMap(), newSchemasMap, errorMessage, "schema");
        PolicyModelMerger.checkForDuplicateItem(existingEvents.getEventMap(), newEventsMap, errorMessage, "event");
        PolicyModelMerger.checkForDuplicateItem(existingAlbums.getAlbumsMap(), newAlbumsMap, errorMessage, "album");
        PolicyModelMerger.checkForDuplicateItem(existingTasks.getTaskMap(), newTasksMap, errorMessage, "task");
        PolicyModelMerger.checkForDuplicateItem(existingPolicies.getPolicyMap(), newPoliciesMap, errorMessage,
            "policy");
        if (errorMessage.length() > 0) {
            throw new ApexModelException(errorMessage.toString());
        }

        var axKeyInformation = ModelService.getModel(AxKeyInformation.class);
        Map<AxArtifactKey, AxKeyInfo> newKeyInfoMap = axKeyInformation.getKeyInfoMap();
        // Now add all the concepts that must be copied over
        newKeyInfoMap.putAll(existingKeyInformation.getKeyInfoMap());
        newSchemasMap.putAll(existingSchemas.getSchemasMap());
        newEventsMap.putAll(existingEvents.getEventMap());
        newAlbumsMap.putAll(existingAlbums.getAlbumsMap());
        newTasksMap.putAll(existingTasks.getTaskMap());
        newPoliciesMap.putAll(existingPolicies.getPolicyMap());
    }

    private void setUpNewMarshallerAndUnmarshaller(EngineServiceParameters engineServiceParameters,
        Map<String, EventHandlerParameters> inputParametersMap, Map<String, EventHandlerParameters> outputParametersMap)
        throws ApexEventException {

        // Producer parameters specify what event marshalers to handle events leaving
        // Apex are
        // set up and how they are set up
        for (Entry<String, EventHandlerParameters> outputParameters : outputParametersMap.entrySet()) {
            final var marshaller = new ApexEventMarshaller(outputParameters.getKey(),
                engineServiceParameters, outputParameters.getValue());
            marshaller.init();
            apexEngineService.registerActionListener(outputParameters.getKey(), marshaller);
            marshallerMap.put(outputParameters.getKey(), marshaller);
        }

        // Consumer parameters specify what event unmarshalers to handle events coming
        // into Apex
        // are set up and how they are set up
        for (final Entry<String, EventHandlerParameters> inputParameters : inputParametersMap.entrySet()) {
            final var unmarshaller = new ApexEventUnmarshaller(inputParameters.getKey(),
                engineServiceParameters, inputParameters.getValue());
            unmarshallerMap.put(inputParameters.getKey(), unmarshaller);
            unmarshaller.init(engineServiceHandler);
        }
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
                    final ApexEventMarshaller peeredMarshaler =
                        marshallerMap.get(inputParameters.getValue().getPeer(peeredMode));

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
        // Clear the services in case if this was the only policy running in the engine
        ApexParameters apexParams = null;
        if (ParameterService.contains(ApexParameterConstants.MAIN_GROUP_NAME)) {
            apexParams = ParameterService.get(ApexParameterConstants.MAIN_GROUP_NAME);
        }
        if (null != apexParams
            && apexParameters.getEventInputParameters().size() == apexParams.getEventInputParameters().size()) {
            ModelService.clear();
            ParameterService.clear();
        }
    }

    /**
     * Shuts down all marshallers and unmarshallers.
     */
    private void shutdownMarshallerAndUnmarshaller() {
        unmarshallerMap.values().forEach(ApexEventUnmarshaller::stop);
        unmarshallerMap.clear();
        marshallerMap.values().forEach(ApexEventMarshaller::stop);
        marshallerMap.clear();
    }
}
