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

package org.onap.policy.apex.test.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
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
 * This class creates sample evaluation Policy Models.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
public class EvalDomainModelFactory {
    private static final int THIRD_MEMBER = 3;

    private static final String PACKAGE = EvalDomainModelFactory.class.getPackage().getName();

    private String justOneLang = null;

    private final AxContextSchema typeTimestamp;
    private final AxContextSchema typeDouble;
    private final AxContextSchema typeCounter;
    private final AxContextSchemas schemas;
    private final AxEvent event0000;
    private final AxEvent event0001;
    private final AxEvent event0002;
    private final AxEvent event0003;
    private final AxEvent event0004;
    private final AxEvents events;

    /**
     * The Constructor for the factory.
     */
    public EvalDomainModelFactory() {
        this(null);
    }

    /**
     * The Constructor for the factory that creates models for a single executor language flavour.
     *
     * @param justOneLang the just one lang
     */
    public EvalDomainModelFactory(final String justOneLang) {
        this.justOneLang = justOneLang;

        typeTimestamp = new AxContextSchema(new AxArtifactKey("TestTimestamp", "0.0.1"), "Java", "java.lang.Long");
        typeDouble = new AxContextSchema(new AxArtifactKey("TestTemperature", "0.0.1"), "Java", "java.lang.Double");
        typeCounter = new AxContextSchema(new AxArtifactKey("TestTimestamp", "0.0.1"), "Java", "java.lang.Long");

        schemas = new AxContextSchemas(new AxArtifactKey("TestDatatypes", "0.0.1"));
        schemas.getSchemasMap().put(typeTimestamp.getKey(), typeTimestamp);
        schemas.getSchemasMap().put(typeDouble.getKey(), typeDouble);
        schemas.getSchemasMap().put(typeCounter.getKey(), typeCounter);

        event0000 = new AxEvent(new AxArtifactKey("Event0000", "0.0.1"), PACKAGE + ".events");
        event0000.getParameterMap().put("SentTimestamp", new AxField(new AxReferenceKey(event0000.getKey(), "SentTimestamp"), typeTimestamp.getKey()));
        event0000.getParameterMap().put("TestTemperature", new AxField(new AxReferenceKey(event0000.getKey(), "TestTemperature"), typeDouble.getKey()));
        event0000.getParameterMap().put("FirstEventTimestamp",
                new AxField(new AxReferenceKey(event0000.getKey(), "FirstEventTimestamp"), typeTimestamp.getKey()));
        event0000.getParameterMap().put("EventNumber", new AxField(new AxReferenceKey(event0000.getKey(), "EventNumber"), typeCounter.getKey()));

        event0001 = new AxEvent(new AxArtifactKey("Event0001", "0.0.1"), PACKAGE + ".events");
        event0001.getParameterMap().put("SentTimestamp", new AxField(new AxReferenceKey(event0001.getKey(), "SentTimestamp"), typeTimestamp.getKey()));
        event0001.getParameterMap().put("State1Timestamp", new AxField(new AxReferenceKey(event0001.getKey(), "State1Timestamp"), typeTimestamp.getKey()));
        event0001.getParameterMap().put("TestTemperature", new AxField(new AxReferenceKey(event0001.getKey(), "TestTemperature"), typeDouble.getKey()));
        event0001.getParameterMap().put("FirstEventTimestamp",
                new AxField(new AxReferenceKey(event0001.getKey(), "FirstEventTimestamp"), typeTimestamp.getKey()));
        event0001.getParameterMap().put("EventNumber", new AxField(new AxReferenceKey(event0001.getKey(), "EventNumber"), typeCounter.getKey()));

        event0002 = new AxEvent(new AxArtifactKey("Event0002", "0.0.1"), PACKAGE + ".events");
        event0002.getParameterMap().put("SentTimestamp", new AxField(new AxReferenceKey(event0002.getKey(), "SentTimestamp"), typeTimestamp.getKey()));
        event0002.getParameterMap().put("State1Timestamp", new AxField(new AxReferenceKey(event0002.getKey(), "State1Timestamp"), typeTimestamp.getKey()));
        event0002.getParameterMap().put("State2Timestamp", new AxField(new AxReferenceKey(event0002.getKey(), "State2Timestamp"), typeTimestamp.getKey()));
        event0002.getParameterMap().put("TestTemperature", new AxField(new AxReferenceKey(event0002.getKey(), "TestTemperature"), typeDouble.getKey()));
        event0002.getParameterMap().put("FirstEventTimestamp",
                new AxField(new AxReferenceKey(event0002.getKey(), "FirstEventTimestamp"), typeTimestamp.getKey()));
        event0002.getParameterMap().put("EventNumber", new AxField(new AxReferenceKey(event0002.getKey(), "EventNumber"), typeCounter.getKey()));

        event0003 = new AxEvent(new AxArtifactKey("Event0003", "0.0.1"), PACKAGE + ".events");
        event0003.getParameterMap().put("SentTimestamp", new AxField(new AxReferenceKey(event0003.getKey(), "SentTimestamp"), typeTimestamp.getKey()));
        event0003.getParameterMap().put("State1Timestamp", new AxField(new AxReferenceKey(event0003.getKey(), "State1Timestamp"), typeTimestamp.getKey()));
        event0003.getParameterMap().put("State2Timestamp", new AxField(new AxReferenceKey(event0003.getKey(), "State2Timestamp"), typeTimestamp.getKey()));
        event0003.getParameterMap().put("State3Timestamp", new AxField(new AxReferenceKey(event0003.getKey(), "State3Timestamp"), typeTimestamp.getKey()));
        event0003.getParameterMap().put("TestTemperature", new AxField(new AxReferenceKey(event0003.getKey(), "TestTemperature"), typeDouble.getKey()));
        event0003.getParameterMap().put("FirstEventTimestamp",
                new AxField(new AxReferenceKey(event0003.getKey(), "FirstEventTimestamp"), typeTimestamp.getKey()));
        event0003.getParameterMap().put("EventNumber", new AxField(new AxReferenceKey(event0003.getKey(), "EventNumber"), typeCounter.getKey()));

        event0004 = new AxEvent(new AxArtifactKey("Event0004", "0.0.1"), PACKAGE + ".events");
        event0004.getParameterMap().put("SentTimestamp", new AxField(new AxReferenceKey(event0004.getKey(), "SentTimestamp"), typeTimestamp.getKey()));
        event0004.getParameterMap().put("State1Timestamp", new AxField(new AxReferenceKey(event0004.getKey(), "State1Timestamp"), typeTimestamp.getKey()));
        event0004.getParameterMap().put("State2Timestamp", new AxField(new AxReferenceKey(event0004.getKey(), "State2Timestamp"), typeTimestamp.getKey()));
        event0004.getParameterMap().put("State3Timestamp", new AxField(new AxReferenceKey(event0004.getKey(), "State3Timestamp"), typeTimestamp.getKey()));
        event0004.getParameterMap().put("State4Timestamp", new AxField(new AxReferenceKey(event0004.getKey(), "State4Timestamp"), typeTimestamp.getKey()));
        event0004.getParameterMap().put("TestTemperature", new AxField(new AxReferenceKey(event0004.getKey(), "TestTemperature"), typeDouble.getKey()));
        event0004.getParameterMap().put("FirstEventTimestamp",
                new AxField(new AxReferenceKey(event0004.getKey(), "FirstEventTimestamp"), typeTimestamp.getKey()));
        event0004.getParameterMap().put("EventNumber", new AxField(new AxReferenceKey(event0004.getKey(), "EventNumber"), typeCounter.getKey()));

        events = new AxEvents(new AxArtifactKey("Events", "0.0.1"));
        events.getEventMap().put(event0000.getKey(), event0000);
        events.getEventMap().put(event0001.getKey(), event0001);
        events.getEventMap().put(event0002.getKey(), event0002);
        events.getEventMap().put(event0003.getKey(), event0003);
        events.getEventMap().put(event0004.getKey(), event0004);
    }

