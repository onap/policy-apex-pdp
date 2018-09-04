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

package org.onap.policy.apex.context.impl.schema;

import java.lang.reflect.Constructor;

import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class implements the {@link SchemaHelper} functionality that is common across all
 * implementations. Schema helpers for specific schema mechanisms specialize this class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class AbstractSchemaHelper implements SchemaHelper {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AbstractSchemaHelper.class);

    // The key of the user of this schema helper
    private AxKey userKey = AxArtifactKey.getNullKey();

    // The schema of this schema helper
    private AxContextSchema schema = null;

    // The class of objects for this schema
    private Class<?> schemaClass;

    /**
     * Sets the schema class for the schema, designed jots to be called by sub classes.
     *
     * @param schemaClass the Java class that is used to hold items of this schema
     */
    protected void setSchemaClass(final Class<?> schemaClass) {
        this.schemaClass = schemaClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.context.SchemaHelper#init(org.onap.policy.apex.model.basicmodel.concepts
     * .AxKey, org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema)
     */
    @Override
    public void init(final AxKey incomingUserKey, final AxContextSchema incomingSchema) throws ContextRuntimeException {
        Assertions.argumentNotNull(incomingUserKey, ContextRuntimeException.class, "incomingUserKey may not be null");
        Assertions.argumentNotNull(incomingSchema, ContextRuntimeException.class, "incomingSchema may not be null");

        this.userKey = incomingUserKey;
        this.schema = incomingSchema;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.SchemaHelper#getKey()
     */
    @Override
    public AxKey getUserKey() {
        return userKey;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.SchemaHelper#getSchema()
     */
    @Override
    public AxContextSchema getSchema() {
        return schema;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.SchemaHelper#getSchemaClass()
     */
    @Override
    public Class<?> getSchemaClass() {
        return schemaClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.SchemaHelper#getSchemaObject()
     */
    @Override
    public Object getSchemaObject() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.SchemaHelper#createNewInstance()
     */
    @Override
    public Object createNewInstance() {
        if (schemaClass == null) {
            final String returnString =
                    userKey.getId() + ": could not create an instance, schema class for the schema is null";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        try {
            return schemaClass.newInstance();
        } catch (final Exception e) {
            final String returnString =
                    userKey.getId() + ": could not create an instance of class \"" + schemaClass.getCanonicalName()
                            + "\" using the default constructor \"" + schemaClass.getSimpleName() + "()\"";
            LOGGER.warn(returnString, e);
            throw new ContextRuntimeException(returnString, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.SchemaHelper#createNewInstance(java.lang.String)
     */
    @Override
    public Object createNewInstance(final String stringValue) {
        if (schemaClass == null) {
            final String returnString =
                    userKey.getId() + ": could not create an instance, schema class for the schema is null";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        try {
            // Find a string constructor
            final Constructor<?> stringConstructor = schemaClass.getConstructor(String.class);

            // Invoke the constructor
            return stringConstructor.newInstance(stringValue);
        } catch (final Exception e) {
            final String returnString =
                    userKey.getId() + ": could not create an instance of class \"" + schemaClass.getCanonicalName()
                            + "\" using the string constructor \"" + schemaClass.getSimpleName() + "(String)\"";
            LOGGER.warn(returnString, e);
            throw new ContextRuntimeException(returnString);
        }
    }
}
