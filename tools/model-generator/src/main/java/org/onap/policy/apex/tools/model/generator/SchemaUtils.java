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

package org.onap.policy.apex.tools.model.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.reflect.ReflectData;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.plugins.context.schema.avro.AvroSchemaHelper;
import org.onap.policy.apex.service.engine.event.ApexEventException;

/**
 * Utility methods for schema handling.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
public final class SchemaUtils {

    /**
     * Private constructor to avoid instantiation.
     */
    private SchemaUtils() {}

    /**
     * Returns the schema for an event.
     *
     * @param event the event to process
     * @return the schema of the event
     * @throws ApexEventException in any error case
     */
    public static Schema getEventSchema(final AxEvent event) throws ApexEventException {
        final Schema skeletonSchema = Schema.createRecord(event.getKey().getName(), event.getNameSpace(),
                "org.onap.policy.apex.model.eventmodel.events", false);

        // Get the schema field for each parameter
        final List<Field> fields = new ArrayList<>(getSkeletonEventSchemaFields());

        final Map<String, Schema> preExistingParamSchemas = new LinkedHashMap<>();
        for (final AxField parameter : event.getParameterMap().values()) {
            final Schema fieldSchema = getEventParameterSchema(parameter, preExistingParamSchemas);
            final Field f = new Field(parameter.getKey().getLocalName(), fieldSchema, (String) null, (Object) null);
            fields.add(f);
        }
        skeletonSchema.setFields(fields);

        return skeletonSchema;
    }

    /**
     * Returns the schema fields as an array.
     *
     * @return an array with schema fields in the following order: nameSpace, name, version, source, target
     */
    public static List<Field> getSkeletonEventSchemaFields() {
        // Fixed fields
        final Field f1 = new Field("nameSpace", Schema.create(Schema.Type.STRING), (String) null, (Object) null);
        final Field f2 = new Field("name", Schema.create(Schema.Type.STRING), (String) null, (Object) null);
        final Field f3 = new Field("version", Schema.create(Schema.Type.STRING), (String) null, (Object) null);
        final Field f4 = new Field("source", Schema.create(Schema.Type.STRING), (String) null, (Object) null);
        final Field f5 = new Field("target", Schema.create(Schema.Type.STRING), (String) null, (Object) null);

        return Arrays.asList(f1, f2, f3, f4, f5);
    }

    /**
     * Returns the schema for an event parameter.
     *
     * @param parameter the parameter to process
     * @param preexistingParamSchemas map of pre-existing schemas
     * @return the schema for the event parameter
     * @throws ApexEventException in case of any error
     */
    public static Schema getEventParameterSchema(final AxField parameter,
            final Map<String, Schema> preexistingParamSchemas) throws ApexEventException {
        final SchemaHelper schemaHelper =
                new SchemaHelperFactory().createSchemaHelper(parameter.getKey(), parameter.getSchema().getKey());

        Schema parameterSchema = null;
        try {
            if (schemaHelper instanceof AvroSchemaHelper) {
                parameterSchema = ((AvroSchemaHelper) schemaHelper).getAvroSchema();
            } else {
                parameterSchema = ReflectData.get().getSchema(schemaHelper.getSchemaClass());
            }
        } catch (final AvroRuntimeException e) {
            throw new ApexEventException("failed to decode a schema for parameter " + parameter.getKey().getLocalName()
                    + " of type " + parameter.getSchema().getId() + " with Java type " + schemaHelper.getSchemaClass(),
                    e);
        }
        final String schemaname = parameterSchema.getFullName();

        // Get the Avro schema for this parameter, we need to keep track of sub-schemas for records because Avro does
        // not
        // allow re-declaration of sub-schema records of the same type. You simply reference the first sub-schema.
        final Schema alreadyseen = preexistingParamSchemas.get(schemaname);

        try {
            processSubSchemas(parameterSchema, preexistingParamSchemas);
        } catch (AvroRuntimeException | ApexEventException e) {
            throw new ApexEventException("failed to decode a schema for parameter " + parameter.getKey().getLocalName()
                    + " of type " + parameter.getSchema().getId() + " using Schema type " + schemaname, e);
        }
        if (alreadyseen != null) {
            // logger.warn("parameter "+ parameter.getKey().getLocalName() + " of type " + parameter.getSchema().getID()
            // + " tries to redfine AVRO type
            // "+schemaname+", but it was previously defined. This parameter will use the previously defined version
            // because AVRO does not support redefinition
            // of types that have already been defined");
            parameterSchema = alreadyseen;
        }

        return parameterSchema;
    }

    /**
     * Processes a sub-schema.
     *
     * @param avroParameterSchema an AVRO schema to process
     * @param map mapping of strings to schemas
     * @throws ApexEventException in case of any error
     */
    public static void processSubSchemas(final Schema avroParameterSchema, final Map<String, Schema> map)
            throws ApexEventException {
        if (avroParameterSchema.getType() == Schema.Type.RECORD) {
            final String schematypename = avroParameterSchema.getFullName();
            final Schema alreadyregistered = map.get(schematypename);
            if (alreadyregistered != null && !avroParameterSchema.equals(alreadyregistered)) {
                throw new ApexEventException(
                        "Parameter attempts to redefine type " + schematypename + " when it has already been defined");
            }
            map.put(schematypename, avroParameterSchema);
            for (final Schema.Field f : avroParameterSchema.getFields()) {
                final Schema fieldschema = f.schema();
                processSubSchemas(fieldschema, map);
            }
        } else if (avroParameterSchema.getType() == Schema.Type.ARRAY) {
            processSubSchemas(avroParameterSchema.getElementType(), map);
        } else if (avroParameterSchema.getType() == Schema.Type.MAP) {
            processSubSchemas(avroParameterSchema.getValueType(), map);
        } else if (avroParameterSchema.getType() == Schema.Type.UNION) {
            for (final Schema s : avroParameterSchema.getTypes()) {
                processSubSchemas(s, map);
            }
        }
    }

}
