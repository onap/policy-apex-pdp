/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.CharacterDelimitedTextBlockReader;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlock;

/**
 * Test JSON Tagged Event Consumer.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JsonTaggedEventConsumerTest {

    @Test
    public void testGarbageText() throws IOException {
        verifyNoEvent("testGarbageText", "hello there");
    }

    @Test
    public void testPartialEvent() throws IOException {
        verifyNoEvent("testGarbageText", "\"TestTimestamp\": 1469781869268}");
    }

    @Test
    public void testFullEvent() throws IOException {
        verifyMulti("testFullEvent", "{TestTimestamp\": 1469781869268}", "{TestTimestamp\": 1469781869268}");
    }

    @Test
    public void testFullEventGarbageBefore() throws IOException {
        verifyMulti("testFullEventGarbageBefore", "Garbage{TestTimestamp\": 1469781869268}",
                        "{TestTimestamp\": 1469781869268}");
    }

    @Test
    public void testFullEventGarbageBeforeAfter() throws IOException {
        verifyMulti("testFullEventGarbageBeforeAfter", "Garbage{TestTimestamp\": 1469781869268}Rubbish",
                        "{TestTimestamp\": 1469781869268}");
    }

    @Test
    public void testFullEventGarbageAfter() throws IOException {
        verifyMulti("testFullEventGarbageAfter", "{TestTimestamp\": 1469781869268}Rubbish",
                        "{TestTimestamp\": 1469781869268}");
    }

    private void verifyNoEvent(String testName, String input) throws IOException {
        final InputStream jsonInputStream = new ByteArrayInputStream(input.getBytes());

        final CharacterDelimitedTextBlockReader taggedReader = new CharacterDelimitedTextBlockReader('{', '}');
        taggedReader.init(jsonInputStream);

        final TextBlock textBlock = taggedReader.readTextBlock();
        assertNull(testName, textBlock.getText());
        assertTrue(testName, textBlock.isEndOfText());
    }

    private void verifyMulti(String testName, String input, String expected) throws IOException {
        final InputStream jsonInputStream = new ByteArrayInputStream(input.getBytes());

        final CharacterDelimitedTextBlockReader taggedReader = new CharacterDelimitedTextBlockReader('{', '}');
        taggedReader.init(jsonInputStream);

        TextBlock textBlock = taggedReader.readTextBlock();
        assertEquals(testName, expected, textBlock.getText());
        assertFalse(testName, textBlock.isEndOfText());

        textBlock = taggedReader.readTextBlock();
        assertNull(testName, textBlock.getText());
        assertTrue(testName, textBlock.isEndOfText());
    }
}
