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

package org.onap.policy.apex.service.parameters.eventprotocol;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin.ApexEventProtocolParameters;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.common.parameters.ParameterRuntimeException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class serialises and deserialises various type of event protocol parameters to and from
 * JSON.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventProtocolParametersJsonAdapter
        implements JsonSerializer<EventProtocolParameters>, JsonDeserializer<EventProtocolParameters> {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventProtocolParametersJsonAdapter.class);

    // Recurring string constants
    private static final String EVENT_PROTOCOL_PREFIX = "event protocol \"";
    private static final String VALUE_TAG = "\" value \"";

    private static final String PARAMETER_CLASS_NAME = "parameterClassName";

    private static final String EVENT_PROTOCOL_TOKEN = "eventProtocol";
    private static final String EVENT_PROTOCOL_PARAMETERS = "parameters";

    // Built in event protocol parameters
    private static final Map<String, String> BUILT_IN_EVENT_PROTOCOL_PARMETER_CLASS_MAP = new HashMap<>();
    
    static {
        BUILT_IN_EVENT_PROTOCOL_PARMETER_CLASS_MAP.put("JSON", JsonEventProtocolParameters.class.getCanonicalName());
        BUILT_IN_EVENT_PROTOCOL_PARMETER_CLASS_MAP.put("APEX", ApexEventProtocolParameters.class.getCanonicalName());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
     * com.google.gson.JsonSerializationContext)
     */
    @Override
    public JsonElement serialize(final EventProtocolParameters src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final String returnMessage = "serialization of Apex event protocol parameters to Json is not supported";
        LOGGER.error(returnMessage);
        throw new ParameterRuntimeException(returnMessage);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    @Override
    public EventProtocolParameters deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        final JsonObject jsonObject = json.getAsJsonObject();

        // Get the event protocol label primitive
        final JsonPrimitive labelJsonPrimitive = (JsonPrimitive) jsonObject.get(EVENT_PROTOCOL_TOKEN);

        // Check if we found our event protocol
        if (labelJsonPrimitive == null) {
            LOGGER.warn("event protocol parameter \"" + EVENT_PROTOCOL_TOKEN + "\" not found in JSON file");
            return null;
        }

        // Get and check the event protocol label
        final String eventProtocolLabel = labelJsonPrimitive.getAsString().replaceAll("\\s+", "");
        if (eventProtocolLabel == null || eventProtocolLabel.length() == 0) {
            final String errorMessage = "event protocol parameter \"" + EVENT_PROTOCOL_TOKEN + VALUE_TAG
                    + labelJsonPrimitive.getAsString() + "\" invalid in JSON file";
            LOGGER.warn(errorMessage);
            throw new ParameterRuntimeException(errorMessage);
        }

        // We now get the event protocol parameter class
        String eventProtocolParameterClassName = null;

        // Get the event protocol parameter class for the event protocol plugin class from the
        // configuration parameters
        final JsonPrimitive classNameJsonPrimitive = (JsonPrimitive) jsonObject.get(PARAMETER_CLASS_NAME);

        // If no event protocol parameter class was specified, we use the default
        if (classNameJsonPrimitive == null) {
            eventProtocolParameterClassName = BUILT_IN_EVENT_PROTOCOL_PARMETER_CLASS_MAP.get(eventProtocolLabel);
        } else {
            // We use the specified one
            eventProtocolParameterClassName = classNameJsonPrimitive.getAsString().replaceAll("\\s+", "");
        }

        // Check the event protocol parameter class
        if (eventProtocolParameterClassName == null || eventProtocolParameterClassName.length() == 0) {
            final String errorMessage =
                    EVENT_PROTOCOL_PREFIX + eventProtocolLabel + "\" parameter \"" + PARAMETER_CLASS_NAME + VALUE_TAG
                            + (classNameJsonPrimitive != null ? classNameJsonPrimitive.getAsString() : "null")
                            + "\" invalid in JSON file";
            LOGGER.warn(errorMessage);
            throw new ParameterRuntimeException(errorMessage);
        }

        // Get the class for the event protocol
        Class<?> eventProtocolParameterClass = null;
        try {
            eventProtocolParameterClass = Class.forName(eventProtocolParameterClassName);
        } catch (final ClassNotFoundException e) {
            final String errorMessage =
                    EVENT_PROTOCOL_PREFIX + eventProtocolLabel + "\" parameter \"" + PARAMETER_CLASS_NAME + VALUE_TAG
                            + eventProtocolParameterClassName + "\", could not find class";
            LOGGER.warn(errorMessage, e);
            throw new ParameterRuntimeException(errorMessage, e);
        }

        // Deserialise the class
        EventProtocolParameters eventProtocolParameters =
                context.deserialize(jsonObject.get(EVENT_PROTOCOL_PARAMETERS), eventProtocolParameterClass);
        if (eventProtocolParameters == null) {
            // OK no parameters for the event protocol have been specified, just instantiate the
            // default parameters
            try {
                eventProtocolParameters = (EventProtocolParameters) eventProtocolParameterClass.newInstance();
            } catch (final Exception e) {
                final String errorMessage = "could not create default parameters for event protocol \""
                        + eventProtocolLabel + "\"\n" + e.getMessage();
                LOGGER.warn(errorMessage, e);
                throw new ParameterRuntimeException(errorMessage, e);
            }
        }

        // Check that the event protocol label matches the label in the event protocol parameters
        // object
        if (!eventProtocolParameters.getLabel().equals(eventProtocolLabel)) {
            final String errorMessage = EVENT_PROTOCOL_PREFIX + eventProtocolLabel + "\" does not match plugin \""
                    + eventProtocolParameters.getLabel() + "\" in \"" + eventProtocolParameterClassName
                    + "\", specify correct event protocol parameter plugin in parameter \"" + PARAMETER_CLASS_NAME
                    + "\"";
            LOGGER.warn(errorMessage);
            throw new ParameterRuntimeException(errorMessage);
        }

        return eventProtocolParameters;
    }
}
