/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.onap.policy.apex.core.engine.EngineParameters;
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

    private Map<ToscaConceptIdentifier, ApexMain> apexMainMap;

    /**
     * Constructs the object. Extracts the config and model files from each policy and instantiates the apex engine.
     *
     * @param policies the list of policies
     * @throws ApexStarterException if the apex engine instantiation failed using the policies passed
     */
    public ApexEngineHandler(List<ToscaPolicy> policies)  throws ApexStarterException {
        LOGGER.debug("Starting apex engine.");
        apexMainMap = initiateApexEngineForPolicies(policies);
        if (apexMainMap.isEmpty()) {
            ModelService.clear();
            ParameterService.clear();
            throw new ApexStarterException("Apex Engine failed to start.");
        }
    }

    /**
     * Updates the Apex Engine with the policy model created from new list of policies.
     *
     * @param policies the list of policies
     * @throws ApexStarterException if the apex engine instantiation failed using the policies passed
     */
    public void updateApexEngine(List<ToscaPolicy> policies) throws ApexStarterException {
        List<ToscaConceptIdentifier> runningPolicies = getRunningPolicies();
        List<ToscaPolicy> policiesToDeploy = policies.stream()
            .filter(policy -> !runningPolicies.contains(policy.getIdentifier())).collect(Collectors.toList());
        List<ToscaConceptIdentifier> policiesToUnDeploy = runningPolicies.stream()
            .filter(polId -> policies.stream().noneMatch(policy -> policy.getIdentifier().equals(polId)))
            .collect(Collectors.toList());
        Map<ToscaConceptIdentifier, ApexMain> undeployedPoliciesMainMap = new LinkedHashMap<>();
        policiesToUnDeploy.forEach(policyId -> {
            ApexMain apexMain = apexMainMap.get(policyId);
            try {
                apexMain.shutdown();
                undeployedPoliciesMainMap.put(policyId, apexMain);
                apexMainMap.remove(policyId);
          } catch (ApexException e) {
              LOGGER.error("Shutting down policy {} failed", policyId, e);
          }
        });
        if (!undeployedPoliciesMainMap.isEmpty()) {
            updateModelAndParameterServices(undeployedPoliciesMainMap);
        }
        if (!policiesToDeploy.isEmpty()) {
            Map<ToscaConceptIdentifier, ApexMain> aMainMap = initiateApexEngineForPolicies(policiesToDeploy);
            if (aMainMap.isEmpty()) {
                throw new ApexStarterException("Updating the APEX engine with new policies failed.");
            }
            apexMainMap.putAll(aMainMap);
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
        List<TaskParameters> taskParametersToRetain = new ArrayList<>();
        List<String> executorParamKeysToRetain = new ArrayList<>();
        List<String> schemaParamKeysToRetain = new ArrayList<>();

        List<AxArtifactKey> keyInfoKeystoRetain = new ArrayList<>();
        List<AxArtifactKey> schemaKeystoRetain = new ArrayList<>();
        List<AxArtifactKey> eventKeystoRetain = new ArrayList<>();
        List<AxArtifactKey> albumKeystoRetain = new ArrayList<>();
        List<AxArtifactKey> taskKeystoRetain = new ArrayList<>();
        List<AxArtifactKey> policyKeystoRetain = new ArrayList<>();

        apexMainMap.values().forEach(main -> {
            taskParametersToRetain.addAll(
                main.getApexParameters().getEngineServiceParameters().getEngineParameters().getTaskParameters());
            executorParamKeysToRetain.addAll(main.getApexParameters().getEngineServiceParameters().getEngineParameters()
                .getExecutorParameterMap().keySet());
            schemaParamKeysToRetain.addAll(main.getApexParameters().getEngineServiceParameters().getEngineParameters()
                .getContextParameters().getSchemaParameters().getSchemaHelperParameterMap().keySet());

            keyInfoKeystoRetain
                .addAll(main.getActivator().getPolicyModel().getKeyInformation().getKeyInfoMap().keySet());
            schemaKeystoRetain.addAll(main.getActivator().getPolicyModel().getSchemas().getSchemasMap().keySet());
            eventKeystoRetain.addAll(main.getActivator().getPolicyModel().getEvents().getEventMap().keySet());
            albumKeystoRetain.addAll(main.getActivator().getPolicyModel().getAlbums().getAlbumsMap().keySet());
            taskKeystoRetain.addAll(main.getActivator().getPolicyModel().getTasks().getTaskMap().keySet());
            policyKeystoRetain.addAll(main.getActivator().getPolicyModel().getPolicies().getPolicyMap().keySet());
        });
        for (ApexMain main : undeployedPoliciesMainMap.values()) {
            EngineParameters engineParameters =
                main.getApexParameters().getEngineServiceParameters().getEngineParameters();
            List<TaskParameters> taskParametersToRemove = engineParameters.getTaskParameters().stream()
                .filter(taskParameter -> !taskParametersToRetain.contains(taskParameter)).collect(Collectors.toList());
            List<String> executorParamKeysToRemove = engineParameters.getExecutorParameterMap().keySet().stream()
                .filter(key -> !executorParamKeysToRetain.contains(key)).collect(Collectors.toList());
            List<String> schemaParamKeysToRemove =
                engineParameters.getContextParameters().getSchemaParameters().getSchemaHelperParameterMap().keySet()
                    .stream().filter(key -> !schemaParamKeysToRetain.contains(key)).collect(Collectors.toList());
            ApexParameters existingParameters = ParameterService.get(ApexParameterConstants.MAIN_GROUP_NAME);
            EngineParameters aggregatedEngineParameters =
                existingParameters.getEngineServiceParameters().getEngineParameters();
            aggregatedEngineParameters.getTaskParameters().removeAll(taskParametersToRemove);
            executorParamKeysToRemove.forEach(aggregatedEngineParameters.getExecutorParameterMap()::remove);
            schemaParamKeysToRemove.forEach(aggregatedEngineParameters.getContextParameters().getSchemaParameters()
                .getSchemaHelperParameterMap()::remove);

            AxPolicyModel policyModel = main.getActivator().getPolicyModel();
            List<AxArtifactKey> keyInfoKeystoRemove = policyModel.getKeyInformation().getKeyInfoMap().keySet().stream()
                .filter(key -> !keyInfoKeystoRetain.contains(key)).collect(Collectors.toList());
            List<AxArtifactKey> schemaKeystoRemove = policyModel.getSchemas().getSchemasMap().keySet().stream()
                .filter(key -> !schemaKeystoRetain.contains(key)).collect(Collectors.toList());
            List<AxArtifactKey> eventKeystoRemove = policyModel.getEvents().getEventMap().keySet().stream()
                .filter(key -> !eventKeystoRetain.contains(key)).collect(Collectors.toList());
            List<AxArtifactKey> albumKeystoRemove = policyModel.getAlbums().getAlbumsMap().keySet().stream()
                .filter(key -> !albumKeystoRetain.contains(key)).collect(Collectors.toList());
            List<AxArtifactKey> taskKeystoRemove = policyModel.getTasks().getTaskMap().keySet().stream()
                .filter(key -> !taskKeystoRetain.contains(key)).collect(Collectors.toList());
            List<AxArtifactKey> policyKeystoRemove = policyModel.getPolicies().getPolicyMap().keySet().stream()
                .filter(key -> !policyKeystoRetain.contains(key)).collect(Collectors.toList());

            Map<AxArtifactKey, AxKeyInfo> keyInfoMap = ModelService.getModel(AxKeyInformation.class).getKeyInfoMap();
            Map<AxArtifactKey, AxContextSchema> schemasMap = ModelService.getModel(AxContextSchemas.class).getSchemasMap();
            Map<AxArtifactKey, AxEvent> eventMap = ModelService.getModel(AxEvents.class).getEventMap();
            Map<AxArtifactKey, AxContextAlbum> albumsMap = ModelService.getModel(AxContextAlbums.class).getAlbumsMap();
            Map<AxArtifactKey, AxTask> taskMap = ModelService.getModel(AxTasks.class).getTaskMap();
            Map<AxArtifactKey, AxPolicy> policyMap = ModelService.getModel(AxPolicies.class).getPolicyMap();

            keyInfoKeystoRemove.forEach(keyInfoMap::remove);
            schemaKeystoRemove.forEach(schemasMap::remove);
            eventKeystoRemove.forEach(eventMap::remove);
            albumKeystoRemove.forEach(albumsMap::remove);
            taskKeystoRemove.forEach(taskMap::remove);
            policyKeystoRemove.forEach(policyMap::remove);
        }
    }

    private Map<ToscaConceptIdentifier, ApexMain> initiateApexEngineForPolicies(List<ToscaPolicy> policies)
        throws ApexStarterException {
        Map<ToscaConceptIdentifier, ApexMain> aMainMap = new LinkedHashMap<>();
        for (ToscaPolicy policy : policies) {
            String policyName = policy.getIdentifier().getName();
            final StandardCoder standardCoder = new StandardCoder();
            ToscaServiceTemplate toscaServiceTemplate = new ToscaServiceTemplate();
            ToscaTopologyTemplate toscaTopologyTemplate = new ToscaTopologyTemplate();
            toscaTopologyTemplate.setPolicies(List.of(Map.of(policyName, policy)));
            toscaServiceTemplate.setToscaTopologyTemplate(toscaTopologyTemplate);
            File file;
            try {
                file = File.createTempFile(policyName, ".json");
                standardCoder.encode(file, toscaServiceTemplate);
            } catch (CoderException | IOException e) {
                throw new ApexStarterException(e);
            }
            final String[] apexArgs = {"-p", file.getAbsolutePath()};
            LOGGER.info("Starting apex engine for policy {}", policy.getIdentifier());
            try {
                ApexMain apexMain = new ApexMain(apexArgs);
                aMainMap.put(policy.getIdentifier(), apexMain);
//                ProcessBuilder builder = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"), ApexMain.class.getName(), "-p", file.getAbsolutePath());
//                Process process = builder.inheritIO().start();
//                Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
                //process.waitFor();
            } catch (Exception e) {
                LOGGER.error("Execution of policy {} failed", policy.getIdentifier(), e);
            }
        }
        return aMainMap;
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
        } catch (final ApexException e) {
            throw new ApexStarterException(e);
        }
    }
}
