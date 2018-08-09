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

package org.onap.policy.apex.service.engine.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ApexEvent is an event class that external systems use to send events to and receive
 * events from Apex engines. The event itself is a hash map of string keys and object values, used
 * to pass data.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEvent extends HashMap<String, Object> implements Serializable {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEvent.class);

    private static final long serialVersionUID = -4451918242101961685L;

    // Holds the next identifier for event execution.
    private static AtomicLong nextExecutionID = new AtomicLong(0L);

    /**
     * The name of the Apex event, a mandatory field. All Apex events must have a name so that the
     * event can be looked up in the Apex policy model.
     */
    public static final String NAME_HEADER_FIELD = "name";

    /**
     * The version of the Apex event, an optional field. If a version is specified on an Apex event,
     * the definition of that version of the event is taken from the Apex policy model. If no
     * version is specified, the latest version of the event is used.
     */
    public static final String VERSION_HEADER_FIELD = "version";

    /**
     * The name space of the Apex event, an optional field. If a name space is specified on an Apex
     * event it must match the name space on the event definition taken from the Apex policy model.
     * If no name space is specified, the name space from the event definition in the Apex policy
     * model is used.
     */
    public static final String NAMESPACE_HEADER_FIELD = "nameSpace";

    /**
     * The source of the Apex event, an optional field. It specifies where the Apex event has come
     * from and its use is reserved for now. If no source is specified, the source from the event
     * definition in the Apex policy model is used.
     */
    public static final String SOURCE_HEADER_FIELD = "source";

    /**
     * The target of the Apex event, an optional field. It specifies where the Apex event is going
     * to and its use is reserved for now. If no target is specified, the target from the event
     * definition in the Apex policy model is used.
     */
    public static final String TARGET_HEADER_FIELD = "target";

    /**
     * The exception message field of an Apex event is an exception message indicating that an event
     * failed.
     */
    public static final String EXCEPTION_MESSAGE_HEADER_FIELD = "exceptionMessage";

    /** The name of an Apex event must match this regular expression. */
    public static final String NAME_REGEXP = "[A-Za-z0-9\\-_.]+";

    /** The version of an Apex event must match this regular expression. */
    public static final String VERSION_REGEXP = "[A-Za-z0-9.]+";

    /** The name space of an Apex event must match this regular expression. */
    public static final String NAMESPACE_REGEXP = "([a-zA_Z_][\\.\\w]*)";

    /** The source of an Apex event must match this regular expression. */
    public static final String SOURCE_REGEXP = "^$|[A-Za-z0-9\\.\\-_:]+";

    /** The target of an Apex event must match this regular expression. */
    public static final String TARGET_REGEXP = "^$|[A-Za-z0-9\\.\\-_:]+";

    // The fields of the event
    // @formatter:off
    private final String name;
    private final String version;
    private final String nameSpace;
    private final String source;
    private final String target;
    // @formatter:on

    // An identifier for the current event execution. The default value here will always be unique
    // in a single JVM
    private long executionID = ApexEvent.getNextExecutionID();

    // A string holding a message that indicates why processing of this event threw an exception
    private String exceptionMessage;

    /**
     * Private utility to get the next candidate value for a Execution ID. This value will always be
     * unique in a single JVM
     * 
     * @return the next candidate value for a Execution ID
     */
    private static synchronized long getNextExecutionID() {
        return nextExecutionID.getAndIncrement();
    }

    /**
     * Instantiates a new apex event.
     *
     * @param name the name of the event
     * @param version the version of the event
     * @param nameSpace the name space (java package) of the event
     * @param source the source of the event
     * @param target the target of the event
     * @throws ApexEventException thrown on validation errors on event names and versions
     */
    public ApexEvent(final String name, final String version, final String nameSpace, final String source,
            final String target) throws ApexEventException {
        // @formatter:off
        this.name      = validateField("name",      name,      NAME_REGEXP);
        this.version   = validateField("version",   version,   VERSION_REGEXP);
        this.nameSpace = validateField("nameSpace", nameSpace, NAMESPACE_REGEXP);
        this.source    = validateField("source",    source,    SOURCE_REGEXP);
        this.target    = validateField("target",    target,    TARGET_REGEXP);
        // @formatter:on
    }

    /**
     * Check that a field of the event is valid.
     *
     * @param fieldName the name of the field to check
     * @param fieldValue the value of the field to check
     * @param fieldRegexp the regular expression to check the field against
     * @return the validated field value
     * @throws ApexEventException thrown if the field is invalid
     */
    private String validateField(final String fieldName, final String fieldValue, final String fieldRegexp)
            throws ApexEventException {
        if (fieldValue.matches(fieldRegexp)) {
            return fieldValue;
        } else {
            LOGGER.warn("event \"" + name + ": field \"" + fieldName + "=" + fieldValue
                    + "\"  is illegal. It doesn't match regex '" + fieldRegexp + "'");
            throw new ApexEventException(
                    "event \"" + name + ": field \"" + fieldName + "=" + fieldValue + "\"  is illegal");
        }
    }

    /**
     * Check that the key of an event is valid.
     *
     * @param key the key
     * @return the string
     * @throws ApexEventException the apex event exception
     */
    private String validKey(final String key) throws ApexEventException {
        if (key.matches(AxReferenceKey.LOCAL_NAME_REGEXP)) {
            return key;
        } else {
            LOGGER.warn("event \"" + name + ": key \"" + key + "\" is illegal");
            throw new ApexEventException("event \"" + name + ": key \"" + key + "\" is illegal");
        }
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the name space.
     *
     * @return the name space
     */
    public String getNameSpace() {
        return nameSpace;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * Gets the pass-thru executionID for this event.
     *
     * @return the executionID
     */
    public long getExecutionID() {
        return executionID;
    }

    /**
     * Sets the pass-thru executionID for this event. The default value for executionID will be be
     * unique in the current JVM. For some applications/deployments this executionID may need to
     * globally unique
     *
     * @param executionID the executionID
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

    /*
     * Map overrides from here
     */

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(final String key, final Object value) {
        // Check if the key is valid
        try {
            return super.put(validKey(key), value);
        } catch (final ApexEventException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends String, ? extends Object> incomingMap) {
        // Check the keys are valid
        try {
            for (final String key : incomingMap.keySet()) {
                validKey(key);
            }
        } catch (final ApexEventException e) {
            // One of the keys is invalid
            return;
        }

        // Go ahead and put everything
        super.putAll(incomingMap);
    }

    /*
     * Object overrides from here
     */

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("name=");
        builder.append(name);
        builder.append(",version=");
        builder.append(version);
        builder.append(",nameSpace=");
        builder.append(nameSpace);
        builder.append(",source=");
        builder.append(source);
        builder.append(",target=");
        builder.append(target);
        builder.append(",executionID=");
        builder.append(executionID);
        builder.append(",exceptionMessage=");
        builder.append(exceptionMessage);
        builder.append(",");
        builder.append("[");

        boolean firstData = true;
        for (final Map.Entry<String, Object> dataEntry : this.entrySet()) {
            if (firstData) {
                firstData = false;
            } else {
                builder.append(',');
            }

            builder.append(dataEntry.getKey());
            builder.append('=');
            builder.append(dataEntry.getValue());
        }

        builder.append("]");
        return builder.toString();
    }
}
