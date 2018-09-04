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

package org.onap.policy.apex.core.engine.executor.context;

import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.core.engine.event.EnException;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineRuntimeException;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class AxTaskFacade acts as a facade into the AxTask class so that task logic can easily
 * access information in an AxTask instance.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class AxTaskFacade {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AxTaskFacade.class);

    // CHECKSTYLE:OFF: checkstyle:visibilityModifier Logic has access to this field

    /**
     * The full definition of the task we are presenting a facade to, executing logic has full
     * access to the task definition.
     */
    public final AxTask task;

    // CHECKSTYLE:ON: checkstyle:visibilityModifier

    /**
     * Instantiates a new AxTask facade.
     *
     * @param task the task for which a facade is being presented
     */
    public AxTaskFacade(final AxTask task) {
        this.task = task;
    }

    /**
     * Gets the name of the task.
     *
     * @return the task name
     */
    public String getTaskName() {
        return task.getKey().getName();
    }

    /**
     * Gets the task ID.
     *
     * @return the task ID
     */
    public String getId() {
        return task.getId();
    }

    /**
     * Creates a schema helper for an incoming field of this task.
     *
     * @param fieldName The name of the field to get a schema helper for
     * @return the schema helper for this field
     */
    public SchemaHelper getInFieldSchemaHelper(final String fieldName) {
        // Find the field for the field name
        return getFieldSchemaHelper(fieldName, task.getInputFields().get(fieldName), "incoming");
    }

    /**
     * Creates a schema helper for an outgoing field of this task.
     *
     * @param fieldName The name of the field to get a schema helper for
     * @return the schema helper for this field
     */
    public SchemaHelper getOutFieldSchemaHelper(final String fieldName) {
        // Find the field for the field name
        return getFieldSchemaHelper(fieldName, task.getOutputFields().get(fieldName), "outgoing");
    }

    /**
     * Creates a schema helper for an incoming field of this task.
     *
     * @param fieldName The name of the field to get a schema helper for
     * @param field the field
     * @param directionString the direction string
     * @return the schema helper for this field
     */
    private SchemaHelper getFieldSchemaHelper(final String fieldName, final AxField field,
            final String directionString) {
        // Find the field for the field name
        if (field == null) {
            final String message = "no " + directionString + " field with name \"" + fieldName + "\" defined on task \""
                    + task.getId() + "\"";
            LOGGER.warn(message);
            throw new StateMachineRuntimeException(message);
        }

        // Get a schema helper to handle translations of fields to and from the schema
        try {
            return new SchemaHelperFactory().createSchemaHelper(field.getKey(), field.getSchema());
        } catch (final ContextRuntimeException e) {
            final String message = "schema helper cannot be created for task field \"" + fieldName + "\" with key \""
                    + field.getId() + "\" with schema \"" + field.getSchema() + "\"";
            LOGGER.warn(message, e);
            throw new EnException(message, e);
        }
    }

}
