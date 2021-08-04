/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.common.utils.validation.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class implements the {@link SchemaHelper} functionality that is common across all implementations. Schema
 * helpers for specific schema mechanisms specialize this class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final AxKey incomingUserKey, final AxContextSchema incomingSchema) {
        Assertions.argumentOfClassNotNull(incomingUserKey, ContextRuntimeException.class,
                "incomingUserKey may not be null");
        Assertions.argumentOfClassNotNull(incomingSchema, ContextRuntimeException.class,
                "incomingSchema may not be null");

        this.userKey = incomingUserKey;
        this.schema = incomingSchema;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getSchemaObject() {
        return null;
    }

    /**
     * {@inheritDoc}.
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
            return schemaClass.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            final String returnString =
                    userKey.getId() + ": could not create an instance of class \"" + schemaClass.getName()
                            + "\" using the default constructor \"" + schemaClass.getSimpleName() + "()\"";
            LOGGER.warn(returnString, e);
            throw new ContextRuntimeException(returnString, e);
        }
    }

    /**
     * {@inheritDoc}.
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
                    userKey.getId() + ": could not create an instance of class \"" + schemaClass.getName()
                            + "\" using the string constructor \"" + schemaClass.getSimpleName() + "(String)\"";
            LOGGER.warn(returnString, e);
            throw new ContextRuntimeException(returnString);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object createNewSubInstance(String subType) {
        throw new NotImplementedException("sub types are not supported on this schema helper");
    }
}
