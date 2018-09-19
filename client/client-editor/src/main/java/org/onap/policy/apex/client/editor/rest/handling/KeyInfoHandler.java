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

/**
 * This class handles commands on key information in Apex models.
 */
public class KeyInfoHandler implements RestCommandHandler {
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
        return getUnsupportedCommandResultMessage(session, commandType, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String name, final String version) {

        if (RestCommandType.KEY_INFO.equals(commandType) && RestCommand.LIST.equals(command)) {
            return listKeyInformation(session, name, version);
        }
        else {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * Get the key information for a concept with the given name and version.
     * @param session the editor session containing the Apex model
     * @param name the name for the search
     * @param version the version for the search
     * @return the key information
     */
    private ApexApiResult listKeyInformation(final RestSession session, final String name, final String version) {
        return session.getApexModel().listKeyInformation(blank2Null(name), blank2Null(version));
    }
}
