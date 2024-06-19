/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.CharacterDelimitedTextBlockReader;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlock;

/**
 * Test JSON Tagged Event Consumer.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class JsonTaggedEventConsumerTest {

    @Test
    void testGarbageText() throws IOException {
        verifyNoEvent("hello there");
    }

    @Test
    void testPartialEvent() throws IOException {
        verifyNoEvent("\"TestTimestamp\": 1469781869268}");
    }

    @Test
    void testFullEvent() throws IOException {
        verifyMulti("{TestTimestamp\": 1469781869268}");
    }

    @Test
    void testFullEventGarbageBefore() throws IOException {
        verifyMulti("Garbage{TestTimestamp\": 1469781869268}");
    }

    @Test
    void testFullEventGarbageBeforeAfter() throws IOException {
        verifyMulti("Garbage{TestTimestamp\": 1469781869268}Rubbish");
    }

    @Test
    void testFullEventGarbageAfter() throws IOException {
        verifyMulti("{TestTimestamp\": 1469781869268}Rubbish");
    }

    private void verifyNoEvent(String input) throws IOException {
        final InputStream jsonInputStream = new ByteArrayInputStream(input.getBytes());

        final CharacterDelimitedTextBlockReader taggedReader = new CharacterDelimitedTextBlockReader('{', '}');
        taggedReader.init(jsonInputStream);

        final TextBlock textBlock = taggedReader.readTextBlock();
        assertNull(textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    private void verifyMulti(String input) throws IOException {
        final InputStream jsonInputStream = new ByteArrayInputStream(input.getBytes());

        final CharacterDelimitedTextBlockReader taggedReader = new CharacterDelimitedTextBlockReader('{', '}');
        taggedReader.init(jsonInputStream);

        TextBlock textBlock = taggedReader.readTextBlock();
        assertEquals("{TestTimestamp\": 1469781869268}", textBlock.getText());
        assertFalse(textBlock.isEndOfText());

        textBlock = taggedReader.readTextBlock();
        assertNull(textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }
}
