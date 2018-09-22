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

package org.onap.policy.apex.plugins.context.schema.avro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.io.JsonEncoder;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.impl.schema.AbstractSchemaHelper;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is the implementation of the {@link org.onap.policy.apex.context.SchemaHelper} interface for Avro schemas.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AvroSchemaHelper extends AbstractSchemaHelper {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AvroSchemaHelper.class);

    // Recurring string constants
    private static final String OBJECT_TAG = ": object \"";

    // The Avro schema for this context schema
    private Schema avroSchema;

    // The mapper that translates between Java and Avro objects
    private AvroObjectMapper avroObjectMapper;

    @Override
    public void init(final AxKey userKey, final AxContextSchema schema) {
        super.init(userKey, schema);

        // Configure the Avro schema
        try {
            avroSchema = new Schema.Parser().parse(schema.getSchema());
        } catch (final Exception e) {
            final String resultSting = userKey.getId() + ": avro context schema \"" + schema.getId()
                            + "\" schema is invalid: " + e.getMessage() + ", schema: " + schema.getSchema();
            LOGGER.warn(resultSting, e);
            throw new ContextRuntimeException(resultSting);
        }

        // Get the object mapper for the schema type to a Java class
        avroObjectMapper = new AvroObjectMapperFactory().get(userKey, avroSchema);

        // Get the Java type for this schema, if it is a primitive type then we can do direct
        // conversion to JAva
        setSchemaClass(avroObjectMapper.getJavaClass());
    }

    /**
     * Getter to get the Avro schema.
     *
     * @return the Avro schema
     */
    public Schema getAvroSchema() {
        return avroSchema;
    }

    @Override
    public Object getSchemaObject() {
        return getAvroSchema();
    }

    @Override
    public Object createNewInstance() {
        // Create a new instance using the Avro object mapper
        final Object newInstance = avroObjectMapper.createNewInstance(avroSchema);

        // If no new instance is created, use default schema handler behavior
        if (newInstance != null) {
            return newInstance;
        } else {
            return super.createNewInstance();
        }
    }

    @Override
    public Object createNewInstance(final String stringValue) {
        return unmarshal(stringValue);
    }

    @Override
    public Object createNewInstance(final Object incomingObject) {
        if (incomingObject instanceof JsonElement) {
            final Gson gson = new GsonBuilder().serializeNulls().create();
            final String elementJsonString = gson.toJson((JsonElement) incomingObject);

            return createNewInstance(elementJsonString);
        } else {
            final String returnString = getUserKey().getId() + ": the object \"" + incomingObject
                            + "\" is not an instance of JsonObject";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }
    }

    @Override
    public Object unmarshal(final Object object) {
        // If an object is already in the correct format, just carry on
        if (passThroughObject(object)) {
            return object;
        }

        String objectString = getStringObject(object);

        // Translate illegal characters in incoming JSON keys to legal Avro values
        objectString = AvroSchemaKeyTranslationUtilities.translateIllegalKeys(objectString, false);

        // Decode the object
        Object decodedObject;
        try {
            final JsonDecoder jsonDecoder = DecoderFactory.get().jsonDecoder(avroSchema, objectString);
            decodedObject = new GenericDatumReader<GenericRecord>(avroSchema).read(null, jsonDecoder);
        } catch (final Exception e) {
            final String returnString = getUserKey().getId() + OBJECT_TAG + objectString
                            + "\" Avro unmarshalling failed: " + e.getMessage();
            LOGGER.warn(returnString, e);
            throw new ContextRuntimeException(returnString, e);
        }

        // Now map the decoded object into something we can handle
        return avroObjectMapper.mapFromAvro(decodedObject);
    }

    /**
     * Check that the incoming object is a string, the incoming object must be a string containing Json.
     * 
     * @param object incoming object
     * @return object as String
     */
    private String getStringObject(final Object object) {
        try {
            if (isObjectString(object)) {
                return getObjectString(object);
            } else {
                return (String) object;
            }
        } catch (final ClassCastException e) {
            final String returnString = getUserKey().getId() + OBJECT_TAG + object + "\" of type \""
                            + (object != null ? object.getClass().getCanonicalName() : "null")
                            + "\" must be assignable to \"" + getSchemaClass().getCanonicalName()
                            + "\" or be a Json string representation of it for Avro unmarshalling";
            LOGGER.warn(returnString, e);
            throw new ContextRuntimeException(returnString);
        }
    }

    /**
     * Get a string object.
     * 
     * @param object the string object
     * @return the string
     */
    private String getObjectString(final Object object) {
        String objectString = object.toString().trim();
        if (objectString.length() == 0) {
            return "\"\"";
        } else if (objectString.length() == 1) {
            return "\"" + objectString + "\"";
        } else {
            // All strings must be quoted for decoding
            if (objectString.charAt(0) != '"') {
                objectString = '"' + objectString;
            }
            if (objectString.charAt(objectString.length() - 1) != '"') {
                objectString += '"';
            }
        }
        return objectString;
    }

    private boolean isObjectString(final Object object) {
        return object != null && avroSchema.getType().equals(Schema.Type.STRING);
    }

    @Override
    public String marshal2String(final Object object) {
        // Condition the object for Avro encoding
        final Object conditionedObject = avroObjectMapper.mapToAvro(object);

        final String jsonString = getJsonString(object, conditionedObject);

        return AvroSchemaKeyTranslationUtilities.translateIllegalKeys(jsonString, true);
    }

    private String getJsonString(final Object object, final Object conditionedObject) {

        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            final DatumWriter<Object> writer = new GenericDatumWriter<>(avroSchema);
            final JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(avroSchema, output, true);
            writer.write(conditionedObject, jsonEncoder);
            jsonEncoder.flush();
            return new String(output.toByteArray());
        } catch (final Exception e) {
            final String returnString = getUserKey().getId() + OBJECT_TAG + object + "\" Avro marshalling failed: "
                            + e.getMessage();
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString, e);
        }
    }

    @Override
    public JsonElement marshal2Object(final Object schemaObject) {
        // Get the object as a Json string
        final String schemaObjectAsString = marshal2String(schemaObject);

        // Get a Gson instance to convert the Json string to an object created by Json
        final Gson gson = new Gson();

        // Convert the Json string into an object
        final Object schemaObjectAsObject = gson.fromJson(schemaObjectAsString, Object.class);

        return gson.toJsonTree(schemaObjectAsObject);
    }

    /**
     * Check if we can pass this object straight through encoding or decoding, is it an object native to the schema.
     *
     * @param object the object to check
     * @return true if it's a straight pass through
     */
    private boolean passThroughObject(final Object object) {
        if (object == null || getSchemaClass() == null) {
            return false;
        }

        // All strings must be mapped
        if (object instanceof String) {
            return false;
        }

        // Now, check if the object is native
        return getSchemaClass().isAssignableFrom(object.getClass());
    }
}
