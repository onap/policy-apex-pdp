/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.core.engine.executor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Getter;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class is the output of a state, and is used by the engine to decide what the next state for execution is.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
public class StateOutput {
    // The state output has a state and an event
    private final AxStateOutput stateOutputDefinition;
    private AxEvent outputEventDef;
    private final Map<AxArtifactKey, EnEvent> outputEvents;

    /**
     * Create a new state output from a state output definition.
     *
     * @param axStateOutput the state output definition
     */
    public StateOutput(final AxStateOutput axStateOutput) {
        this(axStateOutput, new EnEvent(axStateOutput.getOutgoingEvent()));
    }

    /**
     * Create a new state output with the given definition and event key.
     *
     * @param stateOutputDefinition the state output definition
     * @param outputEvent the output event
     */
    public StateOutput(final AxStateOutput stateOutputDefinition, final EnEvent outputEvent) {
        Assertions.argumentNotNull(stateOutputDefinition, "stateOutputDefinition may not be null");
        Assertions.argumentNotNull(outputEvent, "outputEvent may not be null");

        this.stateOutputDefinition = stateOutputDefinition;
        this.outputEvents = new TreeMap<>();
        if (stateOutputDefinition.getOutgoingEventSet() != null
            && !stateOutputDefinition.getOutgoingEventSet().isEmpty()) {
            stateOutputDefinition.getOutgoingEventSet()
                .forEach(outEvent -> outputEvents.put(outEvent, new EnEvent(outEvent)));
        } else {
            outputEvents.put(outputEvent.getKey(), outputEvent);
        }
        outputEventDef = ModelService.getModel(AxEvents.class).get(stateOutputDefinition.getOutgoingEvent());
    }

    /**
     * Gets the next state.
     *
     * @return the next state
     */
    public AxReferenceKey getNextState() {
        return stateOutputDefinition.getNextState();
    }

    /**
     * Transfer the fields from the incoming field map into the event.
     *
     * @param incomingEventDefinitionMap definitions of the incoming fields
     * @param eventFieldMaps the event field map
     * @throws StateMachineException on errors populating the event fields
     */
    public void setEventFields(final Map<String, AxEvent> incomingEventDefinitionMap,
        final Map<String, Map<String, Object>> eventFieldMaps) throws StateMachineException {
        Assertions.argumentNotNull(incomingEventDefinitionMap, "incomingFieldDefinitionMap may not be null");
        Assertions.argumentNotNull(eventFieldMaps, "eventFieldMaps may not be null");

        for (Entry<String, AxEvent> incomingEventDefinitionEntry : incomingEventDefinitionMap.entrySet()) {
            String eventName = incomingEventDefinitionEntry.getKey();
            AxEvent eventDef = incomingEventDefinitionEntry.getValue();
            if (!eventDef.getParameterMap().keySet().equals(eventFieldMaps.get(eventName).keySet())) {
                throw new StateMachineException(
                    "field definitions and values do not match for event " + eventDef.getId() + '\n'
                        + eventDef.getParameterMap().keySet() + '\n' + eventFieldMaps.get(eventName).keySet());
            }
        }
        var updateOnceFlag = false;
        if (!outputEvents.keySet().stream().map(AxArtifactKey::getName).collect(Collectors.toSet())
            .equals(eventFieldMaps.keySet())) {
            // when same task is used by multiple policies with different eventName but same fields,
            // state outputs and task output events may be different
            // in this case, update the output fields in the state output only once to avoid overwriting.
            updateOnceFlag = true;
        }
        for (Entry<String, Map<String, Object>> eventFieldMapEntry : eventFieldMaps.entrySet()) {
            String eventName = eventFieldMapEntry.getKey();
            Map<String, Object> outputEventFields = eventFieldMapEntry.getValue();
            AxEvent taskOutputEvent = incomingEventDefinitionMap.get(eventName);
            EnEvent outputEventToUpdate = outputEvents.get(taskOutputEvent.getKey());

            if (null == outputEventToUpdate) {
                // happens only when same task is used by multiple policies with different eventName but same fields
                // in this case, just match the fields and get the event in the stateOutput
                Optional<EnEvent> outputEventOpt = outputEvents.values().stream().filter(outputEvent -> outputEvent
                    .getAxEvent().getParameterMap().keySet().equals(outputEventFields.keySet())).findFirst();
                if (outputEventOpt.isEmpty()) {
                    throw new StateMachineException(
                        "Task output event field definition and state output event field doesn't match");
                } else {
                    outputEventToUpdate = outputEventOpt.get();
                }
            }
            updateOutputEventFields(taskOutputEvent, outputEventFields, outputEventToUpdate);
            if (updateOnceFlag) {
                break;
            }
        }
    }

