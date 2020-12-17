/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JsonEventConverter;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Test JSON Event Handler.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JsonEventHandlerTest {
    private static final XLogger logger = XLoggerFactory.getXLogger(JsonEventHandlerTest.class);

    /**
     * Setup event model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException the apex model exception
     */
    @BeforeClass
    public static void setupEventModel() throws IOException, ApexModelException {
        final String policyModelString =
                TextFileUtils.getTextFileAsString("src/test/resources/policymodels/SmallModel.json");
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
        ModelService.clear();
    }

    /**
     * Test JSON to apex event.
     *
     * @throws ApexException the apex exception
     */
    @Test
    public void testJsontoApexEvent() throws ApexException {
        final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
        assertNotNull(jsonEventConverter);
        jsonEventConverter.init(new JsonEventProtocolParameters());

        final String apexEventJsonStringIn = SupportJsonEventGenerator.jsonEvent();

        logger.debug("input event\n" + apexEventJsonStringIn);

        final List<ApexEvent> apexEventList = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        for (final ApexEvent apexEvent : apexEventList) {
            assertNotNull(apexEvent);

            logger.debug(apexEvent.toString());

            assertEquals("BasicEvent", apexEvent.getName());
            assertEquals("0.0.1", apexEvent.getVersion());
            assertEquals("org.onap.policy.apex.events", apexEvent.getNameSpace());
            assertEquals("test", apexEvent.getSource());
            assertEquals("apex", apexEvent.getTarget());
            assertEquals(12345, apexEvent.get("intPar"));
            final Object testMatchCaseSelected = apexEvent.get("TestMatchCaseSelected");
            assertNull(testMatchCaseSelected);
        }

    }

    /**
     * Test JSON to apex bad event.
     *
     * @throws ApexException the apex exception
     */
    @Test
    public void testJsontoApexBadEvent() throws ApexException {
        final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
        Throwable throwable;

        assertNotNull(jsonEventConverter);
        jsonEventConverter.init(new JsonEventProtocolParameters());

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventNoName();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("event received without "
            + "mandatory parameter \"name\" ");

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventBadName();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("field \"name\" with value \"%%%%\" is invalid");

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventNoExName();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("an event definition for an event named \"I_DONT_EXI");

        String apexEventJsonStringIn1 = null;
        apexEventJsonStringIn1 = SupportJsonEventGenerator.jsonEventNoVersion();
        ApexEvent event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn1).get(0);
        assertEquals("0.0.1", event.getVersion());

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventBadVersion();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("field \"version\" with value \"#####\" is invalid");

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventNoExVersion();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("an event definition for an event named "
            + "\"BasicEvent\" with version \"1.2.3\" not found in Apex model");

        apexEventJsonStringIn1 = SupportJsonEventGenerator.jsonEventNoNamespace();
        event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn1).get(0);
        assertEquals("org.onap.policy.apex.events", event.getNameSpace());

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventBadNamespace();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("field \"nameSpace\" with value \"hello.&&&&\" "
            + "is invalid");

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventNoExNamespace();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("namespace \"pie.in.the.sky\" "
            + "on event \"BasicEvent\" does not" + " match namespace \"org.onap.policy.apex.events\" "
            + "for that event in the Apex model");

        apexEventJsonStringIn1 = SupportJsonEventGenerator.jsonEventNoSource();
        event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn1).get(0);
        assertEquals("source", event.getSource());

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventBadSource();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("field \"source\" with value \"%!@**@!\" is invalid");

        apexEventJsonStringIn1 = SupportJsonEventGenerator.jsonEventNoTarget();
        event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn1).get(0);
        assertEquals("target", event.getTarget());

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventBadTarget();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("field \"target\" with value \"KNIO(*S)A(S)D\" "
            + "is invalid");

        throwable = assertThrows(ApexEventException.class, () -> {
            String apexEventJsonStringIn = null;
            apexEventJsonStringIn = SupportJsonEventGenerator.jsonEventMissingFields();
            jsonEventConverter.toApexEvent(null, apexEventJsonStringIn);
        });
        assertThat(throwable).hasMessageStartingWith("Failed to unmarshal JSON event");
        assertThat(throwable.getCause()).hasMessageStartingWith("error parsing BasicEvent:0.0.1 "
            + "event from Json. Field \"intPar\" is missing, but is mandatory.");

        apexEventJsonStringIn1 = SupportJsonEventGenerator.jsonEventNullFields();
        event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn1).get(0);
        assertEquals(null, event.get("TestSlogan"));
        assertEquals(-1, event.get("intPar"));

        // Set the missing fields as optional in the model
        final AxEvent eventDefinition = ModelService.getModel(AxEvents.class).get("BasicEvent");
        eventDefinition.getParameterMap().get("intPar").setOptional(true);

        apexEventJsonStringIn1 = SupportJsonEventGenerator.jsonEventMissingFields();
        event = jsonEventConverter.toApexEvent(null, apexEventJsonStringIn1).get(0);
        assertEquals(null, event.get("TestSlogan"));
        assertEquals(null, event.get("intPar"));
    }

    /**
     * Test apex event to JSON.
     *
     * @throws ApexException the apex exception
     */
    @Test
    public void testApexEventToJson() throws ApexException {
        final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
        jsonEventConverter.init(new JsonEventProtocolParameters());
        assertNotNull(jsonEventConverter);

        final Map<String, Object> basicEventMap = new HashMap<String, Object>();
        basicEventMap.put("intPar", 12345);

        final ApexEvent basicEvent =
               new ApexEvent("BasicEvent", "0.0.1", "org.onap.policy.apex.events", "test", "apex");
        basicEvent.putAll(basicEventMap);

        final String apexEvent0000JsonString = (String) jsonEventConverter.fromApexEvent(basicEvent);

        logger.debug(apexEvent0000JsonString);

        assertTrue(apexEvent0000JsonString.contains("\"name\": \"BasicEvent\""));
        assertTrue(apexEvent0000JsonString.contains("\"version\": \"0.0.1\""));
        assertTrue(apexEvent0000JsonString.contains("\"nameSpace\": \"org.onap.policy.apex.events\""));
        assertTrue(apexEvent0000JsonString.contains("\"source\": \"test\""));
        assertTrue(apexEvent0000JsonString.contains("\"target\": \"apex\""));
        assertTrue(apexEvent0000JsonString.contains("\"intPar\": 12345"));
    }
}
