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

package org.onap.policy.apex.core.engine.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.onap.policy.apex.core.engine.monitoring.EventMonitor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Instances of the Class EnEvent are events being passed through the Apex system. All events in the system are
 * instances of this class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EnEvent extends HashMap<String, Object> {
    private static final long serialVersionUID = 6311863111866294637L;

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EnEvent.class);

    // The definition of this event in the Apex model
    private final AxEvent axEvent;

    // The event monitor for this event
    private final EventMonitor eventMonitor = new EventMonitor();

    // The stack of execution of this event, used for monitoring
    private AxConcept[] userArtifactStack;

    private static Random rand = new Random(System.nanoTime());

    // An identifier for the current event execution. The default value here will always be a random number, and should
    // be reset
    private long executionID = rand.nextLong();

    // A string holding a message that indicates why processing of this event threw an exception
    private String exceptionMessage;

    /**
     * Instantiates a new EnEvent, an Engine Event.
     *
     * @param eventKey the key of the event definition from the Apex model
     */
    public EnEvent(final AxArtifactKey eventKey) {
        this(ModelService.getModel(AxEvents.class).get(eventKey));
    }

    /**
     * Instantiates a new EnEvent, an Engine Event.
     *
     * @param axEvent the event definition from the Apex model
     */
    public EnEvent(final AxEvent axEvent) {
        super();
        // Save the event definition from the Apex model
        this.axEvent = axEvent;
    }

    /**
     * Gets the event definition of this event.
     *
     * @return the event definition
     */
    public AxEvent getAxEvent() {
        return axEvent;
    }

    /**
     * Get the name of the event.
     *
     * @return the event name
     */
    public String getName() {
        return axEvent.getKey().getName();
    }

    /**
     * Get the key of the event.
     *
     * @return the event key
     */
    public AxArtifactKey getKey() {
        return axEvent.getKey();
    }

    /**
     * Get the ID of the event.
     *
     * @return the event key
     */
    public String getID() {
        return axEvent.getKey().getID();
    }

    /**
     * Get the currently set value for the ExecutionID for this event. A ExecutionID in an EnEvent is used identify all
     * EnEvents (input, internal and output events) used in a single Engine invocation. Therefore, a ExecutionID can be
     * used to match which output event is the result of a particular input event. The default initialized value for the
     * ExecutionID is always unique in a single JVM.
     *
     * @return the currently set value for the ExecutionID for this event.
     */
    public long getExecutionID() {
        return executionID;
    }

    /**
     * Set the value for the ExecutionID for this event. A ExecutionID in an EnEvent is used identify all EnEvents
     * (input, internal and output events) used in a single Engine invocation. Therefore, a ExecutionID can be used to
     * match which output event is the result of a particular input event. The default initialised value for the
     * ExecutionID is always unique in a single JVM.
     *
     * @param executionID the new value for the ExecutionID for this event.
     */
    public void setExecutionID(final long executionID) {
        this.executionID = executionID;
    }

    /**
     * Gets the exception message explaining why processing of this event to fail.
     *
     * @return the exception message
     */
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    /**
     * Sets the exception message explaining why processing of this event to fail.
     *
     * @param exceptionMessage the exception message
     */
    public void setExceptionMessage(final String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * Store the user artifact stack of the event.
     *
     * @param usedArtifactStackArray the event user artifact stack
     */
    public void setUserArtifactStack(final AxConcept[] usedArtifactStackArray) {
        userArtifactStack = usedArtifactStackArray;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public Object get(final Object key) {
        if (key == null) {
            LOGGER.warn("null values are illegal on method parameter \"key\"");
            throw new EnException("null values are illegal on method parameter \"key\"");
        }

        // Check if this key is a parameter on our event
        final AxField eventParameter = axEvent.getParameterMap().get(key);
        if (eventParameter == null) {
            LOGGER.warn("parameter with key " + key + " not defined on this event");
            throw new EnException("parameter with key " + key + " not defined on this event");
        }

        // Get the item
        final Object item = super.get(key);

        // Get the parameter value and monitor it
        eventMonitor.monitorGet(eventParameter, item, userArtifactStack);
        return item;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#values()
     */
    @Override
    public Collection<Object> values() {
        // Build the key set and return it
        final ArrayList<Object> valueList = new ArrayList<>();

        // Override the generic "values()" call as we want to monitor the gets
        for (final String key : super.keySet()) {
            valueList.add(this.get(key));
        }

        return valueList;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        // Build the entry set and return it
        final Set<Map.Entry<String, Object>> entrySet = new HashSet<>();

        // Override the generic "entrySet()" call as we want to monitor the gets
        for (final String key : super.keySet()) {
            entrySet.add(new SimpleEntry<>(key, this.get(key)));
        }

        return entrySet;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(final String key, final Object incomingValue) {
        if (key == null) {
            LOGGER.warn("null keys are illegal on method parameter \"key\"");
            throw new EnException("null keys are illegal on method parameter \"key\"");
        }

        // Check if this key is a parameter on our event
        final AxField eventParameter = axEvent.getParameterMap().get(key);
        if (eventParameter == null) {
            LOGGER.warn("parameter with key \"" + key + "\" not defined on event \"" + getName() + "\"");
            throw new EnException("parameter with key \"" + key + "\" not defined on event \"" + getName() + "\"");
        }

        // We allow null values
        if (incomingValue == null) {
            eventMonitor.monitorSet(eventParameter, incomingValue, userArtifactStack);
            return super.put(key, incomingValue);
        }

        // Holder for the object to assign
        final Object valueToAssign = new EnField(eventParameter, incomingValue).getAssignableValue();

        // Update the value in the parameter map
        eventMonitor.monitorSet(eventParameter, valueToAssign, userArtifactStack);
        return super.put(key, valueToAssign);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends String, ? extends Object> incomingMap) {
        // Override the generic "putAll()" call as we want to monitor the puts
        for (final java.util.Map.Entry<? extends String, ? extends Object> incomingEntry : incomingMap.entrySet()) {
            put(incomingEntry.getKey(), incomingEntry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public Object remove(final Object key) {
        if (key == null) {
            LOGGER.warn("null keys are illegal on method parameter \"key\"");
            throw new EnException("null keys are illegal on method parameter \"key\"");
        }

        // Check if this key is a parameter on our event
        final AxField eventParameter = axEvent.getParameterMap().get(key);
        if (eventParameter == null) {
            LOGGER.warn("parameter with key " + key + " not defined on this event");
            throw new EnException("parameter with key " + key + " not defined on this event");
        }

        final Object removedValue = super.remove(key);
        eventMonitor.monitorRemove(eventParameter, removedValue, userArtifactStack);
        return removedValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        // Override the generic "clear()" call as we want to monitor removals
        final Set<String> deleteSet = new HashSet<>();
        deleteSet.addAll(keySet());

        for (final String deleteKey : deleteSet) {
            this.remove(deleteKey);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#toString()
     */
    @Override
    public String toString() {
        return "EnEvent [axEvent=" + axEvent + ", userArtifactStack=" + Arrays.toString(userArtifactStack) + ", map="
                + super.toString() + "]";
    }
}
