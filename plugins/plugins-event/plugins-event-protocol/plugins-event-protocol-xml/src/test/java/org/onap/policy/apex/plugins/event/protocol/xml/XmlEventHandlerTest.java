/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestApexXMLEventHandlerURL.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class XmlEventHandlerTest {
    private static final XLogger logger = XLoggerFactory.getXLogger(XmlEventHandlerTest.class);

    /**
     * Test XML to apex event. Null value is passed as parameter.
     *
     * @throws ApexException on Apex event handling errors
     */
    @Test(expected = ApexException.class)
    public void testApexEventToApexNullObject() throws ApexException {
        final Apex2XmlEventConverter xmlEventConverter = new Apex2XmlEventConverter();
        // This is only for code coverage stats. This method does nothing
        xmlEventConverter.init(null);

        xmlEventConverter.toApexEvent("XMLEventName", null);
    }

    /**
     * Test XML to apex event. There is no string passed as parameter.
     *
     * @throws ApexException on Apex event handling errors
     */
    @Test(expected = ApexEventRuntimeException.class)
    public void testApexEventToApexNotString() throws ApexException {
        final Apex2XmlEventConverter xmlEventConverter = new Apex2XmlEventConverter();

        xmlEventConverter.toApexEvent("XMLEventName", new Random().nextInt());
    }

    /**
     * Test not valid XML to apex event.
     *
     * @throws ApexException on Apex event handling errors
     */
    @Test(expected = ApexException.class)
    public void testApexEventToApexNotXml() throws ApexException {
        final Apex2XmlEventConverter xmlEventConverter = new Apex2XmlEventConverter();

        xmlEventConverter.toApexEvent("XMLEventName", RandomStringUtils.randomAlphabetic(25));
    }

    /**
     * Test XML to apex event.
     *
     * @throws ApexException on Apex event handling errors
     */
    @Test
    public void testXmltoApexEvent() throws ApexException {
        try {
            final Apex2XmlEventConverter xmlEventConverter = new Apex2XmlEventConverter();
            assertNotNull(xmlEventConverter);

            final String apexEventXmlStringIn = XmlEventGenerator.xmlEvent();

            logger.debug("input event\n" + apexEventXmlStringIn);

            for (final ApexEvent apexEvent : xmlEventConverter.toApexEvent("XMLEventName", apexEventXmlStringIn)) {
                assertNotNull(apexEvent);

                logger.debug(apexEvent.toString());

                assertTrue(apexEvent.getName().equals("Event0000") || apexEvent.getName().equals("Event0100"));
                assertEquals("0.0.1", apexEvent.getVersion());
                assertEquals("org.onap.policy.apex.sample.events", apexEvent.getNameSpace());
                assertEquals("test", apexEvent.getSource());
                assertEquals("apex", apexEvent.getTarget());
                assertTrue(apexEvent.get("TestSlogan").toString().startsWith("Test slogan for External Event"));

                final Object testMatchCaseSelected = apexEvent.get("TestMatchCaseSelected");
                assertNull(testMatchCaseSelected);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApexException("Exception reading Apex event xml file", e);
        }
    }

    /**
     * Test null as apex event to xml.
     *
     * @throws ApexEventException on Apex event handling errors
     */
    @Test(expected = ApexEventException.class)
    public void testApexEventToXmlNullEvent() throws ApexEventException {
        final Apex2XmlEventConverter xmlEventConverter = new Apex2XmlEventConverter();
        xmlEventConverter.fromApexEvent(null);
    }

    /**
     * Test apex event to xml.
     *
     * @throws ApexException on Apex event handling errors
     */
    @Test
    public void testApexEventToXml() throws ApexException {
        try {
            final Apex2XmlEventConverter xmlEventConverter = new Apex2XmlEventConverter();
            assertNotNull(xmlEventConverter);

            final Date event0000StartTime = new Date();
            final Map<String, Object> event0000DataMap = new HashMap<String, Object>();
            event0000DataMap.put("TestSlogan", "This is a test slogan");
            event0000DataMap.put("TestMatchCase", 12345);
            event0000DataMap.put("TestTimestamp", event0000StartTime.getTime());
            event0000DataMap.put("TestTemperature", 34.5445667);
            event0000DataMap.put("NullValue", null);

            final ApexEvent apexEvent0000 =
                    new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.sample.events", "test", "apex", "");
            apexEvent0000.putAll(event0000DataMap);

            final String apexEvent0000XmlString = xmlEventConverter.fromApexEvent(apexEvent0000);

            logger.debug(apexEvent0000XmlString);

            assertTrue(apexEvent0000XmlString.contains("<name>Event0000</name>"));
            assertTrue(apexEvent0000XmlString.contains("<version>0.0.1</version>"));
            assertTrue(apexEvent0000XmlString.contains("<value>This is a test slogan</value>"));
            assertTrue(apexEvent0000XmlString.contains("<value>12345</value>"));
            assertTrue(apexEvent0000XmlString.contains("<value></value>"));
            assertTrue(apexEvent0000XmlString.contains("<value>" + event0000StartTime.getTime() + "</value>"));
            assertTrue(apexEvent0000XmlString.contains("<value>34.5445667</value>"));

            final Date event0004StartTime = new Date(1434363272000L);
            final Map<String, Object> event0004DataMap = new HashMap<String, Object>();
            event0004DataMap.put("TestSlogan", "Test slogan for External Event");
            event0004DataMap.put("TestMatchCase", Integer.valueOf(2));
            event0004DataMap.put("TestTimestamp", Long.valueOf(event0004StartTime.getTime()));
            event0004DataMap.put("TestTemperature", Double.valueOf(1064.43));
            event0004DataMap.put("TestMatchCaseSelected", Integer.valueOf(2));
            event0004DataMap.put("TestMatchStateTime", Long.valueOf(1434370506078L));
            event0004DataMap.put("TestEstablishCaseSelected", Integer.valueOf(0));
            event0004DataMap.put("TestEstablishStateTime", Long.valueOf(1434370506085L));
            event0004DataMap.put("TestDecideCaseSelected", Integer.valueOf(3));
            event0004DataMap.put("TestDecideStateTime", Long.valueOf(1434370506092L));
            event0004DataMap.put("TestActCaseSelected", Integer.valueOf(2));
            event0004DataMap.put("TestActStateTime", Long.valueOf(1434370506095L));

            final ApexEvent apexEvent0004 = new ApexEvent("Event0004", "0.0.1",
                    "org.onap.policy.apex.domains.sample.events", "test", "apex", "");
            apexEvent0004.putAll(event0004DataMap);

            final String apexEvent0004XmlString = xmlEventConverter.fromApexEvent(apexEvent0004);

            logger.debug(apexEvent0004XmlString);

            assertTrue(apexEvent0004XmlString.contains("<name>Event0004</name>"));
            assertTrue(apexEvent0004XmlString.contains("<version>0.0.1</version>"));
            assertTrue(apexEvent0004XmlString.contains("<value>Test slogan for External Event</value>"));
            assertTrue(apexEvent0004XmlString.contains("<value>1434370506078</value>"));
            assertTrue(apexEvent0004XmlString.contains("<value>" + event0004StartTime.getTime() + "</value>"));
            assertTrue(apexEvent0004XmlString.contains("<value>1064.43</value>"));
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApexException("Exception reading Apex event xml file", e);
        }
    }
}