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

package org.onap.policy.apex.core.engine.executor;

import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class is the output of a state, and is used by the engine to decide what the next state for execution is.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class StateOutput {
    // The state output has a state and an event
    private final AxStateOutput stateOutputDefinition;
    private final AxEvent outputEventDef;
    private final EnEvent outputEvent;

    /**
     * Create a new state output from a state output definition.
     *
     * @param axStateOutput the state output definition
     */
    public StateOutput(final AxStateOutput axStateOutput) {
        this(axStateOutput, new EnEvent(axStateOutput.getOutgingEvent()));
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
        this.outputEvent = outputEvent;
        outputEventDef = ModelService.getModel(AxEvents.class).get(stateOutputDefinition.getOutgingEvent());
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
     * Gets the state output definition.
     *
     * @return the state output definition
     */
    public AxStateOutput getStateOutputDefinition() {
        return stateOutputDefinition;
    }

    /**
     * Gets the output event.
     *
     * @return the output event
     */
    public EnEvent getOutputEvent() {
        return outputEvent;
    }

    /**
     * Transfer the fields from the incoming field map into the event.
     *
     * @param incomingFieldDefinitionMap definitions of the incoming fields
     * @param eventFieldMap the event field map
     * @throws StateMachineException on errors populating the event fields
     */
    public void setEventFields(final Map<String, AxField> incomingFieldDefinitionMap,
                    final Map<String, Object> eventFieldMap) throws StateMachineException {
        Assertions.argumentNotNull(incomingFieldDefinitionMap, "incomingFieldDefinitionMap may not be null");
        Assertions.argumentNotNull(eventFieldMap, "eventFieldMap may not be null");

        if (!incomingFieldDefinitionMap.keySet().equals(eventFieldMap.keySet())) {
            throw new StateMachineException(
                            "field definitions and values do not match for event " + outputEventDef.getId() + '\n'
                                            + incomingFieldDefinitionMap.keySet() + '\n' + eventFieldMap.keySet());
        }
        for (final Entry<String, Object> incomingFieldEntry : eventFieldMap.entrySet()) {
            final String fieldName = incomingFieldEntry.getKey();
            final AxField fieldDef = incomingFieldDefinitionMap.get(fieldName);

            // Check if this field is a field in the event
            if (!outputEventDef.getFields().contains(fieldDef)) {
                throw new StateMachineException("field \"" + fieldName + "\" does not exist on event \""
                                + outputEventDef.getId() + "\"");
            }

            // Set the value in the output event
            outputEvent.put(fieldName, incomingFieldEntry.getValue());
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

        for (final Entry<String, Object> incomingField : incomingEvent.entrySet()) {
            final String fieldName = incomingField.getKey();

            // Check if the field exists on the outgoing event
            if ((!outputEventDef.getParameterMap().containsKey(fieldName))

                            // Check if the field is set on the outgoing event
                            || (outputEvent.containsKey(fieldName))

                            // Now, check the fields have the same type
                            || (!incomingEvent.getAxEvent().getParameterMap().get(fieldName)
                                            .equals(outputEvent.getAxEvent().getParameterMap().get(fieldName)))) {
                continue;
            }

            // All checks done, we can copy the value
            outputEvent.put(fieldName, incomingField.getValue());
        }

    }
}
