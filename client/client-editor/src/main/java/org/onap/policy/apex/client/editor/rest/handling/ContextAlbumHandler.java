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

import org.onap.policy.apex.client.editor.rest.handling.bean.BeanContextAlbum;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles commands on context albums in Apex models.
 */
public class ContextAlbumHandler implements RestCommandHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextAlbumHandler.class);

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
        if (!RestCommandType.CONTEXT_ALBUM.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case CREATE:
                return createContextAlbum(session, jsonString);
            case UPDATE:
                return updateContextAlbum(session, jsonString);
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
        if (!RestCommandType.CONTEXT_ALBUM.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case LIST:
                return listContextAlbums(session, name, version);
            case DELETE:
                return deleteContextAlbum(session, name, version);
            case VALIDATE:
                return validateContextAlbum(session, name, version);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * Creates a context album with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createContextAlbum(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        session.editModel();

        final BeanContextAlbum jsonbean = RestUtils.getJsonParameters(jsonString, BeanContextAlbum.class);

        ApexApiResult result = session.getApexModelEdited().createContextAlbum(jsonbean.getName(),
                        jsonbean.getVersion(), jsonbean.getScope(), Boolean.toString(jsonbean.getWriteable()),
                        jsonbean.getItemSchema().getName(), jsonbean.getItemSchema().getVersion(), jsonbean.getUuid(),
                        jsonbean.getDescription());

        if (result.isOk()) {
            session.commitChanges();
        } else {
            session.discardChanges();
        }

        LOGGER.exit("ContextAlbum/Create" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Update a context album with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult updateContextAlbum(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);
        
        session.editModel();

        final BeanContextAlbum jsonbean = RestUtils.getJsonParameters(jsonString, BeanContextAlbum.class);

        ApexApiResult result = session.getApexModelEdited().updateContextAlbum(jsonbean.getName(),
                        jsonbean.getVersion(), jsonbean.getScope(), Boolean.toString(jsonbean.getWriteable()),
                        jsonbean.getItemSchema().getName(), jsonbean.getItemSchema().getVersion(), jsonbean.getUuid(),
                        jsonbean.getDescription());

        if (result.isOk()) {
            session.commitChanges();
        } else {
            session.discardChanges();
        }
        
        LOGGER.exit("ContextAlbum/Update" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * List context albums with the given key names/versions. If successful the result(s) will be available in the
     * result messages. The returned value(s) will be similar to {@link AxContextAlbum}, with merged
     * {@linkplain AxKeyInfo} for the root object.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult listContextAlbums(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().listContextAlbum(blank2Null(name), blank2Null(version));

        LOGGER.exit("ContextAlbum/Get" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Delete context albums with the given key names/versions.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult deleteContextAlbum(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().deleteContextAlbum(blank2Null(name), blank2Null(version));

        if (result.isOk()) {
            session.commitChanges();
        } else {
            session.discardChanges();
        }

        LOGGER.exit("ContextAlbum/Delete" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Validate context albums with the given key names/versions. The result(s) will be available in the result
     * messages.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult validateContextAlbum(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().validateContextAlbum(blank2Null(name), blank2Null(version));

        LOGGER.exit("Validate/ContextAlbum" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }
}
