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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextTokenDelimitedParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TextBlockReader reads the next block of text from an input stream.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class HeaderDelimitedTextBlockReader implements TextBlockReader, Runnable {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(HeaderDelimitedTextBlockReader.class);

    // The amount of time to wait for input on the text block reader
    private static final long TEXT_BLOCK_DELAY = 250;

    // Tag for the start and end of text blocks
    private final String blockStartToken;
    private final String blockEndToken;

    // Indicates that text block processing starts at the first block of text
    private final boolean delimiterAtStart;
    private boolean blockEndTokenUsed = false;

    // The thread used to read the text from the stream
    Thread textConsumputionThread;

    // The input stream for text
    private InputStream inputStream;

    // The lines of input read from the input stream
    private final Queue<String> textLineQueue = new LinkedBlockingQueue<>();

    // True while EOF has not been seen on input
    private boolean eofOnInputStream = false;

    /**
     * Constructor, initialize the text block reader using token delimited event protocol parameters.
     *
     * @param tokenDelimitedParameters
     *        the token delimited event protocol parameters
     */
    public HeaderDelimitedTextBlockReader(final EventProtocolTextTokenDelimitedParameters tokenDelimitedParameters) {
        this(tokenDelimitedParameters.getStartDelimiterToken(), tokenDelimitedParameters.getEndDelimiterToken(),
                        tokenDelimitedParameters.isDelimiterAtStart());
    }

    /**
     * Constructor, initialize the text block reader.
     *
     * @param blockStartToken
     *        the block start token for the start of a text block
     * @param blockEndToken
     *        the block end token for the end of a text block
     * @param delimiterAtStart
     *        indicates that text block processing starts at the first block of text
     */
    public HeaderDelimitedTextBlockReader(final String blockStartToken, final String blockEndToken,
                    final boolean delimiterAtStart) {
        this.blockStartToken = blockStartToken;
        this.delimiterAtStart = delimiterAtStart;

        if (blockEndToken == null) {
            this.blockEndToken = blockStartToken;
            this.blockEndTokenUsed = false;
        } else {
            this.blockEndToken = blockEndToken;
            this.blockEndTokenUsed = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlockReader# init(
     * java.io.InputStream)
     */
    @Override
    public void init(final InputStream incomingInputStream) {
        this.inputStream = incomingInputStream;

        // Configure and start the text reading thread
        textConsumputionThread = new ApplicationThreadFactory(this.getClass().getName()).newThread(this);
        textConsumputionThread.setDaemon(true);
        textConsumputionThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlockReader# readTextBlock()
     */
    @Override
    public TextBlock readTextBlock() throws IOException {
        // Holder for the current text block
        final StringBuilder textBlockBuilder = new StringBuilder();

        // Wait for the timeout period if there is no input
        if (!eofOnInputStream && textLineQueue.isEmpty()) {
            ThreadUtilities.sleep(TEXT_BLOCK_DELAY);
        }

        // Scan the lines in the queue
        while (!textLineQueue.isEmpty()) {
            // Scroll down in the available lines looking for the start of the text block
            if (!delimiterAtStart || textLineQueue.peek().startsWith(blockStartToken)) {
                // Process the input line header
                textBlockBuilder.append(textLineQueue.remove());
                textBlockBuilder.append('\n');
                break;
            } else {
                String consumer = textLineQueue.remove();
                LOGGER.warn("invalid input on consumer: {}", consumer);
            }
        }

        // Get the rest of the text document
        while (!textLineQueue.isEmpty() && !textLineQueue.peek().startsWith(blockEndToken)
                        && !textLineQueue.peek().startsWith(blockStartToken)) {
            // We just strip out block end tokens because we use block start tokens to delimit the blocks of text
            textBlockBuilder.append(textLineQueue.remove());
            textBlockBuilder.append('\n');
        }

        // Check if we should add the block end token to the end of the text block
        if (!textLineQueue.isEmpty() && blockEndTokenUsed && textLineQueue.peek().startsWith(blockEndToken)) {
            // Process the input line header
            textBlockBuilder.append(textLineQueue.remove());
            textBlockBuilder.append('\n');
        }

        // Condition the text block and return it
        final String textBlock = textBlockBuilder.toString().trim();
        final boolean endOfText = (eofOnInputStream && textLineQueue.isEmpty() ? true : false);

        if (textBlock.length() > 0) {
            return new TextBlock(endOfText, textBlock);
        } else {
            return new TextBlock(endOfText, null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        final BufferedReader textReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            // Read the input line by line until we see end of file on the stream
            String line;
            while ((line = textReader.readLine()) != null) {
                textLineQueue.add(line);
            }
        } catch (final IOException e) {
            LOGGER.warn("I/O exception on text input on consumer: ", e);
        } finally {
            eofOnInputStream = true;
        }
    }
}
