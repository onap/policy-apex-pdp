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

package org.onap.policy.apex.service.engine.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JsonEventConverter;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Test JSON Event Handler.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestJsonEventHandler {
    private static final XLogger logger = XLoggerFactory.getXLogger(TestJsonEventHandler.class);

    /**
     * Setup event model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException the apex model exception
     */
    @BeforeClass
    public static void setupEventModel() throws IOException, ApexModelException {
        final String policyModelString = TextFileUtils
                        .getTextFileAsString("src/test/resources/policymodels/SamplePolicyModelMVEL.json");
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<AxPolicyModel>(AxPolicyModel.class);
        final AxPolicyModel apexPolicyModel = modelReader.read(new ByteArrayInputStream(policyModelString.getBytes()));

        // Set up the models in the model service
        apexPolicyModel.register();
    }

    /**
     * Initialize default schema parameters.
     */
    @BeforeClass
    public static void initializeDefaultSchemaParameters() {
        ParameterService.clear();
        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.register(schemaParameters);
    }

    /**
     * Teardown default schema parameters.
     */
    @AfterClass
    public static void teardownDefaultSchemaParameters() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    /**
     * Test JSON to apex event.
     *
     * @throws ApexException the apex exception
     */
    @Test
    public void testJsontoApexEvent() throws ApexException {
        try {
            final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
            assertNotNull(jsonEventConverter);
            jsonEventConverter.init(new JsonEventProtocolParameters());

            final String apexEventJsonStringIn = JsonEventGenerator.jsonEvent();

            logger.debug("input event\n" + apexEventJsonStringIn);

            final List<ApexEvent> apexEventList = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
            for (final ApexEvent apexEvent : apexEventList) {
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
            throw new ApexException("Exception reading Apex event JSON file", e);
        }
    }

    /**
     * Test JSON to apex bad event.
     *
     * @throws ApexException the apex exception
     */
    @Test
    public void testJsontoApexBadEvent() throws ApexException {
        try {
            final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
            assertNotNull(jsonEventConverter);
            jsonEventConverter.init(new JsonEventProtocolParameters());

            String apexEventJsonStringIn = null;

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventNoName();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertEquals("Failed to unmarshal JSON event: event received without mandatory parameter \"name\" ",
                                e.getMessage().substring(0, 82));
            }

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventBadName();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage().startsWith(
                                "Failed to unmarshal JSON event: field \"name\" with value \"%%%%\" is invalid"));
            }

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventNoExName();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertEquals("Failed to unmarshal JSON event: an event definition for an event named \"I_DONT_EXI",
                                e.getMessage().substring(0, 82));
            }

            apexEventJsonStringIn = JsonEventGenerator.jsonEventNoVersion();
            ApexEvent event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn).get(0);
            assertEquals("0.0.1", event.getVersion());

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventBadVersion();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage().startsWith(
                                "Failed to unmarshal JSON event: field \"version\" with value \"#####\" is invalid"));
            }

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventNoExVersion();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage()
                                .startsWith("Failed to unmarshal JSON event: an event definition for an event named "
                                                + "\"Event0000\" with version \"1.2.3\" not found in Apex model"));
            }

            apexEventJsonStringIn = JsonEventGenerator.jsonEventNoNamespace();
            event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn).get(0);
            assertEquals("org.onap.policy.apex.sample.events", event.getNameSpace());

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventBadNamespace();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage().startsWith("Failed to unmarshal JSON event: field \"nameSpace\" "
                                + "with value \"hello.&&&&\" is invalid"));
            }

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventNoExNamespace();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage()
                                .startsWith("Failed to unmarshal JSON event: namespace \"pie.in.the.sky\" "
                                                + "on event \"Event0000\" does not"
                                                + " match namespace \"org.onap.policy.apex.sample.events\" "
                                                + "for that event in the Apex model"));
            }

            apexEventJsonStringIn = JsonEventGenerator.jsonEventNoSource();
            event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn).get(0);
            assertEquals("Outside", event.getSource());

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventBadSource();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage().startsWith(
                                "Failed to unmarshal JSON event: field \"source\" with value \"%!@**@!\" is invalid"));
            }

            apexEventJsonStringIn = JsonEventGenerator.jsonEventNoTarget();
            event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn).get(0);
            assertEquals("Match", event.getTarget());

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventBadTarget();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage().startsWith("Failed to unmarshal JSON event: field \"target\" "
                                + "with value \"KNIO(*S)A(S)D\" is invalid"));
            }

            try {
                apexEventJsonStringIn = JsonEventGenerator.jsonEventMissingFields();
                jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
                fail("Test should throw an exception here");
            } catch (final ApexEventException e) {
                assertTrue(e.getMessage().startsWith("Failed to unmarshal JSON event: error parsing Event0000:0.0.1 "
                                + "event from Json. Field \"TestMatchCase\" is missing, but is mandatory."));
            }

            apexEventJsonStringIn = JsonEventGenerator.jsonEventNullFields();
            event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn).get(0);
            assertEquals(null, event.get("TestSlogan"));
            assertEquals((byte) -1, event.get("TestMatchCase"));
            assertEquals((long) -1, event.get("TestTimestamp"));
            assertEquals(-1.0, event.get("TestTemperature"));

            // Set the missing fields as optional in the model
            final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get("Event0000");
            eventDefinition.getParameterMap().get("TestSlogan").setOptional(true);
            eventDefinition.getParameterMap().get("TestMatchCase").setOptional(true);
            eventDefinition.getParameterMap().get("TestTimestamp").setOptional(true);
            eventDefinition.getParameterMap().get("TestTemperature").setOptional(true);

            apexEventJsonStringIn = JsonEventGenerator.jsonEventMissingFields();
            event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn).get(0);
            assertEquals(null, event.get("TestSlogan"));
            assertEquals(null, event.get("TestMatchCase"));
            assertEquals(null, event.get("TestTimestamp"));
            assertEquals(null, event.get("TestTemperature"));
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApexException("Exception reading Apex event JSON file", e);
        }
    }

    /**
     * Test apex event to JSON.
     *
     * @throws ApexException the apex exception
     */
    @Test
    public void testApexEventToJson() throws ApexException {
        try {
            final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
            jsonEventConverter.init(new JsonEventProtocolParameters());
            assertNotNull(jsonEventConverter);

            final Date event0000StartTime = new Date();
            final Map<String, Object> event0000DataMap = new HashMap<String, Object>();
            event0000DataMap.put("TestSlogan", "This is a test slogan");
            event0000DataMap.put("TestMatchCase", 12345);
            event0000DataMap.put("TestTimestamp", event0000StartTime.getTime());
            event0000DataMap.put("TestTemperature", 34.5445667);

            final ApexEvent apexEvent0000 = new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.sample.events",
                            "test", "apex");
            apexEvent0000.putAll(event0000DataMap);

            final String apexEvent0000JsonString = (String) jsonEventConverter.fromApexEvent(apexEvent0000);

            logger.debug(apexEvent0000JsonString);

            assertTrue(apexEvent0000JsonString.contains("\"name\": \"Event0000\""));
            assertTrue(apexEvent0000JsonString.contains("\"version\": \"0.0.1\""));
            assertTrue(apexEvent0000JsonString.contains("\"nameSpace\": \"org.onap.policy.apex.sample.events\""));
            assertTrue(apexEvent0000JsonString.contains("\"source\": \"test\""));
            assertTrue(apexEvent0000JsonString.contains("\"target\": \"apex\""));
            assertTrue(apexEvent0000JsonString.contains("\"TestSlogan\": \"This is a test slogan\""));
            assertTrue(apexEvent0000JsonString.contains("\"TestMatchCase\": 12345"));
            assertTrue(apexEvent0000JsonString.contains("\"TestTimestamp\": " + event0000StartTime.getTime()));
            assertTrue(apexEvent0000JsonString.contains("\"TestTemperature\": 34.5445667"));

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

            final ApexEvent apexEvent0004 = new ApexEvent("Event0004", "0.0.1", "org.onap.policy.apex.sample.events",
                            "test", "apex");
            apexEvent0004.putAll(event0004DataMap);

            final String apexEvent0004JsonString = (String) jsonEventConverter.fromApexEvent(apexEvent0004);

            logger.debug(apexEvent0004JsonString);

            assertTrue(apexEvent0004JsonString.contains("\"name\": \"Event0004\""));
            assertTrue(apexEvent0004JsonString.contains("\"version\": \"0.0.1\""));
            assertTrue(apexEvent0004JsonString.contains("\"nameSpace\": \"org.onap.policy.apex.sample.events\""));
            assertTrue(apexEvent0004JsonString.contains("\"source\": \"test\""));
            assertTrue(apexEvent0004JsonString.contains("\"target\": \"apex\""));
            assertTrue(apexEvent0004JsonString.contains("\"TestSlogan\": \"Test slogan for External Event\""));
            assertTrue(apexEvent0004JsonString.contains("1434370506078"));
            assertTrue(apexEvent0004JsonString.contains("1064.43"));
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApexException("Exception reading Apex event JSON file", e);
        }
    }
}
