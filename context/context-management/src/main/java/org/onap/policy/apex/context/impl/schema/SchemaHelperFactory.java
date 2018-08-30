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

import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaHelperParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.utilities.Assertions;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class returns a {@link SchemaHelper} for the particular type of schema mechanism configured for use.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SchemaHelperFactory {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(SchemaHelperFactory.class);

    /**
     * Return a {@link SchemaHelper} for the particular type of schema mechanism configured for use.
     *
     * @param owningEntityKey The key of the entity that owns the schema helper
     * @param schemaKey The key of the schema the schema helper is operating on
     * @return a lock schema that can handle translation of objects in a particular schema format
     * @throws ContextRuntimeException the context runtime exception
     */
    public SchemaHelper createSchemaHelper(final AxKey owningEntityKey, final AxArtifactKey schemaKey) {
        LOGGER.entry("schema helper factory, owningEntityKey=" + owningEntityKey);
        Assertions.argumentNotNull(owningEntityKey, ContextRuntimeException.class,
                "Parameter \"owningEntityKey\" may not be null");
        Assertions.argumentNotNull(schemaKey, ContextRuntimeException.class, "Parameter \"schemaKey\" may not be null");

        // Get the schema for items in the album
        final AxContextSchema schema = ModelService.getModel(AxContextSchemas.class).get(schemaKey);
        if (schema == null) {
            final String resultString =
                    "schema \"" + schemaKey.getID() + "\" for entity " + owningEntityKey.getID() + " does not exist";
            LOGGER.warn(resultString);
            throw new ContextRuntimeException(resultString);
        }

        // Get the schema class using the parameter service
        final SchemaParameters schemaParameters = ParameterService.get(ContextParameterConstants.SCHEMA_GROUP_NAME);

        // Get the class for the schema helper from the schema parameters
        final SchemaHelperParameters schemaHelperParameters =
                schemaParameters.getSchemaHelperParameters(schema.getSchemaFlavour());
        if (schemaHelperParameters == null) {
            final String resultString = "context schema helper parameters not found for context schema  \""
                    + schema.getSchemaFlavour() + "\"";
            LOGGER.warn(resultString);
            throw new ContextRuntimeException(resultString);
        }

        // Get the class for the schema helper using reflection
        Object schemaHelperObject = null;
        final String pluginClass = schemaHelperParameters.getSchemaHelperPluginClass();
        try {
            schemaHelperObject = Class.forName(pluginClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            final String resultString = "Apex context schema helper class not found for context schema helper plugin \""
                    + pluginClass + "\"";
            LOGGER.warn(resultString, e);
            throw new ContextRuntimeException(resultString, e);
        }

        // Check the class is a schema helper
        if (!(schemaHelperObject instanceof SchemaHelper)) {
            final String resultString = "Specified Apex context schema helper plugin class \"" + pluginClass
                    + "\" does not implement the SchemaHelper interface";
            LOGGER.warn(resultString);
            throw new ContextRuntimeException(resultString);
        }

        // The context schema helper to return
        final SchemaHelper schemaHelper = (SchemaHelper) schemaHelperObject;

        // Lock and load the schema helper
        schemaHelper.init(owningEntityKey.getKey(), schema);

        LOGGER.exit("Schema Helper factory, owningEntityKey=" + owningEntityKey + ", selected schema helper of class "
                + schemaHelper.getClass());
        return schemaHelper;
    }
}
