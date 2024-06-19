/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.onap.policy.apex.core.engine.TaskParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
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
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.apex.service.parameters.ApexParameterConstants;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.onap.policy.models.tosca.authorative.concepts.ToscaConceptIdentifier;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.authorative.concepts.ToscaTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class instantiates the Apex Engine based on instruction from PAP.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexEngineHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexEngineHandler.class);

    private final Map<ToscaConceptIdentifier, ApexMain> apexMainMap = new LinkedHashMap<>();

    /**
     * Constructs the object. Extracts the config and model files from each policy and instantiates the apex engine.
     *
     * @param policies the list of policies
     * @throws ApexStarterException if the apex engine instantiation failed using the policies passed
     */
    public ApexEngineHandler(List<ToscaPolicy> policies) throws ApexStarterException {
        LOGGER.debug("Starting apex engine.");
        initiateApexEngineForPolicies(policies);
    }

    /**
     * Updates the Apex Engine with the policy model created from new list of policies.
     *

     * @param polsToDeploy list of policies to deploy which will be modified to remove running policies
     * @param polsToUndeploy list of policies to undeploy which will be modified to remove policies not running
     * @throws ApexStarterException if the apex engine instantiation failed using the policies passed
     */
    public void updateApexEngine(final List<ToscaPolicy> polsToDeploy,
                                 final List<ToscaConceptIdentifier> polsToUndeploy)
            throws ApexStarterException {
        Set<ToscaConceptIdentifier> runningPolicies = new HashSet<>(getRunningPolicies());
        List<ToscaPolicy> policiesToDeploy = new ArrayList<>(polsToDeploy);
        policiesToDeploy.removeIf(p -> runningPolicies.contains(p.getIdentifier()));
        List<ToscaConceptIdentifier> policiesToUnDeploy = new ArrayList<>(polsToUndeploy);
        policiesToUnDeploy.removeIf(p -> !runningPolicies.contains(p));
        Map<ToscaConceptIdentifier, ApexMain> undeployedPoliciesMainMap = new LinkedHashMap<>();
        policiesToUnDeploy.forEach(policyId -> {
            var apexMain = apexMainMap.get(policyId);
            try {
                apexMain.shutdown();
                undeployedPoliciesMainMap.put(policyId, apexMain);
                apexMainMap.remove(policyId);
            } catch (ApexException e) {
                LOGGER.error("Shutting down policy {} failed", policyId, e);
            }
        });
        if (!undeployedPoliciesMainMap.isEmpty() && !apexMainMap.isEmpty()) {
            updateModelAndParameterServices(undeployedPoliciesMainMap);
        }
        if (!policiesToDeploy.isEmpty()) {
            initiateApexEngineForPolicies(policiesToDeploy);
        }
        if (apexMainMap.isEmpty()) {
            ModelService.clear();
            ParameterService.clear();
        }
    }

    /**
     * Clear the corresponding items from ModelService and ParameterService.
     *
     * @param undeployedPoliciesMainMap the policies that are undeployed
     */
    private void updateModelAndParameterServices(Map<ToscaConceptIdentifier, ApexMain> undeployedPoliciesMainMap) {
        Set<String> inputParamKeysToRetain = new HashSet<>();
        Set<String> outputParamKeysToRetain = new HashSet<>();
        List<TaskParameters> taskParametersToRetain = new ArrayList<>();
        List<String> executorParamKeysToRetain = new ArrayList<>();
        List<String> schemaParamKeysToRetain = new ArrayList<>();

        Map<AxArtifactKey, AxKeyInfo> keyInfoMapToRetain = new HashMap<>();
        Map<AxArtifactKey, AxContextSchema> schemaMapToRetain = new HashMap<>();
        Map<AxArtifactKey, AxEvent> eventMapToRetain = new HashMap<>();
        Map<AxArtifactKey, AxContextAlbum> albumMapToRetain = new HashMap<>();
        Map<AxArtifactKey, AxTask> taskMapToRetain = new HashMap<>();
        Map<AxArtifactKey, AxPolicy> policyMapToRetain = new HashMap<>();

        apexMainMap.values().forEach(main -> {
            inputParamKeysToRetain.addAll(main.getApexParameters().getEventInputParameters().keySet());
            outputParamKeysToRetain.addAll(main.getApexParameters().getEventOutputParameters().keySet());
            taskParametersToRetain.addAll(
                main.getApexParameters().getEngineServiceParameters().getEngineParameters().getTaskParameters());
            executorParamKeysToRetain.addAll(main.getApexParameters().getEngineServiceParameters().getEngineParameters()
                .getExecutorParameterMap().keySet());
            schemaParamKeysToRetain.addAll(main.getApexParameters().getEngineServiceParameters().getEngineParameters()
                .getContextParameters().getSchemaParameters().getSchemaHelperParameterMap().keySet());

            AxPolicyModel policyModel = main.getActivator().getPolicyModel();
            keyInfoMapToRetain.putAll(policyModel.getKeyInformation().getKeyInfoMap());
            schemaMapToRetain.putAll(policyModel.getSchemas().getSchemasMap());
            eventMapToRetain.putAll(policyModel.getEvents().getEventMap());
            albumMapToRetain.putAll(policyModel.getAlbums().getAlbumsMap());
            taskMapToRetain.putAll(policyModel.getTasks().getTaskMap());
            policyMapToRetain.putAll(policyModel.getPolicies().getPolicyMap());
        });
        for (ApexMain main : undeployedPoliciesMainMap.values()) {
            if (null != main.getApexParameters()) {
                handleParametersRemoval(inputParamKeysToRetain, outputParamKeysToRetain, taskParametersToRetain,
                    executorParamKeysToRetain, schemaParamKeysToRetain, main);
            }
            if (null != main.getActivator() && null != main.getActivator().getPolicyModel()) {
                handleAxConceptsRemoval(keyInfoMapToRetain, schemaMapToRetain, eventMapToRetain, albumMapToRetain,
                    taskMapToRetain, policyMapToRetain, main);
            }
        }
    }

    private void handleParametersRemoval(Set<String> inputParamKeysToRetain, Set<String> outputParamKeysToRetain,
        List<TaskParameters> taskParametersToRetain, List<String> executorParamKeysToRetain,
        List<String> schemaParamKeysToRetain, ApexMain main) {
        ApexParameters existingParameters = ParameterService.get(ApexParameterConstants.MAIN_GROUP_NAME);
        List<String> eventInputParamKeysToRemove = main.getApexParameters().getEventInputParameters().keySet().stream()
            .filter(key -> !inputParamKeysToRetain.contains(key)).toList();
        List<String> eventOutputParamKeysToRemove = main.getApexParameters().getEventOutputParameters().keySet()
            .stream().filter(key -> !outputParamKeysToRetain.contains(key)).toList();
        eventInputParamKeysToRemove.forEach(existingParameters.getEventInputParameters()::remove);
        eventOutputParamKeysToRemove.forEach(existingParameters.getEventOutputParameters()::remove);
        var engineParameters = main.getApexParameters().getEngineServiceParameters().getEngineParameters();
        final List<TaskParameters> taskParametersToRemove = engineParameters.getTaskParameters().stream()
            .filter(taskParameter -> !taskParametersToRetain.contains(taskParameter)).toList();
        final List<String> executorParamKeysToRemove = engineParameters.getExecutorParameterMap().keySet().stream()
            .filter(key -> !executorParamKeysToRetain.contains(key)).toList();
        final List<String> schemaParamKeysToRemove =
            engineParameters.getContextParameters().getSchemaParameters().getSchemaHelperParameterMap().keySet()
                .stream().filter(key -> !schemaParamKeysToRetain.contains(key)).toList();
        var aggregatedEngineParameters = existingParameters.getEngineServiceParameters().getEngineParameters();
        aggregatedEngineParameters.getTaskParameters().removeAll(taskParametersToRemove);
        executorParamKeysToRemove.forEach(aggregatedEngineParameters.getExecutorParameterMap()::remove);
        schemaParamKeysToRemove.forEach(aggregatedEngineParameters.getContextParameters().getSchemaParameters()
            .getSchemaHelperParameterMap()::remove);
    }

    private void handleAxConceptsRemoval(Map<AxArtifactKey, AxKeyInfo> keyInfoMapToRetain,
        Map<AxArtifactKey, AxContextSchema> schemaMapToRetain, Map<AxArtifactKey, AxEvent> eventMapToRetain,
        Map<AxArtifactKey, AxContextAlbum> albumMapToRetain, Map<AxArtifactKey, AxTask> taskMapToRetain,
        Map<AxArtifactKey, AxPolicy> policyMapToRetain, ApexMain main) {
        final AxPolicyModel policyModel = main.getActivator().getPolicyModel();
        final List<AxArtifactKey> keyInfoKeystoRemove = policyModel.getKeyInformation().getKeyInfoMap().keySet()
            .stream().filter(key -> !keyInfoMapToRetain.containsKey(key)).toList();
        final List<AxArtifactKey> schemaKeystoRemove = policyModel.getSchemas().getSchemasMap().keySet().stream()
            .filter(key -> !schemaMapToRetain.containsKey(key)).toList();
        final List<AxArtifactKey> eventKeystoRemove = policyModel.getEvents().getEventMap().keySet().stream()
            .filter(key -> !eventMapToRetain.containsKey(key)).toList();
        final List<AxArtifactKey> albumKeystoRemove = policyModel.getAlbums().getAlbumsMap().keySet().stream()
            .filter(key -> !albumMapToRetain.containsKey(key)).toList();
        final List<AxArtifactKey> taskKeystoRemove = policyModel.getTasks().getTaskMap().keySet().stream()
            .filter(key -> !taskMapToRetain.containsKey(key)).toList();
        final List<AxArtifactKey> policyKeystoRemove = policyModel.getPolicies().getPolicyMap().keySet().stream()
            .filter(key -> !policyMapToRetain.containsKey(key)).toList();

        final Map<AxArtifactKey, AxKeyInfo> keyInfoMap = ModelService.getModel(AxKeyInformation.class).getKeyInfoMap();
        final Map<AxArtifactKey, AxContextSchema> schemasMap =
            ModelService.getModel(AxContextSchemas.class).getSchemasMap();
        final Map<AxArtifactKey, AxEvent> eventMap = ModelService.getModel(AxEvents.class).getEventMap();
        final Map<AxArtifactKey, AxContextAlbum> albumsMap =
            ModelService.getModel(AxContextAlbums.class).getAlbumsMap();
        final Map<AxArtifactKey, AxTask> taskMap = ModelService.getModel(AxTasks.class).getTaskMap();
        final Map<AxArtifactKey, AxPolicy> policyMap = ModelService.getModel(AxPolicies.class).getPolicyMap();

        // replace the ModelService with the right concept definition
        // this can get corrupted in case of deploying policies with duplicate concept keys
        keyInfoMap.putAll(keyInfoMapToRetain);
        schemasMap.putAll(schemaMapToRetain);
        eventMap.putAll(eventMapToRetain);
        albumsMap.putAll(albumMapToRetain);
        taskMap.putAll(taskMapToRetain);
        policyMap.putAll(policyMapToRetain);

        keyInfoKeystoRemove.forEach(keyInfoMap::remove);
        schemaKeystoRemove.forEach(schemasMap::remove);
        eventKeystoRemove.forEach(eventMap::remove);
        albumKeystoRemove.forEach(albumsMap::remove);
        taskKeystoRemove.forEach(taskMap::remove);
        policyKeystoRemove.forEach(policyMap::remove);
    }

    private void initiateApexEngineForPolicies(List<ToscaPolicy> policies)
        throws ApexStarterException {
        Map<ToscaConceptIdentifier, ApexMain> failedPoliciesMainMap = new LinkedHashMap<>();
        for (ToscaPolicy policy : policies) {
            String policyName = policy.getIdentifier().getName();
            final var standardCoder = new StandardCoder();
            var toscaServiceTemplate = new ToscaServiceTemplate();
            var toscaTopologyTemplate = new ToscaTopologyTemplate();
            toscaTopologyTemplate.setPolicies(List.of(Map.of(policyName, policy)));
            toscaServiceTemplate.setToscaTopologyTemplate(toscaTopologyTemplate);
            File file;
            try {
                file = TextFileUtils.createTempFile(policyName, ".json");
                standardCoder.encode(file, toscaServiceTemplate);
            } catch (CoderException | IOException e) {
                throw new ApexStarterException(e);
            }
            final var apexArgs = new String[] {"-p", file.getAbsolutePath()};
            LOGGER.info("Starting apex engine for policy {}", policy.getIdentifier());
            var apexMain = new ApexMain(apexArgs);
            if (apexMain.isAlive()) {
                apexMainMap.put(policy.getIdentifier(), apexMain);
            } else {
                failedPoliciesMainMap.put(policy.getIdentifier(), apexMain);
                LOGGER.error("Execution of policy {} failed", policy.getIdentifier());
            }
        }
        if (apexMainMap.isEmpty()) {
            ModelService.clear();
            ParameterService.clear();
            throw new ApexStarterException("Apex Engine failed to start.");
        } else if (!failedPoliciesMainMap.isEmpty()) {
            updateModelAndParameterServices(failedPoliciesMainMap);
            if (failedPoliciesMainMap.size() == policies.size()) {
                throw new ApexStarterException("Updating the APEX engine with new policies failed.");
            }
        }
    }

    /**
     * Method to get the APEX engine statistics.
     */
    public List<AxEngineModel> getEngineStats() {
        // engineStats from all the apexMain instances running individual tosca policies are combined here.
        return apexMainMap.values().stream().filter(apexMain -> (null != apexMain && apexMain.isAlive()))
            .flatMap(m -> m.getEngineStats().stream()).collect(Collectors.toList());
    }

    /**
     * Method to check whether the apex engine is running or not.
     */
    public boolean isApexEngineRunning() {
        return apexMainMap.values().stream().anyMatch(apexMain -> (null != apexMain && apexMain.isAlive()));
    }

    /**
     * Method that return the list of running policies in the apex engine.
     */
    public List<ToscaConceptIdentifier> getRunningPolicies() {
        return new ArrayList<>(apexMainMap.keySet());
    }

    /**
     * Method to shut down the apex engine.
     */
    public void shutdown() throws ApexStarterException {
        try {
            LOGGER.debug("Shutting down apex engine.");
            for (ApexMain apexMain : apexMainMap.values()) {
                apexMain.shutdown();
            }
            apexMainMap.clear();
            ModelService.clear();
            ParameterService.clear();
        } catch (final ApexException e) {
            throw new ApexStarterException(e);
        }
    }
}
