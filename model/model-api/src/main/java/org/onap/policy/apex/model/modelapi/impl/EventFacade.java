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

package org.onap.policy.apex.model.modelapi.impl;

import java.util.Properties;
import java.util.Set;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class acts as a facade for operations towards a policy model for event operations
 * operations.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventFacade {
    private static final String CONCEPT = "concept ";
    private static final String CONCEPT_S = "concept(s) ";
    private static final String DOES_NOT_EXIST = " does not exist";
    private static final String DO_ES_NOT_EXIST = " do(es) not exist";
    private static final String ALREADY_EXISTS = " already exists";

    // Apex model we're working towards
    private final ApexModel apexModel;

    // Properties to use for the model
    private final Properties apexProperties;

    // Facade classes for working towards the real Apex model
    private final KeyInformationFacade keyInformationFacade;

    // JSON output on list/delete if set
    private final boolean jsonMode;

    /**
     * Constructor to create an event facade for the Model API.
     *
     * @param apexModel the apex model
     * @param apexProperties Properties for the model
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise
     *        set to false
     */
    public EventFacade(final ApexModel apexModel, final Properties apexProperties, final boolean jsonMode) {
        this.apexModel = apexModel;
        this.apexProperties = apexProperties;
        this.jsonMode = jsonMode;

        keyInformationFacade = new KeyInformationFacade(apexModel, apexProperties, jsonMode);
    }

    /**
     * Create an event.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the default version
     * @param nameSpace of the event, set to null to use the default value
     * @param source of the event, set to null to use the default value
     * @param target of the event, set to null to use the default value
     * @param uuid event UUID, set to null to generate a UUID
     * @param description event description, set to null to generate a description
     * @return result of the operation
     */
    public ApexApiResult createEvent(final String name, final String version, final String nameSpace,
            final String source, final String target, final String uuid, final String description) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                key.setVersion(apexProperties.getProperty("DEFAULT_CONCEPT_VERSION"));
            }

            if (apexModel.getPolicyModel().getEvents().getEventMap().containsKey(key)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, CONCEPT + key.getId() + ALREADY_EXISTS);
            }

            final AxEvent event = new AxEvent(key);

            event.setNameSpace((nameSpace != null ? nameSpace : apexProperties.getProperty("DEFAULT_EVENT_NAMESPACE")));
            event.setSource((source != null ? source : apexProperties.getProperty("DEFAULT_EVENT_SOURCE")));
            event.setTarget((target != null ? target : apexProperties.getProperty("DEFAULT_EVENT_TARGET")));

            apexModel.getPolicyModel().getEvents().getEventMap().put(key, event);

            if (apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().containsKey(key)) {
                return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
            } else {
                return keyInformationFacade.createKeyInformation(name, version, uuid, description);
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update an event.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the latest version
     * @param nameSpace of the event, set to null to not update
     * @param source of the event, set to null to not update
     * @param target of the event, set to null to not update
     * @param uuid event UUID, set to null to not update
     * @param description event description, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updateEvent(final String name, final String version, final String nameSpace,
            final String source, final String target, final String uuid, final String description) {
        try {
            final AxEvent event = apexModel.getPolicyModel().getEvents().get(name, version);
            if (event == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (nameSpace != null) {
                event.setNameSpace(nameSpace);
            }
            if (source != null) {
                event.setSource(source);
            }
            if (target != null) {
                event.setTarget(target);
            }

            return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List events.
     *
     * @param name name of the event, set to null to list all
     * @param version starting version of the event, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult listEvent(final String name, final String version) {
        try {
            final Set<AxEvent> eventSet = apexModel.getPolicyModel().getEvents().getAll(name, version);
            if (name != null && eventSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxEvent event : eventSet) {
                result.addMessage(
                        new ApexModelStringWriter<AxEvent>(false).writeString(event, AxEvent.class, jsonMode));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete an event.
     *
     * @param name name of the event
     * @param version version of the event, set to null to delete all versions
     * @return result of the operation
     */
    public ApexApiResult deleteEvent(final String name, final String version) {
        try {
            if (version != null) {
                final AxArtifactKey key = new AxArtifactKey(name, version);
                final AxEvent removedEvent = apexModel.getPolicyModel().getEvents().getEventMap().remove(key);
                if (removedEvent != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS, new ApexModelStringWriter<AxEvent>(false)
                            .writeString(removedEvent, AxEvent.class, jsonMode));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + key.getId() + DOES_NOT_EXIST);
                }
            }

            final Set<AxEvent> eventSet = apexModel.getPolicyModel().getEvents().getAll(name, version);
            if (eventSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxEvent event : eventSet) {
                result.addMessage(
                        new ApexModelStringWriter<AxEvent>(false).writeString(event, AxEvent.class, jsonMode));
                apexModel.getPolicyModel().getEvents().getEventMap().remove(event.getKey());
                keyInformationFacade.deleteKeyInformation(name, version);
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Validate events.
     *
     * @param name name of the event, set to null to list all
     * @param version starting version of the event, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult validateEvent(final String name, final String version) {
        try {
            final Set<AxEvent> eventSet = apexModel.getPolicyModel().getEvents().getAll(name, version);
            if (eventSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxEvent event : eventSet) {
                final AxValidationResult validationResult = event.validate(new AxValidationResult());
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(event.getKey(),
                        AxArtifactKey.class, jsonMode));
                result.addMessage(validationResult.toString());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create an event parameter.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the latest version
     * @param parName of the parameter
     * @param contextSchemaName name of the parameter context schema
     * @param contextSchemaVersion version of the parameter context schema, set to null to use the
     *        latest version
     * @param optional true if the event parameter is optional, false otherwise
     * @return result of the operation
     */
    public ApexApiResult createEventPar(final String name, final String version, final String parName,
            final String contextSchemaName, final String contextSchemaVersion, final boolean optional) {
        try {
            Assertions.argumentNotNull(parName, "parName may not be null");

            final AxEvent event = apexModel.getPolicyModel().getEvents().get(name, version);
            if (event == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(event.getKey(), parName);

            if (event.getParameterMap().containsKey(refKey.getLocalName())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                        CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            final AxContextSchema schema =
                    apexModel.getPolicyModel().getSchemas().get(contextSchemaName, contextSchemaVersion);
            if (schema == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + contextSchemaName + ':' + contextSchemaVersion + DOES_NOT_EXIST);
            }

            event.getParameterMap().put(refKey.getLocalName(), new AxField(refKey, schema.getKey(), optional));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List event parameters.
     *
     * @param name name of the event
     * @param version version of the event, set to null to list latest version
     * @param parName name of the parameter, set to null to list all parameters of the event
     * @return result of the operation
     */
    public ApexApiResult listEventPar(final String name, final String version, final String parName) {
        try {
            final AxEvent event = apexModel.getPolicyModel().getEvents().get(name, version);
            if (event == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (parName != null) {
                final AxField eventField = event.getParameterMap().get(parName);
                if (eventField != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                            new ApexModelStringWriter<AxField>(false).writeString(eventField, AxField.class, jsonMode));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + parName + DOES_NOT_EXIST);
                }
            } else {
                if (event.getParameterMap().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no parameters defined on event " + event.getKey().getId());
                }

                final ApexApiResult result = new ApexApiResult();
                for (final AxField eventPar : event.getParameterMap().values()) {
                    result.addMessage(
                            new ApexModelStringWriter<AxField>(false).writeString(eventPar, AxField.class, jsonMode));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete an event parameter.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the latest version
     * @param parName of the parameter, set to null to delete all parameters
     * @return result of the operation
     */
    public ApexApiResult deleteEventPar(final String name, final String version, final String parName) {
        try {
            final AxEvent event = apexModel.getPolicyModel().getEvents().get(name, version);
            if (event == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            if (parName != null) {
                if (event.getParameterMap().containsKey(parName)) {
                    result.addMessage(new ApexModelStringWriter<AxField>(false)
                            .writeString(event.getParameterMap().get(parName), AxField.class, jsonMode));
                    event.getParameterMap().remove(parName);
                    return result;
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + parName + DOES_NOT_EXIST);
                }
            } else {
                if (event.getParameterMap().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no parameters defined on event " + event.getKey().getId());
                }

                for (final AxField eventPar : event.getParameterMap().values()) {
                    result.addMessage(
                            new ApexModelStringWriter<AxField>(false).writeString(eventPar, AxField.class, jsonMode));
                }
                event.getParameterMap().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }
}
