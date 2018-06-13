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

package org.onap.policy.apex.plugins.event.protocol.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.yaml.snakeyaml.Yaml;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;

/**
 * The Class Apex2YamlEventConverter converts {@link ApexEvent} instances to and from YAML string representations of
 * Apex events.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class Apex2YamlEventConverter implements ApexEventProtocolConverter {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(Apex2YamlEventConverter.class);

    // The parameters for the YAML event protocol
    private YamlEventProtocolParameters yamlPars;

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter#init(org.onap.policy.
     * apex.service.parameters.eventprotocol.EventProtocolParameters)
     */
    @Override
    public void init(final EventProtocolParameters parameters) {
        // Check and get the YAML parameters
        if (!(parameters instanceof YamlEventProtocolParameters)) {
            final String errorMessage = "specified consumer properties are not applicable to the YAML event protocol";
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }

        yamlPars = (YamlEventProtocolParameters) parameters;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConverter#toApexEvent(java.lang.String, java.lang.Object)
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
        String yamlEventString = null;
        try {
            yamlEventString = (String) eventObject;
        } catch (final Exception e) {
            final String errorMessage = "error converting event \"" + eventObject + "\" to a string";
            LOGGER.debug(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // The list of events we will return
        final List<ApexEvent> eventList = new ArrayList<>();

        // Convert the YAML document string into an object
        Object yamlObject = new Yaml().load(yamlEventString);

        // If the incoming YAML did not create a map it is a primitive type or a collection so we
        // convert it into a map for processing
        Map<?, ?> yamlMap;
        if (yamlObject != null && yamlObject instanceof Map) {
            // We already have a map so just cast the object
            yamlMap = (Map<?, ?>) yamlObject;
        }
        else {
            // Create a single entry map, new map creation and assignment is to avoid a
            // type checking warning
            LinkedHashMap<String, Object> newYamlMap = new LinkedHashMap<>();
            newYamlMap.put(yamlPars.getYamlFieldName(), yamlObject);
            yamlMap = newYamlMap;
        }

        try {
            eventList.add(yamlMap2ApexEvent(eventName, yamlMap));
        } catch (final Exception e) {
            final String errorString = "Failed to unmarshal YAML event: " + e.getMessage() + ", event="
                            + yamlEventString;
            LOGGER.warn(errorString, e);
            throw new ApexEventException(errorString, e);
        }

        // Return the list of events we have unmarshalled
        return eventList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConverter#fromApexEvent(org.onap.policy.
     * apex.service.engine.event.ApexEvent)
     */
    @Override
    public Object fromApexEvent(final ApexEvent apexEvent) throws ApexEventException {
        // Check the Apex event
        if (apexEvent == null) {
            LOGGER.warn("event processing failed, Apex event is null");
            throw new ApexEventException("event processing failed, Apex event is null");
        }

        // Get the event definition for the event from the model service
        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get(apexEvent.getName(),
                        apexEvent.getVersion());

        // Create a map for output of the APEX event to YAML
        LinkedHashMap<String, Object> yamlMap = new LinkedHashMap<>();

        yamlMap.put(ApexEvent.NAME_HEADER_FIELD, apexEvent.getName());
        yamlMap.put(ApexEvent.VERSION_HEADER_FIELD, apexEvent.getVersion());
        yamlMap.put(ApexEvent.NAMESPACE_HEADER_FIELD, apexEvent.getNameSpace());
        yamlMap.put(ApexEvent.SOURCE_HEADER_FIELD, apexEvent.getSource());
        yamlMap.put(ApexEvent.TARGET_HEADER_FIELD, apexEvent.getTarget());

        if (apexEvent.getExceptionMessage() != null) {
            yamlMap.put(ApexEvent.EXCEPTION_MESSAGE_HEADER_FIELD, apexEvent.getExceptionMessage());
        }

        for (final AxField eventField : eventDefinition.getFields()) {
            final String fieldName = eventField.getKey().getLocalName();

            if (!apexEvent.containsKey(fieldName)) {
                if (!eventField.getOptional()) {
                    final String errorMessage = "error parsing " + eventDefinition.getID() + " event to Json. "
                                    + "Field \"" + fieldName + "\" is missing, but is mandatory. Fields: " + apexEvent;
                    LOGGER.debug(errorMessage);
                    throw new ApexEventRuntimeException(errorMessage);
                }
                continue;
            }

            yamlMap.put(fieldName,  apexEvent.get(fieldName));
        }

        // Use Snake YAML to convert the APEX event to YAML
        Yaml yaml = new Yaml();
        return yaml.dumpAs(yamlMap, null, FlowStyle.BLOCK);
    }

    /**
     * This method converts a YAML map into an Apex event.
     *
     * @param eventName the name of the event
     * @param yamlMap the YAML map that holds the event
     * @return the apex event that we have converted the JSON object into
     * @throws ApexEventException
     *         thrown on unmarshaling exceptions
     */
    private ApexEvent yamlMap2ApexEvent(final String eventName, final Map<?, ?> yamlMap)
                    throws ApexEventException {
        // Process the mandatory Apex header
        final ApexEvent apexEvent = processApexEventHeader(eventName, yamlMap);

        // Get the event definition for the event from the model service
        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get(apexEvent.getName(),
                        apexEvent.getVersion());

        // Iterate over the input fields in the event
        for (final AxField eventField : eventDefinition.getFields()) {
            final String fieldName = eventField.getKey().getLocalName();
            if (!yamlMap.containsKey(fieldName)) {
                if (!eventField.getOptional()) {
                    final String errorMessage = "error parsing " + eventDefinition.getID() + " event from Json. "
                                    + "Field \"" + fieldName + "\" is missing, but is mandatory.";
                    LOGGER.debug(errorMessage);
                    throw new ApexEventException(errorMessage);
                }
                continue;
            }

            final Object fieldValue = getYamlField(yamlMap, fieldName, null, !eventField.getOptional());

            if (fieldValue != null) {
                // Get the schema helper
                final SchemaHelper fieldSchemaHelper = new SchemaHelperFactory().createSchemaHelper(eventField.getKey(),
                                eventField.getSchema());
                apexEvent.put(fieldName, fieldSchemaHelper.createNewInstance(fieldValue));
            } else {
                apexEvent.put(fieldName, null);
            }
        }
        return apexEvent;

    }

    /**
     * This method processes the event header of an Apex event.
     *
     * @param eventName the name of the event
     * @param yamlMap the YAML map that holds the event
     * @return an apex event constructed using the header fields of the event
     * @throws ApexEventRuntimeException the apex event runtime exception
     * @throws ApexEventException on invalid events with missing header fields
     */
    private ApexEvent processApexEventHeader(final String eventName,  final Map<?, ?> yamlMap)
                    throws ApexEventException {
        // Get the event header fields
        // @formatter:off
        String name      = getYamlStringField(yamlMap, ApexEvent.NAME_HEADER_FIELD,      yamlPars.getNameAlias(),      ApexEvent.NAME_REGEXP,      false);
        String version   = getYamlStringField(yamlMap, ApexEvent.VERSION_HEADER_FIELD,   yamlPars.getVersionAlias(),   ApexEvent.VERSION_REGEXP,   false);
        String namespace = getYamlStringField(yamlMap, ApexEvent.NAMESPACE_HEADER_FIELD, yamlPars.getNameSpaceAlias(), ApexEvent.NAMESPACE_REGEXP, false);
        String source    = getYamlStringField(yamlMap, ApexEvent.SOURCE_HEADER_FIELD,    yamlPars.getSourceAlias(),    ApexEvent.SOURCE_REGEXP,    false);
        String target    = getYamlStringField(yamlMap, ApexEvent.TARGET_HEADER_FIELD,    yamlPars.getTargetAlias(),    ApexEvent.TARGET_REGEXP,    false);
        // @formatter:on

        // Check that an event name has been specified
        if (name == null && eventName == null) {
            throw new ApexEventRuntimeException(
                            "event received without mandatory parameter \"name\" on configuration or on event");
        }

        // Check if an event name was specified on the event parameters
        if (eventName != null) {
            if (name != null && !eventName.equals(name)) {
                LOGGER.warn("The incoming event name \"{}\" does not match the configured event name \"{}\", using configured event name",
                                name, eventName);
            }
            name = eventName;
        }

        // Now, find the event definition in the model service. If version is null, the newest event
        // definition in the model service is used
        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get(name, version);
        if (eventDefinition == null) {
            throw new ApexEventRuntimeException("an event definition for an event named \"" + name
                            + "\" with version \"" + version + "\" not found in Apex model");
        }

        // Use the defined event version if no version is specified on the incoming fields
        if (version == null) {
            version = eventDefinition.getKey().getVersion();
        }

        // Check the name space is OK if it is defined, if not, use the name space from the model
        if (namespace != null) {
            if (!namespace.equals(eventDefinition.getNameSpace())) {
                throw new ApexEventRuntimeException("namespace \"" + namespace + "\" on event \"" + name
                                + "\" does not match namespace \"" + eventDefinition.getNameSpace()
                                + "\" for that event in the Apex model");
            }
        } else {
            namespace = eventDefinition.getNameSpace();
        }

        // For source, use the defined source only if the source is not found on the incoming event
        if (source == null) {
            source = eventDefinition.getSource();
        }

        // For target, use the defined source only if the source is not found on the incoming event
        if (target == null) {
            target = eventDefinition.getTarget();
        }

        return new ApexEvent(name, version, namespace, source, target);
    }

    /**
     * This method gets an event string field from a JSON object.
     *
     * @param yamlMap
     *        the YAML containing the YAML representation of the incoming event
     * @param fieldName
     *        the field name to find in the event
     * @param fieldAlias
     *        the alias for the field to find in the event, overrides the field name if it is not null
     * @param fieldRE
     *        the regular expression to check the field against for validity
     * @param mandatory
     *        true if the field is mandatory
     * @return the value of the field in the JSON object or null if the field is optional
     * @throws ApexEventRuntimeException
     *         the apex event runtime exception
     */
    private String getYamlStringField(final Map<?, ?> yamlMap, final String fieldName, final String fieldAlias,
                    final String fieldRE, final boolean mandatory) {
        // Get the YAML field for the string field
        final Object yamlField = getYamlField(yamlMap, fieldName, fieldAlias, mandatory);

        // Null strings are allowed
        if (yamlField == null) {
            return null;
        }

        // Check if this is a string field
        String fieldValueString = null;
        try {
            fieldValueString = (String) yamlField;
        } catch (final Exception e) {
            // The element is not a string so throw an error
            throw new ApexEventRuntimeException("field \"" + fieldName + "\" with type \""
                            + yamlField.getClass().getCanonicalName() + "\" is not a string value");
        }

        // Is regular expression checking required
        if (fieldRE == null) {
            return fieldValueString;
        }

        // Check the event field against its regular expression
        if (!fieldValueString.matches(fieldRE)) {
            throw new ApexEventRuntimeException(
                            "field \"" + fieldName + "\" with value \"" + fieldValueString + "\" is invalid");
        }

        return fieldValueString;
    }

    /**
     * This method gets an event field from a YAML object.
     *
     * @param yamlMap
     *        the YAML containing the YAML representation of the incoming event
     * @param fieldName
     *        the field name to find in the event
     * @param fieldAlias
     *        the alias for the field to find in the event, overrides the field name if it is not null
     * @param mandatory
     *        true if the field is mandatory
     * @return the value of the field in the YAML object or null if the field is optional
     * @throws ApexEventRuntimeException
     *         the apex event runtime exception
     */
    private Object getYamlField(final Map<?, ?> yamlMap, final String fieldName, final String fieldAlias,
                    final boolean mandatory) {

        // Check if we should use the alias for this field
        String fieldToFind = fieldName;
        if (fieldAlias != null) {
            fieldToFind = fieldAlias;
        }

        // Get the event field
        final Object eventElement = yamlMap.get(fieldToFind);
        if (eventElement == null) {
            if (!mandatory) {
                return null;
            } else {
                throw new ApexEventRuntimeException("mandatory field \"" + fieldToFind + "\" is missing");
            }
        }

        return eventElement;
    }
}
