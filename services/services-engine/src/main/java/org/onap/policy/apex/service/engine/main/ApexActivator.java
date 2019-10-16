/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.engdep.EngDepMessagingService;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class wraps an Apex engine so that it can be activated as a complete service together with
 * all its context, executor, and event plugins.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexActivator {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexActivator.class);

    // The parameters of this Apex activator
    @Getter
    private ApexParameters apexParameters;

    // The parameters of the Apex activator when running with multiple policies
    @Getter
    private Map<ToscaPolicyIdentifier, ApexParameters> apexParametersMap;

    // Event unmarshalers are used to receive events asynchronously into Apex
    private final Map<String, ApexEventUnmarshaller> unmarshallerMap = new LinkedHashMap<>();

    // Event marshalers are used to send events asynchronously from Apex
    private final Map<String, ApexEventMarshaller> marshallerMap = new LinkedHashMap<>();

    // The engine service handler holds the references to the engine and its EngDep deployment
    // interface. It also acts as a receiver for asynchronous
    // and synchronous events from the engine.
    private ApexEngineServiceHandler engineServiceHandler = null;

    /**
     * Instantiate the activator for the Apex engine as a complete service.
     *
     * @param parameters the apex parameters for the Apex service
     */
    public ApexActivator(final ApexParameters parameters) {
        apexParameters = parameters;
    }

    /**
     * Instantiate the activator for the Apex engine as a complete service when running with multiple policies.
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
            EngineService apexEngineService = instantiateEngine(apexParameters);

            // Check if a policy model file has been specified
            if (apexParameters.getEngineServiceParameters().getPolicyModelFileName() != null) {
                LOGGER.debug("deploying policy model in \""
                        + apexParameters.getEngineServiceParameters().getPolicyModelFileName()
                        + "\" to the apex engines . . .");

                // Set the policy model in the engine
                final String policyModelString = TextFileUtils
                        .getTextFileAsString(apexParameters.getEngineServiceParameters().getPolicyModelFileName());
                apexEngineService.updateModel(apexParameters.getEngineServiceParameters().getEngineKey(),
                        policyModelString, true);
            }

            setUpMarshallerAndUnmarshaller(apexEngineService, apexParameters);
            setUpmarshalerPairings(apexParameters);
        } catch (final Exception e) {
            LOGGER.debug("Apex engine failed to start as a service", e);
            throw new ApexActivatorException("Apex engine failed to start as a service", e);
        }

        LOGGER.debug("Apex engine started as a service");
    }

    /**
     * Initialize the Apex engine as a complete service when running with multiple policies.
     *
     * @throws ApexActivatorException on errors in initializing the engine
     */
    public void initializeForMultiplePolicies() throws ApexActivatorException {
        LOGGER.debug("Apex engine starting as a service . . .");

        try {
            apexParameters = apexParametersMap.values().iterator().next();
            // totalInstanceCount is the sum of instance counts required as per each policy
            int totalInstanceCount = apexParametersMap.values().stream()
                .mapToInt(p -> p.getEngineServiceParameters().getInstanceCount()).sum();
            apexParameters.getEngineServiceParameters().setInstanceCount(totalInstanceCount);
            EngineService apexEngineService = instantiateEngine(apexParameters);

            Map<ToscaPolicyIdentifier, AxPolicyModel> policyModelsMap =
                new LinkedHashMap<ToscaPolicyIdentifier, AxPolicyModel>();
            for (Entry<ToscaPolicyIdentifier, ApexParameters> apexParamsEntry : apexParametersMap.entrySet()) {
                ApexParameters apexParams = apexParamsEntry.getValue();
                // Check if a policy model file has been specified
                if (apexParams.getEngineServiceParameters().getPolicyModelFileName() != null) {
                    LOGGER.debug("deploying policy model in \""
                        + apexParams.getEngineServiceParameters().getPolicyModelFileName()
                        + "\" to the apex engines . . .");

                    final String policyModelString = TextFileUtils
                        .getTextFileAsString(apexParams.getEngineServiceParameters().getPolicyModelFileName());
                    AxPolicyModel policyModel = EngineServiceImpl
                        .createModel(apexParams.getEngineServiceParameters().getEngineKey(), policyModelString);
                    policyModelsMap.put(apexParamsEntry.getKey(), policyModel);
                }
                setUpMarshallerAndUnmarshaller(apexEngineService, apexParams);
                setUpmarshalerPairings(apexParams);
            }
            AxPolicyModel finalPolicyModel = aggregatePolicyModels(policyModelsMap);
            // Set the policy model in the engine
            apexEngineService.updateModel(apexParameters.getEngineServiceParameters().getEngineKey(),
                finalPolicyModel, true);
        } catch (final Exception e) {
            LOGGER.debug("Apex engine failed to start as a service", e);
            throw new ApexActivatorException("Apex engine failed to start as a service", e);
        }

        LOGGER.debug("Apex engine started as a service");
    }

    private AxPolicyModel aggregatePolicyModels(Map<ToscaPolicyIdentifier, AxPolicyModel> policyModelsMap) {
        AxContextSchemas axContextSchemas = new AxContextSchemas();
        AxKeyInformation axKeyInformation = new AxKeyInformation();
        AxEvents axEvents = new AxEvents();
        AxContextAlbums axContextAlbums = new AxContextAlbums();
        AxTasks axTasks = new AxTasks();
        AxPolicies axPolicies = new AxPolicies();
        for (Entry<ToscaPolicyIdentifier, AxPolicyModel> policyModelEntry : policyModelsMap.entrySet()) {
            AxPolicyModel policyModel = policyModelEntry.getValue();
            StringBuilder errorMessage = new StringBuilder();
            for (AxArtifactKey key : policyModel.getSchemas().getSchemasMap().keySet()) {
                AxContextSchema schemaKey = axContextSchemas.getSchemasMap().get(key);
                // same context schema name with different definitions cannot occur in multiple policies
                if (null != schemaKey) {
                    if (schemaKey.equals(policyModel.getSchemas().get(key))) {
                        LOGGER.info("Same contextSchema - " + key.getId() + "is being used by multiple policies.");
                    } else {
                        errorMessage.append("\n Same context schema - ").append(key.getId())
                            .append(" with different definitions used in different policies");
                    }
                }
            }
            for (AxArtifactKey key : policyModel.getEvents().getEventMap().keySet()) {
                if (axEvents.getEventMap().containsKey(key)) {
                    errorMessage.append("\n Duplicate event found - ").append(key.getId());
                }
            }
            for (AxArtifactKey key : policyModel.getAlbums().getAlbumsMap().keySet()) {
                AxContextAlbum albumsKey = axContextAlbums.getAlbumsMap().get(key);
                // same context album name with different definitions cannot occur in multiple policies
                if (null != albumsKey) {
                    if (albumsKey.equals(policyModel.getAlbums().get(key))) {
                        LOGGER.info("Same contextAlbum - " + key.getId() + "is being used by multiple policies.");
                    } else {
                        errorMessage.append("\n Same context album - ").append(key.getId())
                            .append(" with different definitions used in different policies");
                    }
                }
            }
            for (AxArtifactKey key : policyModel.getTasks().getTaskMap().keySet()) {
                if (axTasks.getTaskMap().containsKey(key)) {
                    errorMessage.append("\n Duplicate task found - ").append(key.getId());
                }
            }
            for (AxArtifactKey key : policyModel.getPolicies().getPolicyMap().keySet()) {
                if (axPolicies.getPolicyMap().containsKey(key)) {
                    errorMessage.append("\n Duplicate policy found - ").append(key.getId());
                }
            }
            if (errorMessage.length() > 0) {
                LOGGER.error("Policy model for " + policyModelEntry.getKey().getName() + ":"
                    + policyModelEntry.getKey().getVersion() + " is having duplicates. So this policy is not executed");
                apexParametersMap.remove(policyModelEntry.getKey());
            } else {
                // key information can be same in multiple policies
                axKeyInformation.getKeyInfoMap().putAll(policyModel.getKeyInformation().getKeyInfoMap());
                axContextSchemas.getSchemasMap().putAll(policyModel.getSchemas().getSchemasMap());
                axEvents.getEventMap().putAll(policyModel.getEvents().getEventMap());
                axContextAlbums.getAlbumsMap().putAll(policyModel.getAlbums().getAlbumsMap());
                axTasks.getTaskMap().putAll(policyModel.getTasks().getTaskMap());
                axPolicies.getPolicyMap().putAll(policyModel.getPolicies().getPolicyMap());
            }
        }
        return new AxPolicyModel(new AxArtifactKey(), axContextSchemas, axKeyInformation, axEvents, axContextAlbums,
            axTasks, axPolicies);
    }

    private void setUpMarshallerAndUnmarshaller(EngineService apexEngineService, ApexParameters apexParameters)
        throws ApexEventException {
        // Producer parameters specify what event marshalers to handle events leaving Apex are
        // set up and how they are set up
        for (final Entry<String, EventHandlerParameters> outputParameters : apexParameters
                .getEventOutputParameters().entrySet()) {
            final ApexEventMarshaller marshaller = new ApexEventMarshaller(outputParameters.getKey(),
                    apexParameters.getEngineServiceParameters(), outputParameters.getValue());
            marshaller.init();
            apexEngineService.registerActionListener(outputParameters.getKey(), marshaller);
            marshallerMap.put(outputParameters.getKey(), marshaller);
        }

        // Consumer parameters specify what event unmarshalers to handle events coming into Apex
        // are set up and how they are set up
        for (final Entry<String, EventHandlerParameters> inputParameters : apexParameters.getEventInputParameters()
                .entrySet()) {
            final ApexEventUnmarshaller unmarshaller = new ApexEventUnmarshaller(inputParameters.getKey(),
                    apexParameters.getEngineServiceParameters(), inputParameters.getValue());
            unmarshallerMap.put(inputParameters.getKey(), unmarshaller);
            unmarshaller.init(engineServiceHandler);
        }
    }

    private EngineService instantiateEngine(ApexParameters apexParameters) throws ApexException {
        // Create engine with specified thread count
        LOGGER.debug("starting apex engine service . . .");
        EngineService apexEngineService = EngineServiceImpl.create(apexParameters.getEngineServiceParameters());

        // Instantiate and start the messaging service for Deployment
        LOGGER.debug("starting apex deployment service . . .");
        final EngDepMessagingService engDepService = new EngDepMessagingService(apexEngineService,
                apexParameters.getEngineServiceParameters().getDeploymentPort());
        engDepService.start();

        // Create the engine holder to hold the engine's references and act as an event receiver
        engineServiceHandler = new ApexEngineServiceHandler(apexEngineService, engDepService);
        return apexEngineService;
    }

    /**
     * Set up unmarshaler/marshaler pairing for synchronized event handling. We only need to
     * traverse the unmarshalers because the
     * unmarshalers and marshalers are paired one to one uniquely so if we find a
     * synchronized unmarshaler we'll also find its
     * paired marshaler
     * @param apexParameters the apex parameters
     */
    private void setUpmarshalerPairings(ApexParameters apexParameters) {
        for (final Entry<String, EventHandlerParameters> inputParameters : apexParameters.getEventInputParameters()
                .entrySet()) {
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
            // Now let's get events flowing
            unmarshaller.start();
        }
    }

    /**
     * Terminate the Apex engine.
     *
     * @throws ApexException on termination errors
     */
    public void terminate() throws ApexException {
        // Shut down all marshalers and unmarshalers
        for (final ApexEventMarshaller marshaller : marshallerMap.values()) {
            marshaller.stop();
        }
        marshallerMap.clear();

        for (final ApexEventUnmarshaller unmarshaller : unmarshallerMap.values()) {
            unmarshaller.stop();
        }
        unmarshallerMap.clear();

        // Check if the engine service handler has been shut down already
        if (engineServiceHandler != null) {
            engineServiceHandler.terminate();
            engineServiceHandler = null;
        }

        // Clear the services
        ModelService.clear();
        ParameterService.clear();
    }
}
