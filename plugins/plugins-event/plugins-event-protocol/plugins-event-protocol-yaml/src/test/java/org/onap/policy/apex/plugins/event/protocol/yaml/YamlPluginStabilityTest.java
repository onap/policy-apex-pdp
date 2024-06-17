/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxToscaPolicyProcessingStatus;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.common.parameters.ParameterService;

/**
 * The Class TestYamlPluginStability.
 */
class YamlPluginStabilityTest {
    static AxEvent testEvent;

    /**
     * Register test events and schemas.
     */
    @BeforeAll
    static void registerTestEventsAndSchemas() {
        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());
        ParameterService.register(schemaParameters);

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

        testEvent = new AxEvent(new AxArtifactKey("TestEvent", "0.0.1"));
        testEvent.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        testEvent.setToscaPolicyState(AxToscaPolicyProcessingStatus.ENTRY.name());
        AxField teField0 = new AxField(new AxReferenceKey(testEvent.getKey(), "intValue"), simpleIntSchema.getKey());
        testEvent.getParameterMap().put("intValue", teField0);
        AxField teField1 = new AxField(new AxReferenceKey(testEvent.getKey(), "doubleValue"),
            simpleDoubleSchema.getKey());
        testEvent.getParameterMap().put("doubleValue", teField1);
        AxField teField2 = new AxField(new AxReferenceKey(testEvent.getKey(), "stringValue"),
            simpleStringSchema.getKey(), true);
        testEvent.getParameterMap().put("stringValue", teField2);

        AxEvents events = new AxEvents();
        events.getEventMap().put(testEvent.getKey(), testEvent);

        ModelService.registerModel(AxEvents.class, events);
    }

    /**
     * Unregister test events and schemas.
     */
    @AfterAll
    static void unregisterTestEventsAndSchemas() {
        ModelService.clear();
        ParameterService.clear();
    }

    /**
     * Test stability.
     *
     * @throws ApexEventException the apex event exception
     */
    @Test
    void testStability() throws ApexEventException {
        Apex2YamlEventConverter converter = new Apex2YamlEventConverter();

        assertThatThrownBy(() -> converter.init(null))
            .hasMessage("specified consumer properties are not applicable to the YAML event protocol");
        YamlEventProtocolParameters pars = new YamlEventProtocolParameters();
        converter.init(pars);

        assertThatThrownBy(() -> converter.toApexEvent("NonExistentEvent", ""))
            .hasMessageContaining("Failed to unmarshal YAML event")
            .cause().hasMessageStartingWith("an event definition for an event named \"NonExistentEvent\"");
        assertThatThrownBy(() -> converter.toApexEvent("TestEvent", null))
            .hasMessage("event processing failed, event is null");
        assertThatThrownBy(() -> converter.toApexEvent("TestEvent", 1))
            .hasMessage("error converting event \"1\" to a string");
        assertThatThrownBy(() -> converter.toApexEvent("TestEvent", ""))
            .cause().hasMessageContaining("Field \"doubleValue\" is missing");
        assertThatThrownBy(() -> converter.fromApexEvent(null))
            .hasMessage("event processing failed, Apex event is null");
        ApexEvent apexEvent = new ApexEvent(testEvent.getKey().getName(), testEvent.getKey().getVersion(),
            testEvent.getNameSpace(), testEvent.getSource(), testEvent.getTarget(),
            testEvent.getToscaPolicyState());
        apexEvent.put("doubleValue", 123.45);
        apexEvent.put("intValue", 123);
        apexEvent.put("stringValue", "123.45");

        apexEvent.setExceptionMessage("my wonderful exception message");
        String yamlString = (String) converter.fromApexEvent(apexEvent);
        assertTrue(yamlString.contains("my wonderful exception message"));

        apexEvent.remove("intValue");
        assertThatThrownBy(() -> converter.fromApexEvent(apexEvent))
            .hasMessageContaining("error parsing TestEvent:0.0.1 event to Json. Field \"intValue\" is missing");
        assertThatThrownBy(() -> converter.toApexEvent(null, ""))
            .hasMessageStartingWith("Failed to unmarshal YAML event")
            .cause().hasMessageStartingWith("event received without mandatory parameter \"name\"");
        pars.setNameAlias("TheNameField");
        assertThatThrownBy(() -> converter.toApexEvent(null, ""))
            .hasMessageStartingWith("Failed to unmarshal YAML event")
            .cause().hasMessageStartingWith("event received without mandatory parameter \"name\"");
        apexEvent.put("intValue", 123);

        apexEvent.remove("stringValue");
        assertNotNull(converter.fromApexEvent(apexEvent));
        apexEvent.put("stringValue", "123.45");

        String yamlInputString = "doubleValue: 123.45\n" + "intValue: 123";

        List<ApexEvent> eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals(123.45, eventList.get(0).get("doubleValue"));

        yamlInputString = "doubleValue: 123.45\nintValue: 123\nstringValue: null";

        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertNull(eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\nintValue: 123\nstringValue: TestEvent";
        pars.setNameAlias("stringValue");
        eventList = converter.toApexEvent(null, yamlInputString);
        assertEquals("TestEvent", eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\nintValue: 123\nstringValue: SomeOtherEvent";
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("SomeOtherEvent", eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\nintValue: 123\nstringValue: 0.0.1";
        pars.setNameAlias(null);
        pars.setVersionAlias("stringValue");
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("0.0.1", eventList.get(0).get("stringValue"));

        yamlInputString = "doubleValue: 123.45\nintValue: 123\nstringValue: org.some.other.namespace";
        pars.setVersionAlias(null);
        pars.setNameSpaceAlias("stringValue");
        final String yamlInputStringCopy = yamlInputString;
        assertThatThrownBy(() -> converter.toApexEvent("TestEvent", yamlInputStringCopy))
            .hasMessageStartingWith("Failed to unmarshal YAML event")
            .cause().hasMessageStartingWith("namespace \"org.some.other.namespace\" on event");

        yamlInputString = """
            doubleValue: 123.45
            intValue: 123
            stringValue: \
            org.onap.policy.apex.plugins.event.protocol.yaml""";
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("org.onap.policy.apex.plugins.event.protocol.yaml", eventList.get(0).getNameSpace());

        yamlInputString = """
            doubleValue: 123.45
            intValue: 123
            stringValue: MySource""";
        pars.setNameSpaceAlias(null);
        pars.setSourceAlias("stringValue");
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("MySource", eventList.get(0).getSource());

        yamlInputString = """
            doubleValue: 123.45
            intValue: 123
            stringValue: MyTarget""";
        pars.setSourceAlias(null);
        pars.setTargetAlias("stringValue");
        eventList = converter.toApexEvent("TestEvent", yamlInputString);
        assertEquals("MyTarget", eventList.get(0).getTarget());
        pars.setTargetAlias(null);

        pars.setSourceAlias(null);
        pars.setTargetAlias("intValue");
        assertThatThrownBy(() -> converter.toApexEvent("TestEvent", yamlInputStringCopy))
            .hasMessageStartingWith("Failed to unmarshal YAML event")
            .cause().hasMessageStartingWith("field \"target\" with type \"java.lang.Integer\"");
        pars.setTargetAlias(null);

        assertThatThrownBy(() -> converter.toApexEvent("TestEvent", """
            doubleValue: 123.45
            intValue: ~
            stringValue: MyString""")).cause().hasMessageStartingWith("mandatory field \"intValue\" is missing");
    }
}
