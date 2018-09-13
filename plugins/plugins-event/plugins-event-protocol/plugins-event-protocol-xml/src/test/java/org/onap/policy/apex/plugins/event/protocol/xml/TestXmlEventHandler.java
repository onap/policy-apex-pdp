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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestApexXMLEventHandlerURL.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestXmlEventHandler {
    private static final XLogger logger = XLoggerFactory.getXLogger(TestXmlEventHandler.class);

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
                assertTrue(apexEvent.getVersion().equals("0.0.1"));
                assertTrue(apexEvent.getNameSpace().equals("org.onap.policy.apex.sample.events"));
                assertTrue(apexEvent.getSource().equals("test"));
                assertTrue(apexEvent.getTarget().equals("apex"));
                assertTrue(apexEvent.get("TestSlogan").toString().startsWith("Test slogan for External Event"));

                final Object testMatchCaseSelected = apexEvent.get("TestMatchCaseSelected");
                assertTrue(testMatchCaseSelected == null);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApexException("Exception reading Apex event xml file", e);
        }
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

            final ApexEvent apexEvent0000 =
                    new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.sample.events", "test", "apex");
            apexEvent0000.putAll(event0000DataMap);

            final String apexEvent0000XmlString = xmlEventConverter.fromApexEvent(apexEvent0000);

            logger.debug(apexEvent0000XmlString);

            assertTrue(apexEvent0000XmlString.contains("<name>Event0000</name>"));
            assertTrue(apexEvent0000XmlString.contains("<version>0.0.1</version>"));
            assertTrue(apexEvent0000XmlString.contains("<value>This is a test slogan</value>"));
            assertTrue(apexEvent0000XmlString.contains("<value>12345</value>"));
            assertTrue(apexEvent0000XmlString.contains("<value>" + event0000StartTime.getTime() + "</value>"));
            assertTrue(apexEvent0000XmlString.contains("<value>34.5445667</value>"));

            final Date event0004StartTime = new Date(1434363272000L);
            final Map<String, Object> event0004DataMap = new HashMap<String, Object>();
            event0004DataMap.put("TestSlogan", "Test slogan for External Event");
            event0004DataMap.put("TestMatchCase", new Integer(2));
            event0004DataMap.put("TestTimestamp", new Long(event0004StartTime.getTime()));
            event0004DataMap.put("TestTemperature", new Double(1064.43));
            event0004DataMap.put("TestMatchCaseSelected", new Integer(2));
            event0004DataMap.put("TestMatchStateTime", new Long(1434370506078L));
            event0004DataMap.put("TestEstablishCaseSelected", new Integer(0));
            event0004DataMap.put("TestEstablishStateTime", new Long(1434370506085L));
            event0004DataMap.put("TestDecideCaseSelected", new Integer(3));
            event0004DataMap.put("TestDecideStateTime", new Long(1434370506092L));
            event0004DataMap.put("TestActCaseSelected", new Integer(2));
            event0004DataMap.put("TestActStateTime", new Long(1434370506095L));

            final ApexEvent apexEvent0004 =
                    new ApexEvent("Event0004", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
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
