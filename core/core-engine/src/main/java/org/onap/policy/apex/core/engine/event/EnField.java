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

package org.onap.policy.apex.core.engine.event;

import java.io.Serializable;

import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Instances of the Class EnField are event fields being passed through the Apex system.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EnField implements Serializable {
    private static final long serialVersionUID = -5713525780081840333L;

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EnField.class);

    // The definition of this field in the Apex model
    private final AxField axField;

    // The schema helper for this field
    private SchemaHelper schemaHelper;

    // The value of this field
    private final Object value;

    /**
     * Instantiates a new EnField, an Engine Field.
     *
     * @param axField the field definition from the Apex model
     * @param value the value
     */
    public EnField(final AxField axField, final Object value) {
        // Save the field definition from the Apex model
        this.axField = axField;
        this.value = value;

        // Get a schema helper to handle translations of fields to and from the schema
        try {
            schemaHelper = new SchemaHelperFactory().createSchemaHelper(axField.getKey(), axField.getSchema());
        } catch (final ContextRuntimeException e) {
            final String message = "schema helper cannot be created for parameter with key \"" + axField.getID()
                    + "\" with schema \"" + axField.getSchema() + "\"";
            LOGGER.warn(message, e);
            throw new EnException(message, e);
        }
    }

    /**
     * Gets the field definition of this field.
     *
     * @return the field definition
     */
    public AxField getAxField() {
        return axField;
    }

    /**
     * Gets the schema helper of this field.
     *
     * @return the schema helper for this field
     */
    public SchemaHelper getSchemaHelper() {
        return schemaHelper;
    }

    /**
     * Get the name of the field.
     *
     * @return the field name
     */
    public String getName() {
        return axField.getKey().getLocalName();
    }

    /**
     * Get the key of the field.
     *
     * @return the field key
     */
    public AxReferenceKey getKey() {
        return axField.getKey();
    }

    /**
     * Get the value of the field.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EnField [axField=" + axField + ", value=" + value + "]";
    }

    /**
     * Get an assignable object that will work with the field.
     *
     * @return the assignable value
     */
    public Object getAssignableValue() {
        // Use the schema helper to get the translated value of the object
        return schemaHelper.unmarshal(value);
    }

    /**
     * Is the value object assignable to this field.
     *
     * @return true if the value is assignable
     */
    public boolean isAssignableValue() {
        try {
            schemaHelper.unmarshal(value);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
}
