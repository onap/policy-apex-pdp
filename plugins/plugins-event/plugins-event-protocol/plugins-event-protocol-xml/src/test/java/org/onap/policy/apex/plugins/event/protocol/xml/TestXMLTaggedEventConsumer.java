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

package org.onap.policy.apex.plugins.event.protocol.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.HeaderDelimitedTextBlockReader;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlock;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestXMLTaggedEventConsumer {
    @Test
    public void testGarbageTextLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream("hello there".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertNull(textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testPartialEventLine() throws IOException {
        final InputStream xmlInputStream =
                new ByteArrayInputStream("1469781869268</TestTimestamp></MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertNull(textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(), "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventGarbageBeforeLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventGarbageBeforeAfterLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventGarbageAfterLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testGarbageTextMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream("hello\nthere".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testPartialEventMultiLine() throws IOException {
        final InputStream xmlInputStream =
                new ByteArrayInputStream("1469781869268\n</TestTimestamp>\n</MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n\n".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventGarbageBeforeMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n\n".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventGarbageBeforeAfterMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish\n\n"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventGarbageAfterMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testPartialEventsLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "1469781869268</TestTimestamp></MainTag><?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp>"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventsGarbageBeforeLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag><?xml><MainTag><TestTimestamp>"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventsGarbageBeforeAfterLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish<?xml><MainTag><TestTimestamp>\nRefuse"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventsGarbageAfterLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish<?xml><MainTag><TestTimestamp>Refuse"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish<?xml><MainTag><TestTimestamp>Refuse");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testPartialEventsMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "1469781869268\n</TestTimestamp>\n</MainTag>\n<?xml>\n<MainTag>\n<TestTimestamp>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(), "<?xml>\n<MainTag>\n<TestTimestamp>");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventsMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
        assertFalse(textBlock.isEndOfText());

        textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventsGarbageBeforeMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
        assertFalse(textBlock.isEndOfText());

        textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventsGarbageBeforeAfterMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRefuse\n"
                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish");
        assertFalse(textBlock.isEndOfText());

        textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRefuse");
        assertTrue(textBlock.isEndOfText());
    }

    @Test
    public void testFullEventsGarbageAfterMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml");
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(),
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish");
        assertTrue(textBlock.isEndOfText());
    }
}
