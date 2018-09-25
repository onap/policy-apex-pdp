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

package org.onap.policy.apex.testsuites.integration.common.model;

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
    // Recurring string constants
    private static final String ACTION = "Action";
    private static final String CONDITION = "Condition";
    private static final String EVENT = "Event";
    private static final String TASK_SELECTION_LOGIC = "TaskSelectionLogic";
    private static final String JYTHON = "JYTHON";
    private static final String JRUBY = "JRUBY";
    private static final String MVEL = "MVEL";
    private static final String ORIENT = "Orient";
    private static final String STATE_NAME = "<STATE_NAME>";
    private static final String OBSERVE = "Observe";
    private static final String JAVASCRIPT = "JAVASCRIPT";
    private static final String TASK_NAME = "<TASK_NAME>";
    private static final String TASK_LOGIC = "TaskLogic";
    private static final String STATE_NUMBER = "<STATE_NUMBER>";
    private static final String STATE3_TIMESTAMP = "State3Timestamp";
    private static final String EVENT_NUMBER = "EventNumber";
    private static final String FIRST_EVENT_TIMESTAMP = "FirstEventTimestamp";
    private static final String STATE2_TIMESTAMP = "State2Timestamp";
    private static final String STATE1_TIMESTAMP = "State1Timestamp";
    private static final String TEST_TEMPERATURE = "TestTemperature";
    private static final String SENT_TIMESTAMP = "SentTimestamp";
    private static final String EVENT_TAG = ".events";
    private static final String DEFAULT_VERSION = "0.0.1";

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

        typeTimestamp = new AxContextSchema(new AxArtifactKey("TestTimestamp", DEFAULT_VERSION), "Java",
                        "java.lang.Long");
        typeDouble = new AxContextSchema(new AxArtifactKey(TEST_TEMPERATURE, DEFAULT_VERSION), "Java",
                        "java.lang.Double");
        typeCounter = new AxContextSchema(new AxArtifactKey("TestTimestamp", DEFAULT_VERSION), "Java",
                        "java.lang.Long");

        schemas = new AxContextSchemas(new AxArtifactKey("TestDatatypes", DEFAULT_VERSION));
        schemas.getSchemasMap().put(typeTimestamp.getKey(), typeTimestamp);
        schemas.getSchemasMap().put(typeDouble.getKey(), typeDouble);
        schemas.getSchemasMap().put(typeCounter.getKey(), typeCounter);

        event0000 = new AxEvent(new AxArtifactKey("Event0000", DEFAULT_VERSION), PACKAGE + EVENT_TAG);
        event0000.getParameterMap().put(SENT_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0000.getKey(), SENT_TIMESTAMP), typeTimestamp.getKey()));
        event0000.getParameterMap().put(TEST_TEMPERATURE,
                        new AxField(new AxReferenceKey(event0000.getKey(), TEST_TEMPERATURE), typeDouble.getKey()));
        event0000.getParameterMap().put(FIRST_EVENT_TIMESTAMP, new AxField(
                        new AxReferenceKey(event0000.getKey(), FIRST_EVENT_TIMESTAMP), typeTimestamp.getKey()));
        event0000.getParameterMap().put(EVENT_NUMBER,
                        new AxField(new AxReferenceKey(event0000.getKey(), EVENT_NUMBER), typeCounter.getKey()));

        event0001 = new AxEvent(new AxArtifactKey("Event0001", DEFAULT_VERSION), PACKAGE + EVENT_TAG);
        event0001.getParameterMap().put(SENT_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0001.getKey(), SENT_TIMESTAMP), typeTimestamp.getKey()));
        event0001.getParameterMap().put(STATE1_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0001.getKey(), STATE1_TIMESTAMP), typeTimestamp.getKey()));
        event0001.getParameterMap().put(TEST_TEMPERATURE,
                        new AxField(new AxReferenceKey(event0001.getKey(), TEST_TEMPERATURE), typeDouble.getKey()));
        event0001.getParameterMap().put(FIRST_EVENT_TIMESTAMP, new AxField(
                        new AxReferenceKey(event0001.getKey(), FIRST_EVENT_TIMESTAMP), typeTimestamp.getKey()));
        event0001.getParameterMap().put(EVENT_NUMBER,
                        new AxField(new AxReferenceKey(event0001.getKey(), EVENT_NUMBER), typeCounter.getKey()));

        event0002 = new AxEvent(new AxArtifactKey("Event0002", DEFAULT_VERSION), PACKAGE + EVENT_TAG);
        event0002.getParameterMap().put(SENT_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0002.getKey(), SENT_TIMESTAMP), typeTimestamp.getKey()));
        event0002.getParameterMap().put(STATE1_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0002.getKey(), STATE1_TIMESTAMP), typeTimestamp.getKey()));
        event0002.getParameterMap().put(STATE2_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0002.getKey(), STATE2_TIMESTAMP), typeTimestamp.getKey()));
        event0002.getParameterMap().put(TEST_TEMPERATURE,
                        new AxField(new AxReferenceKey(event0002.getKey(), TEST_TEMPERATURE), typeDouble.getKey()));
        event0002.getParameterMap().put(FIRST_EVENT_TIMESTAMP, new AxField(
                        new AxReferenceKey(event0002.getKey(), FIRST_EVENT_TIMESTAMP), typeTimestamp.getKey()));
        event0002.getParameterMap().put(EVENT_NUMBER,
                        new AxField(new AxReferenceKey(event0002.getKey(), EVENT_NUMBER), typeCounter.getKey()));

        event0003 = new AxEvent(new AxArtifactKey("Event0003", DEFAULT_VERSION), PACKAGE + EVENT_TAG);
        event0003.getParameterMap().put(SENT_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0003.getKey(), SENT_TIMESTAMP), typeTimestamp.getKey()));
        event0003.getParameterMap().put(STATE1_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0003.getKey(), STATE1_TIMESTAMP), typeTimestamp.getKey()));
        event0003.getParameterMap().put(STATE2_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0003.getKey(), STATE2_TIMESTAMP), typeTimestamp.getKey()));
        event0003.getParameterMap().put(STATE3_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0003.getKey(), STATE3_TIMESTAMP), typeTimestamp.getKey()));
        event0003.getParameterMap().put(TEST_TEMPERATURE,
                        new AxField(new AxReferenceKey(event0003.getKey(), TEST_TEMPERATURE), typeDouble.getKey()));
        event0003.getParameterMap().put(FIRST_EVENT_TIMESTAMP, new AxField(
                        new AxReferenceKey(event0003.getKey(), FIRST_EVENT_TIMESTAMP), typeTimestamp.getKey()));
        event0003.getParameterMap().put(EVENT_NUMBER,
                        new AxField(new AxReferenceKey(event0003.getKey(), EVENT_NUMBER), typeCounter.getKey()));

        event0004 = new AxEvent(new AxArtifactKey("Event0004", DEFAULT_VERSION), PACKAGE + EVENT_TAG);
        event0004.getParameterMap().put(SENT_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0004.getKey(), SENT_TIMESTAMP), typeTimestamp.getKey()));
        event0004.getParameterMap().put(STATE1_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0004.getKey(), STATE1_TIMESTAMP), typeTimestamp.getKey()));
        event0004.getParameterMap().put(STATE2_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0004.getKey(), STATE2_TIMESTAMP), typeTimestamp.getKey()));
        event0004.getParameterMap().put(STATE3_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0004.getKey(), STATE3_TIMESTAMP), typeTimestamp.getKey()));
        event0004.getParameterMap().put("State4Timestamp",
                        new AxField(new AxReferenceKey(event0004.getKey(), "State4Timestamp"), typeTimestamp.getKey()));
        event0004.getParameterMap().put(TEST_TEMPERATURE,
                        new AxField(new AxReferenceKey(event0004.getKey(), TEST_TEMPERATURE), typeDouble.getKey()));
        event0004.getParameterMap().put(FIRST_EVENT_TIMESTAMP, new AxField(
                        new AxReferenceKey(event0004.getKey(), FIRST_EVENT_TIMESTAMP), typeTimestamp.getKey()));
        event0004.getParameterMap().put(EVENT_NUMBER,
                        new AxField(new AxReferenceKey(event0004.getKey(), EVENT_NUMBER), typeCounter.getKey()));

        events = new AxEvents(new AxArtifactKey("Events", DEFAULT_VERSION));
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
    public AxPolicyModel getOodaPolicyModel() {
        final AxTasks tasks = new AxTasks(new AxArtifactKey("Tasks", DEFAULT_VERSION));

        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE)
                        .setDefaultLogic("EvalTaskLogic");

        final AxTask obTask = new AxTask(new AxArtifactKey("Task_Observe_0", DEFAULT_VERSION));
        obTask.duplicateInputFields(event0000.getParameterMap());
        obTask.duplicateOutputFields(event0001.getParameterMap());
        final AxTaskLogic obAxLogic = new AxTaskLogic(obTask.getKey(), TASK_LOGIC,
                        (justOneLang == null ? JRUBY : justOneLang), logicReader);
        obAxLogic.setLogic(obAxLogic.getLogic().replaceAll(STATE_NAME, OBSERVE)
                        .replaceAll(TASK_NAME, obTask.getKey().getName()).replaceAll(STATE_NUMBER, "1"));
        obTask.setTaskLogic(obAxLogic);

        final AxTask orTask = new AxTask(new AxArtifactKey("Task_Orient_0", DEFAULT_VERSION));
        orTask.duplicateInputFields(event0001.getParameterMap());
        orTask.duplicateOutputFields(event0002.getParameterMap());
        final AxTaskLogic orAxLogic = new AxTaskLogic(orTask.getKey(), TASK_LOGIC,
                        (justOneLang == null ? JAVASCRIPT : justOneLang), logicReader);
        orAxLogic.setLogic(orAxLogic.getLogic().replaceAll(STATE_NAME, ORIENT)
                        .replaceAll(TASK_NAME, orTask.getKey().getName()).replaceAll(STATE_NUMBER, "2"));
        orTask.setTaskLogic(orAxLogic);

        final AxTask dTask = new AxTask(new AxArtifactKey("Task_Decide_0", DEFAULT_VERSION));
        dTask.duplicateInputFields(event0002.getParameterMap());
        dTask.duplicateOutputFields(event0003.getParameterMap());
        final AxTaskLogic dAxLogic = new AxTaskLogic(dTask.getKey(), TASK_LOGIC,
                        (justOneLang == null ? MVEL : justOneLang), logicReader);
        dAxLogic.setLogic(dAxLogic.getLogic().replaceAll(STATE_NAME, ORIENT)
                        .replaceAll(TASK_NAME, dTask.getKey().getName()).replaceAll(STATE_NUMBER, "3"));
        dTask.setTaskLogic(dAxLogic);

        final AxTask aTask = new AxTask(new AxArtifactKey("Task_Act_0", DEFAULT_VERSION));
        aTask.duplicateInputFields(event0003.getParameterMap());
        aTask.duplicateOutputFields(event0004.getParameterMap());
        final AxTaskLogic aAxLogic = new AxTaskLogic(aTask.getKey(), TASK_LOGIC,
                        (justOneLang == null ? "JAVA" : justOneLang), logicReader);
        aAxLogic.setLogic(aAxLogic.getLogic().replaceAll(STATE_NAME, "Act")
                        .replaceAll(TASK_NAME, aTask.getKey().getName()).replaceAll(STATE_NUMBER, "4"));
        aTask.setTaskLogic(aAxLogic);

        tasks.getTaskMap().put(obTask.getKey(), obTask);
        tasks.getTaskMap().put(orTask.getKey(), orTask);
        tasks.getTaskMap().put(dTask.getKey(), dTask);
        tasks.getTaskMap().put(aTask.getKey(), aTask);

        final Set<AxArtifactKey> obTasks = new TreeSet<>();
        final Set<AxArtifactKey> orTasks = new TreeSet<>();
        final Set<AxArtifactKey> decTasks = new TreeSet<>();
        final Set<AxArtifactKey> actTasks = new TreeSet<>();

        addOodaTasks(tasks, obTasks, orTasks, decTasks, actTasks);
        
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

        final AxPolicy policy0 = new AxPolicy(new AxArtifactKey("OODAPolicy_0", DEFAULT_VERSION));
        final List<String> axLogicExecutorTypeList = initAxLogicExecutorTypeList(justOneLang);

        policy0.setStateMap(getOodaStateMap(policy0.getKey(), p0InEventList, p0OutEventList, axLogicExecutorTypeList,
                        p0defaultTaskList, taskReferenceList));
        policy0.setFirstState(policy0.getStateMap().get(OBSERVE).getKey().getLocalName());

        final AxPolicies policies = new AxPolicies(new AxArtifactKey("OODAPolicies", DEFAULT_VERSION));
        policies.getPolicyMap().put(policy0.getKey(), policy0);

        final AxKeyInformation keyInformation = new AxKeyInformation(
                        new AxArtifactKey("KeyInformation", DEFAULT_VERSION));
        final AxPolicyModel policyModel = new AxPolicyModel(
                        new AxArtifactKey("EvaluationPolicyModel_OODA", DEFAULT_VERSION));
        policyModel.setPolicies(policies);
        policyModel.setEvents(events);
        policyModel.setTasks(tasks);
        policyModel.setAlbums(new AxContextAlbums(new AxArtifactKey("Albums", DEFAULT_VERSION)));
        policyModel.setSchemas(schemas);
        policyModel.setKeyInformation(keyInformation);
        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        final AxValidationResult result = policyModel.validate(new AxValidationResult());
        if (!result.isOk()) {
            throw new ApexRuntimeException("model " + policyModel.getId() + " is not valid" + result);
        }
        return policyModel;
    }

    /**
     * Add OODA tasks to the policy.
     * 
     * @param tasks the policy tasks
     * @param obTasks observe tasks
     * @param orTasks orient tasks
     * @param decTasks decide tasks
     * @param actTasks act tasks
     */
    private void addOodaTasks(final AxTasks tasks, final Set<AxArtifactKey> obTasks, final Set<AxArtifactKey> orTasks,
                    final Set<AxArtifactKey> decTasks, final Set<AxArtifactKey> actTasks) {
        for (final AxTask task : tasks.getTaskMap().values()) {
            if (task.getKey().getName().contains(OBSERVE)) {
                obTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains(ORIENT)) {
                orTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Decide")) {
                decTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Act")) {
                actTasks.add(task.getKey());
            }
        }
    }

    /**
     * Initialize the logic executor list.
     * 
     * @param justOneLang the language to use
     * @return the list of languages
     */
    private List<String> initAxLogicExecutorTypeList(final String justOneLang) {
        List<String> axLogicExecutorTypeList = new ArrayList<>();

        if (justOneLang != null) {
            axLogicExecutorTypeList.add(justOneLang);
        } else {
            axLogicExecutorTypeList.add(JAVASCRIPT);
            axLogicExecutorTypeList.add(JYTHON);
            axLogicExecutorTypeList.add(JYTHON);
            axLogicExecutorTypeList.add(MVEL);
            axLogicExecutorTypeList.add(JRUBY);
        }
        return axLogicExecutorTypeList;
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
    private Map<String, AxState> getOodaStateMap(final AxArtifactKey policyKey,
                    final List<AxArtifactKey> inEventKeyList, final List<AxArtifactKey> outEventKeyList,
                    final List<String> axLogicExecutorTypeList, final List<AxArtifactKey> defaultTaskList,
                    final List<Set<AxArtifactKey>> taskKeySetList) {
        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE)
                        .setDefaultLogic("EvalStateLogic");

        final AxState actState = new AxState(new AxReferenceKey(policyKey, "Act"));
        actState.setTrigger(inEventKeyList.get(THIRD_MEMBER));
        final AxStateOutput act2Out = new AxStateOutput(new AxReferenceKey(actState.getKey(), "Act2Out"),
                        outEventKeyList.get(THIRD_MEMBER), AxReferenceKey.getNullKey());
        actState.getStateOutputs().put(act2Out.getKey().getLocalName(), act2Out);
        actState.setTaskSelectionLogic(new AxTaskSelectionLogic(actState.getKey(), TASK_SELECTION_LOGIC,
                        axLogicExecutorTypeList.get(THIRD_MEMBER), logicReader));
        actState.setDefaultTask(defaultTaskList.get(THIRD_MEMBER));
        for (final AxArtifactKey taskKey : taskKeySetList.get(THIRD_MEMBER)) {
            actState.getTaskReferences().put(taskKey,
                            new AxStateTaskReference(new AxReferenceKey(actState.getKey(), taskKey.getName()),
                                            AxStateTaskOutputType.DIRECT, act2Out.getKey()));
        }

        final AxState decState = new AxState(new AxReferenceKey(policyKey, "Decide"));
        decState.setTrigger(inEventKeyList.get(2));
        final AxStateOutput dec2Act = new AxStateOutput(new AxReferenceKey(decState.getKey(), "Dec2Act"),
                        outEventKeyList.get(2), actState.getKey());
        decState.getStateOutputs().put(dec2Act.getKey().getLocalName(), dec2Act);
        decState.setTaskSelectionLogic(new AxTaskSelectionLogic(decState.getKey(), TASK_SELECTION_LOGIC,
                        axLogicExecutorTypeList.get(2), logicReader));
        decState.setDefaultTask(defaultTaskList.get(2));
        for (final AxArtifactKey taskKey : taskKeySetList.get(2)) {
            decState.getTaskReferences().put(taskKey,
                            new AxStateTaskReference(new AxReferenceKey(decState.getKey(), taskKey.getName()),
                                            AxStateTaskOutputType.DIRECT, dec2Act.getKey()));
        }

        final AxState orState = new AxState(new AxReferenceKey(policyKey, ORIENT));
        orState.setTrigger(inEventKeyList.get(1));
        final AxStateOutput or2Dec = new AxStateOutput(new AxReferenceKey(orState.getKey(), "Or2Dec"),
                        outEventKeyList.get(1), decState.getKey());
        orState.getStateOutputs().put(or2Dec.getKey().getLocalName(), or2Dec);
        orState.setTaskSelectionLogic(new AxTaskSelectionLogic(orState.getKey(), TASK_SELECTION_LOGIC,
                        axLogicExecutorTypeList.get(1), logicReader));
        orState.setDefaultTask(defaultTaskList.get(1));
        for (final AxArtifactKey taskKey : taskKeySetList.get(1)) {
            orState.getTaskReferences().put(taskKey,
                            new AxStateTaskReference(new AxReferenceKey(orState.getKey(), taskKey.getName()),
                                            AxStateTaskOutputType.DIRECT, or2Dec.getKey()));
        }

        final AxState obState = new AxState(new AxReferenceKey(policyKey, OBSERVE));
        obState.setTrigger(inEventKeyList.get(0));
        final AxStateOutput ob2Or = new AxStateOutput(new AxReferenceKey(obState.getKey(), "Ob2Or"),
                        outEventKeyList.get(0), orState.getKey());
        obState.getStateOutputs().put(ob2Or.getKey().getLocalName(), ob2Or);
        obState.setTaskSelectionLogic(new AxTaskSelectionLogic(obState.getKey(), TASK_SELECTION_LOGIC,
                        axLogicExecutorTypeList.get(0), logicReader));
        obState.setDefaultTask(defaultTaskList.get(0));
        for (final AxArtifactKey taskKey : taskKeySetList.get(0)) {
            obState.getTaskReferences().put(taskKey,
                            new AxStateTaskReference(new AxReferenceKey(obState.getKey(), taskKey.getName()),
                                            AxStateTaskOutputType.DIRECT, ob2Or.getKey()));
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
    public AxPolicyModel getEcaPolicyModel() {

        final AxTasks tasks = new AxTasks(new AxArtifactKey("Tasks", DEFAULT_VERSION));

        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE)
                        .setDefaultLogic("EvalTaskLogic");

        final AxTask eTask = new AxTask(new AxArtifactKey("Task_Event_0", DEFAULT_VERSION));
        eTask.duplicateInputFields(event0000.getParameterMap());
        eTask.duplicateOutputFields(event0001.getParameterMap());
        final AxTaskLogic eAxLogic = new AxTaskLogic(eTask.getKey(), TASK_LOGIC,
                        (justOneLang == null ? JYTHON : justOneLang), logicReader);
        eAxLogic.setLogic(eAxLogic.getLogic().replaceAll(STATE_NAME, EVENT)
                        .replaceAll(TASK_NAME, eTask.getKey().getName()).replaceAll(STATE_NUMBER, "1"));
        eTask.setTaskLogic(eAxLogic);

        final AxTask cTask = new AxTask(new AxArtifactKey("Task_Condition_0", DEFAULT_VERSION));
        cTask.duplicateInputFields(event0001.getParameterMap());
        cTask.duplicateOutputFields(event0002.getParameterMap());
        final AxTaskLogic cAxLogic = new AxTaskLogic(cTask.getKey(), TASK_LOGIC,
                        (justOneLang == null ? JAVASCRIPT : justOneLang), logicReader);
        cAxLogic.setLogic(cAxLogic.getLogic().replaceAll(STATE_NAME, CONDITION)
                        .replaceAll(TASK_NAME, cTask.getKey().getName()).replaceAll(STATE_NUMBER, "2"));
        cTask.setTaskLogic(cAxLogic);

        final AxTask aTask = new AxTask(new AxArtifactKey("Task_Action_0", DEFAULT_VERSION));
        aTask.duplicateInputFields(event0002.getParameterMap());
        aTask.duplicateOutputFields(event0003.getParameterMap());
        final AxTaskLogic aAxLogic = new AxTaskLogic(aTask.getKey(), TASK_LOGIC,
                        (justOneLang == null ? "JAVA" : justOneLang), logicReader);
        aAxLogic.setLogic(aAxLogic.getLogic().replaceAll(STATE_NAME, ACTION)
                        .replaceAll(TASK_NAME, aTask.getKey().getName()).replaceAll(STATE_NUMBER, "3"));
        aTask.setTaskLogic(aAxLogic);

        tasks.getTaskMap().put(eTask.getKey(), eTask);
        tasks.getTaskMap().put(cTask.getKey(), cTask);
        tasks.getTaskMap().put(aTask.getKey(), aTask);

        final Set<AxArtifactKey> eventTasks = new TreeSet<>();
        final Set<AxArtifactKey> conditionTasks = new TreeSet<>();
        final Set<AxArtifactKey> actionTasks = new TreeSet<>();

        addEcaTasks(tasks, eventTasks, conditionTasks, actionTasks);
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

        final AxPolicy policy0 = new AxPolicy(new AxArtifactKey("ECAPolicy_0", DEFAULT_VERSION));
        final List<String> axLogicExecutorTypeList = Arrays.asList((justOneLang == null ? JAVASCRIPT : justOneLang),
                        (justOneLang == null ? MVEL : justOneLang), (justOneLang == null ? JYTHON : justOneLang));
        policy0.setStateMap(getEcaStateMap(policy0.getKey(), p0InEventList, p0OutEventList, axLogicExecutorTypeList,
                        p0defaultTaskList, taskReferenceList));
        policy0.setFirstState(policy0.getStateMap().get(EVENT).getKey().getLocalName());

        final AxPolicies policies = new AxPolicies(new AxArtifactKey("ECAPolicies", DEFAULT_VERSION));
        policies.getPolicyMap().put(policy0.getKey(), policy0);

        final AxKeyInformation keyInformation = new AxKeyInformation(
                        new AxArtifactKey("KeyInformation", DEFAULT_VERSION));
        final AxPolicyModel policyModel = new AxPolicyModel(
                        new AxArtifactKey("EvaluationPolicyModel_ECA", DEFAULT_VERSION));
        policyModel.setPolicies(policies);
        policyModel.setEvents(events);
        policyModel.setTasks(tasks);
        policyModel.setAlbums(new AxContextAlbums(new AxArtifactKey("Albums", DEFAULT_VERSION)));
        policyModel.setSchemas(schemas);
        policyModel.setKeyInformation(keyInformation);
        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        final AxValidationResult result = policyModel.validate(new AxValidationResult());
        if (!result.isOk()) {
            throw new ApexRuntimeException("model " + policyModel.getId() + " is not valid" + result);
        }
        return policyModel;
    }

    /**
     * Add ECA tasks.
     * 
     * @param tasks the tasks
     * @param eventTasks event tasks
     * @param conditionTasks condition tasks
     * @param actionTasks action tasks
     */
    private void addEcaTasks(final AxTasks tasks, final Set<AxArtifactKey> eventTasks,
                    final Set<AxArtifactKey> conditionTasks, final Set<AxArtifactKey> actionTasks) {
        for (final AxTask task : tasks.getTaskMap().values()) {
            if (task.getKey().getName().contains(EVENT)) {
                eventTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains(CONDITION)) {
                conditionTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains(ACTION)) {
                actionTasks.add(task.getKey());
            }
        }
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
    private Map<String, AxState> getEcaStateMap(final AxArtifactKey policyKey, final List<AxArtifactKey> inEventKeyList,
                    final List<AxArtifactKey> outEventKeyList, final List<String> axLogicExecutorTypeList,
                    final List<AxArtifactKey> defaultTaskList, final List<Set<AxArtifactKey>> taskKeySetList) {
        final AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(PACKAGE)
                        .setDefaultLogic("EvalStateLogic");

        final AxState actionState = new AxState(new AxReferenceKey(policyKey, ACTION));
        actionState.setTrigger(inEventKeyList.get(2));
        final AxStateOutput action2Out = new AxStateOutput(actionState.getKey(), AxReferenceKey.getNullKey(),
                        outEventKeyList.get(2));
        actionState.getStateOutputs().put(action2Out.getKey().getLocalName(), action2Out);
        actionState.setTaskSelectionLogic(new AxTaskSelectionLogic(actionState.getKey(), TASK_SELECTION_LOGIC,
                        axLogicExecutorTypeList.get(2), logicReader));
        actionState.setDefaultTask(defaultTaskList.get(2));
        for (final AxArtifactKey taskKey : taskKeySetList.get(2)) {
            actionState.getTaskReferences().put(taskKey,
                            new AxStateTaskReference(new AxReferenceKey(actionState.getKey(), taskKey.getName()),
                                            AxStateTaskOutputType.DIRECT, action2Out.getKey()));
        }

        final AxState conditionState = new AxState(new AxReferenceKey(policyKey, CONDITION));
        conditionState.setTrigger(inEventKeyList.get(1));
        final AxStateOutput condition2Action = new AxStateOutput(conditionState.getKey(), actionState.getKey(),
                        outEventKeyList.get(1));
        conditionState.getStateOutputs().put(condition2Action.getKey().getLocalName(), condition2Action);
        conditionState.setTaskSelectionLogic(new AxTaskSelectionLogic(conditionState.getKey(), TASK_SELECTION_LOGIC,
                        axLogicExecutorTypeList.get(1), logicReader));
        conditionState.setDefaultTask(defaultTaskList.get(1));
        for (final AxArtifactKey taskKey : taskKeySetList.get(1)) {
            conditionState.getTaskReferences().put(taskKey,
                            new AxStateTaskReference(new AxReferenceKey(conditionState.getKey(), taskKey.getName()),
                                            AxStateTaskOutputType.DIRECT, condition2Action.getKey()));
        }

        final AxState eventState = new AxState(new AxReferenceKey(policyKey, EVENT));
        eventState.setTrigger(inEventKeyList.get(0));
        final AxStateOutput event2Condition = new AxStateOutput(eventState.getKey(), conditionState.getKey(),
                        outEventKeyList.get(0));
        eventState.getStateOutputs().put(event2Condition.getKey().getLocalName(), event2Condition);
        eventState.setTaskSelectionLogic(new AxTaskSelectionLogic(eventState.getKey(), TASK_SELECTION_LOGIC,
                        axLogicExecutorTypeList.get(0), logicReader));
        eventState.setDefaultTask(defaultTaskList.get(0));
        for (final AxArtifactKey taskKey : taskKeySetList.get(0)) {
            eventState.getTaskReferences().put(taskKey,
                            new AxStateTaskReference(new AxReferenceKey(eventState.getKey(), taskKey.getName()),
                                            AxStateTaskOutputType.DIRECT, event2Condition.getKey()));
        }

        final Map<String, AxState> stateMap = new TreeMap<>();
        stateMap.put(eventState.getKey().getLocalName(), eventState);
        stateMap.put(conditionState.getKey().getLocalName(), conditionState);
        stateMap.put(actionState.getKey().getLocalName(), actionState);

        return stateMap;
    }

}
