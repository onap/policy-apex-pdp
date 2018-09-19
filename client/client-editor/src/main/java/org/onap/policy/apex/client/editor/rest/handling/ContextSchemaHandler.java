/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.client.editor.rest.handling;

import org.onap.policy.apex.client.editor.rest.handling.bean.BeanContextSchema;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles commands on context schemas in Apex models.
 */
public class ContextSchemaHandler implements RestCommandHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextSchemaHandler.class);

    // Recurring string constants
    private static final String OK = ": OK";
    private static final String NOT_OK = ": Not OK";

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command) {
        return getUnsupportedCommandResultMessage(session, commandType, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String jsonString) {
        if (!RestCommandType.CONTEXT_SCHEMA.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case CREATE:
                return createContextSchema(session, jsonString);
            case UPDATE:
                return updateContextSchema(session, jsonString);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String name, final String version) {
        if (!RestCommandType.CONTEXT_SCHEMA.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case LIST:
                return listContextSchemas(session, name, version);
            case DELETE:
                return deleteContextSchema(session, name, version);
            case VALIDATE:
                return validateContextSchemas(session, name, version);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * Creates a context schema.
     *
     * @param session the session holding the Apex model
     * @param jsonString the JSON string with the context schema parameters
     * @return the result of the operation
     */
    private ApexApiResult createContextSchema(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        session.editModel();

        final BeanContextSchema jsonbean = RestUtils.getJsonParameters(jsonString, BeanContextSchema.class);
        ApexApiResult result = session.getApexModelEdited().createContextSchema(jsonbean.getName(),
                        jsonbean.getVersion(), jsonbean.getSchemaFlavour(), jsonbean.getSchemaDefinition(),
                        jsonbean.getUuid(), jsonbean.getDescription());

        session.finishSession(result.isOk());

        LOGGER.exit("ContextSchema/create" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Update a context schema.
     *
     * @param session the session holding the Apex model
     * @param jsonString the JSON string with the context schema parameters
     * @return the result of the operation
     */
    private ApexApiResult updateContextSchema(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        session.editModel();

        final BeanContextSchema jsonbean = RestUtils.getJsonParameters(jsonString, BeanContextSchema.class);

        ApexApiResult result = session.getApexModelEdited().updateContextSchema(jsonbean.getName(),
                        jsonbean.getVersion(), jsonbean.getSchemaFlavour(), jsonbean.getSchemaDefinition(),
                        jsonbean.getUuid(), jsonbean.getDescription());

        session.finishSession(result.isOk());

        LOGGER.exit("ContextSchema/Update" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * List context schemas.
     *
     * @param session the session holding the Apex model
     * @param name the context schema name to operate on
     * @param version the context schema version to operate on
     * @return the result of the operation
     */
    private ApexApiResult listContextSchemas(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().listContextSchemas(blank2Null(name), blank2Null(version));

        LOGGER.exit("ContextSchema/Get" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Delete a context schema.
     *
     * @param session the session holding the Apex model
     * @param name the context schema name to operate on
     * @param version the context schema version to operate on
     * @return the result of the operation
     */
    private ApexApiResult deleteContextSchema(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().deleteContextSchema(blank2Null(name), blank2Null(version));

        session.finishSession(result.isOk());

        LOGGER.exit("ContextSchema/Delete" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Validate a context schema.
     * 
     * @param session the session holding the Apex model
     * @param name the context schema name to operate on
     * @param version the context schema version to operate on
     * @return the result of the operation
     */
    private ApexApiResult validateContextSchemas(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().validateContextSchemas(blank2Null(name), blank2Null(version));

        LOGGER.exit("Validate/ContextSchema" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }
}