    /**
     * Get a sample OODA policy model.
     *
     * @return the sample policy model
     */
    public AxPolicyModel getOODAPolicyModel() {
        final AxTasks tasks = new AxTasks(new AxArtifactKey("Tasks", "0.0.1"));

        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE).setDefaultLogic("EvalTask_Logic");

        final AxTask obTask = new AxTask(new AxArtifactKey("Task_Observe_0", "0.0.1"));
        obTask.duplicateInputFields(event0000.getParameterMap());
        obTask.duplicateOutputFields(event0001.getParameterMap());
        final AxTaskLogic obAxLogic = new AxTaskLogic(obTask.getKey(), "TaskLogic", (justOneLang == null ? "JRUBY" : justOneLang), logicReader);
        obAxLogic.setLogic(obAxLogic.getLogic().replaceAll("<STATE_NAME>", "Observe").replaceAll("<TASK_NAME>", obTask.getKey().getName())
                .replaceAll("<STATE_NUMBER>", "1"));
        obTask.setTaskLogic(obAxLogic);

        final AxTask orTask = new AxTask(new AxArtifactKey("Task_Orient_0", "0.0.1"));
        orTask.duplicateInputFields(event0001.getParameterMap());
        orTask.duplicateOutputFields(event0002.getParameterMap());
        final AxTaskLogic orAxLogic = new AxTaskLogic(orTask.getKey(), "TaskLogic", (justOneLang == null ? "JAVASCRIPT" : justOneLang), logicReader);
        orAxLogic.setLogic(orAxLogic.getLogic().replaceAll("<STATE_NAME>", "Orient").replaceAll("<TASK_NAME>", orTask.getKey().getName())
                .replaceAll("<STATE_NUMBER>", "2"));
        orTask.setTaskLogic(orAxLogic);

