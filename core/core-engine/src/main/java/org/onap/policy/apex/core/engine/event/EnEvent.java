/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

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
 * Instances of the Class EnEvent are events being passed through the Apex system. All events in the
 * system are instances of this class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EnEvent extends HashMap<String, Object> {
    private static final long serialVersionUID = 6311863111866294637L;

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EnEvent.class);

    // Repeasted string constants
    private static final String NULL_KEYS_ILLEGAL = "null keys are illegal on method parameter \"key\"";

    // The definition of this event in the Apex model
    private final AxEvent axEvent;

    // The event monitor for this event
    private final transient EventMonitor eventMonitor = new EventMonitor();

    // The stack of execution of this event, used for monitoring
    @Getter
    @Setter
    private AxConcept[] userArtifactStack;

    private static Random rand = new Random(System.nanoTime());

    // An identifier for the current event execution. The default value here will always be a random
    // number, and should
    // be reset
    @Getter
    @Setter
    private long executionId = rand.nextLong();

    // Event related properties used during processing of this event
    @Getter
    @Setter
    private Properties executionProperties = new Properties();

    // A string holding a message that indicates why processing of this event threw an exception
    @Getter
    @Setter
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

        if (axEvent == null) {
            throw new EnException("event definition is null or was not found in model service");
        }
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
    public String getId() {
        return axEvent.getKey().getId();
    }

    /**
     * {@inheritDoc}.
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
            String message = "parameter with key " + key + " not defined on this event";
            LOGGER.warn(message);
            throw new EnException(message);
        }

        // Get the item
        final Object item = super.get(key);

        // Get the parameter value and monitor it
        eventMonitor.monitorGet(eventParameter, item, userArtifactStack);
        return item;
    }

    /**
     * {@inheritDoc}.
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

    /**
     * {@inheritDoc}.
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object put(final String key, final Object incomingValue) {
        if (key == null) {
            String message = NULL_KEYS_ILLEGAL;
            LOGGER.warn(message);
            throw new EnException(message);
        }

        // Check if this key is a parameter on our event
        final AxField eventParameter = axEvent.getParameterMap().get(key);
        if (eventParameter == null) {
            String message = "parameter with key \"" + key + "\" not defined on event \"" + getName() + "\"";
            LOGGER.warn(message);
            throw new EnException(message);
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void putAll(final Map<? extends String, ? extends Object> incomingMap) {
        // Override the generic "putAll()" call as we want to monitor the puts
        for (final Map.Entry<? extends String, ? extends Object> incomingEntry : incomingMap.entrySet()) {
            put(incomingEntry.getKey(), incomingEntry.getValue());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object remove(final Object key) {
        if (key == null) {
            LOGGER.warn(NULL_KEYS_ILLEGAL);
            throw new EnException(NULL_KEYS_ILLEGAL);
        }

        // Check if this key is a parameter on our event
        final AxField eventParameter = axEvent.getParameterMap().get(key);
        if (eventParameter == null) {
            String message = "parameter with key " + key + " not defined on this event";
            LOGGER.warn(message);
            throw new EnException(message);
        }

        final Object removedValue = super.remove(key);
        eventMonitor.monitorRemove(eventParameter, removedValue, userArtifactStack);
        return removedValue;
    }

    /**
     * {@inheritDoc}.
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "EnEvent [axEvent=" + axEvent + ", userArtifactStack=" + Arrays.toString(userArtifactStack) + ", map="
                + super.toString() + "]";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + axEvent.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof EnEvent)) {
            return false;
        }
        EnEvent other = (EnEvent) obj;
        if (axEvent == null) {
            if (other.axEvent != null) {
                return false;
            }
        } else if (!axEvent.equals(other.axEvent)) {
            return false;
        }
        return true;
    }
}
