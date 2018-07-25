/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.examples.adaptive.model;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxLogicReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;
import org.onap.policy.apex.model.policymodel.handling.PolicyLogicReader;

/**
 * The Class AdaptiveDomainModelFactory.
 */
public class AdaptiveDomainModelFactory {

    /**
     * Gets the anomaly detection policy model.
     *
     * @return the anomaly detection policy model
     */
    // CHECKSTYLE:OFF: checkstyle:maximumMethodLength
    public AxPolicyModel getAnomalyDetectionPolicyModel() {
        // CHECKSTYLE:ON: checkstyle:maximumMethodLength
        // Data types for event parameters
        final AxContextSchema monitoredValue =
                new AxContextSchema(new AxArtifactKey("MonitoredValue", "0.0.1"), "Java", "java.lang.Double");
        final AxContextSchema iteration =
                new AxContextSchema(new AxArtifactKey("Iteration", "0.0.1"), "Java", "java.lang.Integer");

        final AxContextSchemas adContextSchemas = new AxContextSchemas(new AxArtifactKey("AADMDatatypes", "0.0.1"));
        adContextSchemas.getSchemasMap().put(monitoredValue.getKey(), monitoredValue);
        adContextSchemas.getSchemasMap().put(iteration.getKey(), iteration);

        final AxEvent anomalyDetectionTriggerEvent =
                new AxEvent(new AxArtifactKey("AnomalyDetectionTriggerEvent", "0.0.1"),
                        "org.onap.policy.apex.examples.adaptive.events");
        anomalyDetectionTriggerEvent.setSource("External");
        anomalyDetectionTriggerEvent.setTarget("Match");
        anomalyDetectionTriggerEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(anomalyDetectionTriggerEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        anomalyDetectionTriggerEvent.getParameterMap().put("Iteration", new AxField(
                new AxReferenceKey(anomalyDetectionTriggerEvent.getKey(), "Iteration"), iteration.getKey()));

        final AxEvent anomalyDetectionMatchEvent = new AxEvent(new AxArtifactKey("AnomalyDetectionMatchEvent", "0.0.1"),
                "org.onap.policy.apex.examples.adaptive.events");
        anomalyDetectionMatchEvent.setSource("Match");
        anomalyDetectionMatchEvent.setTarget("Establish");
        anomalyDetectionMatchEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(anomalyDetectionMatchEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        anomalyDetectionMatchEvent.getParameterMap().put("Iteration",
                new AxField(new AxReferenceKey(anomalyDetectionMatchEvent.getKey(), "Iteration"), iteration.getKey()));

        final AxEvent anomalyDetectionEstablishEvent =
                new AxEvent(new AxArtifactKey("AnomalyDetectionEstablishEvent", "0.0.1"),
                        "org.onap.policy.apex.examples.adaptive.events");
        anomalyDetectionEstablishEvent.setSource("Establish");
        anomalyDetectionEstablishEvent.setTarget("Decide");
        anomalyDetectionEstablishEvent.getParameterMap().put("MonitoredValue",
                new AxField(new AxReferenceKey(anomalyDetectionEstablishEvent.getKey(), "MonitoredValue"),
                        monitoredValue.getKey()));
        anomalyDetectionEstablishEvent.getParameterMap().put("Iteration", new AxField(
                new AxReferenceKey(anomalyDetectionEstablishEvent.getKey(), "Iteration"), iteration.getKey()));

        final AxEvent anomalyDetectionDecideEvent =
                new AxEvent(new AxArtifactKey("AnomalyDetectionDecideEvent", "0.0.1"),
                        "org.onap.policy.apex.examples.adaptive.events");
        anomalyDetectionDecideEvent.setSource("Decide");
        anomalyDetectionDecideEvent.setTarget("Act");
        anomalyDetectionDecideEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(anomalyDetectionDecideEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        anomalyDetectionDecideEvent.getParameterMap().put("Iteration",
                new AxField(new AxReferenceKey(anomalyDetectionDecideEvent.getKey(), "Iteration"), iteration.getKey()));

        final AxEvent anomalyDetectionActEvent = new AxEvent(new AxArtifactKey("AnomalyDetectionActEvent", "0.0.1"),
                "org.onap.policy.apex.examples.adaptive.events");
        anomalyDetectionActEvent.setSource("Act");
        anomalyDetectionActEvent.setTarget("External");
        anomalyDetectionActEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(anomalyDetectionActEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        anomalyDetectionActEvent.getParameterMap().put("Iteration",
                new AxField(new AxReferenceKey(anomalyDetectionActEvent.getKey(), "Iteration"), iteration.getKey()));

        final AxEvents anomalyDetectionEvents = new AxEvents(new AxArtifactKey("AnomalyDetectionEvents", "0.0.1"));
        anomalyDetectionEvents.getEventMap().put(anomalyDetectionTriggerEvent.getKey(), anomalyDetectionTriggerEvent);
        anomalyDetectionEvents.getEventMap().put(anomalyDetectionMatchEvent.getKey(), anomalyDetectionMatchEvent);
        anomalyDetectionEvents.getEventMap().put(anomalyDetectionEstablishEvent.getKey(),
                anomalyDetectionEstablishEvent);
        anomalyDetectionEvents.getEventMap().put(anomalyDetectionDecideEvent.getKey(), anomalyDetectionDecideEvent);
        anomalyDetectionEvents.getEventMap().put(anomalyDetectionActEvent.getKey(), anomalyDetectionActEvent);

        // Data types for context
        final AxContextSchema anomalyDetection = new AxContextSchema(new AxArtifactKey("AnomalyDetection", "0.0.1"),
                "Java", "org.onap.policy.apex.examples.adaptive.concepts.AnomalyDetection");
        adContextSchemas.getSchemasMap().put(anomalyDetection.getKey(), anomalyDetection);

        // One context map
        final AxContextAlbum anomalyDetectionAlbum = new AxContextAlbum(
                new AxArtifactKey("AnomalyDetectionAlbum", "0.0.1"), "APPLICATION", true, anomalyDetection.getKey());
        final AxContextAlbums anomalyDetectionAlbums =
                new AxContextAlbums(new AxArtifactKey("AnomalyDetectionAlbums", "0.0.1"));
        anomalyDetectionAlbums.getAlbumsMap().put(anomalyDetectionAlbum.getKey(), anomalyDetectionAlbum);

        // Tasks
        final AxLogicReader logicReader =
                new PolicyLogicReader().setLogicPackage(this.getClass().getPackage().getName())
                        .setDefaultLogic("DefaultAnomalyDetectionTask_Logic");

        final AxTask anomalyDetectionMatchTask = new AxTask(new AxArtifactKey("AnomalyDetectionMatchTask", "0.0.1"));
        anomalyDetectionMatchTask.duplicateInputFields(anomalyDetectionTriggerEvent.getParameterMap());
        anomalyDetectionMatchTask.duplicateOutputFields(anomalyDetectionMatchEvent.getParameterMap());
        anomalyDetectionMatchTask
                .setTaskLogic(new AxTaskLogic(anomalyDetectionMatchTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask anomalyDetectionEstablishTask =
                new AxTask(new AxArtifactKey("AnomalyDetectionEstablishTask", "0.0.1"));
        anomalyDetectionEstablishTask.duplicateInputFields(anomalyDetectionMatchEvent.getParameterMap());
        anomalyDetectionEstablishTask.duplicateOutputFields(anomalyDetectionEstablishEvent.getParameterMap());
        anomalyDetectionEstablishTask.setTaskLogic(
                new AxTaskLogic(anomalyDetectionEstablishTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask anomalyDetectionDecideTask0 =
                new AxTask(new AxArtifactKey("AnomalyDetectionDecideTask0", "0.0.1"));
        anomalyDetectionDecideTask0.duplicateInputFields(anomalyDetectionEstablishEvent.getParameterMap());
        anomalyDetectionDecideTask0.duplicateOutputFields(anomalyDetectionDecideEvent.getParameterMap());
        anomalyDetectionDecideTask0
                .setTaskLogic(new AxTaskLogic(anomalyDetectionDecideTask0.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask anomalyDetectionDecideTask1 =
                new AxTask(new AxArtifactKey("AnomalyDetectionDecideTask1", "0.0.1"));
        anomalyDetectionDecideTask1.duplicateInputFields(anomalyDetectionEstablishEvent.getParameterMap());
        anomalyDetectionDecideTask1.duplicateOutputFields(anomalyDetectionDecideEvent.getParameterMap());
        anomalyDetectionDecideTask1
                .setTaskLogic(new AxTaskLogic(anomalyDetectionDecideTask1.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask anomalyDetectionDecideTask2 =
                new AxTask(new AxArtifactKey("AnomalyDetectionDecideTask2", "0.0.1"));
        anomalyDetectionDecideTask2.duplicateInputFields(anomalyDetectionEstablishEvent.getParameterMap());
        anomalyDetectionDecideTask2.duplicateOutputFields(anomalyDetectionDecideEvent.getParameterMap());
        anomalyDetectionDecideTask2
                .setTaskLogic(new AxTaskLogic(anomalyDetectionDecideTask2.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask anomalyDetectionActTask = new AxTask(new AxArtifactKey("AnomalyDetectionActTask", "0.0.1"));
        anomalyDetectionActTask.duplicateInputFields(anomalyDetectionDecideEvent.getParameterMap());
        anomalyDetectionActTask.duplicateOutputFields(anomalyDetectionActEvent.getParameterMap());
        anomalyDetectionActTask
                .setTaskLogic(new AxTaskLogic(anomalyDetectionActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTasks anomalyDetectionTasks = new AxTasks(new AxArtifactKey("AnomalyDetectionTasks", "0.0.1"));
        anomalyDetectionTasks.getTaskMap().put(anomalyDetectionMatchTask.getKey(), anomalyDetectionMatchTask);
        anomalyDetectionTasks.getTaskMap().put(anomalyDetectionEstablishTask.getKey(), anomalyDetectionEstablishTask);
        anomalyDetectionTasks.getTaskMap().put(anomalyDetectionDecideTask0.getKey(), anomalyDetectionDecideTask0);
        anomalyDetectionTasks.getTaskMap().put(anomalyDetectionDecideTask1.getKey(), anomalyDetectionDecideTask1);
        anomalyDetectionTasks.getTaskMap().put(anomalyDetectionDecideTask2.getKey(), anomalyDetectionDecideTask2);
        anomalyDetectionTasks.getTaskMap().put(anomalyDetectionActTask.getKey(), anomalyDetectionActTask);

        // Policies
        logicReader.setDefaultLogic("DefaultState_Logic");

        final AxPolicy anomalyDetectionPolicy = new AxPolicy(new AxArtifactKey("AnomalyDetectionPolicy", "0.0.1"));
        anomalyDetectionPolicy.setTemplate("MEDA");

        final AxState anomalyDetectionActState =
                new AxState(new AxReferenceKey(anomalyDetectionPolicy.getKey(), "Act"));
        anomalyDetectionActState.setTrigger(anomalyDetectionDecideEvent.getKey());
        final AxStateOutput adAct2Out = new AxStateOutput(anomalyDetectionActState.getKey(),
                AxReferenceKey.getNullKey(), anomalyDetectionActEvent.getKey());
        anomalyDetectionActState.getStateOutputs().put(adAct2Out.getKey().getLocalName(), adAct2Out);
        anomalyDetectionActState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(anomalyDetectionActState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        anomalyDetectionActState.setDefaultTask(anomalyDetectionActTask.getKey());
        anomalyDetectionActState.getTaskReferences().put(anomalyDetectionActTask.getKey(),
                new AxStateTaskReference(anomalyDetectionActState.getKey(), anomalyDetectionActTask.getKey(),
                        AxStateTaskOutputType.DIRECT, adAct2Out.getKey()));

        logicReader.setDefaultLogic(null);

        final AxState anomalyDetectionDecideState =
                new AxState(new AxReferenceKey(anomalyDetectionPolicy.getKey(), "Decide"));
        anomalyDetectionDecideState.setTrigger(anomalyDetectionEstablishEvent.getKey());
        final AxStateOutput adDec2Act = new AxStateOutput(anomalyDetectionDecideState.getKey(),
                anomalyDetectionActState.getKey(), anomalyDetectionDecideEvent.getKey());
        anomalyDetectionDecideState.getStateOutputs().put(adDec2Act.getKey().getLocalName(), adDec2Act);
        anomalyDetectionDecideState.setTaskSelectionLogic(new AxTaskSelectionLogic(anomalyDetectionDecideState.getKey(),
                "TaskSelectionLogic", "JAVA", logicReader));
        anomalyDetectionDecideState.setDefaultTask(anomalyDetectionDecideTask0.getKey());
        anomalyDetectionDecideState.getContextAlbumReferences().add(anomalyDetectionAlbum.getKey());
        anomalyDetectionDecideState.getTaskReferences().put(anomalyDetectionDecideTask0.getKey(),
                new AxStateTaskReference(anomalyDetectionDecideState.getKey(), anomalyDetectionDecideTask0.getKey(),
                        AxStateTaskOutputType.DIRECT, adDec2Act.getKey()));
        anomalyDetectionDecideState.getTaskReferences().put(anomalyDetectionDecideTask1.getKey(),
                new AxStateTaskReference(anomalyDetectionDecideState.getKey(), anomalyDetectionDecideTask1.getKey(),
                        AxStateTaskOutputType.DIRECT, adDec2Act.getKey()));
        anomalyDetectionDecideState.getTaskReferences().put(anomalyDetectionDecideTask2.getKey(),
                new AxStateTaskReference(anomalyDetectionDecideState.getKey(), anomalyDetectionDecideTask2.getKey(),
                        AxStateTaskOutputType.DIRECT, adDec2Act.getKey()));

        logicReader.setDefaultLogic("DefaultState_Logic");

        final AxState anomalyDetectionEstablishState =
                new AxState(new AxReferenceKey(anomalyDetectionPolicy.getKey(), "Establish"));
        anomalyDetectionEstablishState.setTrigger(anomalyDetectionMatchEvent.getKey());
        final AxStateOutput adEst2Dec = new AxStateOutput(anomalyDetectionEstablishState.getKey(),
                anomalyDetectionDecideState.getKey(), anomalyDetectionEstablishEvent.getKey());
        anomalyDetectionEstablishState.getStateOutputs().put(adEst2Dec.getKey().getLocalName(), adEst2Dec);
        anomalyDetectionEstablishState.setTaskSelectionLogic(new AxTaskSelectionLogic(
                anomalyDetectionEstablishState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        anomalyDetectionEstablishState.setDefaultTask(anomalyDetectionEstablishTask.getKey());
        anomalyDetectionEstablishState.getTaskReferences().put(anomalyDetectionEstablishTask.getKey(),
                new AxStateTaskReference(anomalyDetectionEstablishState.getKey(),
                        anomalyDetectionEstablishTask.getKey(), AxStateTaskOutputType.DIRECT, adEst2Dec.getKey()));

        final AxState anomalyDetectionMatchState =
                new AxState(new AxReferenceKey(anomalyDetectionPolicy.getKey(), "Match"));
        anomalyDetectionMatchState.setTrigger(anomalyDetectionTriggerEvent.getKey());
        final AxStateOutput adMat2Est = new AxStateOutput(anomalyDetectionMatchState.getKey(),
                anomalyDetectionEstablishState.getKey(), anomalyDetectionMatchEvent.getKey());
        anomalyDetectionMatchState.getStateOutputs().put(adMat2Est.getKey().getLocalName(), adMat2Est);
        anomalyDetectionMatchState.setTaskSelectionLogic(new AxTaskSelectionLogic(anomalyDetectionMatchState.getKey(),
                "TaskSelectionLogic", "MVEL", logicReader));
        anomalyDetectionMatchState.setDefaultTask(anomalyDetectionMatchTask.getKey());
        anomalyDetectionMatchState.getTaskReferences().put(anomalyDetectionMatchTask.getKey(),
                new AxStateTaskReference(anomalyDetectionMatchState.getKey(), anomalyDetectionMatchTask.getKey(),
                        AxStateTaskOutputType.DIRECT, adMat2Est.getKey()));

        anomalyDetectionPolicy.setFirstState(anomalyDetectionMatchState.getKey().getLocalName());
        anomalyDetectionPolicy.getStateMap().put(anomalyDetectionMatchState.getKey().getLocalName(),
                anomalyDetectionMatchState);
        anomalyDetectionPolicy.getStateMap().put(anomalyDetectionEstablishState.getKey().getLocalName(),
                anomalyDetectionEstablishState);
        anomalyDetectionPolicy.getStateMap().put(anomalyDetectionDecideState.getKey().getLocalName(),
                anomalyDetectionDecideState);
        anomalyDetectionPolicy.getStateMap().put(anomalyDetectionActState.getKey().getLocalName(),
                anomalyDetectionActState);

        final AxPolicies anomalyDetectionPolicies =
                new AxPolicies(new AxArtifactKey("AnomalyDetectionPolicies", "0.0.1"));
        anomalyDetectionPolicies.getPolicyMap().put(anomalyDetectionPolicy.getKey(), anomalyDetectionPolicy);

        final AxKeyInformation keyInformation =
                new AxKeyInformation(new AxArtifactKey("AnomalyDetectionKeyInformation", "0.0.1"));
        final AxPolicyModel anomalyDetectionPolicyModel =
                new AxPolicyModel(new AxArtifactKey("AnomalyDetectionPolicyModel", "0.0.1"));
        anomalyDetectionPolicyModel.setPolicies(anomalyDetectionPolicies);
        anomalyDetectionPolicyModel.setEvents(anomalyDetectionEvents);
        anomalyDetectionPolicyModel.setTasks(anomalyDetectionTasks);
        anomalyDetectionPolicyModel.setAlbums(anomalyDetectionAlbums);
        anomalyDetectionPolicyModel.setSchemas(adContextSchemas);
        anomalyDetectionPolicyModel.setKeyInformation(keyInformation);
        anomalyDetectionPolicyModel.getKeyInformation().generateKeyInfo(anomalyDetectionPolicyModel);

        final AxValidationResult result = anomalyDetectionPolicyModel.validate(new AxValidationResult());
        if (!result.getValidationResult().equals(AxValidationResult.ValidationResult.VALID)) {
            throw new ApexRuntimeException("model " + anomalyDetectionPolicyModel.getID() + " is not valid" + result);
        }
        return anomalyDetectionPolicyModel;
    }

    /**
     * Gets the auto learn policy model.
     *
     * @return the auto learn policy model
     */
    // CHECKSTYLE:OFF: checkstyle:maximumMethodLength
    public AxPolicyModel getAutoLearnPolicyModel() {
        // CHECKSTYLE:ON: checkstyle:maximumMethodLength
        // Data types for event parameters
        final AxContextSchema monitoredValue =
                new AxContextSchema(new AxArtifactKey("MonitoredValue", "0.0.1"), "Java", "java.lang.Double");

        final AxContextSchemas alContextSchemas = new AxContextSchemas(new AxArtifactKey("ALDatatypes", "0.0.1"));
        alContextSchemas.getSchemasMap().put(monitoredValue.getKey(), monitoredValue);

        final AxEvent autoLearnTriggerEvent = new AxEvent(new AxArtifactKey("AutoLearnTriggerEvent", "0.0.1"),
                "org.onap.policy.apex.examples.adaptive.events");
        autoLearnTriggerEvent.setSource("External");
        autoLearnTriggerEvent.setTarget("Match");
        autoLearnTriggerEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(autoLearnTriggerEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        autoLearnTriggerEvent.getParameterMap().put("LastMonitoredValue", new AxField(
                new AxReferenceKey(autoLearnTriggerEvent.getKey(), "LastMonitoredValue"), monitoredValue.getKey()));

        final AxEvent autoLearnMatchEvent = new AxEvent(new AxArtifactKey("AutoLearnMatchEvent", "0.0.1"),
                "org.onap.policy.apex.examples.adaptive.events");
        autoLearnMatchEvent.setSource("Match");
        autoLearnMatchEvent.setTarget("Establish");
        autoLearnMatchEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(autoLearnMatchEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        autoLearnMatchEvent.getParameterMap().put("LastMonitoredValue", new AxField(
                new AxReferenceKey(autoLearnMatchEvent.getKey(), "LastMonitoredValue"), monitoredValue.getKey()));

        final AxEvent autoLearnEstablishEvent = new AxEvent(new AxArtifactKey("AutoLearnEstablishEvent", "0.0.1"),
                "org.onap.policy.apex.examples.adaptive.events");
        autoLearnEstablishEvent.setSource("Establish");
        autoLearnEstablishEvent.setTarget("Decide");
        autoLearnEstablishEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(autoLearnEstablishEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        autoLearnEstablishEvent.getParameterMap().put("LastMonitoredValue", new AxField(
                new AxReferenceKey(autoLearnEstablishEvent.getKey(), "LastMonitoredValue"), monitoredValue.getKey()));

        final AxEvent autoLearnDecideEvent = new AxEvent(new AxArtifactKey("AutoLearnDecideEvent", "0.0.1"),
                "org.onap.policy.apex.examples.adaptive.events");
        autoLearnDecideEvent.setSource("Decide");
        autoLearnDecideEvent.setTarget("Act");
        autoLearnDecideEvent.getParameterMap().put("MonitoredValue", new AxField(
                new AxReferenceKey(autoLearnDecideEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        autoLearnDecideEvent.getParameterMap().put("LastMonitoredValue", new AxField(
                new AxReferenceKey(autoLearnDecideEvent.getKey(), "LastMonitoredValue"), monitoredValue.getKey()));

        final AxEvent autoLearnActEvent = new AxEvent(new AxArtifactKey("AutoLearnActEvent", "0.0.1"),
                "org.onap.policy.apex.examples.adaptive.events");
        autoLearnActEvent.setSource("Act");
        autoLearnActEvent.setTarget("External");
        autoLearnActEvent.getParameterMap().put("MonitoredValue",
                new AxField(new AxReferenceKey(autoLearnActEvent.getKey(), "MonitoredValue"), monitoredValue.getKey()));
        autoLearnActEvent.getParameterMap().put("LastMonitoredValue", new AxField(
                new AxReferenceKey(autoLearnActEvent.getKey(), "LastMonitoredValue"), monitoredValue.getKey()));

        final AxEvents autoLearnEvents = new AxEvents(new AxArtifactKey("AutoLearnEvents", "0.0.1"));
        autoLearnEvents.getEventMap().put(autoLearnTriggerEvent.getKey(), autoLearnTriggerEvent);
        autoLearnEvents.getEventMap().put(autoLearnMatchEvent.getKey(), autoLearnMatchEvent);
        autoLearnEvents.getEventMap().put(autoLearnEstablishEvent.getKey(), autoLearnEstablishEvent);
        autoLearnEvents.getEventMap().put(autoLearnDecideEvent.getKey(), autoLearnDecideEvent);
        autoLearnEvents.getEventMap().put(autoLearnActEvent.getKey(), autoLearnActEvent);

        // Data types for context
        final AxContextSchema autoLearn = new AxContextSchema(new AxArtifactKey("AutoLearn", "0.0.1"), "Java",
                "org.onap.policy.apex.examples.adaptive.concepts.AutoLearn");
        alContextSchemas.getSchemasMap().put(autoLearn.getKey(), autoLearn);

        // One context map
        final AxContextAlbum autoLearnAlbum = new AxContextAlbum(new AxArtifactKey("AutoLearnAlbum", "0.0.1"),
                "APPLICATION", true, autoLearn.getKey());

        final AxContextAlbums autoLearnAlbums = new AxContextAlbums(new AxArtifactKey("AutoLearnContext", "0.0.1"));
        autoLearnAlbums.getAlbumsMap().put(autoLearnAlbum.getKey(), autoLearnAlbum);

        // Tasks
        final AxLogicReader logicReader = new PolicyLogicReader()
                .setLogicPackage(this.getClass().getPackage().getName()).setDefaultLogic("DefaultAutoLearnTask_Logic");

        final AxTask autoLearnMatchTask = new AxTask(new AxArtifactKey("AutoLearnMatchTask", "0.0.1"));
        autoLearnMatchTask.duplicateInputFields(autoLearnTriggerEvent.getParameterMap());
        autoLearnMatchTask.duplicateOutputFields(autoLearnMatchEvent.getParameterMap());
        autoLearnMatchTask.setTaskLogic(new AxTaskLogic(autoLearnMatchTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask autoLearnEstablishTask = new AxTask(new AxArtifactKey("AutoLearnEstablishTask", "0.0.1"));
        autoLearnEstablishTask.duplicateInputFields(autoLearnMatchEvent.getParameterMap());
        autoLearnEstablishTask.duplicateOutputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnEstablishTask
                .setTaskLogic(new AxTaskLogic(autoLearnEstablishTask.getKey(), "TaskLogic", "MVEL", logicReader));

        logicReader.setDefaultLogic(null);

        final AxTask autoLearnDecideTask0 = new AxTask(new AxArtifactKey("AutoLearnDecideTask0", "0.0.1"));
        autoLearnDecideTask0.duplicateInputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnDecideTask0.duplicateOutputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnDecideTask0
                .setTaskLogic(new AxTaskLogic(autoLearnDecideTask0.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask autoLearnDecideTask1 = new AxTask(new AxArtifactKey("AutoLearnDecideTask1", "0.0.1"));
        autoLearnDecideTask1.duplicateInputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnDecideTask1.duplicateOutputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnDecideTask1
                .setTaskLogic(new AxTaskLogic(autoLearnDecideTask1.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask autoLearnDecideTask2 = new AxTask(new AxArtifactKey("AutoLearnDecideTask2", "0.0.1"));
        autoLearnDecideTask2.duplicateInputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnDecideTask2.duplicateOutputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnDecideTask2
                .setTaskLogic(new AxTaskLogic(autoLearnDecideTask2.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask autoLearnDecideTask3 = new AxTask(new AxArtifactKey("AutoLearnDecideTask3", "0.0.1"));
        autoLearnDecideTask3.duplicateInputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnDecideTask3.duplicateOutputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnDecideTask3
                .setTaskLogic(new AxTaskLogic(autoLearnDecideTask3.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask autoLearnDecideTask4 = new AxTask(new AxArtifactKey("AutoLearnDecideTask4", "0.0.1"));
        autoLearnDecideTask4.duplicateInputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnDecideTask4.duplicateOutputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnDecideTask4
                .setTaskLogic(new AxTaskLogic(autoLearnDecideTask4.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask autoLearnDecideTask5 = new AxTask(new AxArtifactKey("AutoLearnDecideTask5", "0.0.1"));
        autoLearnDecideTask5.duplicateInputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnDecideTask5.duplicateOutputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnDecideTask5
                .setTaskLogic(new AxTaskLogic(autoLearnDecideTask5.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask autoLearnDecideTask6 = new AxTask(new AxArtifactKey("AutoLearnDecideTask6", "0.0.1"));
        autoLearnDecideTask6.duplicateInputFields(autoLearnEstablishEvent.getParameterMap());
        autoLearnDecideTask6.duplicateOutputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnDecideTask6
                .setTaskLogic(new AxTaskLogic(autoLearnDecideTask6.getKey(), "TaskLogic", "MVEL", logicReader));

        logicReader.setDefaultLogic("DefaultAutoLearnTask_Logic");

        final AxTask autoLearnActTask = new AxTask(new AxArtifactKey("AutoLearnActTask", "0.0.1"));
        autoLearnActTask.duplicateInputFields(autoLearnDecideEvent.getParameterMap());
        autoLearnActTask.duplicateOutputFields(autoLearnActEvent.getParameterMap());
        autoLearnActTask.setTaskLogic(new AxTaskLogic(autoLearnActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTasks autoLearnTasks = new AxTasks(new AxArtifactKey("AutoLearnTasks", "0.0.1"));
        autoLearnTasks.getTaskMap().put(autoLearnMatchTask.getKey(), autoLearnMatchTask);
        autoLearnTasks.getTaskMap().put(autoLearnEstablishTask.getKey(), autoLearnEstablishTask);
        autoLearnTasks.getTaskMap().put(autoLearnDecideTask0.getKey(), autoLearnDecideTask0);
        autoLearnTasks.getTaskMap().put(autoLearnDecideTask1.getKey(), autoLearnDecideTask1);
        autoLearnTasks.getTaskMap().put(autoLearnDecideTask2.getKey(), autoLearnDecideTask2);
        autoLearnTasks.getTaskMap().put(autoLearnDecideTask3.getKey(), autoLearnDecideTask3);
        autoLearnTasks.getTaskMap().put(autoLearnDecideTask4.getKey(), autoLearnDecideTask4);
        autoLearnTasks.getTaskMap().put(autoLearnDecideTask5.getKey(), autoLearnDecideTask5);
        autoLearnTasks.getTaskMap().put(autoLearnDecideTask6.getKey(), autoLearnDecideTask6);
        autoLearnTasks.getTaskMap().put(autoLearnActTask.getKey(), autoLearnActTask);

        // Policies
        logicReader.setDefaultLogic("DefaultState_Logic");

        final AxPolicy autoLearnPolicy = new AxPolicy(new AxArtifactKey("AutoLearnPolicy", "0.0.1"));
        autoLearnPolicy.setTemplate("MEDA");

        final AxState autoLearnActState = new AxState(new AxReferenceKey(autoLearnPolicy.getKey(), "Act"));
        autoLearnActState.setTrigger(autoLearnDecideEvent.getKey());
        final AxStateOutput alAct2Out =
                new AxStateOutput(autoLearnActState.getKey(), AxReferenceKey.getNullKey(), autoLearnActEvent.getKey());
        autoLearnActState.getStateOutputs().put(alAct2Out.getKey().getLocalName(), alAct2Out);
        autoLearnActState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(autoLearnActState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        autoLearnActState.setDefaultTask(autoLearnActTask.getKey());
        autoLearnActState.getTaskReferences().put(autoLearnActTask.getKey(),
                new AxStateTaskReference(autoLearnActState.getKey(), autoLearnActTask.getKey(),
                        AxStateTaskOutputType.DIRECT, alAct2Out.getKey()));

        logicReader.setDefaultLogic(null);

        final AxState autoLearnDecideState = new AxState(new AxReferenceKey(autoLearnPolicy.getKey(), "Decide"));
        autoLearnDecideState.setTrigger(autoLearnEstablishEvent.getKey());
        final AxStateOutput alDec2Act = new AxStateOutput(autoLearnDecideState.getKey(), autoLearnActState.getKey(),
                autoLearnDecideEvent.getKey());
        autoLearnDecideState.getStateOutputs().put(alDec2Act.getKey().getLocalName(), alDec2Act);
        autoLearnDecideState.getContextAlbumReferences().add(autoLearnAlbum.getKey());
        autoLearnDecideState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(autoLearnDecideState.getKey(), "TaskSelectionLogic", "JAVA", logicReader));
        autoLearnDecideState.setDefaultTask(autoLearnDecideTask0.getKey());
        autoLearnDecideState.getTaskReferences().put(autoLearnDecideTask0.getKey(),
                new AxStateTaskReference(autoLearnDecideState.getKey(), autoLearnDecideTask0.getKey(),
                        AxStateTaskOutputType.DIRECT, alDec2Act.getKey()));
        autoLearnDecideState.getTaskReferences().put(autoLearnDecideTask1.getKey(),
                new AxStateTaskReference(autoLearnDecideState.getKey(), autoLearnDecideTask1.getKey(),
                        AxStateTaskOutputType.DIRECT, alDec2Act.getKey()));
        autoLearnDecideState.getTaskReferences().put(autoLearnDecideTask2.getKey(),
                new AxStateTaskReference(autoLearnDecideState.getKey(), autoLearnDecideTask2.getKey(),
                        AxStateTaskOutputType.DIRECT, alDec2Act.getKey()));
        autoLearnDecideState.getTaskReferences().put(autoLearnDecideTask3.getKey(),
                new AxStateTaskReference(autoLearnDecideState.getKey(), autoLearnDecideTask3.getKey(),
                        AxStateTaskOutputType.DIRECT, alDec2Act.getKey()));
        autoLearnDecideState.getTaskReferences().put(autoLearnDecideTask4.getKey(),
                new AxStateTaskReference(autoLearnDecideState.getKey(), autoLearnDecideTask4.getKey(),
                        AxStateTaskOutputType.DIRECT, alDec2Act.getKey()));
        autoLearnDecideState.getTaskReferences().put(autoLearnDecideTask5.getKey(),
                new AxStateTaskReference(autoLearnDecideState.getKey(), autoLearnDecideTask5.getKey(),
                        AxStateTaskOutputType.DIRECT, alDec2Act.getKey()));
        autoLearnDecideState.getTaskReferences().put(autoLearnDecideTask6.getKey(),
                new AxStateTaskReference(autoLearnDecideState.getKey(), autoLearnDecideTask6.getKey(),
                        AxStateTaskOutputType.DIRECT, alDec2Act.getKey()));

        logicReader.setDefaultLogic("DefaultState_Logic");

        final AxState autoLearnEstablishState = new AxState(new AxReferenceKey(autoLearnPolicy.getKey(), "Establish"));
        autoLearnEstablishState.setTrigger(autoLearnMatchEvent.getKey());
        final AxStateOutput alEst2Dec = new AxStateOutput(autoLearnEstablishState.getKey(),
                autoLearnDecideState.getKey(), autoLearnEstablishEvent.getKey());
        autoLearnEstablishState.getStateOutputs().put(alEst2Dec.getKey().getLocalName(), alEst2Dec);
        autoLearnEstablishState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(autoLearnEstablishState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        autoLearnEstablishState.setDefaultTask(autoLearnEstablishTask.getKey());
        autoLearnEstablishState.getTaskReferences().put(autoLearnEstablishTask.getKey(),
                new AxStateTaskReference(autoLearnEstablishState.getKey(), autoLearnEstablishTask.getKey(),
                        AxStateTaskOutputType.DIRECT, alEst2Dec.getKey()));

        final AxState autoLearnMatchState = new AxState(new AxReferenceKey(autoLearnPolicy.getKey(), "Match"));
        autoLearnMatchState.setTrigger(autoLearnTriggerEvent.getKey());
        final AxStateOutput alMat2Est = new AxStateOutput(autoLearnMatchState.getKey(),
                autoLearnEstablishState.getKey(), autoLearnMatchEvent.getKey());
        autoLearnMatchState.getStateOutputs().put(alMat2Est.getKey().getLocalName(), alMat2Est);
        autoLearnMatchState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(autoLearnMatchState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        autoLearnMatchState.setDefaultTask(autoLearnMatchTask.getKey());
        autoLearnMatchState.getTaskReferences().put(autoLearnMatchTask.getKey(),
                new AxStateTaskReference(autoLearnMatchState.getKey(), autoLearnMatchTask.getKey(),
                        AxStateTaskOutputType.DIRECT, alMat2Est.getKey()));

        autoLearnPolicy.setFirstState(autoLearnMatchState.getKey().getLocalName());
        autoLearnPolicy.getStateMap().put(autoLearnMatchState.getKey().getLocalName(), autoLearnMatchState);
        autoLearnPolicy.getStateMap().put(autoLearnEstablishState.getKey().getLocalName(), autoLearnEstablishState);
        autoLearnPolicy.getStateMap().put(autoLearnDecideState.getKey().getLocalName(), autoLearnDecideState);
        autoLearnPolicy.getStateMap().put(autoLearnActState.getKey().getLocalName(), autoLearnActState);

        final AxPolicies autoLearnPolicies = new AxPolicies(new AxArtifactKey("AutoLearnPolicies", "0.0.1"));
        autoLearnPolicies.getPolicyMap().put(autoLearnPolicy.getKey(), autoLearnPolicy);

        final AxKeyInformation keyInformation =
                new AxKeyInformation(new AxArtifactKey("AutoLearnKeyInformation", "0.0.1"));
        final AxPolicyModel autoLearnPolicyModel =
                new AxPolicyModel(new AxArtifactKey("AutoLearnPolicyModel", "0.0.1"));
        autoLearnPolicyModel.setPolicies(autoLearnPolicies);
        autoLearnPolicyModel.setEvents(autoLearnEvents);
        autoLearnPolicyModel.setTasks(autoLearnTasks);
        autoLearnPolicyModel.setAlbums(autoLearnAlbums);
        autoLearnPolicyModel.setSchemas(alContextSchemas);
        autoLearnPolicyModel.setKeyInformation(keyInformation);
        autoLearnPolicyModel.getKeyInformation().generateKeyInfo(autoLearnPolicyModel);

        final AxValidationResult result = autoLearnPolicyModel.validate(new AxValidationResult());
        if (!result.getValidationResult().equals(AxValidationResult.ValidationResult.VALID)) {
            throw new ApexRuntimeException("model " + autoLearnPolicyModel.getID() + " is not valid" + result);
        }
        return autoLearnPolicyModel;
    }

}
