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

package org.onap.policy.apex.tools.model.generator.model2event;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.commons.lang3.Validate;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.modelapi.ApexModelFactory;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.plugins.context.schema.avro.AvroSchemaHelperParameters;
import org.onap.policy.apex.tools.model.generator.SchemaUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * Takes a model and generates the JSON event schemas.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class Model2JsonEventSchema {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(Model2JsonEventSchema.class);

    // Recurring string constants
    private static final String TARGET = "target";
    private static final String SOURCE = "source";
    private static final String VERSION = "version";
    private static final String NAME_SPACE = "nameSpace";

    /** Application name, used as prompt. */
    private final String appName;

    /** The file name of the policy model. */
    private final String modelFile;

    /** The type of events to generate: stimuli, response, internal. */
    private final String type;

    /**
     * Creates a new model to event schema generator.
     *
     * @param modelFile the model file to be used
     * @param type the type of events to generate, one of: stimuli, response, internal
     * @param appName application name for printouts
     */
    public Model2JsonEventSchema(final String modelFile, final String type, final String appName) {
        Validate.notNull(modelFile, "Model2JsonEvent: given model file name was blank");
        Validate.notNull(type, "Model2JsonEvent: given type was blank");
        Validate.notNull(appName, "Model2JsonEvent: given application name was blank");
        this.modelFile = modelFile;
        this.type = type;
        this.appName = appName;
    }

    /**
     * Adds a type to a field for a given schema.
     *
     * @param schema the schema to add a type for
     * @param stg the STG
     * @return a template with the type
     */
    protected ST addFieldType(final Schema schema, final STGroup stg) {
        ST ret = null;
        switch (schema.getType()) {
            case BOOLEAN:
            case BYTES:
            case DOUBLE:
            case FIXED:
            case FLOAT:
            case INT:
            case LONG:
            case STRING:
                ret = stg.getInstanceOf("fieldTypeAtomic");
                ret.add("type", schema.getType());
                break;

            case ARRAY:
                ret = stg.getInstanceOf("fieldTypeArray");
                ret.add("array", this.addFieldType(schema.getElementType(), stg));
                break;
            case ENUM:
                ret = stg.getInstanceOf("fieldTypeEnum");
                ret.add("symbols", schema.getEnumSymbols());
                break;

            case MAP:
                ret = stg.getInstanceOf("fieldTypeMap");
                ret.add("map", this.addFieldType(schema.getValueType(), stg));
                break;

            case RECORD:
                ret = stg.getInstanceOf("fieldTypeRecord");
                for (final Field field : schema.getFields()) {
                    final ST st = stg.getInstanceOf("field");
                    st.add("name", field.name());
                    st.add("type", this.addFieldType(field.schema(), stg));
                    ret.add("fields", st);
                }
                break;

            case NULL:
                break;
            case UNION:
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * Runs the application.
     *
     *
     * @return status of the application execution, 0 for success, positive integer for exit
     *         condition (such as help or version), negative integer for errors
     * @throws ApexException if any problem occurred in the model
     */
    public int runApp() throws ApexException {
        final STGroupFile stg = new STGroupFile("org/onap/policy/apex/tools/model/generator/event-json.stg");
        final ST stEvents = stg.getInstanceOf("events");

        final ApexModelFactory factory = new ApexModelFactory();
        final ApexModel model = factory.createApexModel(new Properties(), true);

        final ApexApiResult result = model.loadFromFile(modelFile);
        if (result.isNok()) {
            String message = appName + ": " + result.getMessage();
            LOGGER.error(message);
            return -1;
        }

        final AxPolicyModel policyModel = model.getPolicyModel();
        policyModel.register();
        new SchemaParameters().getSchemaHelperParameterMap().put("Avro", new AvroSchemaHelperParameters());

        final Set<AxEvent> events = new HashSet<>();
        final Set<AxArtifactKey> eventKeys = new HashSet<>();
        final AxPolicies policies = policyModel.getPolicies();
        switch (type) {
            case "stimuli":
                for (final AxPolicy policy : policies.getPolicyMap().values()) {
                    final String firsState = policy.getFirstState();
                    for (final AxState state : policy.getStateMap().values()) {
                        if (state.getKey().getLocalName().equals(firsState)) {
                            eventKeys.add(state.getTrigger());
                        }
                    }
                }
                break;
            case "response":
                for (final AxPolicy policy : policies.getPolicyMap().values()) {
                    for (final AxState state : policy.getStateMap().values()) {
                        if (state.getNextStateSet().iterator().next().equals("NULL")) {
                            for (final AxStateOutput output : state.getStateOutputs().values()) {
                                eventKeys.add(output.getOutgingEvent());
                            }
                        }
                    }
                }
                break;
            case "internal":
                for (final AxPolicy policy : policies.getPolicyMap().values()) {
                    final String firsState = policy.getFirstState();
                    for (final AxState state : policy.getStateMap().values()) {
                        if (state.getKey().getLocalName().equals(firsState)) {
                            continue;
                        }
                        if (state.getNextStateSet().iterator().next().equals("NULL")) {
                            continue;
                        }
                        for (final AxStateOutput output : state.getStateOutputs().values()) {
                            eventKeys.add(output.getOutgingEvent());
                        }
                    }
                }
                break;
            default:
                LOGGER.error("{}: unknown type <{}>, cannot proceed", appName, type);
                return -1;
        }

        for (final AxEvent event : policyModel.getEvents().getEventMap().values()) {
            for (final AxArtifactKey key : eventKeys) {
                if (event.getKey().equals(key)) {
                    events.add(event);
                }
            }
        }

        for (final AxEvent event : events) {
            final ST stEvent = stg.getInstanceOf("event");
            stEvent.add("name", event.getKey().getName());
            stEvent.add(NAME_SPACE, event.getNameSpace());
            stEvent.add(VERSION, event.getKey().getVersion());
            stEvent.add(SOURCE, event.getSource());
            stEvent.add(TARGET, event.getTarget());

            final Schema avro = SchemaUtils.getEventSchema(event);
            for (final Field field : avro.getFields()) {
                // filter magic names
                switch (field.name()) {
                    case "name":
                    case NAME_SPACE:
                    case VERSION:
                    case SOURCE:
                    case TARGET:
                        break;
                    default:
                        stEvent.add("fields", this.setField(field, stg));
                }
            }
            stEvents.add("event", stEvent);
        }
        String renderMessage = stEvents.render();
        LOGGER.error(renderMessage);
        return 0;
    }

    /**
     * Adds a field to the output.
     *
     * @param field the field from the event
     * @param stg the STG
     * @return a template for the field
     */
    protected ST setField(final Field field, final STGroup stg) {
        final ST st = stg.getInstanceOf("field");
        switch (field.name()) {
            case "name":
            case NAME_SPACE:
            case VERSION:
            case SOURCE:
            case TARGET:
                break;
            default:
                st.add("name", field.name());
                st.add("type", this.addFieldType(field.schema(), stg));
        }
        return st;
    }

}
