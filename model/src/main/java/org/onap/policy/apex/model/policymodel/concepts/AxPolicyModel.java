/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import java.io.Serial;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * A container class for an Apex policy model. This class is a container class that allows an Apex
 * model to be constructed that contains definitions of all the context, event, and policy concepts
 * required to run policies in Apex. The model contains schema definitions, definitions of events
 * and context albums that use those schemas, definitions of tasks for policies and definitions of
 * the policies themselves.
 *
 * <p>An Apex policy model is an important artifact in Apex. At editing time, an Apex editor creates
 * and edits a policy model and a policy model is loaded into and is executed by an Apex engine.
 * Therefore, an Apex model and the set of policies that it holds is the way that the policy domain
 * that an Apex engine or a group of Apex engines executes across is expressed, both at design time
 * and run time. The Apex deployment system is responsible for deploying Apex models to and the
 * context they need the appropriate engines for execution.
 *
 * <p>Model registration is carried out by calling the {@code register()} method, which registers the
 * policy model and all its constituent containers with the model service. The containers for
 * context schemas, events, context albums, tasks, policies, and key information are all registered.
 *
 * <p>Once a policy model is composed, the overall structure of the policy model and all its references
 * can be validated. During validation of a policy model, the validation checks listed below are
 * executed:
 * <ol>
 * <li>The policy model is validated as an Apex model, which validates the existence, correctness,
 * and duplication of all keys in the model as well as validating the key information of the keys,
 * see validation in {@link AxModel}
 * <li>The schemas in the model must be valid, see validation in {@link AxContextSchemas}
 * <li>The context albums in the model must be valid, see validation in {@link AxContextAlbums}
 * <li>The tasks in the model must be valid, see validation in {@link AxTasks}
 * <li>The policies in the model must be valid, see validation in {@link AxPolicies}
 * <li>The events in the model must be valid, see validation in {@link AxEvents}
 * <li>The context schemas referred to in each field in every event must exist
 * <li>The context schemas referred to in every context album must exist
 * <li>The context schemas referred to in every task input field and output field must exist
 * <li>The context albums referred to in every task must exist
 * <li>The context albums referred to in every state must exist
 * <li>The trigger event referred to in every state must exist
 * <li>The default task referred to in every state must exist
 * <li>In a state, an event that triggers a task must contain all the input fields required by that
 * task
 * <li>In a state, an event that is emitted by a task must contain all the output fields produced by
 * that task
 * <li>All tasks referred to by a state must exist
 * <li>All events referred to on direct state outputs must exist
 * </ol>
 */
@Getter
public class AxPolicyModel extends AxModel {

    private static final String SCHEMAS_TOKEN = "_Schemas";
    private static final String KEY_INFO_TOKEN = "_KeyInfo";
    private static final String EVENTS_TOKEN = "_Events";
    private static final String ALBUMS_TOKEN = "_Albums";
    private static final String TASKS_TOKEN = "_Tasks";
    private static final String POLICIESS_TOKEN = "_Policies";

    private static final String DOES_NOT_EXIST = " does not exist";

    @Serial
    private static final long serialVersionUID = 8800599637708309945L;

    private AxPolicies policies;
    private AxTasks tasks;
    private AxEvents events;
    private AxContextAlbums albums;
    private AxContextSchemas schemas;