        final AxTask dTask = new AxTask(new AxArtifactKey("Task_Decide_0", "0.0.1"));
        dTask.duplicateInputFields(event0002.getParameterMap());
        dTask.duplicateOutputFields(event0003.getParameterMap());
        final AxTaskLogic dAxLogic = new AxTaskLogic(dTask.getKey(), "TaskLogic", (justOneLang == null ? "MVEL" : justOneLang), logicReader);
        dAxLogic.setLogic(
                dAxLogic.getLogic().replaceAll("<STATE_NAME>", "Orient").replaceAll("<TASK_NAME>", dTask.getKey().getName()).replaceAll("<STATE_NUMBER>", "3"));
        dTask.setTaskLogic(dAxLogic);

        final AxTask aTask = new AxTask(new AxArtifactKey("Task_Act_0", "0.0.1"));
        aTask.duplicateInputFields(event0003.getParameterMap());
        aTask.duplicateOutputFields(event0004.getParameterMap());
        final AxTaskLogic aAxLogic = new AxTaskLogic(aTask.getKey(), "TaskLogic", (justOneLang == null ? "JAVA" : justOneLang), logicReader);
        aAxLogic.setLogic(
                aAxLogic.getLogic().replaceAll("<STATE_NAME>", "Act").replaceAll("<TASK_NAME>", aTask.getKey().getName()).replaceAll("<STATE_NUMBER>", "4"));
        aTask.setTaskLogic(aAxLogic);

        tasks.getTaskMap().put(obTask.getKey(), obTask);
        tasks.getTaskMap().put(orTask.getKey(), orTask);
        tasks.getTaskMap().put(dTask.getKey(), dTask);
        tasks.getTaskMap().put(aTask.getKey(), aTask);

        final Set<AxArtifactKey> obTasks = new TreeSet<>();
        final Set<AxArtifactKey> orTasks = new TreeSet<>();
        final Set<AxArtifactKey> decTasks = new TreeSet<>();
        final Set<AxArtifactKey> actTasks = new TreeSet<>();

        for (final AxTask task : tasks.getTaskMap().values()) {
            if (task.getKey().getName().contains("Observe")) {
                obTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Orient")) {
                orTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Decide")) {
                decTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Act")) {
                actTasks.add(task.getKey());
            }
        }
        final List<Set<AxArtifactKey>> taskReferenceList = new ArrayList<>();
        taskReferenceList.add(obTasks);
        taskReferenceList.add(orTasks);
        taskReferenceList.add(decTasks);
        taskReferenceList.add(actTasks);

        final List<AxArtifactKey> p0InEventList = new ArrayList<>();
        p0InEventList.add(event0000.getKey());
        p0InEventList.add(event0001.getKey());
        p0InEventList.add(event0002.getKey());
        p0InEventList.add(event0003.getKey());

        final List<AxArtifactKey> p0OutEventList = new ArrayList<>();
        p0OutEventList.add(event0001.getKey());
        p0OutEventList.add(event0002.getKey());
        p0OutEventList.add(event0003.getKey());
        p0OutEventList.add(event0004.getKey());

