/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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
package org.onap.policy.apex.plugins.event.protocol.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;

public class TestYamlPluginStability {
    static AxEvent testEvent;

    @BeforeClass
    public static void registerTestEventsAndSchemas() throws IOException {
        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());
        ParameterService.registerParameters(SchemaParameters.class, schemaParameters);

        AxContextSchemas schemas = new AxContextSchemas();

        AxContextSchema simpleIntSchema = new AxContextSchema(new AxArtifactKey("SimpleIntSchema", "0.0.1"), "JAVA",
                        "java.lang.Integer");
        schemas.getSchemasMap().put(simpleIntSchema.getKey(), simpleIntSchema);

        AxContextSchema simpleDoubleSchema = new AxContextSchema(new AxArtifactKey("SimpleDoubleSchema", "0.0.1"),
                        "JAVA", "java.lang.Double");
        schemas.getSchemasMap().put(simpleDoubleSchema.getKey(), simpleDoubleSchema);

        AxContextSchema simpleStringSchema = new AxContextSchema(new AxArtifactKey("SimpleStringSchema", "0.0.1"),
                        "JAVA", "java.lang.String");
        schemas.getSchemasMap().put(simpleStringSchema.getKey(), simpleStringSchema);

        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxEvents events = new AxEvents();

        testEvent = new AxEvent(new AxArtifactKey("TestEvent", "0.0.1"));
        testEvent.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField teField0 = new AxField(new AxReferenceKey(testEvent.getKey(), "intValue"), simpleIntSchema.getKey());
        testEvent.getParameterMap().put("intValue", teField0);
        AxField teField1 = new AxField(new AxReferenceKey(testEvent.getKey(), "doubleValue"),
                        simpleDoubleSchema.getKey());
        testEvent.getParameterMap().put("doubleValue", teField1);
        AxField teField2 = new AxField(new AxReferenceKey(testEvent.getKey(), "stringValue"),
                        simpleStringSchema.getKey(), true);
        testEvent.getParameterMap().put("stringValue", teField2);
        events.getEventMap().put(testEvent.getKey(), testEvent);

        ModelService.registerModel(AxEvents.class, events);
    }

    @AfterClass
    public static void unregisterTestEventsAndSchemas() {
        ModelService.clear();
    }

    @Test
    public void testStability() throws ApexEventException {
        Apex2YamlEventConverter converter = new Apex2YamlEventConverter();

        try {
            converter.init(null);
            fail("this test should throw an exception");
        } catch (ApexEventRuntimeException e) {
            assertEquals("specified consumer properties are not applicable to the YAML event protocol", e.getMessage());
        }

        YamlEventProtocolParameters pars = new YamlEventProtocolParameters();
        converter.init(pars);

        try {
            converter.toApexEvent("NonExistantEvent", "");
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("Failed to unmarshal YAML event: an event definition for an event named \"NonExistantEvent\"",
                            e.getMessage().substring(0, 89));
        }

        try {
            converter.toApexEvent("TestEvent", null);
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("event processing failed, event is null", e.getMessage());
        }

        try {
            converter.toApexEvent("TestEvent", 1);
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("error converting event \"1\" to a string", e.getMessage());
        }

        try {
            converter.toApexEvent("TestEvent", "");
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertTrue(e.getMessage().contains("Field \"doubleValue\" is missing"));
        }

        try {
            converter.fromApexEvent(null);
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("event processing failed, Apex event is null", e.getMessage());
        }

        ApexEvent apexEvent = new ApexEvent(testEvent.getKey().getName(), testEvent.getKey().getVersion(),
                        testEvent.getNameSpace(), testEvent.getSource(), testEvent.getTarget());
        apexEvent.put("doubleValue", 123.45);
        apexEvent.put("intValue", 123);
        apexEvent.put("stringValue", "123.45");

        apexEvent.setExceptionMessage("my wonderful exception message");
        String yamlString = (String) converter.fromApexEvent(apexEvent);
        assertTrue(yamlString.contains("my wonderful exception message"));

        apexEvent.remove("intValue");
        try {
            yamlString = (String) converter.fromApexEvent(apexEvent);
            fail("this test should throw an exception");
        } catch (ApexEventRuntimeException e) {
            assertEquals("error parsing TestEvent:0.0.1 event to Json. Field \"intValue\" is missing",
                            e.getMessage().substring(0, 72));
        }

        try {
            converter.toApexEvent(null, "");
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("Failed to unmarshal YAML event: event received without mandatory parameter \"name\"",
                            e.getMessage().substring(0, 81));
        }

        pars.setNameAlias("TheNameField");
        try {
            converter.toApexEvent(null, "");
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("Failed to unmarshal YAML event: event received without mandatory parameter \"name\"",
                            e.getMessage().substring(0, 81));
        }

        apexEvent.put("intValue", 123);

        apexEvent.remove("stringValue");
        yamlString = (String) converter.fromApexEvent(apexEvent);
        apexEvent.put("stringValue", "123.45");

        String yamlInputString = "doubleValue: 123.45\n" + "intValue: 123";

        List<ApexEvent> eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals(123.45, eventList.get(0).get("doubleValue"));

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: null";

        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals(null, eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: TestEvent";
        pars.setNameAlias("stringValue");
        eventList = converter.toApexEvent(null, yamlInputString);
        assertEquals("TestEvent", eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: SomeOtherEvent";
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("SomeOtherEvent", eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: 0.0.1";
        pars.setNameAlias(null);
        pars.setVersionAlias("stringValue");
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("0.0.1", eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: org.some.other.namespace";
        pars.setVersionAlias(null);
        pars.setNameSpaceAlias("stringValue");
        try {
            converter.toApexEvent("TestEvent", yamlInputString);
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("Failed to unmarshal YAML event: namespace \"org.some.other.namespace\" on event",
                            e.getMessage().substring(0, 77));
        }

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: org.onap.policy.apex.plugins.event.protocol.yaml";
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("org.onap.policy.apex.plugins.event.protocol.yaml", eventList.get(0).getNameSpace());

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: MySource";
        pars.setNameSpaceAlias(null);
        pars.setSourceAlias("stringValue");
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("MySource", eventList.get(0).getSource());

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: MyTarget";
        pars.setSourceAlias(null);
        pars.setTargetAlias("stringValue");
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("MyTarget", eventList.get(0).getTarget());
        pars.setTargetAlias(null);

        yamlInputString = "doubleValue: 123.45\n" + "intValue: 123\n" + "stringValue: MyString";
        pars.setSourceAlias(null);
        pars.setTargetAlias("intValue");
        try {
            converter.toApexEvent("TestEvent", yamlInputString);
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertEquals("Failed to unmarshal YAML event: field \"target\" with type \"java.lang.Integer\"",
                            e.getMessage().substring(0, 76));
        }
        pars.setTargetAlias(null);

        yamlInputString = "doubleValue: 123.45\n" + "intValue: ~\n"+ "stringValue: MyString";
        try {
            converter.toApexEvent("TestEvent", yamlInputString);
            fail("this test should throw an exception");
        } catch (ApexEventException e) {
            assertTrue(e.getMessage().contains("mandatory field \"intValue\" is missing"));
        }
    }
}
