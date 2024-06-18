/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.context.impl.schema.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.impl.schema.AbstractSchemaHelper;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class implements translation to and from Apex distributed objects and Java objects when a Java schema is used.
 * It creates schema items as Java objects and marshals and unmarshals these objects in various formats. All objects
 * must be of the type of Java class defined in the schema.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavaSchemaHelper extends AbstractSchemaHelper {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavaSchemaHelper.class);

    // This map defines the built-in types in Java
    // @formatter:off
    private static final Map<String, Class<?>> BUILT_IN_MAP = new HashMap<>();

    static {
        BUILT_IN_MAP.put("int",    Integer.TYPE);
        BUILT_IN_MAP.put("long",   Long.TYPE);
        BUILT_IN_MAP.put("double", Double.TYPE);
        BUILT_IN_MAP.put("float",  Float.TYPE);
        BUILT_IN_MAP.put("bool",   Boolean.TYPE);
        BUILT_IN_MAP.put("char",   Character.TYPE);
        BUILT_IN_MAP.put("byte",   Byte.TYPE);
        BUILT_IN_MAP.put("void",   Void.TYPE);
        BUILT_IN_MAP.put("short",  Short.TYPE);
    }
    // @formatter:on

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final AxKey userKey, final AxContextSchema schema) {
        super.init(userKey, schema);

        final String javatype = schema.getSchema();
        // For Java, the schema is the Java class canonical path

        try {
            setSchemaClass(Class.forName(schema.getSchema()));
        } catch (final Exception e) {

            String resultSting = userKey.getId() + ": class/type " + schema.getSchema() + " for context schema \""
                + schema.getId() + "\" not found.";
            if (JavaSchemaHelper.BUILT_IN_MAP.get(javatype) != null) {
                resultSting += " Primitive types are not supported. Use the appropriate Java boxing type instead.";
            } else {
                resultSting += " Check the class path of the JVM";
            }
            LOGGER.warn(resultSting);
            throw new ContextRuntimeException(resultSting, e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object createNewInstance(final Object incomingObject) {
        if (incomingObject == null) {
            return null;
        }

        if (getSchemaClass() == null) {
            final var returnString =
                getUserKey().getId() + ": could not create an instance, schema class for the schema is null";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        if (incomingObject instanceof JsonElement jsonObject) {
            final var elementJsonString = getGson().toJson(jsonObject);
            return getGson().fromJson(elementJsonString, this.getSchemaClass());
        }

        if (getSchemaClass().isAssignableFrom(incomingObject.getClass())) {
            return incomingObject;
        }

        final var returnString = getUserKey().getId() + ": the object \"" + incomingObject + "\" of type \""
            + incomingObject.getClass().getName()
            + "\" is not an instance of JsonObject and is not assignable to \"" + getSchemaClass().getName() + "\"";
        LOGGER.warn(returnString);
        throw new ContextRuntimeException(returnString);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object unmarshal(final Object object) {
        if (object == null) {
            return null;
        }

        // If the object is an instance of the incoming object, carry on
        if (object.getClass().equals(getSchemaClass())) {
            return object;
        }

        // For numeric types, do a numeric conversion
        if (Number.class.isAssignableFrom(getSchemaClass())) {
            return numericConversion(object);
        }

        if (getSchemaClass().isAssignableFrom(object.getClass())) {
            return object;
        } else {
            return stringConversion(object);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String marshal2String(final Object schemaObject) {
        if (schemaObject == null) {
            return "null";
        }

        // Check the incoming object is of a correct class
        if (getSchemaClass().isAssignableFrom(schemaObject.getClass())) {
            // Use Gson to translate the object
            return getGson().toJson(schemaObject);
        } else {
            final var returnString = getUserKey().getId() + ": object \"" + schemaObject.toString()
                + "\" of class \"" + schemaObject.getClass().getName() + "\" not compatible with class \""
                + getSchemaClass().getName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object marshal2Object(final Object schemaObject) {
        // Use Gson to marshal the schema object into a Json element to return
        return getGson().toJsonTree(schemaObject, getSchemaClass());
    }

    /**
     * Do a numeric conversion between numeric types.
     *
     * @param object The incoming numeric object
     * @return The converted object
     */
    private Object numericConversion(final Object object) {
        // Check if the incoming object is a number, if not do a string conversion
        if (object instanceof Number myNumber) {
            if (getSchemaClass().isAssignableFrom(Byte.class)) {
                return myNumber.byteValue();
            } else if (getSchemaClass().isAssignableFrom(Short.class)) {
                return myNumber.shortValue();
            } else if (getSchemaClass().isAssignableFrom(Integer.class)) {
                return myNumber.intValue();
            } else if (getSchemaClass().isAssignableFrom(Long.class)) {
                return myNumber.longValue();
            } else if (getSchemaClass().isAssignableFrom(Float.class)) {
                return myNumber.floatValue();
            } else if (getSchemaClass().isAssignableFrom(Double.class)) {
                return myNumber.doubleValue();
            }
        }

        // OK, we'll try and convert from a string representation of the incoming object
        return stringConversion(object);
    }

    /**
     * Do a string conversion to the class type.
     *
     * @param object The incoming numeric object
     * @return The converted object
     */
    private Object stringConversion(final Object object) {
        // OK, we'll try and convert from a string representation of the incoming object
        try {
            final Constructor<?> stringConstructor = getSchemaClass().getConstructor(String.class);
            return stringConstructor.newInstance(object.toString());
        } catch (final Exception e) {
            final var returnString = getUserKey().getId() + ": object \"" + object.toString() + "\" of class \""
                + object.getClass().getName() + "\" not compatible with class \"" + getSchemaClass().getName()
                + "\"";
            LOGGER.warn(returnString, e);
            throw new ContextRuntimeException(returnString);
        }
    }

    /**
     * Get a GSON instance that has the correct adaptation included.
     *
     * @return the GSON instance
     */
    private Gson getGson() {
        var gsonBuilder = new GsonBuilder().setPrettyPrinting();

        // Get the Java schema helper parameters from the parameter service
        var schemaParameters = (SchemaParameters) ParameterService.get(ContextParameterConstants.SCHEMA_GROUP_NAME);

        JavaSchemaHelperParameters javaSchemaHelperParmeters =
            (JavaSchemaHelperParameters) schemaParameters.getSchemaHelperParameterMap().get("Java");

        if (javaSchemaHelperParmeters == null) {
            javaSchemaHelperParmeters = new JavaSchemaHelperParameters();
        }

        for (JavaSchemaHelperJsonAdapterParameters jsonAdapterEntry : javaSchemaHelperParmeters.getJsonAdapters()
            .values()) {

            Object adapterObject;
            try {
                adapterObject = jsonAdapterEntry.getAdaptorClazz().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                final var returnString = getUserKey().getId() + ": instantiation of adapter class \""
                    + jsonAdapterEntry.getAdaptorClass() + "\"  to decode and encode class \""
                    + jsonAdapterEntry.getAdaptedClass() + "\" failed: " + e.getMessage();
                LOGGER.warn(returnString, e);
                throw new ContextRuntimeException(returnString);
            }

            gsonBuilder.registerTypeAdapter(jsonAdapterEntry.getAdaptedClazz(), adapterObject);
        }

        return gsonBuilder.create();
    }
}
