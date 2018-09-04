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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class does direct mapping from Avro classes to Java classes, used for Avro primitive types
 * that directly produce Java objects.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AvroDirectObjectMapper implements AvroObjectMapper {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AvroDirectObjectMapper.class);

    // Map for Avro primitive types to Java primitive types
    private static final Map<Schema.Type, Class<?>> AVRO_JAVA_TYPE_MAP = new TreeMap<>();

    // @formatter:off
    // Initialize the mapping
    static {
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.ARRAY,   GenericData.Array.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.BOOLEAN, Boolean.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.DOUBLE,  Double.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.ENUM,    GenericData.EnumSymbol.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.FIXED,   GenericData.Fixed.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.FLOAT,   Float.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.INT,     Integer.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.LONG,    Long.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.MAP,     HashMap.class);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.NULL,    null);
        AVRO_JAVA_TYPE_MAP.put(Schema.Type.RECORD,  GenericData.Record.class);
    }
    // @formatter:on

    // The user keyAvro type for direct mapping
    private AxKey userKey;
    private Type avroType;

    // The Apex compatible class
    private Class<?> schemaClass;

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#getJavaClass()
     */
    @Override
    public Class<?> getJavaClass() {
        return schemaClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#setAvroType(org.apache.
     * avro. Schema.Type)
     */
    @Override
    public void init(final AxKey initUserKey, final Type initAvroType) {
        this.userKey = initUserKey;
        this.avroType = initAvroType;
        schemaClass = AVRO_JAVA_TYPE_MAP.get(avroType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#createNewinstance(org.
     * apache. avro.Schema)
     */
    @Override
    public Object createNewInstance(final Schema avroSchema) {
        // By default, we do not create an instance, normal Java object creation is sufficient
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#getAvroType()
     */
    @Override
    public Type getAvroType() {
        return avroType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#mapFromAvro(java.lang.
     * Object)
     */
    @Override
    public Object mapFromAvro(final Object avroObject) {
        // Always return null if the schema is a null schema
        if (schemaClass == null) {
            return null;
        }

        // It is legal for the schema class to be null, if the Avro schema has a "null" type then
        // the decoded object is always returned as a null
        if (!schemaClass.isAssignableFrom(avroObject.getClass())) {
            final String returnString =
                    userKey.getId() + ": object \"" + avroObject + "\" of class \"" + avroObject.getClass()
                            + "\" cannot be decoded to an object of class \"" + schemaClass.getCanonicalName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        return avroObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#mapToAvro(java.lang.Object)
     */
    @Override
    public Object mapToAvro(final Object object) {
        // Null values are only allowed if the schema class is null
        if (object == null) {
            if (schemaClass != null) {
                final String returnString = userKey.getId() + ": cannot encode a null object of class \""
                        + schemaClass.getCanonicalName() + "\"";
                LOGGER.warn(returnString);
                throw new ContextRuntimeException(returnString);
            }
        }

        // For direct mappings, just work directly with the Java objects
        return object;
    }
}