    /**
     * The Default Constructor creates a policy model with a null key and empty containers for
     * schemas, key information, events, context albums, tasks and policies.
     */
    public AxPolicyModel() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxPolicyModel(final AxPolicyModel copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates a policy model with the given key and empty containers for
     * schemas, key information, events, context albums, tasks and policies.
     *
     * @param key the key
     */
    public AxPolicyModel(final AxArtifactKey key) {
        this(key,
            new AxContextSchemas(new AxArtifactKey(key.getName() + SCHEMAS_TOKEN, key.getVersion())),
            new AxKeyInformation(new AxArtifactKey(key.getName() + KEY_INFO_TOKEN, key.getVersion())),
            new AxEvents(new AxArtifactKey(key.getName() + EVENTS_TOKEN, key.getVersion())),
            new AxContextAlbums(new AxArtifactKey(key.getName() + ALBUMS_TOKEN, key.getVersion())),
            new AxTasks(new AxArtifactKey(key.getName() + TASKS_TOKEN, key.getVersion())),
            new AxPolicies(new AxArtifactKey(key.getName() + POLICIESS_TOKEN, key.getVersion())));
    }

    /**
     * This Constructor creates a policy model with all of its fields specified.
     *
     * @param key            the key of the policy model
     * @param schemas        the context schema container for the policy model
     * @param keyInformation the key information container for the policy model
     * @param events         the event container for the policy model
     * @param albums         the context album container for the policy model
     * @param tasks          the task container for the policy model
     * @param policies       the policy container for the policy model
     */
    public AxPolicyModel(final AxArtifactKey key, final AxContextSchemas schemas,
                         final AxKeyInformation keyInformation, final AxEvents events, final AxContextAlbums albums,
                         final AxTasks tasks, final AxPolicies policies) {
        super(key, keyInformation);
        Assertions.argumentNotNull(schemas, "schemas may not be null");
        Assertions.argumentNotNull(events, "events may not be null");
        Assertions.argumentNotNull(albums, "albums may not be null");
        Assertions.argumentNotNull(tasks, "tasks may not be null");
        Assertions.argumentNotNull(policies, "policies may not be null");

        this.schemas = schemas;
        this.events = events;
        this.albums = albums;
        this.tasks = tasks;
        this.policies = policies;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void register() {
        super.register();
        ModelService.registerModel(AxContextSchemas.class, getSchemas());
        ModelService.registerModel(AxEvents.class, getEvents());
        ModelService.registerModel(AxContextAlbums.class, getAlbums());
        ModelService.registerModel(AxTasks.class, getTasks());
        ModelService.registerModel(AxPolicies.class, getPolicies());
        ModelService.registerModel(AxPolicyModel.class, this);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = super.getKeys();

        keyList.addAll(schemas.getKeys());
        keyList.addAll(events.getKeys());
        keyList.addAll(albums.getKeys());
        keyList.addAll(tasks.getKeys());
        keyList.addAll(policies.getKeys());

        return keyList;
    }

    /**
     * Gets a context model from the policy model.
     *
     * @return the context model
     */
    public AxContextModel getContextModel() {
        return new AxContextModel(new AxArtifactKey(albums.getKey().getName() + "_Model",
            albums.getKey().getVersion()), getSchemas(), getAlbums(), getKeyInformation());
    }

    /**
     * Sets the policy container for the policy model.
     *
     * @param policies the policy container with all the policies in the model
     */
    public void setPolicies(final AxPolicies policies) {
        Assertions.argumentNotNull(policies, "policies may not be null");
        this.policies = policies;
    }

    /**
     * Sets the task container from the policy model.
     *
     * @param tasks the task container with all the tasks in the model
     */
    public void setTasks(final AxTasks tasks) {
        Assertions.argumentNotNull(tasks, "tasks may not be null");
        this.tasks = tasks;
    }

    /**
     * Sets the event container from the policy model.
     *
     * @param events the event container with all the events in the model
     */
    public void setEvents(final AxEvents events) {
        Assertions.argumentNotNull(events, "events may not be null");
        this.events = events;
    }

    /**
     * Sets the context album container from the policy model.
     *
     * @param albums the context album container with all the context albums in the model
     */
    public void setAlbums(final AxContextAlbums albums) {
        Assertions.argumentNotNull(albums, "albums may not be null");
        this.albums = albums;
    }

    /**
     * Sets the context schema container from the policy model.
     *
     * @param schemas the context schema container with all the context schemas in the model
     */
    public void setSchemas(final AxContextSchemas schemas) {
        Assertions.argumentNotNull(schemas, "schemas may not be null");
        this.schemas = schemas;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        result = super.validate(result);
        result = schemas.validate(result);
        result = events.validate(result);
        result = albums.validate(result);
        result = tasks.validate(result);
        result = policies.validate(result);

        validateEventKeys(result);
        validateContextAlbumKeys(result);
        validateAllTaskKeys(result);
        validatePolicyKeys(result);

        return result;
    }

    /**
     * Validate all fundamental concepts keyed in events exist.
     *
     * @param result the validation result to return
     * @return the result
     */
    private AxValidationResult validateEventKeys(final AxValidationResult result) {
        for (final AxEvent event : events.getAll(null)) {
            for (final AxField field : event.getFields()) {
                if (getSchemas().get(field.getSchema()) == null) {
                    result.addValidationMessage(
                        new AxValidationMessage(event.getKey(), this.getClass(), ValidationResult.INVALID,
                            "event field data type " + field.getSchema().getId() + DOES_NOT_EXIST));
                }
            }
        }
        return result;
    }

    /**
     * Validate all fundamental concepts keyed in concept maps exist.
     *
     * @param result the validation result to return
     * @return the result
     */
    private AxValidationResult validateContextAlbumKeys(final AxValidationResult result) {
        for (final AxContextAlbum contextAlbum : albums.getAll(null)) {
            if (getSchemas().get(contextAlbum.getItemSchema()) == null) {
                result.addValidationMessage(
                    new AxValidationMessage(contextAlbum.getKey(), this.getClass(), ValidationResult.INVALID,
                        "context album schema " + contextAlbum.getItemSchema().getId() + DOES_NOT_EXIST));
            }
        }
        return result;
    }

    /**
     * Validate all fundamental concepts keyed in tasks exist.
     *
     * @param result the validation result to return
     * @return the result
     */
    private AxValidationResult validateAllTaskKeys(AxValidationResult result) {
        for (final AxTask task : tasks.getAll(null)) {
            validateTaskKeys(task, result);
        }
        return result;
    }

    /**
     * Validate all fundamental concepts keyed in tasks exist.
     *
     * @param task   The task to validate the keys of
     * @param result the validation result to return
     * @return the result
     */
    private AxValidationResult validateTaskKeys(final AxTask task, AxValidationResult result) {
        for (final AxArtifactKey contextAlbumKey : task.getContextAlbumReferences()) {
            if (albums.get(contextAlbumKey) == null) {
                result.addValidationMessage(new AxValidationMessage(task.getKey(), this.getClass(),
                    ValidationResult.INVALID, "task context album " + contextAlbumKey.getId() + DOES_NOT_EXIST));
            }
        }
        return result;
    }

    /**
     * Validate all fundamental concepts keyed in policies exist.
     *
     * @param result the validation result to return
     * @return the result
     */
    private AxValidationResult validatePolicyKeys(final AxValidationResult result) {
        for (final AxPolicy policy : policies.getAll(null)) {
            for (final AxState state : policy.getStateMap().values()) {
                validateStateReferences(state, result);
            }
        }

        return result;
    }

    /**
     * Validate that the references used on a state are valid.
     *
     * @param state  The state to check
     * @param result the validation result to append to
     */
    private void validateStateReferences(AxState state, AxValidationResult result) {
        for (final AxArtifactKey contextAlbumKey : state.getContextAlbumReferences()) {
            if (albums.get(contextAlbumKey) == null) {
                result.addValidationMessage(new AxValidationMessage(state.getKey(), this.getClass(),
                    ValidationResult.INVALID, "state context album " + contextAlbumKey.getId() + DOES_NOT_EXIST));
            }
        }

        final AxEvent triggerEvent = events.getEventMap().get(state.getTrigger());
        if (triggerEvent == null) {
            result.addValidationMessage(new AxValidationMessage(state.getKey(), this.getClass(),
                ValidationResult.INVALID, "state trigger event " + state.getTrigger().getId() + DOES_NOT_EXIST));
        }

        final AxTask defaultTask = tasks.getTaskMap().get(state.getDefaultTask());
        if (defaultTask == null) {
            result.addValidationMessage(new AxValidationMessage(state.getKey(), this.getClass(),
                ValidationResult.INVALID, "state default task " + state.getDefaultTask().getId() + DOES_NOT_EXIST));
        }

        for (final AxStateOutput stateOutput : state.getStateOutputs().values()) {
            if (events.getEventMap().get(stateOutput.getOutgoingEvent()) == null) {
                result.addValidationMessage(new AxValidationMessage(stateOutput.getKey(), this.getClass(),
                    ValidationResult.INVALID, "output event " + stateOutput.getOutgoingEvent().getId()
                    + " for state output " + stateOutput.getId() + DOES_NOT_EXIST));
            }
        }

        validateEventTaskFieldCompatibilityOnState(state, result);
    }

    /**
     * Validate that the fields on tasks and events that trigger them and are output by them are
     * compatible for all tasks used on a state.
     *
     * @param state  The state to check
     * @param result the validation result to append to
     */
    private void validateEventTaskFieldCompatibilityOnState(AxState state, AxValidationResult result) {
        // Check task output fields and event fields are compatible for tasks that directly
        // reference state outputs
        for (final Entry<AxArtifactKey, AxStateTaskReference> taskRefEntry : state.getTaskReferences().entrySet()) {
            if (!taskRefEntry.getValue().getStateTaskOutputType().equals(AxStateTaskOutputType.DIRECT)) {
                continue;
            }

            final AxTask usedTask = tasks.getTaskMap().get(taskRefEntry.getKey());
            if (usedTask == null) {
                result.addValidationMessage(new AxValidationMessage(state.getKey(), this.getClass(),
                    ValidationResult.INVALID, "state task " + taskRefEntry.getKey().getId() + DOES_NOT_EXIST));
            } else {
                AxStateOutput stateOutput =
                    state.getStateOutputs().get(taskRefEntry.getValue().getOutput().getKey().getLocalName());
                validateEventTaskFieldCompatibilityOnStateOutput(state, usedTask, stateOutput, result);
            }
        }
    }

    /**
     * Validate that the fields on a task of a state output and the events that trigger it are
     * compatible.
     *
     * @param state       The state to check
     * @param task        The task to check
     * @param stateOutput The state output to check
     * @param result      the validation result to append to
     */
    private void validateEventTaskFieldCompatibilityOnStateOutput(final AxState state, final AxTask task,
                                                                  final AxStateOutput stateOutput,
                                                                  AxValidationResult result) {
        if (stateOutput == null) {
            result.addValidationMessage(new AxValidationMessage(state.getKey(), this.getClass(),
                ValidationResult.INVALID, "state output on task reference for task " + task.getId() + " is null"));

        } else {
            final AxEvent usedEvent = events.getEventMap().get(stateOutput.getOutgoingEvent());
            if (usedEvent == null) {
                result.addValidationMessage(new AxValidationMessage(stateOutput.getKey(), this.getClass(),
                    ValidationResult.INVALID, "output event " + stateOutput.getOutgoingEvent().getId()
                    + " for state output " + stateOutput.getId() + DOES_NOT_EXIST));
            }
        }
    }

    /**
     * When a model is deserialized, if the albums field was missing a blank
     * with a null key was added. This method is called by JAXB after deserializing and is
     * used to insert an appropriate key
     */
    @Override
    public void buildReferences() {
        getSchemas().buildReferences();
        getEvents().buildReferences();
        getAlbums().buildReferences();
        getTasks().buildReferences();
        getPolicies().buildReferences();
        getKeyInformation().buildReferences();

        AxArtifactKey nullAlbumsKey = new AxArtifactKey(AxKey.NULL_KEY_NAME + ALBUMS_TOKEN, AxKey.NULL_KEY_VERSION);

        if (AxArtifactKey.getNullKey().equals(getAlbums().getKey())
            || nullAlbumsKey.equals(getAlbums().getKey())) {
            getAlbums().setKey(new AxArtifactKey(getKey().getName() + ALBUMS_TOKEN, getKey().getVersion()));
            getKeyInformation().generateKeyInfo(getAlbums());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        super.clean();
        policies.clean();
        tasks.clean();
        events.clean();
        albums.clean();
        schemas.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName()
            + ":(" + super.toString() + ",policies=" + policies + ",tasks=" + tasks + ",events=" + events + ",albums="
            + albums + ",schemas=" + schemas + ")";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        Assertions.instanceOf(targetObject, AxPolicyModel.class);

        final AxPolicyModel copy = ((AxPolicyModel) targetObject);
        super.copyTo(targetObject);
        copy.setPolicies(new AxPolicies(policies));
        copy.setTasks(new AxTasks(tasks));
        copy.setEvents(new AxEvents(events));
        copy.setAlbums(new AxContextAlbums(albums));
        copy.setSchemas(new AxContextSchemas(schemas));

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + policies.hashCode();
        result = prime * result + tasks.hashCode();
        result = prime * result + events.hashCode();
        result = prime * result + albums.hashCode();
        result = prime * result + schemas.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("comparison object may not be null");
        }

        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxPolicyModel other = (AxPolicyModel) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (!policies.equals(other.policies)) {
            return false;
        }
        if (!tasks.equals(other.tasks)) {
            return false;
        }
        if (!events.equals(other.events)) {
            return false;
        }
        if (!albums.equals(other.albums)) {
            return false;
        }
        return schemas.equals(other.schemas);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        Assertions.argumentNotNull(otherObj, "comparison object may not be null");

        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxPolicyModel other = (AxPolicyModel) otherObj;
        if (!super.equals(other)) {
            return super.compareTo(other);
        }
        if (!policies.equals(other.policies)) {
            return policies.compareTo(other.policies);
        }
        if (!tasks.equals(other.tasks)) {
            return tasks.compareTo(other.tasks);
        }
        if (!events.equals(other.events)) {
            return events.compareTo(other.events);
        }
        if (!albums.equals(other.albums)) {
            return albums.compareTo(other.albums);
        }
        return schemas.compareTo(other.schemas);
    }
}
