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

import java.util.Map.Entry;

import org.onap.policy.apex.client.editor.rest.handling.bean.BeanEvent;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanField;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles commands on events in Apex models.
 */
public class EventHandler implements RestCommandHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventHandler.class);

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
        if (!RestCommandType.EVENT.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case CREATE:
                return createEvent(session, jsonString);
            case UPDATE:
                return updateEvent(session, jsonString);
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
        if (!RestCommandType.EVENT.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case LIST:
                return listEvents(session, name, version);
            case DELETE:
                return deleteEvent(session, name, version);
            case VALIDATE:
                return validateEvent(session, name, version);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * Creates an event with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createEvent(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        final BeanEvent jsonbean = RestUtils.getJsonParameters(jsonString, BeanEvent.class);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().createEvent(jsonbean.getName(), jsonbean.getVersion(),
                        jsonbean.getNameSpace(), jsonbean.getSource(), jsonbean.getTarget(), jsonbean.getUuid(),
                        jsonbean.getDescription());

        if (result.isOk()) {
            result = createEventParameters(session, jsonbean);
        }

        session.finishSession(result.isOk());

        LOGGER.exit("Event/Create" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Create the parameters on an event.
     * 
     * @param session the Apex editor session
     * @param jsonbean the JSON bean holding the parameters
     * @param result the result of the parameter creation operation
     * @return
     */
    private ApexApiResult createEventParameters(final RestSession session, final BeanEvent jsonbean) {
        ApexApiResult result = new ApexApiResult();

        if (jsonbean.getParameters() == null || jsonbean.getParameters().isEmpty()) {
            return result;
        }

        for (final Entry<String, BeanField> parameterEntry : jsonbean.getParameters().entrySet()) {
            if (parameterEntry.getValue() == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null event parameter information for parameter \"" + parameterEntry.getKey()
                                + "\" in event " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The event was created, but there was an error adding the event parameters."
                                + " The event has only been partially defined.");
                continue;
            }

            final ApexApiResult createParResult = session.getApexModelEdited().createEventPar(jsonbean.getName(),
                            jsonbean.getVersion(), parameterEntry.getKey(), parameterEntry.getValue().getName(),
                            parameterEntry.getValue().getVersion(), parameterEntry.getValue().getOptional());
            if (createParResult.isNok()) {
                result.setResult(createParResult.getResult());
                result.addMessage("Failed to add event parameter information for parameter \"" + parameterEntry.getKey()
                                + "\" in event " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The event was created, but there was an error adding the event parameters."
                                + " The event has only been partially defined.");
            }
        }

        return result;
    }

    /**
     * Update an event with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult updateEvent(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        final BeanEvent jsonbean = RestUtils.getJsonParameters(jsonString, BeanEvent.class);

        if (blank2Null(jsonbean.getName()) == null || blank2Null(jsonbean.getVersion()) == null) {
            LOGGER.exit("Event/Update" + NOT_OK);
            return new ApexApiResult(Result.FAILED, "Null/Empty event name/version (\"" + jsonbean.getName() + ":"
                            + jsonbean.getVersion() + "\" passed to UpdateEvent");
        }

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().deleteEvent(blank2Null(jsonbean.getName()),
                        blank2Null(jsonbean.getVersion()));

        if (result.isOk()) {
            result = session.getApexModelEdited().createEvent(jsonbean.getName(), jsonbean.getVersion(),
                            jsonbean.getNameSpace(), jsonbean.getSource(), jsonbean.getTarget(), jsonbean.getUuid(),
                            jsonbean.getDescription());

            if (result.isOk() && jsonbean.getParameters() != null) {
                result = createEventParameters(session, jsonbean);
            }
        }

        session.finishSession(result.isOk());

        LOGGER.exit("Event/Update" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * List events with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@link AxEvent}, with merged {@linkplain AxKeyInfo} for the
     * root object.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult listEvents(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().listEvent(blank2Null(name), blank2Null(version));

        LOGGER.exit("Event/Get" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Delete events with the given key names/versions.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult deleteEvent(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().deleteEvent(blank2Null(name), blank2Null(version));

        session.finishSession(result.isOk());

        LOGGER.exit("Event/Delete" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Validate events with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult validateEvent(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().validateEvent(blank2Null(name), blank2Null(version));

        LOGGER.exit("Validate/Event" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }
}
