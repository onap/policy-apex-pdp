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

package org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class Apex2JSONEventConverter converts {@link ApexEvent} instances to and from JSON string representations of
 * Apex events.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class Apex2JsonEventConverter implements ApexEventProtocolConverter {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(Apex2JsonEventConverter.class);

    // Recurring string constants
    private static final String ERROR_PARSING = "error parsing ";
    private static final String ERROR_CODING = "error coding ";

    // The parameters for the JSON event protocol
    private JsonEventProtocolParameters jsonPars;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final EventProtocolParameters parameters) {
        // Check and get the JSON parameters
        if (!(parameters instanceof JsonEventProtocolParameters)) {
            final String errorMessage = "specified consumer properties are not applicable to the JSON event protocol";
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }

        jsonPars = (JsonEventProtocolParameters) parameters;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<ApexEvent> toApexEvent(final String eventName, final Object eventObject) throws ApexEventException {
        // Check the event eventObject
        if (eventObject == null) {
            LOGGER.warn("event processing failed, event is null");
            throw new ApexEventException("event processing failed, event is null");
        }

        // Cast the event to a string, if our conversion is correctly configured, this cast should
        // always work
        String jsonEventString = null;
        try {
            jsonEventString = (String) eventObject;
        } catch (final Exception e) {
            final String errorMessage = "error converting event \"" + eventObject + "\" to a string";
            LOGGER.debug(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }

        // The list of events we will return
        final List<ApexEvent> eventList = new ArrayList<>();

        try {
            // We may have a single JSON object with a single event or an array of JSON objects
            final Object decodedJsonObject = new GsonBuilder().serializeNulls().create().fromJson(jsonEventString,
                            Object.class);

            // Check if we have a list of objects
            if (decodedJsonObject instanceof List) {
                eventList.addAll(decodeEventList(eventName, jsonEventString, decodedJsonObject));
            } else {
                eventList.add(jsonStringApexEvent(eventName, jsonEventString));
            }
        } catch (final Exception e) {
            final String errorString = "Failed to unmarshal JSON event: " + e.getMessage() + ", event="
                            + jsonEventString;
            LOGGER.warn(errorString, e);
            throw new ApexEventException(errorString, e);
        }

        // Return the list of events we have unmarshalled
        return eventList;
    }

    /**
     * Decode a list of Apex events.
     *
     * @param eventName the name of the incoming events
     * @param jsonEventString the JSON representation of the event list
     * @param decodedJsonObject The JSON list object
     * @return a list of decoded Apex events
     * @throws ApexEventException on event decoding errors
     */
    private Collection<? extends ApexEvent> decodeEventList(final String eventName, String jsonEventString,
                    final Object decodedJsonObject) throws ApexEventException {

        final List<ApexEvent> eventList = new ArrayList<>();

        // Check if it's a list of JSON objects or a list of strings
        @SuppressWarnings("unchecked")
        final List<Object> decodedJsonList = (List<Object>) decodedJsonObject;

        // Decode each of the list elements in sequence
        for (final Object jsonListObject : decodedJsonList) {
            if (jsonListObject instanceof String) {
                eventList.add(jsonStringApexEvent(eventName, (String) jsonListObject));
            } else if (jsonListObject instanceof JsonObject) {
                eventList.add(jsonObject2ApexEvent(eventName, (JsonObject) jsonListObject));
            } else if (jsonListObject instanceof LinkedTreeMap) {
                eventList.add(jsonObject2ApexEvent(eventName, new Gson().toJsonTree(jsonListObject).getAsJsonObject()));
            } else {
                throw new ApexEventException("incoming event (" + jsonEventString
                                + ") is a JSON object array containing an invalid object " + jsonListObject);
            }
        }

        return eventList;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object fromApexEvent(final ApexEvent apexEvent) throws ApexEventException {
        // Check the Apex event
        if (apexEvent == null) {
            LOGGER.warn("event processing failed, Apex event is null");
            throw new ApexEventException("event processing failed, Apex event is null");
        }

        if (jsonPars.getPojoField() == null) {
            return fromApexEventWithFields(apexEvent);
        } else {
            return fromApexEventPojo(apexEvent);
        }
    }

    /**
     * Serialise an Apex event to a JSON string field by field.
     *
     * @param apexEvent the event to Serialise
     * @return the Serialise event as JSON
     * @throws ApexEventException exceptions on marshaling to JSON
     */
    private Object fromApexEventWithFields(final ApexEvent apexEvent) {
        // Get the event definition for the event from the model service
        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get(apexEvent.getName(),
                        apexEvent.getVersion());

        // Use a GSON Json object to marshal the Apex event to JSON
        final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(ApexEvent.NAME_HEADER_FIELD, apexEvent.getName());
        jsonObject.addProperty(ApexEvent.VERSION_HEADER_FIELD, apexEvent.getVersion());
        jsonObject.addProperty(ApexEvent.NAMESPACE_HEADER_FIELD, apexEvent.getNameSpace());
        jsonObject.addProperty(ApexEvent.SOURCE_HEADER_FIELD, apexEvent.getSource());
        jsonObject.addProperty(ApexEvent.TARGET_HEADER_FIELD, apexEvent.getTarget());

        if (apexEvent.getExceptionMessage() != null) {
            jsonObject.addProperty(ApexEvent.EXCEPTION_MESSAGE_HEADER_FIELD, apexEvent.getExceptionMessage());
        }

        for (final AxField eventField : eventDefinition.getFields()) {
            final String fieldName = eventField.getKey().getLocalName();

            if (!apexEvent.containsKey(fieldName)) {
                if (!eventField.getOptional()) {
                    final String errorMessage = ERROR_CODING + eventDefinition.getId() + " event to Json. " + "Field \""
                                    + fieldName + "\" is missing, but is mandatory. Fields: " + apexEvent;
                    LOGGER.debug(errorMessage);
                    throw new ApexEventRuntimeException(errorMessage);
                }
                continue;
            }

            final Object fieldValue = apexEvent.get(fieldName);

            // Get the schema helper
            final SchemaHelper fieldSchemaHelper = new SchemaHelperFactory().createSchemaHelper(eventField.getKey(),
                            eventField.getSchema());
            jsonObject.add(fieldName, (JsonElement) fieldSchemaHelper.marshal2Object(fieldValue));
        }

        // Output JSON string in a pretty format
        return gson.toJson(jsonObject);
    }

    /**
     * Serialise an Apex event to a JSON string as a single POJO.
     *
     * @param apexEvent the event to Serialise
     * @return the Serialise event as JSON
     * @throws ApexEventException exceptions on marshaling to JSON
     */
    private Object fromApexEventPojo(ApexEvent apexEvent) throws ApexEventException {
        // Get the event definition for the event from the model service
        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get(apexEvent.getName(),
                        apexEvent.getVersion());

        if (eventDefinition.getFields().isEmpty()) {
            final String errorMessage = ERROR_CODING + eventDefinition.getId() + " event to Json, Field "
                            + jsonPars.getPojoField() + " not found, no fields defined on event.";
            LOGGER.debug(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        if (eventDefinition.getFields().size() != 1) {
            final String errorMessage = ERROR_CODING + eventDefinition.getId() + " event to Json, Field "
                            + jsonPars.getPojoField() + ", "
                            + " one and only one field may be defined on a POJO event definition.";
            LOGGER.debug(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        AxField pojoFieldDefinition = eventDefinition.getFields().iterator().next();

        if (!jsonPars.getPojoField().equals(pojoFieldDefinition.getKey().getLocalName())) {
            final String errorMessage = ERROR_CODING + eventDefinition.getId() + " event to Json. Field "
                            + jsonPars.getPojoField() + " not found on POJO event definition.";
            LOGGER.debug(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        final Object fieldValue = apexEvent.get(jsonPars.getPojoField());

        // Get the schema helper
        final SchemaHelper fieldSchemaHelper = new SchemaHelperFactory()
                        .createSchemaHelper(pojoFieldDefinition.getKey(), pojoFieldDefinition.getSchema());

        return fieldSchemaHelper.marshal2String(fieldValue);
    }

    /**
     * This method converts a JSON object into an Apex event.
     *
     * @param eventName the name of the event
     * @param jsonEventString the JSON string that holds the event
     * @return the apex event that we have converted the JSON object into
     * @throws ApexEventException thrown on unmarshaling exceptions
     */
    private ApexEvent jsonStringApexEvent(final String eventName, final String jsonEventString)
                    throws ApexEventException {
        // Use GSON to read the event string
        final JsonObject jsonObject = new GsonBuilder().serializeNulls().create().fromJson(jsonEventString,
                        JsonObject.class);

        if (jsonObject == null || !jsonObject.isJsonObject()) {
            throw new ApexEventException(
                            "incoming event (" + jsonEventString + ") is not a JSON object or an JSON object array");
        }

        return jsonObject2ApexEvent(eventName, jsonObject);
    }

    /**
     * This method converts a JSON object into an Apex event.
     *
     * @param eventName the name of the event
     * @param jsonObject the JSON object that holds the event
     * @return the apex event that we have converted the JSON object into
     * @throws ApexEventException thrown on unmarshaling exceptions
     */
    private ApexEvent jsonObject2ApexEvent(final String eventName, final JsonObject jsonObject)
                    throws ApexEventException {
        // Process the mandatory Apex header
        final ApexEvent apexEvent = processApexEventHeader(eventName, jsonObject);

        // Get the event definition for the event from the model service
        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get(apexEvent.getName(),
                        apexEvent.getVersion());

        if (jsonPars.getPojoField() == null) {
            jsonObject2ApexEventWithFields(jsonObject, apexEvent, eventDefinition);
        } else {
            jsonObject2ApexEventPojo(jsonObject, apexEvent, eventDefinition);
        }

        return apexEvent;
    }

    /**
     * Decode an Apex event field by field.
     *
     * @param jsonObject the JSON representation of the event
     * @param apexEvent the incoming event header
     * @param eventDefinition the definition of the event from the model
     * @throws ApexEventException on decode errors
     */
    private void jsonObject2ApexEventWithFields(final JsonObject jsonObject, final ApexEvent apexEvent,
                    final AxEvent eventDefinition) throws ApexEventException {
        // Iterate over the input fields in the event
        for (final AxField eventField : eventDefinition.getFields()) {
            final String fieldName = eventField.getKey().getLocalName();
            if (!hasJsonField(jsonObject, fieldName)) {
                if (!eventField.getOptional()) {
                    final String errorMessage = ERROR_PARSING + eventDefinition.getId() + " event from Json. "
                                    + "Field \"" + fieldName + "\" is missing, but is mandatory.";
                    LOGGER.debug(errorMessage);
                    throw new ApexEventException(errorMessage);
                }
                continue;
            }

            final JsonElement fieldValue = getJsonField(jsonObject, fieldName, null, !eventField.getOptional());

            if (fieldValue != null && !fieldValue.isJsonNull()) {
                // Get the schema helper
                final SchemaHelper fieldSchemaHelper = new SchemaHelperFactory().createSchemaHelper(eventField.getKey(),
                                eventField.getSchema());
                apexEvent.put(fieldName, fieldSchemaHelper.createNewInstance(fieldValue));
            } else {
                apexEvent.put(fieldName, null);
            }
        }
    }

    /**
     * Decode an Apex event as a single POJO.
     *
     * @param jsonObject the JSON representation of the event
     * @param apexEvent the incoming event header
     * @param eventDefinition the definition of the event from the model
     * @throws ApexEventException on decode errors
     */
    private void jsonObject2ApexEventPojo(JsonObject jsonObject, ApexEvent apexEvent, AxEvent eventDefinition)
                    throws ApexEventException {

        if (eventDefinition.getFields().isEmpty()) {
            final String errorMessage = ERROR_PARSING + eventDefinition.getId() + " event from Json, Field "
                            + jsonPars.getPojoField() + " not found, no fields defined on event.";
            LOGGER.debug(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        if (eventDefinition.getFields().size() != 1) {
            final String errorMessage = ERROR_PARSING + eventDefinition.getId() + " event from Json, Field "
                            + jsonPars.getPojoField()
                            + ", one and only one field may be defined on a POJO event definition.";
            LOGGER.debug(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        AxField pojoFieldDefinition = eventDefinition.getFields().iterator().next();

        if (!jsonPars.getPojoField().equals(pojoFieldDefinition.getKey().getLocalName())) {
            final String errorMessage = ERROR_PARSING + eventDefinition.getId() + " event from Json. Field "
                            + jsonPars.getPojoField() + " not found on POJO event definition.";
            LOGGER.debug(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Get the schema helper
        final SchemaHelper fieldSchemaHelper = new SchemaHelperFactory()
                        .createSchemaHelper(pojoFieldDefinition.getKey(), pojoFieldDefinition.getSchema());
        apexEvent.put(jsonPars.getPojoField(), fieldSchemaHelper.createNewInstance(jsonObject));
    }

    /**
     * This method processes the event header of an Apex event.
     *
     * @param parameterEventName the name of the event from the parameters
     * @param jsonObject the JSON object containing the JSON representation of the incoming event
     * @return an apex event constructed using the header fields of the event
     * @throws ApexEventRuntimeException the apex event runtime exception
     * @throws ApexEventException on invalid events with missing header fields
     */
    private ApexEvent processApexEventHeader(final String parameterEventName, final JsonObject jsonObject)
                    throws ApexEventException {

        final String eventName = getHeaderName(jsonObject, parameterEventName);

        String eventVersion = getHeaderVersion(jsonObject);

        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get(eventName, eventVersion);

        if (eventDefinition == null) {
            if (eventVersion == null) {
                throw new ApexEventRuntimeException(
                                "an event definition for an event named \"" + eventName + "\" not found in Apex model");
            } else {
                throw new ApexEventRuntimeException("an event definition for an event named \"" + eventName
                                + "\" with version \"" + eventVersion + "\" not found in Apex model");
            }
        }

        // Use the defined event version if no version is specified on the incoming fields
        if (eventVersion == null) {
            eventVersion = eventDefinition.getKey().getVersion();
        }

        final String eventNamespace = getHeaderNamespace(jsonObject, eventName, eventDefinition);
        final String eventSource = getHeaderSource(jsonObject, eventDefinition);
        final String eventTarget = getHeaderTarget(jsonObject, eventDefinition);

        return new ApexEvent(eventName, eventVersion, eventNamespace, eventSource, eventTarget);
    }

    /**
     * Determine the name field of the event header.
     *
     * @param jsonObject the event in JSON format
     * @param parameterEventName the configured event name from the parameters
     * @return the event name to use on the event header
     */
    private String getHeaderName(final JsonObject jsonObject, final String parameterEventName) {
        final String jsonEventName = getJsonStringField(jsonObject, ApexEvent.NAME_HEADER_FIELD,
                        jsonPars.getNameAlias(), ApexEvent.NAME_REGEXP, false);

        // Check that an event name has been specified
        if (jsonEventName == null && parameterEventName == null) {
            throw new ApexEventRuntimeException(
                            "event received without mandatory parameter \"name\" on configuration or on event");
        }

        // Check if an event name was specified on the event parameters
        if (jsonEventName != null) {
            if (parameterEventName != null && !parameterEventName.equals(jsonEventName)) {
                LOGGER.warn("The incoming event name \"{}\" does not match the configured event name \"{}\","
                                + " using configured event name", jsonEventName, parameterEventName);
            }
            return jsonEventName;
        } else {
            return parameterEventName;
        }
    }

    /**
     * Determine the version field of the event header.
     *
     * @param jsonObject the event in JSON format
     * @return the event version
     */
    private String getHeaderVersion(final JsonObject jsonObject) {
        // Find the event definition in the model service. If version is null, the newest event
        // definition in the model service is used
        return getJsonStringField(jsonObject, ApexEvent.VERSION_HEADER_FIELD, jsonPars.getVersionAlias(),
                        ApexEvent.VERSION_REGEXP, false);
    }

    /**
     * Determine the name space field of the event header.
     *
     * @param jsonObject the event in JSON format
     * @param eventName the name of the event
     * @param eventDefinition the definition of the event structure
     * @return the event version
     */
    private String getHeaderNamespace(final JsonObject jsonObject, final String name, final AxEvent eventDefinition) {
        // Check the name space is OK if it is defined, if not, use the name space from the model
        String namespace = getJsonStringField(jsonObject, ApexEvent.NAMESPACE_HEADER_FIELD,
                        jsonPars.getNameSpaceAlias(), ApexEvent.NAMESPACE_REGEXP, false);
        if (namespace != null) {
            if (!namespace.equals(eventDefinition.getNameSpace())) {
                throw new ApexEventRuntimeException("namespace \"" + namespace + "\" on event \"" + name
                                + "\" does not match namespace \"" + eventDefinition.getNameSpace()
                                + "\" for that event in the Apex model");
            }
        } else {
            namespace = eventDefinition.getNameSpace();
        }
        return namespace;
    }

    /**
     * Determine the source field of the event header.
     *
     * @param jsonObject the event in JSON format
     * @param eventDefinition the definition of the event structure
     * @return the event version
     */
    private String getHeaderSource(final JsonObject jsonObject, final AxEvent eventDefinition) {
        // For source, use the defined source only if the source is not found on the incoming event
        String source = getJsonStringField(jsonObject, ApexEvent.SOURCE_HEADER_FIELD, jsonPars.getSourceAlias(),
                        ApexEvent.SOURCE_REGEXP, false);
        if (source == null) {
            source = eventDefinition.getSource();
        }
        return source;
    }

    /**
     * Determine the target field of the event header.
     *
     * @param jsonObject the event in JSON format
     * @param eventDefinition the definition of the event structure
     * @return the event version
     */
    private String getHeaderTarget(final JsonObject jsonObject, final AxEvent eventDefinition) {
        // For target, use the defined source only if the source is not found on the incoming event
        String target = getJsonStringField(jsonObject, ApexEvent.TARGET_HEADER_FIELD, jsonPars.getTargetAlias(),
                        ApexEvent.TARGET_REGEXP, false);
        if (target == null) {
            target = eventDefinition.getTarget();
        }
        return target;
    }

    /**
     * This method gets an event string field from a JSON object.
     *
     * @param jsonObject the JSON object containing the JSON representation of the incoming event
     * @param fieldName the field name to find in the event
     * @param fieldAlias the alias for the field to find in the event, overrides the field name if it is not null
     * @param fieldRegexp the regular expression to check the field against for validity
     * @param mandatory true if the field is mandatory
     * @return the value of the field in the JSON object or null if the field is optional
     * @throws ApexEventRuntimeException the apex event runtime exception
     */
    private String getJsonStringField(final JsonObject jsonObject, final String fieldName, final String fieldAlias,
                    final String fieldRegexp, final boolean mandatory) {
        // Get the JSON field for the string field
        final JsonElement jsonField = getJsonField(jsonObject, fieldName, fieldAlias, mandatory);

        // Null strings are allowed
        if (jsonField == null || jsonField.isJsonNull()) {
            return null;
        }

        // Check if this is a string field
        String fieldValueString = null;
        try {
            fieldValueString = jsonField.getAsString();
        } catch (final Exception e) {
            // The element is not a string so throw an error
            throw new ApexEventRuntimeException("field \"" + fieldName + "\" with type \""
                            + jsonField.getClass().getName() + "\" is not a string value", e);
        }

        // Is regular expression checking required
        if (fieldRegexp == null) {
            return fieldValueString;
        }

        // Check the event field against its regular expression
        if (!fieldValueString.matches(fieldRegexp)) {
            throw new ApexEventRuntimeException(
                            "field \"" + fieldName + "\" with value \"" + fieldValueString + "\" is invalid");
        }

        return fieldValueString;
    }

    /**
     * This method gets an event field from a JSON object.
     *
     * @param jsonObject the JSON object containing the JSON representation of the incoming event
     * @param fieldName the field name to find in the event
     * @param fieldAlias the alias for the field to find in the event, overrides the field name if it is not null
     * @param mandatory true if the field is mandatory
     * @return the value of the field in the JSON object or null if the field is optional
     * @throws ApexEventRuntimeException the apex event runtime exception
     */
    private JsonElement getJsonField(final JsonObject jsonObject, final String fieldName, final String fieldAlias,
                    final boolean mandatory) {

        // Check if we should use the alias for this field
        String fieldToFind = fieldName;
        if (fieldAlias != null) {
            fieldToFind = fieldAlias;
        }

        // Get the event field
        final JsonElement eventElement = jsonObject.get(fieldToFind);
        if (eventElement == null) {
            if (!mandatory) {
                return null;
            } else {
                throw new ApexEventRuntimeException("mandatory field \"" + fieldToFind + "\" is missing");
            }
        }

        return eventElement;
    }

    /**
     * This method if a JSON object has a named field.
     *
     * @param jsonObject the JSON object containing the JSON representation of the incoming event
     * @param fieldName the field name to find in the event
     * @return true if the field is present
     * @throws ApexEventRuntimeException the apex event runtime exception
     */
    private boolean hasJsonField(final JsonObject jsonObject, final String fieldName) {
        // check for the field
        return jsonObject.has(fieldName);
    }
}