        final List<AxArtifactKey> p0defaultTaskList = new ArrayList<>();
        p0defaultTaskList.add(tasks.get("Task_Observe_0").getKey());
        p0defaultTaskList.add(tasks.get("Task_Orient_0").getKey());
        p0defaultTaskList.add(tasks.get("Task_Decide_0").getKey());
        p0defaultTaskList.add(tasks.get("Task_Act_0").getKey());

        final AxPolicy policy0 = new AxPolicy(new AxArtifactKey("OODAPolicy_0", "0.0.1"));
        final List<String> axLogicExecutorTypeList = Arrays.asList((justOneLang == null ? "JAVASCRIPT" : justOneLang),
                (justOneLang == null ? "MVEL" : justOneLang), (justOneLang == null ? "JYTHON" : justOneLang), (justOneLang == null ? "JRUBY" : justOneLang));
        policy0.setStateMap(getOODAStateMap(policy0.getKey(), p0InEventList, p0OutEventList, axLogicExecutorTypeList, p0defaultTaskList, taskReferenceList));
        policy0.setFirstState(policy0.getStateMap().get("Observe").getKey().getLocalName());

        final AxPolicies policies = new AxPolicies(new AxArtifactKey("OODAPolicies", "0.0.1"));
        policies.getPolicyMap().put(policy0.getKey(), policy0);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInformation", "0.0.1"));
        final AxPolicyModel policyModel = new AxPolicyModel(new AxArtifactKey("EvaluationPolicyModel_OODA", "0.0.1"));
        policyModel.setPolicies(policies);
        policyModel.setEvents(events);
        policyModel.setTasks(tasks);
        policyModel.setAlbums(new AxContextAlbums(new AxArtifactKey("Albums", "0.0.1")));
        policyModel.setSchemas(schemas);
        policyModel.setKeyInformation(keyInformation);
        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        final AxValidationResult result = policyModel.validate(new AxValidationResult());
        if (!result.isOK()) {
            throw new ApexRuntimeException("model " + policyModel.getID() + " is not valid" + result);
        }
        return policyModel;
    }

    /**
     * Gets the OODA state map.
     *
     * @param policyKey the policy key
     * @param inEventKeyList the in event key list
     * @param outEventKeyList the out event key list
     * @param axLogicExecutorTypeList the ax logic executor type list
     * @param defaultTaskList the default task list
     * @param taskKeySetList the task key set list
     * @return the OODA state map
     */
    private Map<String, AxState> getOODAStateMap(final AxArtifactKey policyKey, final List<AxArtifactKey> inEventKeyList,
            final List<AxArtifactKey> outEventKeyList, final List<String> axLogicExecutorTypeList, final List<AxArtifactKey> defaultTaskList,
            final List<Set<AxArtifactKey>> taskKeySetList) {
        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE).setDefaultLogic("EvalState_Logic");

        final AxState actState = new AxState(new AxReferenceKey(policyKey, "Act"));
        actState.setTrigger(inEventKeyList.get(THIRD_MEMBER));
        final AxStateOutput act2Out = new AxStateOutput(new AxReferenceKey(actState.getKey(), "Act2Out"), outEventKeyList.get(THIRD_MEMBER),
                AxReferenceKey.getNullKey());
        actState.getStateOutputs().put(act2Out.getKey().getLocalName(), act2Out);
        actState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(actState.getKey(), "TaskSelectionLogic", axLogicExecutorTypeList.get(THIRD_MEMBER), logicReader));
        actState.setDefaultTask(defaultTaskList.get(THIRD_MEMBER));
        for (final AxArtifactKey taskKey : taskKeySetList.get(THIRD_MEMBER)) {
            actState.getTaskReferences().put(taskKey,
                    new AxStateTaskReference(new AxReferenceKey(actState.getKey(), taskKey.getName()), AxStateTaskOutputType.DIRECT, act2Out.getKey()));
        }

        final AxState decState = new AxState(new AxReferenceKey(policyKey, "Decide"));
        decState.setTrigger(inEventKeyList.get(2));
        final AxStateOutput dec2Act = new AxStateOutput(new AxReferenceKey(decState.getKey(), "Dec2Act"), outEventKeyList.get(2), actState.getKey());
        decState.getStateOutputs().put(dec2Act.getKey().getLocalName(), dec2Act);
        decState.setTaskSelectionLogic(new AxTaskSelectionLogic(decState.getKey(), "TaskSelectionLogic", axLogicExecutorTypeList.get(2), logicReader));
        decState.setDefaultTask(defaultTaskList.get(2));
        for (final AxArtifactKey taskKey : taskKeySetList.get(2)) {
            decState.getTaskReferences().put(taskKey,
                    new AxStateTaskReference(new AxReferenceKey(decState.getKey(), taskKey.getName()), AxStateTaskOutputType.DIRECT, dec2Act.getKey()));
        }

        final AxState orState = new AxState(new AxReferenceKey(policyKey, "Orient"));
        orState.setTrigger(inEventKeyList.get(1));
        final AxStateOutput or2Dec = new AxStateOutput(new AxReferenceKey(orState.getKey(), "Or2Dec"), outEventKeyList.get(1), decState.getKey());
        orState.getStateOutputs().put(or2Dec.getKey().getLocalName(), or2Dec);
        orState.setTaskSelectionLogic(new AxTaskSelectionLogic(orState.getKey(), "TaskSelectionLogic", axLogicExecutorTypeList.get(1), logicReader));
        orState.setDefaultTask(defaultTaskList.get(1));
        for (final AxArtifactKey taskKey : taskKeySetList.get(1)) {
            orState.getTaskReferences().put(taskKey,
                    new AxStateTaskReference(new AxReferenceKey(orState.getKey(), taskKey.getName()), AxStateTaskOutputType.DIRECT, or2Dec.getKey()));
        }

        final AxState obState = new AxState(new AxReferenceKey(policyKey, "Observe"));
        obState.setTrigger(inEventKeyList.get(0));
        final AxStateOutput ob2Or = new AxStateOutput(new AxReferenceKey(obState.getKey(), "Ob2Or"), outEventKeyList.get(0), orState.getKey());
        obState.getStateOutputs().put(ob2Or.getKey().getLocalName(), ob2Or);
        obState.setTaskSelectionLogic(new AxTaskSelectionLogic(obState.getKey(), "TaskSelectionLogic", axLogicExecutorTypeList.get(0), logicReader));
        obState.setDefaultTask(defaultTaskList.get(0));
        for (final AxArtifactKey taskKey : taskKeySetList.get(0)) {
            obState.getTaskReferences().put(taskKey,
                    new AxStateTaskReference(new AxReferenceKey(obState.getKey(), taskKey.getName()), AxStateTaskOutputType.DIRECT, ob2Or.getKey()));
        }

        final Map<String, AxState> stateMap = new TreeMap<>();
        stateMap.put(obState.getKey().getLocalName(), obState);
        stateMap.put(orState.getKey().getLocalName(), orState);
        stateMap.put(decState.getKey().getLocalName(), decState);
        stateMap.put(actState.getKey().getLocalName(), actState);

        return stateMap;
    }

    /**
     * Get a sample ECA policy model.
     *
     * @return the sample policy model
     */
    public AxPolicyModel getECAPolicyModel() {

        final AxTasks tasks = new AxTasks(new AxArtifactKey("Tasks", "0.0.1"));

        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE).setDefaultLogic("EvalTask_Logic");

        final AxTask eTask = new AxTask(new AxArtifactKey("Task_Event_0", "0.0.1"));
        eTask.duplicateInputFields(event0000.getParameterMap());
        eTask.duplicateOutputFields(event0001.getParameterMap());
        final AxTaskLogic eAxLogic = new AxTaskLogic(eTask.getKey(), "TaskLogic", (justOneLang == null ? "JYTHON" : justOneLang), logicReader);
        eAxLogic.setLogic(
                eAxLogic.getLogic().replaceAll("<STATE_NAME>", "Event").replaceAll("<TASK_NAME>", eTask.getKey().getName()).replaceAll("<STATE_NUMBER>", "1"));
        eTask.setTaskLogic(eAxLogic);

        final AxTask cTask = new AxTask(new AxArtifactKey("Task_Condition_0", "0.0.1"));
        cTask.duplicateInputFields(event0001.getParameterMap());
        cTask.duplicateOutputFields(event0002.getParameterMap());
        final AxTaskLogic cAxLogic = new AxTaskLogic(cTask.getKey(), "TaskLogic", (justOneLang == null ? "JAVASCRIPT" : justOneLang), logicReader);
        cAxLogic.setLogic(cAxLogic.getLogic().replaceAll("<STATE_NAME>", "Condition").replaceAll("<TASK_NAME>", cTask.getKey().getName())
                .replaceAll("<STATE_NUMBER>", "2"));
        cTask.setTaskLogic(cAxLogic);

        final AxTask aTask = new AxTask(new AxArtifactKey("Task_Action_0", "0.0.1"));
        aTask.duplicateInputFields(event0002.getParameterMap());
        aTask.duplicateOutputFields(event0003.getParameterMap());
        final AxTaskLogic aAxLogic = new AxTaskLogic(aTask.getKey(), "TaskLogic", (justOneLang == null ? "JAVA" : justOneLang), logicReader);
        aAxLogic.setLogic(
                aAxLogic.getLogic().replaceAll("<STATE_NAME>", "Action").replaceAll("<TASK_NAME>", aTask.getKey().getName()).replaceAll("<STATE_NUMBER>", "3"));
        aTask.setTaskLogic(aAxLogic);

        tasks.getTaskMap().put(eTask.getKey(), eTask);
        tasks.getTaskMap().put(cTask.getKey(), cTask);
        tasks.getTaskMap().put(aTask.getKey(), aTask);

        final Set<AxArtifactKey> eventTasks = new TreeSet<>();
        final Set<AxArtifactKey> conditionTasks = new TreeSet<>();
        final Set<AxArtifactKey> actionTasks = new TreeSet<>();

        for (final AxTask task : tasks.getTaskMap().values()) {
            if (task.getKey().getName().contains("Event")) {
                eventTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Condition")) {
                conditionTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Action")) {
                actionTasks.add(task.getKey());
            }
        }
        final List<Set<AxArtifactKey>> taskReferenceList = new ArrayList<>();
        taskReferenceList.add(eventTasks);
        taskReferenceList.add(conditionTasks);
        taskReferenceList.add(actionTasks);

        final List<AxArtifactKey> p0InEventList = new ArrayList<>();
        p0InEventList.add(event0000.getKey());
        p0InEventList.add(event0001.getKey());
        p0InEventList.add(event0002.getKey());

        final List<AxArtifactKey> p0OutEventList = new ArrayList<>();
        p0OutEventList.add(event0001.getKey());
        p0OutEventList.add(event0002.getKey());
        p0OutEventList.add(event0003.getKey());

        final List<AxArtifactKey> p0defaultTaskList = new ArrayList<>();
        p0defaultTaskList.add(tasks.get("Task_Event_0").getKey());
        p0defaultTaskList.add(tasks.get("Task_Condition_0").getKey());
        p0defaultTaskList.add(tasks.get("Task_Action_0").getKey());

        final AxPolicy policy0 = new AxPolicy(new AxArtifactKey("ECAPolicy_0", "0.0.1"));
        final List<String> axLogicExecutorTypeList = Arrays.asList((justOneLang == null ? "JAVASCRIPT" : justOneLang),
                (justOneLang == null ? "MVEL" : justOneLang), (justOneLang == null ? "JYTHON" : justOneLang));
        policy0.setStateMap(getECAStateMap(policy0.getKey(), p0InEventList, p0OutEventList, axLogicExecutorTypeList, p0defaultTaskList, taskReferenceList));
        policy0.setFirstState(policy0.getStateMap().get("Event").getKey().getLocalName());

        final AxPolicies policies = new AxPolicies(new AxArtifactKey("ECAPolicies", "0.0.1"));
        policies.getPolicyMap().put(policy0.getKey(), policy0);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInformation", "0.0.1"));
        final AxPolicyModel policyModel = new AxPolicyModel(new AxArtifactKey("EvaluationPolicyModel_ECA", "0.0.1"));
        policyModel.setPolicies(policies);
        policyModel.setEvents(events);
        policyModel.setTasks(tasks);
        policyModel.setAlbums(new AxContextAlbums(new AxArtifactKey("Albums", "0.0.1")));
        policyModel.setSchemas(schemas);
        policyModel.setKeyInformation(keyInformation);
        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        final AxValidationResult result = policyModel.validate(new AxValidationResult());
        if (!result.isOK()) {
            throw new ApexRuntimeException("model " + policyModel.getID() + " is not valid" + result);
        }
        return policyModel;
    }

    /**
     * Gets the ECA state map.
     *
     * @param policyKey the policy key
     * @param inEventKeyList the in event key list
     * @param outEventKeyList the out event key list
     * @param axLogicExecutorTypeList the ax logic executor type list
     * @param defaultTaskList the default task list
     * @param taskKeySetList the task key set list
     * @return the ECA state map
     */
    private Map<String, AxState> getECAStateMap(final AxArtifactKey policyKey, final List<AxArtifactKey> inEventKeyList,
            final List<AxArtifactKey> outEventKeyList, final List<String> axLogicExecutorTypeList, final List<AxArtifactKey> defaultTaskList,
            final List<Set<AxArtifactKey>> taskKeySetList) {
        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE).setDefaultLogic("EvalState_Logic");

        final AxState actionState = new AxState(new AxReferenceKey(policyKey, "Action"));
        actionState.setTrigger(inEventKeyList.get(2));
        final AxStateOutput action2Out = new AxStateOutput(actionState.getKey(), AxReferenceKey.getNullKey(), outEventKeyList.get(2));
        actionState.getStateOutputs().put(action2Out.getKey().getLocalName(), action2Out);
        actionState.setTaskSelectionLogic(new AxTaskSelectionLogic(actionState.getKey(), "TaskSelectionLogic", axLogicExecutorTypeList.get(2), logicReader));
        actionState.setDefaultTask(defaultTaskList.get(2));
        for (final AxArtifactKey taskKey : taskKeySetList.get(2)) {
            actionState.getTaskReferences().put(taskKey,
                    new AxStateTaskReference(new AxReferenceKey(actionState.getKey(), taskKey.getName()), AxStateTaskOutputType.DIRECT, action2Out.getKey()));
        }

        final AxState conditionState = new AxState(new AxReferenceKey(policyKey, "Condition"));
        conditionState.setTrigger(inEventKeyList.get(1));
        final AxStateOutput condition2Action = new AxStateOutput(conditionState.getKey(), actionState.getKey(), outEventKeyList.get(1));
        conditionState.getStateOutputs().put(condition2Action.getKey().getLocalName(), condition2Action);
        conditionState
                .setTaskSelectionLogic(new AxTaskSelectionLogic(conditionState.getKey(), "TaskSelectionLogic", axLogicExecutorTypeList.get(1), logicReader));
        conditionState.setDefaultTask(defaultTaskList.get(1));
        for (final AxArtifactKey taskKey : taskKeySetList.get(1)) {
            conditionState.getTaskReferences().put(taskKey, new AxStateTaskReference(new AxReferenceKey(conditionState.getKey(), taskKey.getName()),
                    AxStateTaskOutputType.DIRECT, condition2Action.getKey()));
        }

        final AxState eventState = new AxState(new AxReferenceKey(policyKey, "Event"));
        eventState.setTrigger(inEventKeyList.get(0));
        final AxStateOutput event2Condition = new AxStateOutput(eventState.getKey(), conditionState.getKey(), outEventKeyList.get(0));
        eventState.getStateOutputs().put(event2Condition.getKey().getLocalName(), event2Condition);
        eventState.setTaskSelectionLogic(new AxTaskSelectionLogic(eventState.getKey(), "TaskSelectionLogic", axLogicExecutorTypeList.get(0), logicReader));
        eventState.setDefaultTask(defaultTaskList.get(0));
        for (final AxArtifactKey taskKey : taskKeySetList.get(0)) {
            eventState.getTaskReferences().put(taskKey, new AxStateTaskReference(new AxReferenceKey(eventState.getKey(), taskKey.getName()),
                    AxStateTaskOutputType.DIRECT, event2Condition.getKey()));
        }

        final Map<String, AxState> stateMap = new TreeMap<>();
        stateMap.put(eventState.getKey().getLocalName(), eventState);
        stateMap.put(conditionState.getKey().getLocalName(), conditionState);
        stateMap.put(actionState.getKey().getLocalName(), actionState);

        return stateMap;
    }

}
