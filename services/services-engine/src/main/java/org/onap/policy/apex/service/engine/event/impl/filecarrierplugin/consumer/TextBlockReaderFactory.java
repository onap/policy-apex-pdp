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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer;

import java.io.InputStream;

import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextCharDelimitedParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextTokenDelimitedParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This factory creates text block readers for breaking character streams into blocks of text.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TextBlockReaderFactory {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TextBlockReaderFactory.class);

    /**
     * Get a text block reader for the given event protocol.
     *
     * @param inputStream the input stream that will be used for reading
     * @param eventProtocolParameters the parameters that have been specified for event protocols
     * @return the tagged reader
     * @throws ApexEventException On an unsupported event protocol
     */
    public TextBlockReader getTaggedReader(final InputStream inputStream,
            final EventProtocolParameters eventProtocolParameters) throws ApexEventException {
        // Check the type of event protocol we have
        if (eventProtocolParameters instanceof EventProtocolTextCharDelimitedParameters) {
            // We have character delimited textual input
            final EventProtocolTextCharDelimitedParameters charDelimitedParameters =
                    (EventProtocolTextCharDelimitedParameters) eventProtocolParameters;

            // Create the text block reader
            final TextBlockReader characterDelimitedTextBlockReader =
                    new CharacterDelimitedTextBlockReader(charDelimitedParameters);
            characterDelimitedTextBlockReader.init(inputStream);
            return characterDelimitedTextBlockReader;
        } else if (eventProtocolParameters instanceof EventProtocolTextTokenDelimitedParameters) {
            // We have token delimited textual input
            final EventProtocolTextTokenDelimitedParameters tokenDelimitedParameters =
                    (EventProtocolTextTokenDelimitedParameters) eventProtocolParameters;

            // Create the text block reader
            final HeaderDelimitedTextBlockReader headerDelimitedTextBlockReader =
                    new HeaderDelimitedTextBlockReader(tokenDelimitedParameters);
            headerDelimitedTextBlockReader.init(inputStream);
            return headerDelimitedTextBlockReader;
        } else {
            final String errorMessage =
                    "could not create text block reader for a textual event protocol, the required type "
                            + eventProtocolParameters.getLabel() + " is not supported";
            LOGGER.error(errorMessage);
            throw new ApexEventException(errorMessage);
        }
    }
}
