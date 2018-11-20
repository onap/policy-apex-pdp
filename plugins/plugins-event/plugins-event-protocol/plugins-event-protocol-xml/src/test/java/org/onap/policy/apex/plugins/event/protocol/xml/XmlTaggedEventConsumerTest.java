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
 * The Class TestXmlTaggedEventConsumer.
 */
public class XmlTaggedEventConsumerTest {

    /**
     * Test garbage text line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testGarbageTextLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream("hello there".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertNull(textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test partial event line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "1469781869268</TestTimestamp></MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertNull(textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>", textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event garbage before line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageBeforeLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event garbage before after line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageBeforeAfterLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish"
                                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event garbage after line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageAfterLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test garbage text multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testGarbageTextMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream("hello\nthere".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test partial event multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "1469781869268\n</TestTimestamp>\n</MainTag>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n\n".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event garbage before multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageBeforeMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n\n"
                                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event garbage before after multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageBeforeAfterMultiLine() throws IOException {
        String garbageString = "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>"
                        + "\n</MainTag>\nRubbish\n\n";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full event garbage after multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageAfterMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish"
                                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test partial events line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventsLine() throws IOException {
        String garbageString = "1469781869268</TestTimestamp></MainTag><?xml><MainTag>"
                        + "<TestTimestamp>1469781869268</TestTimestamp>";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full events garbage before line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageBeforeLine() throws IOException {
        String garbageString = "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>"
                        + "<?xml><MainTag><TestTimestamp>";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full events garbage before after line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageBeforeAfterLine() throws IOException {
        String garbageString = "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp>"
                        + "</MainTag>Rubbish<?xml><MainTag><TestTimestamp>\nRefuse";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full events garbage after line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageAfterLine() throws IOException {
        String garbageString = "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp>"
                        + "</MainTag>Rubbish<?xml><MainTag><TestTimestamp>Refuse";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(textBlock.getText(), garbageString);
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test partial events multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventsMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "1469781869268\n</TestTimestamp>\n</MainTag>\n<?xml>\n<MainTag>\n<TestTimestamp>".getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>", textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full events multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsMultiLine() throws IOException {
        String garbageString = "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n"
                        + "</MainTag>\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        textBlock.getText());
        assertFalse(textBlock.isEndOfText());

        textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full events garbage before multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageBeforeMultiLine() throws IOException {
        String garbageString = "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n"
                        + "</MainTag>\n\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        textBlock.getText());
        assertFalse(textBlock.isEndOfText());

        textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full events garbage before after multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageBeforeAfterMultiLine() throws IOException {
        String garbageString = "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n"
                        + "</MainTag>\nRubbish\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n"
                        + "</MainTag>\nRefuse\n";
        final InputStream xmlInputStream = new ByteArrayInputStream(garbageString.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish",
                        textBlock.getText());
        assertFalse(textBlock.isEndOfText());

        textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRefuse",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }

    /**
     * Test full events garbage after multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageAfterMultiLine() throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish"
                                        .getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals("<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish",
                        textBlock.getText());
        assertTrue(textBlock.isEndOfText());
    }
}
