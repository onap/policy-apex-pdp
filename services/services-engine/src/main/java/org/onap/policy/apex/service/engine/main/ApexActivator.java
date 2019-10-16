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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Getter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelMerger;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.engdep.EngDepMessagingService;
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
 * This class wraps an Apex engine so that it can be activated as a complete service together with
 * all its context, executor, and event plugins.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexActivator {
    private static final String APEX_ENGINE_FAILED_MSG = "Apex engine failed to start as a service";

    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexActivator.class);

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

    // The engine service
    private EngineService apexEngineService;

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
            instantiateEngine(apexParameters);
            Map<ToscaPolicyIdentifier, AxPolicyModel> policyModelsMap = new LinkedHashMap<>();
            Map<String, EventHandlerParameters> inputParametersMap = new LinkedHashMap<>();
            Map<String, EventHandlerParameters> outputParametersMap = new LinkedHashMap<>();

            for (Entry<ToscaPolicyIdentifier, ApexParameters> apexParamsEntry : apexParametersMap.entrySet()) {
                ApexParameters apexParams = apexParamsEntry.getValue();
                if (!checkForDuplicateIoParameter(inputParametersMap, apexParams.getEventInputParameters().keySet())
                    && !checkForDuplicateIoParameter(outputParametersMap,
                        apexParams.getEventOutputParameters().keySet())) {
                    inputParametersMap.putAll(apexParams.getEventInputParameters());
                    outputParametersMap.putAll(apexParams.getEventOutputParameters());
                } else {
                    LOGGER.error("I/O Parameters for " + apexParamsEntry.getKey().getName() + ":"
                        + apexParamsEntry.getKey().getVersion()
                        + " is having duplicates. So this policy is not executed");
                    apexParametersMap.remove(apexParamsEntry.getKey());
                    continue;
                }
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
            }
            AxPolicyModel finalPolicyModel = aggregatePolicyModels(policyModelsMap);
            // Set the policy model in the engine
            apexEngineService.updateModel(apexParameters.getEngineServiceParameters().getEngineKey(),
                finalPolicyModel, true);
            setUpMarshallerAndUnmarshaller(apexParameters.getEngineServiceParameters(), inputParametersMap,
                outputParametersMap);
            setUpmarshalerPairings(inputParametersMap);
        } catch (final Exception e) {
            LOGGER.debug(APEX_ENGINE_FAILED_MSG, e);
            throw new ApexActivatorException(APEX_ENGINE_FAILED_MSG, e);
        }

        LOGGER.debug("Apex engine started as a service");
    }

    private boolean checkForDuplicateIoParameter(Map<String, EventHandlerParameters> parametersMap,
        Set<String> paramKeys) {
        for (String key : paramKeys) {
            if (parametersMap.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    private AxPolicyModel aggregatePolicyModels(Map<ToscaPolicyIdentifier, AxPolicyModel> policyModelsMap) {
        Iterator<Map.Entry<ToscaPolicyIdentifier, AxPolicyModel>> entries = policyModelsMap.entrySet().iterator();
        Map.Entry<ToscaPolicyIdentifier, AxPolicyModel> firstEntry = entries.next();
        AxPolicyModel finalPolicyModel = firstEntry.getValue();
        while (entries.hasNext()) {
            Map.Entry<ToscaPolicyIdentifier, AxPolicyModel> entry = entries.next();
            try {
                finalPolicyModel =
                    PolicyModelMerger.getMergedPolicyModel(finalPolicyModel, entry.getValue(), true, true);
            } catch (ApexModelException e) {
                LOGGER.error("Policy model for " + entry.getKey().getName() + ":" + entry.getKey().getVersion()
                    + " is having duplicates. So this policy is not executed", e.getMessage());
                apexParametersMap.remove(entry.getKey());
            }
        }
        return finalPolicyModel;
    }

    private void setUpMarshallerAndUnmarshaller(EngineServiceParameters engineServiceParameters,
        Map<String, EventHandlerParameters> inputParametersMap, Map<String, EventHandlerParameters> outputParametersMap)
        throws ApexEventException {
        // Producer parameters specify what event marshalers to handle events leaving Apex are
        // set up and how they are set up
        for (Entry<String, EventHandlerParameters> outputParameters : outputParametersMap.entrySet()) {
            final ApexEventMarshaller marshaller = new ApexEventMarshaller(outputParameters.getKey(),
                engineServiceParameters, outputParameters.getValue());
            marshaller.init();
            apexEngineService.registerActionListener(outputParameters.getKey(), marshaller);
            marshallerMap.put(outputParameters.getKey(), marshaller);
        }
        // Consumer parameters specify what event unmarshalers to handle events coming into Apex
        // are set up and how they are set up
        for (final Entry<String, EventHandlerParameters> inputParameters : inputParametersMap.entrySet()) {
            final ApexEventUnmarshaller unmarshaller = new ApexEventUnmarshaller(inputParameters.getKey(),
                engineServiceParameters, inputParameters.getValue());
            unmarshallerMap.put(inputParameters.getKey(), unmarshaller);
            unmarshaller.init(engineServiceHandler);
        }
    }

    private void instantiateEngine(ApexParameters apexParameters) throws ApexException {
        if (null != apexEngineService
            && apexEngineService.getKey().equals(apexParameters.getEngineServiceParameters().getEngineKey())) {
            throw new ApexException("Apex Engine already initialized.");
        }
        // Create engine with specified thread count
        LOGGER.debug("starting apex engine service . . .");
        apexEngineService = EngineServiceImpl.create(apexParameters.getEngineServiceParameters());

        // Instantiate and start the messaging service for Deployment
        LOGGER.debug("starting apex deployment service . . .");
        final EngDepMessagingService engDepService = new EngDepMessagingService(apexEngineService,
                apexParameters.getEngineServiceParameters().getDeploymentPort());
        engDepService.start();

        // Create the engine holder to hold the engine's references and act as an event receiver
        engineServiceHandler = new ApexEngineServiceHandler(apexEngineService, engDepService);
    }

    /**
     * Set up unmarshaler/marshaler pairing for synchronized event handling. We only need to
     * traverse the unmarshalers because the
     * unmarshalers and marshalers are paired one to one uniquely so if we find a
     * synchronized unmarshaler we'll also find its
     * paired marshaler
     * @param inputParametersMap the apex parameters
     */
    private void setUpmarshalerPairings(Map<String, EventHandlerParameters> inputParametersMap) {
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
