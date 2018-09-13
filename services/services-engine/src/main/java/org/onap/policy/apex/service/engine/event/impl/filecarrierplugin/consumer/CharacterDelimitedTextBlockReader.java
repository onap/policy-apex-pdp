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

import java.io.IOException;
import java.io.InputStream;

import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextCharDelimitedParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The class CharacterDelimitedTextBlockReader reads the next block of text between two character
 * tags from an input stream.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CharacterDelimitedTextBlockReader implements TextBlockReader {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(CharacterDelimitedTextBlockReader.class);

    // The character tags
    private final char startTagChar;
    private final char endTagChar;

    // The input stream for text
    private InputStream inputStream;

    // Flag indicating we have seen EOF on the stream
    private boolean eofOnInputStream = false;

    /**
     * Constructor, set the delimiters.
     *
     * @param startTagChar The start tag for text blocks
     * @param endTagChar The end tag for text blocks
     */
    public CharacterDelimitedTextBlockReader(final char startTagChar, final char endTagChar) {
        this.startTagChar = startTagChar;
        this.endTagChar = endTagChar;
    }

    /**
     * Constructor, set the delimiters from a character delimited event protocol parameter class.
     *
     * @param charDelimitedParameters the character delimited event protocol parameter class
     */
    public CharacterDelimitedTextBlockReader(final EventProtocolTextCharDelimitedParameters charDelimitedParameters) {
        this.startTagChar = charDelimitedParameters.getStartChar();
        this.endTagChar = charDelimitedParameters.getEndChar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlockReader#init(
     * java.io.InputStream)
     */
    @Override
    public void init(final InputStream incomingInputStream) {
        this.inputStream = incomingInputStream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlockReader#
     * readTextBlock()
     */
    @Override
    public TextBlock readTextBlock() throws IOException {
        // Check if there was a previous end of a text block with a non-empty text block returned
        if (eofOnInputStream) {
            return new TextBlock(eofOnInputStream, null);
        }

        // Read the block of text
        final StringBuilder textBlockBuilder = readTextBlockText();

        // Condition the text block and return it
        final String textBlock = textBlockBuilder.toString().trim();
        if (textBlock.length() > 0) {
            return new TextBlock(eofOnInputStream, textBlock);
        } else {
            return new TextBlock(eofOnInputStream, null);
        }
    }

    /**
     * Read a block of text.
     * @return A string builder containing the text
     * @throws IOException on read errors
     */
    private StringBuilder readTextBlockText() throws IOException {
        // Holder for the text block
        final StringBuilder textBlockBuilder = new StringBuilder();

        int nestingLevel = 0;
        
        // Read the next text block
        while (true) {
            final char nextChar = (char) inputStream.read();

            // Check for EOF
            if (nextChar == (char) -1) {
                eofOnInputStream = true;
                return textBlockBuilder;
            }

            if (nextChar == startTagChar) {
                nestingLevel++;
            } else if (nestingLevel == 0 && !Character.isWhitespace(nextChar)) {
                LOGGER.warn("invalid input on consumer: {}", nextChar);
                continue;
            }

            textBlockBuilder.append(nextChar);

            // Check for end of the text block, we have come back to level 0
            if (nextChar == endTagChar) {
                if (nestingLevel > 0) {
                    nestingLevel--;
                }

                if (nestingLevel == 0) {
                    return textBlockBuilder;
                }
            }
        }
    }
}
