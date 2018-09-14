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

package org.onap.policy.apex.core.infrastructure.xml;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.w3c.dom.Document;

/**
 * A generic class for applying the XPATH queries on XML files.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public class XPathReader {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(XPathReader.class);

    private String xmlFileName = null;
    private InputStream xmlStream = null;
    private Document xmlDocument;
    private XPath xpath;

    /**
     * Construct Reader for the file passed in.
     *
     * @param xmlFileName the xml file name
     */
    public XPathReader(final String xmlFileName) {
        this.xmlFileName = xmlFileName;
        init();
    }

    /**
     * Construct Reader for the stream passed in.
     *
     * @param xmlStream a stream of XML
     */
    public XPathReader(final InputStream xmlStream) {
        this.xmlStream = xmlStream;
        init();
    }

    /**
     * Initialise the x-path reader.
     */
    private void init() {
        try {
            LOGGER.info("Initializing XPath reader");

            // Check if this is operating on a file
            if (xmlFileName != null) {
                xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFileName);
            }
            // Check if this is operating on a stream
            else if (xmlStream != null) {
                xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlStream);

            }
            // We have an error
            else {
                LOGGER.error("XPath reader not initialized with either a file or a stream");
                return;
            }

            xpath = XPathFactory.newInstance().newXPath();
            LOGGER.info("Initialized XPath reader");
        } catch (final Exception ex) {
            LOGGER.error("Error parsing XML file/stream from XPath reading, reason :\n" + ex.getMessage(), ex);
        }
    }

    /**
     * Read items from the file using xpath.
     *
     * @param expression x-path expression
     * @param returnType XML node Set
     * @return last node collected
     */
    public Object read(final String expression, final QName returnType) {
        try {
            final XPathExpression xPathExpression = xpath.compile(expression);
            return xPathExpression.evaluate(xmlDocument, returnType);
        } catch (final XPathExpressionException ex) {
            LOGGER.error("Failed to read XML file for XPath processing, reason:\n" + ex.getMessage(), ex);
            return null;
        }
    }
}
