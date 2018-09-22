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

import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;

/**
 * This interface defines the methods that a REST handler must implement to handle REST editor commands.
 *
 */
public interface RestCommandHandler {

    /**
     * Process a REST command.
     *
     * @param session the Apex editor session
     * @param commandType the type of REST command to execute
     * @param command the REST command to execute
     * @return the apex api result the result of the execution
     */
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command);

    /**
     * Process a REST command.
     *
     * @param session the Apex editor session
     * @param commandType the type of REST command to execute
     * @param command the REST command to execute
     * @param jsonString the json string to use to execute the command
     * @return the apex api result the result of the execution
     */
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String jsonString);

    /**
     * Process a REST command.
     *
     * @param session the Apex editor session
     * @param commandType the type of REST command to execute
     * @param command the REST command to execute
     * @param name the concept name on which to execute
     * @param version the concept version the version on which to execute
     * @return the apex api result the result of the execution
     */
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String name, final String version);

    /**
     * Get an unsupported command result message.
     *
     * @param session the Apex editor session
     * @param commandType the type of REST command to execute
     * @param command the REST command to execute
     */
    public default ApexApiResult getUnsupportedCommandResultMessage(final RestSession session,
                    final RestCommandType commandType, final RestCommand command) {
        return new ApexApiResult(Result.FAILED, "session " + session.getSessionId() + ", command type " + commandType
                        + ", command" + command + " invalid");
    }
    
    /**
     * Convert blank incoming fields to nulls.
     * 
     * @param incomingField the field to check
     * @return null if the field is blank, otherwise, the field trimmed
     */
    public default String blank2Null(final String incomingField) {
        if (incomingField == null) {
            return null;
        }
        
        String trimmedField = incomingField.trim();
        
        if (trimmedField.isEmpty()) {
            return null;
        }
        else {
            return trimmedField;
        }
    }
}