    private void updateOutputEventFields(AxEvent taskOutputEvent, Map<String, Object> outputEventFields,
        EnEvent outputEventToUpdate) throws StateMachineException {
        for (Entry<String, Object> outputEventFieldEntry : outputEventFields.entrySet()) {
            String fieldName = outputEventFieldEntry.getKey();
            Object fieldValue = outputEventFieldEntry.getValue();
            final AxField fieldDef = taskOutputEvent.getParameterMap().get(fieldName);

            Set<AxArtifactKey> outgoingEventSet = new TreeSet<>();
            if (null == stateOutputDefinition.getOutgoingEventSet()
                || stateOutputDefinition.getOutgoingEventSet().isEmpty()) {
                // if current state is not the final state, then the set could be empty.
                // Just take the outgoingEvent field in this case
                outgoingEventSet.add(stateOutputDefinition.getOutgoingEvent());
            } else {
                outgoingEventSet.addAll(stateOutputDefinition.getOutgoingEventSet());
            }
            // Check if this field is a field in the event
            for (AxArtifactKey outputEventKey : outgoingEventSet) {
                if (outputEventKey.equals(taskOutputEvent.getKey())) {
                    outputEventDef = ModelService.getModel(AxEvents.class).get(outputEventKey);
                    // Check if this field is a field in the state output event
                    if (!outputEventDef.getFields().contains(fieldDef)) {
                        throw new StateMachineException(
                            "field \"" + fieldName + "\" does not exist on event \"" + outputEventDef.getId() + "\"");
                    }
                }
            }
            // Set the value in the correct output event
            outputEventToUpdate.put(fieldName, fieldValue);
        }
    }

    /**
     * This method copies any fields that exist on the input event that also exist on the output event if they are not
     * set on the output event.
     *
     * @param incomingEvent The incoming event to copy from
     */
    public void copyUnsetFields(final EnEvent incomingEvent) {
        Assertions.argumentNotNull(incomingEvent, "incomingEvent may not be null");
        Set<AxArtifactKey> outgoingEventSet = new TreeSet<>();
        if (null == stateOutputDefinition.getOutgoingEventSet()
            || stateOutputDefinition.getOutgoingEventSet().isEmpty()) {
            // if current state is not the final state, then the set could be empty.
            // Just take the outgoingEvent field in this case
            outgoingEventSet.add(stateOutputDefinition.getOutgoingEvent());
        } else {
            outgoingEventSet.addAll(stateOutputDefinition.getOutgoingEventSet());
        }
        incomingEvent.forEach((inFieldName, inFieldValue) -> {
            for (AxArtifactKey outputEventKey : outgoingEventSet) {
                outputEventDef = ModelService.getModel(AxEvents.class).get(outputEventKey);
                // Check if the field exists on the outgoing event
                if (!outputEventDef.getParameterMap().containsKey(inFieldName)
                    // Check if the field is set in the outgoing event
                    || outputEvents.get(outputEventKey).containsKey(inFieldName)
                    // Now, check the fields have the same type
                    || !incomingEvent.getAxEvent().getParameterMap().get(inFieldName)
                        .equals(outputEvents.get(outputEventKey).getAxEvent().getParameterMap().get(inFieldName))) {
                    continue;
                }
                // All checks done, we can copy the value
                outputEvents.get(outputEventKey).put(inFieldName, inFieldValue);
            }
        });
    }
}
