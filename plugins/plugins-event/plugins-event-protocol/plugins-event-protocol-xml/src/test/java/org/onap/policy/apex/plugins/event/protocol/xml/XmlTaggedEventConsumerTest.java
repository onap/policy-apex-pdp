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
        verifyNull("testGarbageTextLine", "hello there");
    }

    /**
     * Test partial event line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventLine() throws IOException {
        verifyNull("testPartialEventLine", "1469781869268</TestTimestamp></MainTag>");
    }

    /**
     * Test full event line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventLine() throws IOException {
        verifyLine("testFullEventLine", "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>",
                        "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>");
    }

    /**
     * Test full event garbage before line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageBeforeLine() throws IOException {
        verifyEnd("testFullEventGarbageBeforeLine",
                        "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>");
    }

    /**
     * Test full event garbage before after line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageBeforeAfterLine() throws IOException {
        verifyEnd("testFullEventGarbageBeforeAfterLine",
                        "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish");
    }

    /**
     * Test full event garbage after line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageAfterLine() throws IOException {
        verifyLine("testFullEventGarbageAfterLine",
                        "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish",
                        "<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>Rubbish");
    }

    /**
     * Test garbage text multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testGarbageTextMultiLine() throws IOException {
        verifyEnd("testGarbageTextMultiLine", "hello\nthere");
    }

    /**
     * Test partial event multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventMultiLine() throws IOException {
        verifyEnd("testPartialEventMultiLine", "1469781869268\n</TestTimestamp>\n</MainTag>");
    }

    /**
     * Test full event multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventMultiLine() throws IOException {
        verifyLine("testFullEventMultiLine",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n\n",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
    }

    /**
     * Test full event garbage before multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageBeforeMultiLine() throws IOException {
        verifyLine("testFullEventGarbageBeforeMultiLine",
                "Garbage\n<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\n\n",
                "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
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

        verifyLine("testFullEventsGarbageAfterLine", garbageString,
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish");
    }

    /**
     * Test full event garbage after multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventGarbageAfterMultiLine() throws IOException {
        verifyLine("testFullEventGarbageAfterMultiLine",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish");
    }

    /**
     * Test partial events line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventsLine() throws IOException {
        verifyEnd("testPartialEventsLine", "1469781869268</TestTimestamp></MainTag><?xml><MainTag>"
                        + "<TestTimestamp>1469781869268</TestTimestamp>");
    }

    /**
     * Test full events garbage before line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageBeforeLine() throws IOException {
        verifyEnd("testPartialEventsLine",
                        "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp></MainTag>"
                                        + "<?xml><MainTag><TestTimestamp>");
    }

    /**
     * Test full events garbage before after line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageBeforeAfterLine() throws IOException {
        verifyEnd("testFullEventGarbageBeforeLine", "Garbage<?xml><MainTag><TestTimestamp>1469781869268</TestTimestamp>"
                        + "</MainTag>Rubbish<?xml><MainTag><TestTimestamp>\nRefuse");
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

        verifyLine("testFullEventsGarbageAfterLine", garbageString, garbageString);
    }

    /**
     * Test partial events multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testPartialEventsMultiLine() throws IOException {
        verifyLine("testPartialEventsMultiLine",
                        "1469781869268\n</TestTimestamp>\n</MainTag>\n<?xml>\n<MainTag>\n<TestTimestamp>",
                        "<?xml>\n<MainTag>\n<TestTimestamp>");
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

        verifyLines("testFullEventsMultiLine", garbageString,
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
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

        verifyLines("testFullEventsGarbageBeforeMultiLine", garbageString,
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>");
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

        verifyLines("testFullEventsGarbageBeforeAfterMultiLine", garbageString,
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRefuse");
    }

    /**
     * Test full events garbage after multi line.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFullEventsGarbageAfterMultiLine() throws IOException {
        verifyLine("testFullEventsGarbageAfterMultiLine",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish",
                        "<?xml>\n<MainTag>\n<TestTimestamp>1469781869268</TestTimestamp>\n</MainTag>\nRubbish");
    }

    private void verifyNull(String testName, String xml) throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertNull(testName, textBlock.getText());
        assertTrue(testName, textBlock.isEndOfText());
    }

    private void verifyLine(String testName, String xml, String expected) throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(testName, expected, textBlock.getText());
        assertTrue(testName, textBlock.isEndOfText());
    }

    private void verifyLines(String testName, String xml, String expected, String expected2) throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(testName, expected, textBlock.getText());
        assertFalse(testName, textBlock.isEndOfText());

        textBlock = xmlTaggedReader.readTextBlock();
        assertEquals(testName, expected2, textBlock.getText());
        assertTrue(testName, textBlock.isEndOfText());
    }

    private void verifyEnd(String testName, String xml) throws IOException {
        final InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes());

        final HeaderDelimitedTextBlockReader xmlTaggedReader = new HeaderDelimitedTextBlockReader("<?xml", null, true);
        xmlTaggedReader.init(xmlInputStream);

        final TextBlock textBlock = xmlTaggedReader.readTextBlock();
        assertTrue(testName, textBlock.isEndOfText());
    }
}
