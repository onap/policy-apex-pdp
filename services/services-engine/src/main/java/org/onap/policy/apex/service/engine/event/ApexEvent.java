/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

import com.google.common.base.Strings;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.EnumUtils;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxToscaPolicyProcessingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ApexEvent is an event class that external systems use to send events to and receive
 * events from Apex engines. The event itself is a hash map of string keys and object values, used
 * to pass data.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ApexEvent extends HashMap<String, Object> implements Serializable {

    @Serial
    private static final long serialVersionUID = -4451918242101961685L;

    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexEvent.class);

    // Recurring string constants
    private static final String EVENT_PREAMBLE = "event \"";

    // Holds the next identifier for event execution.
    private static final AtomicLong nextExecutionID = new AtomicLong(0L);

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
     * The toscaPolicyState of Apex event, an optional field. It specifies the TOSCA Policy processing status
     * on an Apex event, with possible values as enumerated in
     * {@link org.onap.policy.apex.model.basicmodel.concepts.AxToscaPolicyProcessingStatus}.
     * If no toscaPolicyState is specified, it indicates that the TOSCA Policy is under process.
     */
    public static final String TOSCA_POLICY_STATE_HEADER_FIELD = "toscaPolicyState";

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
    public static final String TARGET_REGEXP = SOURCE_REGEXP;

    // The standard fields of the event
    private final String name;
    private final String version;
    private final String nameSpace;
    private final String source;
    private final String target;
    private String toscaPolicyState;

    // An identifier for the current event execution. The default value here will always be unique
    // in a single JVM
    @Setter
    private long executionId = ApexEvent.getNextExecutionId();

    // Event related properties used during processing of this event
    @Setter
    private Properties executionProperties = new Properties();

    // A string holding a message that indicates why processing of this event threw an exception
    @Setter
    private String exceptionMessage;

    /**
     * Instantiates a new apex event.
     *
     * @param name the name of the event
     * @param version the version of the event
     * @param nameSpace the name space (java package) of the event
     * @param source the source of the event
     * @param target the target of the event
     * @param toscaPolicyState Policy state
     * @throws ApexEventException thrown on validation errors on event names and versions
     */
    public ApexEvent(final String name, final String version, final String nameSpace, final String source,
            final String target, final String toscaPolicyState) throws ApexEventException {
        this.name = validateField(NAME_HEADER_FIELD, name, NAME_REGEXP);
        this.version = validateField(VERSION_HEADER_FIELD, version, VERSION_REGEXP);
        this.nameSpace = validateField(NAMESPACE_HEADER_FIELD, nameSpace, NAMESPACE_REGEXP);
        this.source = validateField(SOURCE_HEADER_FIELD, source, SOURCE_REGEXP);
        this.target = validateField(TARGET_HEADER_FIELD, target, TARGET_REGEXP);
        this.toscaPolicyState = validateField(TOSCA_POLICY_STATE_HEADER_FIELD, toscaPolicyState, "");
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
        this.name = validateField(NAME_HEADER_FIELD, name, NAME_REGEXP);
        this.version = validateField(VERSION_HEADER_FIELD, version, VERSION_REGEXP);
        this.nameSpace = validateField(NAMESPACE_HEADER_FIELD, nameSpace, NAMESPACE_REGEXP);
        this.source = validateField(SOURCE_HEADER_FIELD, source, SOURCE_REGEXP);
        this.target = validateField(TARGET_HEADER_FIELD, target, TARGET_REGEXP);
    }

    /**
     * Private utility to get the next candidate value for a Execution ID. This value will always be
     * unique in a single JVM
     *
     * @return the next candidate value for an Execution ID
     */
    private static synchronized long getNextExecutionId() {
        return nextExecutionID.getAndIncrement();
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
        if (fieldName.equals(TOSCA_POLICY_STATE_HEADER_FIELD) && !Strings.isNullOrEmpty(fieldValue)) {
            if (EnumUtils.isValidEnum(AxToscaPolicyProcessingStatus.class, fieldValue)) {
                return fieldValue;
            } else {
                String message = EVENT_PREAMBLE + name + ": field \"" + fieldName + "=" + fieldValue
                        + "\"  is illegal. It doesn't match supported values '"
                        + Arrays.asList(AxToscaPolicyProcessingStatus.values()) + "'";
                LOGGER.warn(message);
                throw new ApexEventException(message);
            }
        }
        if (fieldValue.matches(fieldRegexp)) {
            return fieldValue;
        } else {
            String message = EVENT_PREAMBLE + name + ": field \"" + fieldName + "=" + fieldValue
                    + "\"  is illegal. It doesn't match regex '" + fieldRegexp + "'";
            LOGGER.warn(message);
            throw new ApexEventException(message);
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
            String message = EVENT_PREAMBLE + name + ": key \"" + key + "\" is illegal";
            LOGGER.warn(message);
            throw new ApexEventException(message);
        }
    }

    /*
     * Map overrides from here
     */

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object put(final String key, final Object value) {
        // Check if the key is valid
        try {
            return super.put(validKey(key), value);
        } catch (final ApexEventException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("put failed", e);
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void putAll(final Map<? extends String, ?> incomingMap) {
        // Check the keys are valid
        try {
            for (final String key : incomingMap.keySet()) {
                validKey(key);
            }
        } catch (final ApexEventException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("putAll failed", e);
            }

            // One of the keys is invalid
            return;
        }

        // Go ahead and put everything
        super.putAll(incomingMap);
    }
}