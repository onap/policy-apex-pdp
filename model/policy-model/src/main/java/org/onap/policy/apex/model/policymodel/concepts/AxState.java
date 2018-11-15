/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

package org.onap.policy.apex.model.policymodel.concepts;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class holds the definition of a single state in a policy. A state is a single stage in a policy. A state has a
 * single input event, its trigger. A state can output many events, but can only output one event on a single execution.
 * After it executes, a state can pass control to another state or can simply emit its event to an external system. In
 * the case where a state passes control to another state, the output event of the state becomes the input event of the
 * next state. The outputs of a state {@link AxStateOutput} are held as a map in the state. Each state output contains
 * the outgoing event of the state and optionally the next state to pass control to.
 *
 * <p>A state uses tasks {@link AxTask} to execute its logic. A state holds its tasks in a map and must have at least
 * one task. A state uses Task Selection Logic {@link AxTaskSelectionLogic} to select which task should be executed in a
 * given execution cycle. Optional Task Selection Logic can use fields on the incoming event and information from the
 * context albums available on the state to decide what task to execute in a given context. The default task of a state
 * is the task that is executed when task Selection Logic is not specified. In cases where only a single task is
 * specified on a state, the default task must be that task and the state always executes that task.
 *
 * <p>What happens when a state completes its execution cycle depends on the task that is selected for execution by the
 * state. Therefore, the action to be performed a state on execution of each task must be defined in the state as a
 * {@link AxStateTaskReference} instance for each task defined in the state. The {@link AxStateTaskReference} instance
 * defines the action to be performed as either a {@link AxStateTaskOutputType} of {@link AxStateTaskOutputType#DIRECT}
 * or {@link AxStateTaskOutputType#LOGIC} and contains an {@link AxReferenceKey} reference to the instance that will
 * complete the state output.
 *
 * <p>In the case of direct output, the {@link AxReferenceKey} reference in the {@link AxStateTaskReference} instance is
 * a reference to an {@link AxStateOutput} instance. The state output defines the event to be emitted by the state and
 * the next state to pass control to if any. All fields of the executed task are marshaled onto the outgoing event
 * automatically by Apex.
 *
 * <p>In the case of logic output, the {@link AxReferenceKey} reference in the {@link AxStateTaskReference} instance is
 * a reference to State Finalizer Logic in an {@link AxStateFinalizerLogic} instance, which selects the
 * {@link AxStateOutput} that the state will use. The state finalizer logic uses fields emitted by the executed task and
 * information from the context albums available on the state to decide what {@link AxStateOutput} to select in a given
 * context. The state output defines the event to be emitted by the state and the next state to pass control to if any.
 * The State Finalizer Logic instances for the state are held in a map in the state. State Finalizer Logic must marshal
 * the fields of the output event in whatever manner it wishes; Apex does not automatically transfer the output fields
 * from the task directly to the output event.
 *
 * <p>The Task Selection Logic instance or State Finalizer Logic instances in a state may use information in context
 * albums to arrive at their task or state output selections. The context albums that the state uses and that should be
 * made available to the state by Apex policy distribution are held as a set of references to context albums in the
 * state.
 *
 * <p>During validation of a state, the validation checks listed below are executed: <ol> <li>The policy key must not be
 * a null key and must be valid, see validation in {@link AxReferenceKey} <li>The trigger event key must not be a null
 * key and must be valid, see validation in {@link AxArtifactKey} <li>At least one state output must be defined <li>Each
 * state output in a state must have that state as its parent <li>Each state output must be valid, see validation in
 * {@link AxStateOutput} <li>The next state defined in a state output must be unique in a state <li>The default task key
 * must not be a null key and must be valid, see validation in {@link AxArtifactKey} <li>The default task must appear in
 * the task map of the state <li>At least one task must be defined on the state <li>Each task key on the task map for
 * the state must not be a null key and must be valid, see validation in {@link AxArtifactKey} <li>All state task
 * references for each task in the state must exist and must be valid, see validation in {@link AxStateTaskReference}
 * <li>Each state task reference in a state must have that state as its parent <li>For direct state outputs from tasks,
 * the state output must be defined on the state <li>For logic state outputs from tasks, the State Finalizer Logic must
 * be defined on the state <li>An observation is issued for each state output defined on the state that is not used as a
 * direct output on a task <li>An observation is issued for each state finalizer logic instance defined on the state
 * that is not used as an output on a task <li>Each context album key on the context album set for the state must not be
 * a null key and must be valid, see validation in {@link AxArtifactKey} <li>Task Selection logic in a state must have
 * that state as its parent <li>Task Selection logic in a state must be valid, see validation in
 * {@link AxTaskSelectionLogic} <li>Each State Finalizer logic instance in a state must have that state as its parent
 * <li>Each State Finalizer logic instance in a state must be valid, see validation in {@link AxStateFinalizerLogic}
 * </ol>
 */

@Entity
@Table(name = "AxState")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexState", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxState", namespace = "http://www.onap.org/policy/apex-pdp", propOrder =
    { "key", "trigger", "stateOutputs", "contextAlbumReferenceSet", "taskSelectionLogic", "stateFinalizerLogicMap",
                    "defaultTask", "taskReferenceMap" })

public class AxState extends AxConcept {
    private static final String DOES_NOT_EQUAL_STATE_KEY = " does not equal state key";

    private static final long serialVersionUID = 8041771382337655535L;

    @EmbeddedId
    @XmlElement(name = "stateKey", required = true)
    private AxReferenceKey key;

    // @formatter:off
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "name", column = @Column(name = "inTriggerName")),
            @AttributeOverride(name = "version", column = @Column(name = "inTriggerVersion"))})
    @Column(name = "trigger")
    @XmlElement(required = true)
    private AxArtifactKey trigger;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "soParentKeyName", referencedColumnName = "parentKeyName"),
                    @JoinColumn(name = "soParentKeyVersion", referencedColumnName = "parentKeyVersion"),
                    @JoinColumn(name = "soParentLocalName", referencedColumnName = "parentLocalName"),
                    @JoinColumn(name = "soLocalName", referencedColumnName = "localName")},
            inverseJoinColumns = {@JoinColumn(name = "stateParentKeyName", referencedColumnName = "parentKeyName"),
                    @JoinColumn(name = "stateParentKeyVersion", referencedColumnName = "parentKeyVersion"),
                    @JoinColumn(name = "stateParentLocalName", referencedColumnName = "parentLocalName"),
                    @JoinColumn(name = "stateLocalName", referencedColumnName = "localName")})
    @XmlElement(name = "stateOutputs", required = true)
    private Map<String, AxStateOutput> stateOutputs;

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "stateParentKeyName", referencedColumnName = "parentKeyName"),
            @JoinColumn(name = "stateParentKeyVersion", referencedColumnName = "parentKeyVersion"),
            @JoinColumn(name = "stateParentLocalName", referencedColumnName = "parentLocalName"),
            @JoinColumn(name = "stateLocalName", referencedColumnName = "localName")})
    @XmlElement(name = "contextAlbumReference")
    private Set<AxArtifactKey> contextAlbumReferenceSet;

    @OneToOne
    @JoinTable(name = "STATE_TSL_JT",
            joinColumns = {
                    @JoinColumn(name = "tslParentKeyName", referencedColumnName = "parentKeyName", updatable = false,
                            insertable = false),
                    @JoinColumn(name = "tslParentKeyVersion", referencedColumnName = "parentKeyVersion",
                            updatable = false, insertable = false),
                    @JoinColumn(name = "tslParentLocalName ", referencedColumnName = "parentLocalName",
                            updatable = false, insertable = false),
                    @JoinColumn(name = "tslLocalName", referencedColumnName = "localName", updatable = false,
                            insertable = false)})
    @XmlElement(required = true)
    private AxTaskSelectionLogic taskSelectionLogic;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "sflParentKeyName", referencedColumnName = "parentKeyName"),
                    @JoinColumn(name = "sflParentKeyVersion", referencedColumnName = "parentKeyVersion"),
                    @JoinColumn(name = "sflParentLocalName", referencedColumnName = "parentLocalName"),
                    @JoinColumn(name = "sflLocalName", referencedColumnName = "localName")},
            inverseJoinColumns = {@JoinColumn(name = "stateParentKeyName", referencedColumnName = "parentKeyName"),
                    @JoinColumn(name = "stateParentKeyVersion", referencedColumnName = "parentKeyVersion"),
                    @JoinColumn(name = "stateParentLocalName", referencedColumnName = "parentLocalName"),
                    @JoinColumn(name = "stateLocalName", referencedColumnName = "localName")})
    @XmlElement(name = "stateFinalizerLogicMap", required = true)
    private Map<String, AxStateFinalizerLogic> stateFinalizerLogicMap;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "name", column = @Column(name = "defaultTaskName")),
            @AttributeOverride(name = "version", column = @Column(name = "defaultTaskVersion"))})
    @Column(name = "defaultTask")
    @XmlElement(required = true)
    private AxArtifactKey defaultTask;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "trmParentKeyName", referencedColumnName = "parentKeyName"),
                    @JoinColumn(name = "trmParentKeyVersion", referencedColumnName = "parentKeyVersion"),
                    @JoinColumn(name = "trmParentLocalName", referencedColumnName = "parentLocalName"),
                    @JoinColumn(name = "trmLocalName", referencedColumnName = "localName")},
            inverseJoinColumns = {@JoinColumn(name = "stateParentKeyName", referencedColumnName = "parentKeyName"),
                    @JoinColumn(name = "stateParentKeyVersion", referencedColumnName = "parentKeyVersion"),
                    @JoinColumn(name = "stateParentLocalName", referencedColumnName = "parentLocalName"),
                    @JoinColumn(name = "stateLocalName", referencedColumnName = "localName")})
    @XmlElement(name = "taskReferences", required = true)
    private Map<AxArtifactKey, AxStateTaskReference> taskReferenceMap;
    // @formatter:on

    /**
     * The Default Constructor creates a state with a null reference key and with default values for all other fields.
     */
    public AxState() {
        this(new AxReferenceKey());
        contextAlbumReferenceSet = new TreeSet<>();
        taskReferenceMap = new TreeMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxState(final AxState copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates a state with the given reference key and with default values for all other fields.
     *
     * @param key the reference key of the state
     */
    public AxState(final AxReferenceKey key) {
        // @formatter:off
        this(new AxStateParamsBuilder()
                        .key(key)                                                             // Key
                        .trigger(AxArtifactKey.getNullKey())                                  // Trigger Reference
                        .stateOutputs(new TreeMap<String, AxStateOutput>())                   // State Outputs
                        .contextAlbumReferenceSet(new TreeSet<AxArtifactKey>())               // Context Album Refs
                        .taskSelectionLogic(new AxTaskSelectionLogic())                       // Task Selection Logic
                        .stateFinalizerLogicMap(new TreeMap<String, AxStateFinalizerLogic>()) // State Finalizer Logics
                        .defaultTask(AxArtifactKey.getNullKey())                              // Default Task
                        .taskReferenceMap(new TreeMap<AxArtifactKey, AxStateTaskReference>()) // Task References
        );
        // @formatter:on
    }

    /**
     * This Constructor creates a state with all its fields defined.
     *
     * @param axStateParams parameters for state creation
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    public AxState(AxStateParamsBuilder axStateParams) {
        super();
        Assertions.argumentNotNull(axStateParams.getKey(), "key may not be null");
        Assertions.argumentNotNull(axStateParams.getTrigger(), "trigger may not be null");
        Assertions.argumentNotNull(axStateParams.getStateOutputs(), "stateOutputs may not be null");
        Assertions.argumentNotNull(axStateParams.getContextAlbumReferenceSet(),
                        "contextAlbumReferenceSet may not be null");
        Assertions.argumentNotNull(axStateParams.getTaskSelectionLogic(), "taskSelectionLogic may not be null");
        Assertions.argumentNotNull(axStateParams.getStateFinalizerLogicMap(), "stateFinalizerLogicMap may not be null");
        Assertions.argumentNotNull(axStateParams.getDefaultTask(), "defaultTask may not be null");
        Assertions.argumentNotNull(axStateParams.getTaskReferenceMap(), "taskReferenceMap may not be null");

        this.key = axStateParams.getKey();
        this.trigger = axStateParams.getTrigger();
        this.stateOutputs = axStateParams.getStateOutputs();
        this.contextAlbumReferenceSet = axStateParams.getContextAlbumReferenceSet();
        this.taskSelectionLogic = axStateParams.getTaskSelectionLogic();
        this.stateFinalizerLogicMap = axStateParams.getStateFinalizerLogicMap();
        this.defaultTask = axStateParams.getDefaultTask();
        this.taskReferenceMap = axStateParams.getTaskReferenceMap();
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * When a state is unmarshalled from disk or from the database, the parent of contained objects is not defined. This
     * method is called by JAXB after unmarshaling and is used to set the parent keys of all
     * {@link AxTaskSelectionLogic}, {@link AxStateOutput}, and {@link AxStateFinalizerLogic} instance in the state.
     *
     * @param unmarshaler the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller unmarshaler, final Object parent) {
        if (!taskSelectionLogic.getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
            taskSelectionLogic.getKey().setParentReferenceKey(key);
        }

        for (final Entry<String, AxStateOutput> soEntry : stateOutputs.entrySet()) {
            soEntry.getValue().getKey().setParentReferenceKey(key);
        }

        for (final Entry<String, AxStateFinalizerLogic> sflEntry : stateFinalizerLogicMap.entrySet()) {
            sflEntry.getValue().getKey().setParentReferenceKey(key);
        }

        for (final Entry<AxArtifactKey, AxStateTaskReference> trEntry : taskReferenceMap.entrySet()) {
            trEntry.getValue().getKey().setParentReferenceKey(key);
        }
    }

    /**
     * Gets the names of all the states that this state may pass control to.
     *
     * @return the list of possible states that may receive control when this state completes execution
     */
    public Set<String> getNextStateSet() {
        final Set<String> nextStateSet = new TreeSet<>();

        for (final AxStateOutput stateOutput : stateOutputs.values()) {
            nextStateSet.add(stateOutput.getNextState().getLocalName());
        }
        return nextStateSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxReferenceKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();
        keyList.add(new AxKeyUse(trigger.getKey()));
        for (final AxStateOutput stateOutput : stateOutputs.values()) {
            keyList.addAll(stateOutput.getKeys());
        }
        for (final AxArtifactKey contextAlbumReferenceKey : contextAlbumReferenceSet) {
            keyList.add(new AxKeyUse(contextAlbumReferenceKey));
        }
        if (!taskSelectionLogic.getKey().equals(AxReferenceKey.getNullKey())) {
            keyList.addAll(taskSelectionLogic.getKeys());
        }
        for (final Entry<String, AxStateFinalizerLogic> stateFinalizerLogicEntry : stateFinalizerLogicMap.entrySet()) {
            keyList.addAll(stateFinalizerLogicEntry.getValue().getKeys());
        }
        keyList.add(new AxKeyUse(defaultTask.getKey()));
        for (final Entry<AxArtifactKey, AxStateTaskReference> taskReferenceEntry : taskReferenceMap.entrySet()) {
            keyList.add(new AxKeyUse(taskReferenceEntry.getKey()));

            // A state output is allowed to be used more than once but we only return one usage as a
            // key
            for (AxKey referencedKey : taskReferenceEntry.getValue().getKeys()) {
                if (keyList.contains(referencedKey)) {
                    keyList.add(referencedKey);
                }
            }
        }
        return keyList;
    }

    /**
     * Sets the reference key of the state.
     *
     * @param key the state reference key
     */
    public void setKey(final AxReferenceKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the event that triggers the state.
     *
     * @return the event that triggers the state
     */
    public AxArtifactKey getTrigger() {
        return trigger;
    }

    /**
     * Sets the event that triggers the state.
     *
     * @param trigger the event that triggers the state
     */
    public void setTrigger(final AxArtifactKey trigger) {
        Assertions.argumentNotNull(trigger, "trigger may not be null");
        this.trigger = trigger;
    }

    /**
     * Gets the possible state outputs for the state.
     *
     * @return the the possible state outputs for the state
     */
    public Map<String, AxStateOutput> getStateOutputs() {
        return stateOutputs;
    }

    /**
     * Sets the the possible state outputs for the state.
     *
     * @param stateOutputs the the possible state outputs for the state
     */
    public void setStateOutputs(final Map<String, AxStateOutput> stateOutputs) {
        Assertions.argumentNotNull(stateOutputs, "stateOutputs may not be null");
        this.stateOutputs = stateOutputs;
    }

    /**
     * Gets the context album reference set defines the context that may be used by Task Selection Logic and State
     * Finalizer Logic in the state.
     *
     * @return the context album reference set defines the context that may be used by Task Selection Logic and State
     *         Finalizer Logic in the state
     */
    public Set<AxArtifactKey> getContextAlbumReferences() {
        return contextAlbumReferenceSet;
    }

    /**
     * Sets the context album reference set defines the context that may be used by Task Selection Logic and State
     * Finalizer Logic in the state.
     *
     * @param contextAlbumReferences the context album reference set defines the context that may be used by Task
     *        Selection Logic and State Finalizer Logic in the state
     */
    public void setContextAlbumReferences(final Set<AxArtifactKey> contextAlbumReferences) {
        Assertions.argumentNotNull(contextAlbumReferences, "contextAlbumReferenceSet may not be null");
        this.contextAlbumReferenceSet = contextAlbumReferences;
    }

    /**
     * Gets the task selection logic that selects the task a state executes in an execution cycle.
     *
     * @return the task selection logic that selects the task a state executes in an execution cycle
     */
    public AxTaskSelectionLogic getTaskSelectionLogic() {
        return taskSelectionLogic;
    }

    /**
     * Sets the task selection logic that selects the task a state executes in an execution cycle.
     *
     * @param taskSelectionLogic the task selection logic that selects the task a state executes in an execution cycle
     */
    public void setTaskSelectionLogic(final AxTaskSelectionLogic taskSelectionLogic) {
        Assertions.argumentNotNull(taskSelectionLogic, "taskSelectionLogic may not be null");
        this.taskSelectionLogic = taskSelectionLogic;
    }

    /**
     * Check if task selection logic has been specified the state.
     *
     * @return true, if task selection logic has been specified
     */
    public boolean checkSetTaskSelectionLogic() {
        return !taskSelectionLogic.getKey().equals(AxReferenceKey.getNullKey());
    }

    /**
     * Gets the state finalizer logic instances that selects the state output to use after a task executes in a state
     * execution cycle.
     *
     * @return the state finalizer logic instances that selects the state output to use after a task executes in a state
     *         execution cycle
     */
    public Map<String, AxStateFinalizerLogic> getStateFinalizerLogicMap() {
        return stateFinalizerLogicMap;
    }

    /**
     * Sets the state finalizer logic instances that selects the state output to use after a task executes in a state
     * execution cycle.
     *
     * @param stateFinalizerLogicMap the state finalizer logic instances that selects the state output to use after a
     *        task executes in a state execution cycle
     */
    public void setStateFinalizerLogicMap(final Map<String, AxStateFinalizerLogic> stateFinalizerLogicMap) {
        Assertions.argumentNotNull(stateFinalizerLogicMap, "stateFinalizerLogic may not be null");
        this.stateFinalizerLogicMap = stateFinalizerLogicMap;
    }

    /**
     * Gets the default task that will execute in a state if Task Selection Logic is not specified.
     *
     * @return the default task that will execute in a state if Task Selection Logic is not specified
     */
    public AxArtifactKey getDefaultTask() {
        return defaultTask;
    }

    /**
     * Sets the default task that will execute in a state if Task Selection Logic is not specified.
     *
     * @param defaultTask the default task that will execute in a state if Task Selection Logic is not specified
     */
    public void setDefaultTask(final AxArtifactKey defaultTask) {
        Assertions.argumentNotNull(defaultTask, "defaultTask may not be null");
        this.defaultTask = defaultTask;
    }

    /**
     * Gets the task reference map that defines the tasks for the state and how the task outputs are handled.
     *
     * @return the task reference map that defines the tasks for the state and how the task outputs are handled
     */
    public Map<AxArtifactKey, AxStateTaskReference> getTaskReferences() {
        return taskReferenceMap;
    }

    /**
     * Sets the task reference map that defines the tasks for the state and how the task outputs are handled.
     *
     * @param taskReferences the task reference map that defines the tasks for the state and how the task outputs are
     *        handled
     */
    public void setTaskReferences(final Map<AxArtifactKey, AxStateTaskReference> taskReferences) {
        Assertions.argumentNotNull(taskReferences, "taskReferenceMap may not be null");
        this.taskReferenceMap = taskReferences;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.
     * basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key is a null key"));
        }

        result = key.validate(result);

        if (trigger.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "trigger is a null key: " + trigger));
        }
        result = trigger.validate(result);

        if (stateOutputs.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "stateOutputs may not be empty"));
        } else {
            validateStateOutputs(result);
        }

        validateContextAlbumReferences(result);
        result = validateTaskSelectionLogic(result);
        validateStateFinalizerLogics(result);

        if (defaultTask.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "default task has a null key: " + defaultTask));
        }
        result = defaultTask.validate(result);

        if (taskReferenceMap.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "taskReferenceMap may not be empty"));
        } else {
            validateStateTaskReferences(result);
        }

        return result;
    }

    /**
     * Validate the state outputs of the state.
     *
     * @param result the validation result to append to
     */
    private void validateStateOutputs(AxValidationResult result) {
        final Set<String> nextStateNameSet = new TreeSet<>();
        for (final Entry<String, AxStateOutput> stateOutputEntry : stateOutputs.entrySet()) {
            if (stateOutputEntry.getValue() == null) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "null state output value found on state output " + stateOutputEntry.getKey()));
            } else {
                if (!stateOutputEntry.getValue().getKey().getParentReferenceKey().equals(key)) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "parent key on state output " + stateOutputEntry.getKey()
                                                    + DOES_NOT_EQUAL_STATE_KEY));
                }

                if (stateOutputEntry.getValue().getNextState().getLocalName().equals(key.getLocalName())) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "state output next state "
                                                    + stateOutputEntry.getValue().getNextState().getLocalName()
                                                    + " may not be this state"));

                }

                if (nextStateNameSet.contains(stateOutputEntry.getValue().getNextState().getLocalName())) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "duplicate state output next state name "
                                                    + stateOutputEntry.getValue().getNextState().getLocalName()
                                                    + " found"));
                } else {
                    nextStateNameSet.add(stateOutputEntry.getValue().getNextState().getLocalName());
                }
                result = stateOutputEntry.getValue().validate(result);
            }
        }
    }

    /**
     * Validate the context album references of the state.
     *
     * @param result the validation result to append to
     */
    private void validateContextAlbumReferences(AxValidationResult result) {
        for (final AxArtifactKey contextAlbumReference : contextAlbumReferenceSet) {
            if (contextAlbumReference.equals(AxArtifactKey.getNullKey())) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "key on context album reference entry " + contextAlbumReference.getKey()
                                                + " may not be the null key"));
            }

            result = contextAlbumReference.validate(result);
        }
    }

    /**
     * Validate the task selection logic of the state.
     *
     * @param result the validation result to append to
     * @return the result of the validation
     */
    private AxValidationResult validateTaskSelectionLogic(AxValidationResult result) {
        if (!taskSelectionLogic.getKey().equals(AxReferenceKey.getNullKey())) {
            if (!taskSelectionLogic.getKey().getParentReferenceKey().equals(key)) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "taskSelectionLogic key " + taskSelectionLogic.getKey().getId()
                                                + DOES_NOT_EQUAL_STATE_KEY));
            }
            result = taskSelectionLogic.validate(result);
        }

        return result;
    }

    /**
     * Validate all the state finalizer logic of the state.
     *
     * @param result the validation result to append to
     */
    private void validateStateFinalizerLogics(AxValidationResult result) {
        for (final Entry<String, AxStateFinalizerLogic> stateFinalizerLogicEntry : stateFinalizerLogicMap.entrySet()) {
            if (stateFinalizerLogicEntry.getValue() == null) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "null state finalizer logic value found on state finalizer entry "
                                                + stateFinalizerLogicEntry.getKey()));
            } else {
                if (!stateFinalizerLogicEntry.getValue().getKey().getParentReferenceKey().equals(key)) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "stateFinalizerLogic parent key "
                                                    + stateFinalizerLogicEntry.getValue().getKey().getId()
                                                    + DOES_NOT_EQUAL_STATE_KEY));
                }

                result = stateFinalizerLogicEntry.getValue().validate(result);
            }
        }
    }

    /**
     * Validate the tasks used the state.
     *
     * @param result the validation result to append to
     */
    private void validateStateTaskReferences(AxValidationResult result) {
        final Set<String> usedStateOutputNameSet = new TreeSet<>();
        final Set<String> usedStateFinalizerLogicNameSet = new TreeSet<>();

        for (final Entry<AxArtifactKey, AxStateTaskReference> taskRefEntry : taskReferenceMap.entrySet()) {
            if (taskRefEntry.getKey().equals(AxArtifactKey.getNullKey())) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "task has a null key: " + taskRefEntry.getKey()));
            }
            result = taskRefEntry.getKey().validate(result);

            if (taskRefEntry.getValue() == null) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "null task reference value found on task reference " + taskRefEntry.getKey()));
            } else {
                result = validateStateTaskReference(taskRefEntry.getKey(), taskRefEntry.getValue(),
                                usedStateOutputNameSet, usedStateFinalizerLogicNameSet, result);
            }
        }

        final Set<String> unUsedStateOutputNameSet = new TreeSet<>(stateOutputs.keySet());
        unUsedStateOutputNameSet.removeAll(usedStateOutputNameSet);
        for (final String unUsedStateOutputName : unUsedStateOutputNameSet) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.OBSERVATION,
                            "state output " + unUsedStateOutputName + " is not used directly by any task"));
        }

        final Set<String> usnUedStateFinalizerLogicNameSet = new TreeSet<>(stateFinalizerLogicMap.keySet());
        usnUedStateFinalizerLogicNameSet.removeAll(usedStateFinalizerLogicNameSet);
        for (final String unusedStateFinalizerLogicName : usnUedStateFinalizerLogicNameSet) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.OBSERVATION,
                            "state finalizer logic " + unusedStateFinalizerLogicName + " is not used by any task"));
        }

        if (!taskReferenceMap.containsKey(defaultTask)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "defaultTask " + defaultTask + " not found in taskReferenceMap"));
        }
    }

    /**
     * Validate the references of a task used in a state.
     *
     * @param taskKey The key of the task
     * @param taskReference the task reference of the task
     * @param stateOutputNameSet State outputs that have been used so far, will be appended for this task reference
     * @param stateFinalizerLogicNameSet State finalizers that have been used so far, may be appended if this task
     *        reference uses a finalzier
     * @param result the validation result to append to
     * @return the result of the validation
     */
    private AxValidationResult validateStateTaskReference(final AxArtifactKey taskKey,
                    final AxStateTaskReference taskReference, Set<String> stateOutputNameSet,
                    Set<String> stateFinalizerLogicNameSet, AxValidationResult result) {
        if (!taskReference.getKey().getParentReferenceKey().equals(key)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "stateTaskReference parent key " + taskReference.getKey().getId()
                                            + DOES_NOT_EQUAL_STATE_KEY));
        }

        if (taskReference.getStateTaskOutputType().equals(AxStateTaskOutputType.DIRECT)) {
            if (stateOutputs.containsKey(taskReference.getOutput().getLocalName())) {
                stateOutputNameSet.add(taskReference.getOutput().getLocalName());
            } else {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "state output for task " + taskKey + " not found in stateOutputs"));
            }
        } else if (taskReference.getStateTaskOutputType().equals(AxStateTaskOutputType.LOGIC)) {
            if (stateFinalizerLogicMap.containsKey(taskReference.getOutput().getLocalName())) {
                stateFinalizerLogicNameSet.add(taskReference.getOutput().getLocalName());
            } else {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                "state finalizer logic for task " + taskKey + " not found in stateFinalizerLogicMap"));
            }
        } else {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "stateTaskReference task output type " + taskReference.getStateTaskOutputType()
                                            + " is invalid"));
        }

        return taskReference.validate(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        trigger.clean();
        for (final AxStateOutput stateOutput : stateOutputs.values()) {
            stateOutput.clean();
        }
        for (final AxArtifactKey contextAlbumReference : contextAlbumReferenceSet) {
            contextAlbumReference.clean();
        }
        taskSelectionLogic.clean();
        for (final AxStateFinalizerLogic stateFinalizerLogic : stateFinalizerLogicMap.values()) {
            stateFinalizerLogic.clean();
        }
        defaultTask.clean();
        for (final AxStateTaskReference taskReference : taskReferenceMap.values()) {
            taskReference.clean();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("stateKey=");
        builder.append(key);
        builder.append(",trigger=");
        builder.append(trigger);
        builder.append(",stateOutputs=");
        builder.append(stateOutputs);
        builder.append(",contextAlbumReferenceSet=");
        builder.append(contextAlbumReferenceSet);
        builder.append(",taskSelectionLogic=");
        builder.append(taskSelectionLogic);
        builder.append(",stateFinalizerLogicSet=");
        builder.append(stateFinalizerLogicMap);
        builder.append(",defaultTask=");
        builder.append(defaultTask);
        builder.append(",taskReferenceMap=");
        builder.append(taskReferenceMap);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.
     * basicmodel.concepts.AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxState.class);

        final AxState copy = ((AxState) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setTrigger(new AxArtifactKey(trigger));

        final Map<String, AxStateOutput> newStateOutputs = new TreeMap<>();
        for (final Entry<String, AxStateOutput> stateOutputEntry : stateOutputs.entrySet()) {
            newStateOutputs.put(stateOutputEntry.getKey(), new AxStateOutput(stateOutputEntry.getValue()));
        }
        copy.setStateOutputs(newStateOutputs);

        final Set<AxArtifactKey> newContextUsage = new TreeSet<>();
        for (final AxArtifactKey contextAlbumReferenceItem : contextAlbumReferenceSet) {
            newContextUsage.add(new AxArtifactKey(contextAlbumReferenceItem));
        }
        copy.setContextAlbumReferences(newContextUsage);

        copy.setTaskSelectionLogic(new AxTaskSelectionLogic(taskSelectionLogic));

        final Map<String, AxStateFinalizerLogic> newStateFinalizerLogicMap = new TreeMap<>();
        for (final Entry<String, AxStateFinalizerLogic> stateFinalizerLogicEntry : stateFinalizerLogicMap.entrySet()) {
            newStateFinalizerLogicMap.put(stateFinalizerLogicEntry.getKey(),
                            new AxStateFinalizerLogic(stateFinalizerLogicEntry.getValue()));
        }
        copy.setStateFinalizerLogicMap(newStateFinalizerLogicMap);

        copy.setDefaultTask(new AxArtifactKey(defaultTask));

        final Map<AxArtifactKey, AxStateTaskReference> newTaskReferenceMap = new TreeMap<>();
        for (final Entry<AxArtifactKey, AxStateTaskReference> taskReferenceEntry : taskReferenceMap.entrySet()) {
            newTaskReferenceMap.put(new AxArtifactKey(taskReferenceEntry.getKey()),
                            new AxStateTaskReference(taskReferenceEntry.getValue()));
        }
        copy.setTaskReferences(newTaskReferenceMap);

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + trigger.hashCode();
        result = prime * result + stateOutputs.hashCode();
        result = prime * result + contextAlbumReferenceSet.hashCode();
        result = prime * result + taskSelectionLogic.hashCode();
        result = prime * result + stateFinalizerLogicMap.hashCode();
        result = prime * result + defaultTask.hashCode();
        result = prime * result + taskReferenceMap.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxState other = (AxState) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (!trigger.equals(other.trigger)) {
            return false;
        }
        if (!stateOutputs.equals(other.stateOutputs)) {
            return false;
        }
        if (!contextAlbumReferenceSet.equals(other.contextAlbumReferenceSet)) {
            return false;
        }
        if (!taskSelectionLogic.equals(other.taskSelectionLogic)) {
            return false;
        }
        if (!stateFinalizerLogicMap.equals(other.stateFinalizerLogicMap)) {
            return false;
        }
        if (!defaultTask.equals(other.defaultTask)) {
            return false;
        }
        return taskReferenceMap.equals(other.taskReferenceMap);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        if (otherObj == null) {
            return -1;
        }
        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        return compareObjectFields((AxState) otherObj);
    }

    /**
     * Compare the object fields on this state to another state.
     * 
     * @param the other state to compare with
     * @return the result of the comparison 
     */
    private int compareObjectFields(final AxState other) {
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!trigger.equals(other.trigger)) {
            return trigger.compareTo(other.trigger);
        }
        if (!stateOutputs.equals(other.stateOutputs)) {
            return stateOutputs.hashCode() - other.stateOutputs.hashCode();
        }
        if (!contextAlbumReferenceSet.equals(other.contextAlbumReferenceSet)) {
            return (contextAlbumReferenceSet.hashCode() - other.contextAlbumReferenceSet.hashCode());
        }
        if (!taskSelectionLogic.equals(other.taskSelectionLogic)) {
            return taskSelectionLogic.compareTo(other.taskSelectionLogic);
        }
        if (!stateFinalizerLogicMap.equals(other.stateFinalizerLogicMap)) {
            return stateFinalizerLogicMap.hashCode() - other.stateFinalizerLogicMap.hashCode();
        }
        if (!defaultTask.equals(other.defaultTask)) {
            return defaultTask.compareTo(other.defaultTask);
        }
        if (!taskReferenceMap.equals(other.taskReferenceMap)) {
            return (taskReferenceMap.hashCode() - other.taskReferenceMap.hashCode());
        }
        
        return 0;
    }
}
